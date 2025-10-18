#!/bin/bash

# Performance Monitoring Script for ShareConnect QA
# Analyzes performance trends and generates alerts

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPORTS_DIR="$SCRIPT_DIR/reports"
PERFORMANCE_DIR="$REPORTS_DIR/performance_trends"
LATEST_REPORT_DIR=$(find "$REPORTS_DIR" -maxdepth 1 -type d -name "20*" | sort | tail -1)

# Create performance trends directory
mkdir -p "$PERFORMANCE_DIR"

echo "🔍 Analyzing performance trends..."

# Check if we have reports to analyze
if [ -z "$LATEST_REPORT_DIR" ] || [ ! -d "$LATEST_REPORT_DIR" ]; then
    echo "❌ No test reports found"
    exit 1
fi

LATEST_JSON="$LATEST_REPORT_DIR/comprehensive_report.json"
PERFORMANCE_JSON="$LATEST_REPORT_DIR/performance/performance_report.json"

if [ ! -f "$LATEST_JSON" ] || [ ! -f "$PERFORMANCE_JSON" ]; then
    echo "❌ Required report files not found"
    exit 1
fi

# Extract current metrics
CURRENT_SUCCESS_RATE=$(jq -r '.summary.success_rate' "$LATEST_JSON")
CURRENT_TOTAL_DURATION=$(jq -r '.summary.total_duration_seconds' "$LATEST_JSON")
CURRENT_AVG_EXEC_TIME=$(jq -r '.performance_report.summary.average_duration_ms' "$PERFORMANCE_JSON")
CURRENT_MEMORY_PEAK=$(jq -r '.performance_report.summary.peak_memory_mb' "$PERFORMANCE_JSON")
CURRENT_CPU_PEAK=$(jq -r '.performance_report.summary.peak_cpu_percent' "$PERFORMANCE_JSON")

echo "📊 Current Performance Metrics:"
echo "  Success Rate: $CURRENT_SUCCESS_RATE%"
echo "  Total Duration: $CURRENT_TOTAL_DURATION seconds"
echo "  Avg Execution Time: $CURRENT_AVG_EXEC_TIME ms"
echo "  Memory Peak: $CURRENT_MEMORY_PEAK MB"
echo "  CPU Peak: $CURRENT_CPU_PEAK%"

# Find previous reports for trend analysis
PREVIOUS_REPORTS=$(find "$REPORTS_DIR" -maxdepth 1 -type d -name "20*" | sort | head -n -1 | tail -n 5)

