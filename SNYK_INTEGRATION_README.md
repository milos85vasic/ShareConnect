# ShareConnect Snyk Security Integration

## Overview

ShareConnect now includes comprehensive security vulnerability scanning using Snyk, integrated with the existing AI QA testing framework. This provides automated detection and monitoring of security vulnerabilities across all dependencies, containers, and code.

## Features

### 🔍 Comprehensive Scanning
- **Dependency Analysis**: Scans all Gradle dependencies for known vulnerabilities
- **Container Security**: Scans Docker images for OS-level vulnerabilities
- **Code Analysis**: Static analysis for security issues in Kotlin/Java code
- **License Compliance**: Checks for license compatibility issues

### 🤖 AI QA Integration
- **Automated Execution**: Snyk scans run as part of AI QA test suites
- **Integrated Reporting**: Combined reports showing both functional and security test results
- **Quality Gates**: Automated pass/fail decisions based on vulnerability severity

### 🐳 Docker Integration
- **Containerized Scanning**: Runs in isolated Docker containers
- **On-Demand Scanning**: Quick scans without full environment setup
- **CI/CD Ready**: Easy integration with build pipelines

## Setup Instructions

### Option 1: Full Snyk Experience (Recommended)

#### 1. Obtain Snyk API Token

1. Sign up for a Snyk account at [snyk.io](https://snyk.io)
2. Navigate to Account Settings → API Token
3. Generate a new token with appropriate permissions
4. Set the environment variable:

```bash
export SNYK_TOKEN=your_snyk_token_here
```

**Benefits with token:**
- ✅ Full dependency scanning
- ✅ Container image scanning
- ✅ Private repository support
- ✅ Advanced reporting and analytics
- ✅ Integration with Snyk dashboard
- ✅ Unlimited scans

### Option 2: Freemium Mode (No Token Required)

ShareConnect supports running Snyk in freemium mode without an API token for basic security scanning.

**Freemium limitations:**
- ⚠️ Limited scan frequency
- ⚠️ Public repositories only
- ⚠️ Basic vulnerability detection
- ⚠️ No container scanning
- ⚠️ No advanced reporting
- ⚠️ No dashboard integration

**To use freemium mode:**
```bash
# Simply run without setting SNYK_TOKEN
./snyk_scan_on_demand.sh --severity medium
```

**Freemium mode is ideal for:**
- Getting started with security scanning
- Open source projects
- Basic vulnerability awareness
- Learning about security practices

### 2. Configure Organization (Optional)

If you have a Snyk organization, set the organization ID:

```bash
export SNYK_ORG_ID=your_org_id_here
```

### 3. Environment Setup

The integration automatically detects and uses:
- Local Snyk CLI (if installed)
- Docker-based scanning (fallback)
- Environment-specific configurations

## Usage

### Quick Security Scan

Run a fast security scan on-demand:

```bash
# With token (full functionality)
export SNYK_TOKEN=your_token_here
./snyk_scan_on_demand.sh --severity medium

# Without token (freemium mode)
./snyk_scan_on_demand.sh --severity medium
```

Options:
- `--severity high`: Only report high/critical vulnerabilities
- `--fail-on-issues`: Exit with error code if vulnerabilities found (token mode only)

### Comprehensive Security Scan

Run full security analysis with detailed reporting:

```bash
# With token (full functionality)
export SNYK_TOKEN=your_token_here
./run_snyk_scan.sh --scan

# Without token (freemium mode - limited)
./run_snyk_scan.sh --scan  # Will show freemium limitations
```

Options:
- `--deps-only`: Scan dependencies only
- `--container-only`: Scan containers only (token required)
- `--gradle-only`: Scan Gradle files only
- `--severity LEVEL`: Set severity threshold (low|medium|high|critical)

### Integrated AI QA + Security Testing

Run complete testing including security scans:

```bash
# With token (full functionality)
export SNYK_TOKEN=your_token_here
./run_ai_qa_with_snyk.sh

# Without token (freemium mode)
./run_ai_qa_with_snyk.sh  # Security scan will run in freemium mode
```

This combines:
- AI-powered functional testing
- Security vulnerability scanning (freemium or full mode)
- Integrated reporting and analysis

### Docker-Based Scanning

Start Snyk containers for persistent scanning:

```bash
./run_snyk_scan.sh --start
./run_snyk_scan.sh --scan
./run_snyk_scan.sh --stop
```

## Configuration Files

### Docker Compose Configuration (`docker-compose.snyk.yml`)

```yaml
version: '3.8'
services:
  snyk:
    image: snyk/snyk:linux
    environment:
      - SNYK_TOKEN=${SNYK_TOKEN}
    volumes:
      - .:/app
```

### Snyk Configuration (`.snyk`)

```yaml
org: ${SNYK_ORG_ID}
severity-threshold: medium
detection-depth: 6
exclude:
  - "**/build/**"
  - "**/generated/**"
```

### AI QA Integration (`qa-ai/qa-config.yaml`)

```yaml
security:
  snyk_scanning:
    enabled: true
    severity_threshold: "medium"
    fail_on_vulnerabilities: true
    scan_types:
      - "dependencies"
      - "containers"
      - "gradle"
```

## Test Cases

### Security Test Suite (`qa-ai/testbank/security/snyk_security_scan.yaml`)

Comprehensive test case covering:
- Environment setup validation
- Dependency scanning
- Container scanning
- Gradle-specific analysis
- Report generation
- Vulnerability analysis

## Report Formats

### HTML Reports
- **Integrated Report**: `integrated_report.html` - Combined AI QA + Security results
- **Security Report**: `snyk_report.html` - Detailed vulnerability analysis
- **AI QA Report**: `comprehensive_report.html` - Functional test results

### JSON Reports
- **Security Data**: `snyk_report.json` - Machine-readable vulnerability data
- **Test Results**: `comprehensive_report.json` - AI QA test data

### Text Summaries
- **Quick Summary**: `snyk_quick_summary.txt` - Fast vulnerability overview
- **Integrated Summary**: `integrated_summary.txt` - Combined test status

## Quality Gates

### Security Thresholds
- **Critical**: 0 allowed (blocks deployment)
- **High**: ≤5 allowed (warnings)
- **Medium**: ≤20 allowed (monitoring)
- **Low**: Unlimited (informational)

### Integration Rules
- Security scans must pass for successful builds
- Critical vulnerabilities block releases
- High vulnerabilities require security review
- Automated fixes suggested where available

## CI/CD Integration (Manual Only)

**⚠️ IMPORTANT:** All CI/CD workflows are configured for manual execution only (`workflow_dispatch`). They will NOT run automatically on commits, pull requests, or any other triggers.

### Available GitHub Actions Workflows

1. **Snyk Security Scanning** (`.github/workflows/snyk-security-scan.yml`)
   - Dedicated security vulnerability scanning
   - Manual trigger only
   - Multiple scan types and severity levels

2. **Combined QA + Security** (`.github/workflows/combined-qa-security.yml`)
   - Integrated functional testing + security scanning
   - Manual trigger only
   - Configurable test suites and scan parameters

3. **Comprehensive QA** (`.github/workflows/comprehensive-qa.yml`)
   - Existing QA testing workflow
   - Manual trigger only

### Manual Workflow Execution

**To run security scanning:**
1. Go to GitHub Actions tab
2. Select "Snyk Security Scanning" workflow
3. Click "Run workflow"
4. Configure parameters:
   - Scan type (full, deps-only, container-only*, gradle-only, quick)
   - Severity threshold (low, medium, high, critical)
   - Fail on vulnerabilities (true/false)

*Container scanning requires SNYK_TOKEN - will be skipped in freemium mode

**To run combined QA + Security:**
1. Go to GitHub Actions tab
2. Select "Combined QA + Security Testing" workflow
3. Click "Run workflow"
4. Configure parameters:
   - Enable/disable QA tests and security scanning
   - Select test suites and scan parameters

### Example Manual Execution

```bash
# This will NOT run automatically - requires manual GitHub Actions trigger
# Go to: https://github.com/your-org/ShareConnect/actions
# Select workflow and click "Run workflow"
```

### Local Development

For local development and testing, use the provided scripts. **No token required** - works in freemium mode!

```bash
# Quick security check (freemium mode)
./snyk_scan_on_demand.sh --severity medium

# Full security analysis (freemium mode)
./run_snyk_scan.sh --start
./run_snyk_scan.sh --scan

# Integrated QA + Security (freemium mode)
./run_ai_qa_with_snyk.sh

# With token for full functionality (optional)
export SNYK_TOKEN=your_token_here
./snyk_scan_on_demand.sh --severity medium --fail-on-issues
```

**Freemium Mode Benefits:**
- ✅ No setup required
- ✅ Basic vulnerability detection
- ✅ Works immediately
- ✅ Great for learning security practices
- ✅ Perfect for open source projects

### Legacy CI/CD Examples (For Reference Only)

If you need to adapt these for automated CI/CD (not recommended for this project):

#### GitHub Actions (Automated - NOT USED)

```yaml
name: Security Scan
on: [push, pull_request]  # ⚠️ AUTOMATED - NOT USED IN THIS PROJECT

jobs:
  security:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run Snyk Security Scan
        run: ./snyk_scan_on_demand.sh --fail-on-issues
        env:
          SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
```

#### Jenkins Pipeline (Automated - NOT USED)

```groovy
pipeline {
    agent any
    environment {
        SNYK_TOKEN = credentials('snyk-token')
    }
    stages {
        stage('Security Scan') {
            steps {
                sh './snyk_scan_on_demand.sh --fail-on-issues'
            }
        }
    }
}
```

## Vulnerability Management

### Automated Fixes
- Snyk suggests specific version upgrades
- Dependency updates with compatibility checking
- Automated PR creation for fixes

### Manual Review Process
1. **Triage**: Classify vulnerabilities by severity and impact
2. **Assessment**: Evaluate exploitability and business risk
3. **Remediation**: Apply fixes or implement mitigations
4. **Verification**: Re-scan to confirm resolution

### Risk Assessment Matrix

| Severity | Impact | Likelihood | Action |
|----------|--------|------------|--------|
| Critical | High   | High       | Fix immediately |
| High     | High   | Medium     | Fix in 24-48 hours |
| Medium   | Medium | Medium     | Fix in next sprint |
| Low      | Low    | Low        | Monitor and plan |

## Monitoring and Alerts

### Dashboard Integration
- Real-time vulnerability tracking
- Trend analysis over time
- Compliance reporting
- Risk scoring

### Notification Channels
- **Slack**: Real-time alerts for new vulnerabilities
- **Email**: Weekly security summaries
- **JIRA**: Automatic ticket creation for critical issues
- **Dashboard**: Web-based security status overview

## Troubleshooting

### Common Issues

#### "SNYK_TOKEN not set"
```bash
export SNYK_TOKEN=your_token_here
```

#### "Docker not running"
```bash
# Start Docker service
sudo systemctl start docker
# Or use local Snyk CLI
npm install -g snyk
```

#### "Scan timeout"
- Increase timeout in configuration
- Run partial scans (`--deps-only`)
- Check network connectivity

#### "False positives"
- Use `.snyk` file to exclude known false positives
- Report issues to Snyk support
- Add custom ignore rules

### Debug Mode

Enable detailed logging:

```bash
export SNYK_DEBUG=true
./snyk_scan_on_demand.sh
```

## Performance Optimization

### Scan Optimization
- **Incremental Scans**: Only scan changed dependencies
- **Parallel Execution**: Multiple scan types run concurrently
- **Caching**: Reuse scan results for unchanged code
- **Filtering**: Exclude irrelevant files and dependencies

### Resource Management
- **Memory Limits**: Configurable heap sizes for large projects
- **Timeout Controls**: Prevent runaway scans
- **Cleanup**: Automatic removal of temporary files

## Compliance and Standards

### Security Standards
- **OWASP Top 10**: Coverage for web application vulnerabilities
- **CVE Database**: Integration with National Vulnerability Database
- **License Compliance**: Open source license checking
- **Industry Standards**: SOC 2, ISO 27001 compatibility

### Audit Trail
- **Scan History**: Complete record of all security scans
- **Change Tracking**: Correlation with code changes
- **Evidence Collection**: Documentation for compliance audits
- **Reporting**: Automated compliance report generation

## Support and Resources

### Documentation
- [Snyk CLI Documentation](https://docs.snyk.io/snyk-cli)
- [Snyk API Reference](https://snyk.docs.apiary.io/)
- [ShareConnect Security Guide](./SECURITY.md)

### Community Resources
- [Snyk Community Forum](https://community.snyk.io/)
- [GitHub Security Advisories](https://github.com/advisories)
- [OWASP Resources](https://owasp.org/)

### Professional Services
- Snyk Professional support
- Security consulting partners
- Training and certification programs

---

## Example Output

### Successful Scan
```
🔍 ShareConnect Snyk Security Scanning
======================================

🔧 Testing configuration... ✅
📦 Scanning dependencies... ✅
🐳 Scanning container images... ✅
📱 Scanning Android dependencies... ✅
📊 Generating reports... ✅

✅ Snyk scanning completed
- Total vulnerabilities: 0
- Critical: 0
- High: 0
- Ready for deployment
```

### Scan with Vulnerabilities
```
🔍 ShareConnect Snyk Security Scanning
======================================

🔧 Testing configuration... ✅
📦 Scanning dependencies... ⚠️
🐳 Scanning container images... ✅
📱 Scanning Android dependencies... ✅
📊 Generating reports... ✅

⚠️ Security vulnerabilities detected
- Total vulnerabilities: 3
- Critical: 0
- High: 1
- Review report: reports/snyk_report.html
```

This integration provides ShareConnect with enterprise-grade security scanning capabilities, ensuring that security is built into the development lifecycle from the start.