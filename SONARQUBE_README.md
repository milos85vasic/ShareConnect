# SonarQube Code Quality Integration

## Overview

ShareConnect now includes comprehensive code quality analysis using SonarQube, integrated into the automated testing pipeline. This ensures consistent code quality standards across the entire Android/Kotlin codebase.

## Architecture

### Dockerized Infrastructure
- **SonarQube Server:** Community Edition 10.6
- **Database:** PostgreSQL 15
- **Port:** 9001 (configurable)
- **Persistent Storage:** Docker volumes for data persistence

### Integration Points
- **Test Pipeline:** Integrated into `run_all_tests.sh`
- **CI/CD:** GitHub Actions workflow configured
- **Non-blocking:** Asynchronous container management
- **Auto-fixing:** Automated issue resolution capabilities

## Quick Start

### Prerequisites
```bash
# Docker and Docker Compose
docker --version
docker compose version  # or docker-compose --version
```

### Basic Usage
```bash
# Start SonarQube containers asynchronously
./run_sonarqube_tests.sh --async

# Check status
./run_sonarqube_scan.sh --status

# Run full analysis
./run_sonarqube_tests.sh

# Stop containers
./run_sonarqube_scan.sh --stop
```

### Integration with Test Suite
```bash
# Run all tests including SonarQube analysis
./run_all_tests.sh
```

## Configuration

### Project Configuration
- **File:** `sonar-project.properties`
- **Project Key:** `shareconnect`
- **Languages:** Kotlin, Java, Multi-language
- **Exclusions:** Build artifacts, generated code

### Quality Profiles
- **Android:** Standard Android rules
- **Kotlin:** Kotlin-specific best practices
- **Security:** OWASP security rules
- **Performance:** Performance optimization rules

## Analysis Scope

### Source Code
- All Android modules (ShareConnector, TransmissionConnect, etc.)
- Kotlin and Java source files
- Resource files and manifests

### Test Coverage
- Unit tests analysis
- Integration test structure
- Test naming conventions

### Metrics Collected
- **Complexity:** Cyclomatic complexity, cognitive complexity
- **Duplications:** Code duplication percentage
- **Coverage:** Test coverage integration (when available)
- **Security:** Security hotspots and vulnerabilities
- **Maintainability:** Technical debt assessment

## Reports

### Generated Reports
- **HTML Report:** Visual analysis dashboard
- **JSON Report:** Machine-readable metrics
- **Text Summary:** Quick overview
- **SonarQube UI:** Web-based detailed analysis

### Report Location
```
Documentation/Tests/YYYYMMDD_HHMMSS_SONARQUBE_SCAN/
├── sonarqube_report.html
├── sonarqube_report.json
└── sonarqube_summary.txt
```

## Quality Gates

### Default Gates
- **Reliability:** No bugs
- **Security:** No vulnerabilities
- **Maintainability:** Technical debt ratio < 5%
- **Coverage:** Test coverage > 80% (when available)

### Custom Gates
Configurable through SonarQube UI for project-specific requirements.

## Automation Features

### Auto-fixing
The `fix_sonarqube_issues.sh` script provides automated fixing for common issues:
- Unused variables removal
- Import optimization
- Code formatting

### CI/CD Integration
- **GitHub Actions:** `.github/workflows/comprehensive-qa.yml`
- **Quality Gate:** Pipeline fails on violations
- **Notifications:** Automated reporting

## Troubleshooting

### Common Issues

#### Containers Won't Start
```bash
# Check Docker status
docker info

# Clean up existing containers
./run_sonarqube_scan.sh --stop

# Check port conflicts
lsof -i :9001
```

#### Analysis Fails
```bash
# Check SonarQube status
./run_sonarqube_scan.sh --status

# Verify API availability
curl http://localhost:9001/api/system/status
```

#### Memory Issues
```bash
# Increase Docker memory allocation
# Or reduce SonarQube memory settings in docker-compose.sonarqube.yml
```

### Performance Optimization

#### For Large Codebases
- Increase container memory limits
- Use incremental analysis
- Configure appropriate exclusions

#### For CI/CD
- Use pre-built containers
- Implement caching strategies
- Parallel analysis execution

## Advanced Configuration

### Custom Rules
```xml
<!-- Example custom rule configuration -->
<rule>
  <key>custom:android-naming</key>
  <name>Android Naming Convention</name>
  <description>Enforce Android naming conventions</description>
</rule>
```

### Integration with IDE
- **IntelliJ IDEA:** SonarLint plugin
- **Android Studio:** Built-in code analysis
- **VS Code:** SonarLint extension

## API Reference

### SonarQube REST API
- **System Status:** `GET /api/system/status`
- **Project Analysis:** `GET /api/measures/component`
- **Issues:** `GET /api/issues/search`
- **Quality Gates:** `GET /api/qualitygates/project_status`

### Script API
```bash
# Start analysis
./run_sonarqube_scan.sh --start

# Check status
./run_sonarqube_scan.sh --status

# Run scan
./run_sonarqube_scan.sh --scan

# Stop services
./run_sonarqube_scan.sh --stop
```

## Contributing

### Code Quality Standards
1. **Pre-commit:** Run SonarQube analysis locally
2. **PR Checks:** Automated quality gate verification
3. **Code Reviews:** Quality metrics review
4. **Documentation:** Update analysis configuration as needed

### Adding New Rules
1. Configure in SonarQube UI
2. Test locally
3. Update documentation
4. Integrate into CI/CD

## Support

### Resources
- **SonarQube Documentation:** https://docs.sonarsource.com/sonarqube/
- **Community Edition:** https://www.sonarsource.com/products/sonarqube/community-edition/
- **Docker Images:** https://hub.docker.com/_/sonarqube

### Getting Help
1. Check the troubleshooting section
2. Review SonarQube logs: `docker logs shareconnect-sonarqube`
3. Consult the comprehensive test documentation
4. Open issues for complex problems

---

## Summary

The SonarQube integration provides enterprise-grade code quality analysis with:
- ✅ Dockerized, reusable infrastructure
- ✅ Non-blocking CLI integration
- ✅ Comprehensive Android/Kotlin analysis
- ✅ Automated reporting and quality gates
- ✅ CI/CD pipeline integration
- ✅ Auto-fixing capabilities

This ensures ShareConnect maintains the highest code quality standards throughout development.