if [ -n "$PREVIOUS_REPORTS" ]; then
    echo ""
    echo "📈 Performance Trends (last 5 runs):"

    # Create trend data
    echo "date,success_rate,total_duration,avg_exec_time,memory_peak,cpu_peak" > "$PERFORMANCE_DIR/trends.csv"

    # Process each report
    for report_dir in $PREVIOUS_REPORTS; do
        if [ -f "$report_dir/comprehensive_report.json" ] && [ -f "$report_dir/performance/performance_report.json" ]; then
            timestamp=$(basename "$report_dir" | cut -d'_' -f1)
            success_rate=$(jq -r '.summary.success_rate' "$report_dir/comprehensive_report.json")
            total_duration=$(jq -r '.summary.total_duration_seconds' "$report_dir/comprehensive_report.json")
            avg_exec_time=$(jq -r '.performance_report.summary.average_duration_ms' "$report_dir/performance/performance_report.json")
            memory_peak=$(jq -r '.performance_report.summary.peak_memory_mb' "$report_dir/performance/performance_report.json")
            cpu_peak=$(jq -r '.performance_report.summary.peak_cpu_percent' "$report_dir/performance/performance_report.json")

            echo "$timestamp,$success_rate,$total_duration,$avg_exec_time,$memory_peak,$cpu_peak" >> "$PERFORMANCE_DIR/trends.csv"
        fi
    done

    # Add current data
    timestamp=$(basename "$LATEST_REPORT_DIR" | cut -d'_' -f1)
    echo "$timestamp,$CURRENT_SUCCESS_RATE,$CURRENT_TOTAL_DURATION,$CURRENT_AVG_EXEC_TIME,$CURRENT_MEMORY_PEAK,$CURRENT_CPU_PEAK" >> "$PERFORMANCE_DIR/trends.csv"

    # Calculate trends
    if [ $(wc -l < "$PERFORMANCE_DIR/trends.csv") -gt 2 ]; then
        # Calculate averages from last 3 runs
        LAST_3_SUCCESS=$(tail -n 3 "$PERFORMANCE_DIR/trends.csv" | cut -d',' -f2 | awk '{sum+=$1} END {print sum/NR}')
        LAST_3_DURATION=$(tail -n 3 "$PERFORMANCE_DIR/trends.csv" | cut -d',' -f3 | awk '{sum+=$1} END {print sum/NR}')
        LAST_3_EXEC_TIME=$(tail -n 3 "$PERFORMANCE_DIR/trends.csv" | cut -d',' -f4 | awk '{sum+=$1} END {print sum/NR}')

        echo ""
        echo "📊 Trend Analysis (avg of last 3 runs):"
        echo "  Success Rate: $LAST_3_SUCCESS%"
        echo "  Duration: $LAST_3_DURATION seconds"
        echo "  Exec Time: $LAST_3_EXEC_TIME ms"

        # Performance alerts
        ALERTS=()

        # Success rate alert
        if (( $(echo "$CURRENT_SUCCESS_RATE < 95.0" | bc -l) )); then
            ALERTS+=("⚠️  Success rate dropped below 95%: $CURRENT_SUCCESS_RATE%")
        fi

        # Duration increase alert (more than 20% increase)
        DURATION_CHANGE=$(echo "scale=2; ($CURRENT_TOTAL_DURATION - $LAST_3_DURATION) / $LAST_3_DURATION * 100" | bc -l)
        if (( $(echo "$DURATION_CHANGE > 20.0" | bc -l) )); then
            ALERTS+=("⚠️  Test duration increased by $DURATION_CHANGE%")
        fi

        # Memory usage alert
        if (( $(echo "$CURRENT_MEMORY_PEAK > 300" | bc -l) )); then
            ALERTS+=("⚠️  Memory usage exceeded 300MB: $CURRENT_MEMORY_PEAK MB")
        fi

        # CPU usage alert
        if (( $(echo "$CURRENT_CPU_PEAK > 60.0" | bc -l) )); then
            ALERTS+=("⚠️  CPU usage exceeded 60%: $CURRENT_CPU_PEAK%")
        fi

        # Display alerts
        if [ ${#ALERTS[@]} -gt 0 ]; then
            echo ""
            echo "🚨 Performance Alerts:"
            for alert in "${ALERTS[@]}"; do
                echo "  $alert"
            done
        else
            echo ""
            echo "✅ No performance alerts detected"
        fi
    fi
else
    echo "📝 First run - no trend data available yet"
fi

# Generate performance report
REPORT_FILE="$PERFORMANCE_DIR/performance_analysis_$(date +%Y%m%d_%H%M%S).md"

cat > "$REPORT_FILE" << EOF
# Performance Analysis Report
Generated: $(date)

## Current Metrics
- Success Rate: $CURRENT_SUCCESS_RATE%
- Total Duration: $CURRENT_TOTAL_DURATION seconds
- Average Execution Time: $CURRENT_AVG_EXEC_TIME ms
- Memory Peak Usage: $CURRENT_MEMORY_PEAK MB
- CPU Peak Usage: $CURRENT_CPU_PEAK%

## System Health
$(if (( $(echo "$CURRENT_SUCCESS_RATE >= 95.0" | bc -l) )); then echo "✅ Test success rate is healthy"; else echo "❌ Test success rate needs attention"; fi)
$(if (( $(echo "$CURRENT_MEMORY_PEAK <= 300" | bc -l) )); then echo "✅ Memory usage is within limits"; else echo "❌ Memory usage is high"; fi)
$(if (( $(echo "$CURRENT_CPU_PEAK <= 60.0" | bc -l) )); then echo "✅ CPU usage is within limits"; else echo "❌ CPU usage is high"; fi)

## Recommendations
$(if (( $(echo "$CURRENT_SUCCESS_RATE < 100.0" | bc -l) )); then echo "- Investigate test failures to maintain 100% success rate"; fi)
$(if (( $(echo "$CURRENT_TOTAL_DURATION > 3000" | bc -l) )); then echo "- Consider optimizing test execution time"; fi)
$(if (( $(echo "$CURRENT_MEMORY_PEAK > 250" | bc -l) )); then echo "- Monitor memory usage for potential leaks"; fi)

---
Report generated by performance_monitor.sh
EOF

echo ""
echo "📄 Performance report saved: $REPORT_FILE"

# Save metrics for CI/CD integration
echo "{
  \"timestamp\": \"$(date -Iseconds)\",
  \"success_rate\": $CURRENT_SUCCESS_RATE,
  \"total_duration\": $CURRENT_TOTAL_DURATION,
  \"avg_execution_time\": $CURRENT_AVG_EXEC_TIME,
  \"memory_peak\": $CURRENT_MEMORY_PEAK,
  \"cpu_peak\": $CURRENT_CPU_PEAK,
  \"alerts_count\": ${#ALERTS[@]}
}" > "$PERFORMANCE_DIR/latest_metrics.json"

echo "✅ Performance monitoring completed"