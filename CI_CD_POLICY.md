# CI/CD Policy - Manual Execution Only

## Overview

ShareConnect implements a **manual-only CI/CD policy** where all automated testing and security scanning workflows must be explicitly triggered by authorized personnel. This approach ensures:

- **Controlled Resource Usage**: No unexpected costs from automated runs
- **Quality Assurance**: Tests run only when code is ready for validation
- **Security Focus**: Security scans performed deliberately with full attention
- **Development Flexibility**: No blocking of development workflow

## Policy Statement

**ALL CI/CD workflows are configured with `workflow_dispatch` triggers only.** They will NOT execute automatically on:
- Git commits
- Pull requests
- Branch pushes
- Tag creation
- Any other automated triggers

## Available Workflows

### 1. Snyk Security Scanning
**File:** `.github/workflows/snyk-security-scan.yml`
**Purpose:** Comprehensive security vulnerability assessment

**Parameters:**
- `scan_type`: full, deps-only, container-only, gradle-only, quick
- `severity_threshold`: low, medium, high, critical
- `fail_on_vulnerabilities`: true/false

**When to Run:**
- Before major releases
- After dependency updates
- During security audits
- When investigating security concerns

### 2. Combined QA + Security Testing
**File:** `.github/workflows/combined-qa-security.yml`
**Purpose:** Integrated functional testing and security scanning

**Parameters:**
- `run_qa_tests`: Enable/disable QA testing
- `run_security_scan`: Enable/disable security scanning
- `qa_test_suite`: comprehensive, smoke, regression
- `security_scan_type`: full, deps-only, container-only, gradle-only, quick
- `security_severity`: low, medium, high, critical
- `fail_on_security_issues`: true/false

**When to Run:**
- Pre-release validation
- Major feature completion
- Integration testing phases
- End-to-end validation

### 3. Comprehensive QA Testing
**File:** `.github/workflows/comprehensive-qa.yml`
**Purpose:** AI-powered functional and performance testing

**Parameters:**
- `test_suite`: comprehensive, smoke, regression

**When to Run:**
- Feature development completion
- Bug fix validation
- Performance regression testing
- UI/UX validation

## Execution Guidelines

### Who Can Trigger Workflows

**Authorized Personnel:**
- Development Team Leads
- QA Engineers
- Security Officers
- Release Managers
- Repository Administrators

### When to Trigger Workflows

**Recommended Schedule:**
- **Daily**: Quick security scans during active development
- **Weekly**: Full security scans and QA test suites
- **Pre-Release**: Combined QA + Security testing
- **Post-Release**: Regression testing validation

**Trigger Conditions:**
- Code is committed and stable
- Dependencies are updated and tested
- No known critical issues exist
- Team is prepared to address findings

### How to Trigger Workflows

1. **Navigate to GitHub Actions**
   ```
   https://github.com/your-org/ShareConnect/actions
   ```

2. **Select Workflow**
   - Choose appropriate workflow from the list

3. **Configure Parameters**
   - Set scan types, severity levels, test suites
   - Enable/disable specific testing components

4. **Execute Workflow**
   - Click "Run workflow"
   - Monitor execution in real-time

5. **Review Results**
   - Check workflow summary for key metrics
   - Download detailed reports from artifacts
   - Address any issues found

## Local Development

### Alternative to CI/CD

For development and testing without CI/CD resources:

```bash
# Quick security check (no Docker required)
./snyk_scan_on_demand.sh --severity medium

# Full security analysis (requires Docker)
./run_snyk_scan.sh --start
./run_snyk_scan.sh --scan

# Integrated QA + Security (requires emulator/device)
./run_ai_qa_with_snyk.sh
```

### Local vs CI/CD Comparison

| Aspect | Local Development | CI/CD Workflows |
|--------|------------------|-----------------|
| Speed | Fast (subset testing) | Comprehensive |
| Resources | Local machine | Cloud resources |
| Cost | Free | GitHub Actions minutes |
| Automation | Manual execution | Workflow automation |
| Reporting | Local files | GitHub artifacts |

## Quality Gates

### Security Requirements

**Before Release:**
- ✅ Zero critical vulnerabilities
- ✅ ≤5 high-severity vulnerabilities (with mitigation plans)
- ✅ All dependencies up-to-date within security SLA
- ✅ Security scan completed within last 7 days

**Before Deployment:**
- ✅ QA tests passing (≥95% success rate)
- ✅ No blocking security issues
- ✅ Performance benchmarks met
- ✅ Integration tests successful

### Automated Enforcement

**Workflow Behavior:**
- Security scans fail on critical/high vulnerabilities (configurable)
- QA tests fail on test failures (non-configurable)
- Reports generated regardless of outcome
- Notifications sent for failures (if configured)

## Monitoring and Reporting

### Workflow Metrics

**Tracked Metrics:**
- Execution time and success rate
- Vulnerability counts by severity
- Test pass/fail ratios
- Resource utilization

**Reporting:**
- GitHub workflow summaries
- Detailed HTML/JSON reports
- Trend analysis over time
- Slack/email notifications (optional)

### Audit Trail

**Maintained Records:**
- All workflow executions with parameters
- Security scan results and timestamps
- QA test results and coverage
- Issue identification and resolution

## Exceptions and Overrides

### Emergency Situations

**Allowed Overrides:**
- Critical security patches (immediate deployment)
- Emergency bug fixes (reduced testing scope)
- Infrastructure failures (alternative validation)

**Override Process:**
1. Document emergency situation
2. Notify team leads
3. Execute minimal required testing
4. Document override decision
5. Schedule full testing post-deployment

### Policy Changes

**Policy Modification:**
- Requires approval from Development Lead
- Security Officer review required
- Documentation update mandatory
- Team notification required

## Support and Troubleshooting

### Common Issues

**Workflow Won't Start:**
- Check repository permissions
- Verify workflow files are present
- Confirm GitHub Actions are enabled

**Scan Failures:**
- Verify SNYK_TOKEN is set in secrets
- Check Docker availability (for full scans)
- Review error logs in workflow output

**Test Failures:**
- Check emulator setup
- Verify ANTHROPIC_API_KEY for AI QA
- Review device connectivity

### Getting Help

**Support Resources:**
- Check workflow logs for detailed errors
- Review CI_CD_POLICY.md for procedures
- Contact DevOps team for infrastructure issues
- Security team for vulnerability concerns

## Policy Compliance

### Regular Reviews

**Quarterly Review:**
- Assess workflow effectiveness
- Review resource utilization
- Update procedures as needed
- Confirm manual-only policy adherence

### Compliance Monitoring

**Automated Checks:**
- Workflow trigger validation
- Permission auditing
- Resource usage monitoring
- Policy compliance reporting

---

## Quick Reference

### Trigger Security Scan
1. Go to Actions → "Snyk Security Scanning"
2. Click "Run workflow"
3. Select scan type and severity
4. Monitor results

### Trigger Combined Testing
1. Go to Actions → "Combined QA + Security Testing"
2. Click "Run workflow"
3. Configure QA and security parameters
4. Review integrated results

### Emergency Override
1. Document emergency situation
2. Get approval from team lead
3. Execute with minimal scope
4. Schedule full testing later

**Remember:** Manual control ensures quality and prevents unexpected resource usage.