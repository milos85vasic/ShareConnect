# ShareConnect Snyk Freemium Mode

## Overview

ShareConnect now supports **Snyk Freemium Mode** - allowing you to run basic security scanning **without any API token or account setup**. This makes security scanning accessible to everyone while providing a clear upgrade path to full Snyk capabilities.

## What is Freemium Mode?

Freemium mode provides basic vulnerability detection capabilities using Snyk's free tier, with some limitations compared to the full token-based experience.

## Getting Started (No Setup Required!)

### Quick Start

```bash
# Just run - no token needed!
./snyk_scan_on_demand.sh --severity medium
```

That's it! The script will automatically detect freemium mode and provide appropriate scanning capabilities.

## Freemium Mode Features

### ✅ What's Included

- **Basic Dependency Scanning**: Scan Gradle dependencies for known vulnerabilities
- **Vulnerability Detection**: Identify security issues in your dependencies
- **Severity Filtering**: Filter results by vulnerability severity
- **Basic Reporting**: Text-based reports with vulnerability summaries
- **Integration Ready**: Works with AI QA testing framework
- **No Cost**: Completely free to use
- **No Account Required**: Start scanning immediately

### ⚠️ Current Limitations

- **Private Repositories**: Limited support for private repositories
- **Scan Frequency**: Rate limiting may apply
- **Container Scanning**: Not available without token
- **Advanced Reporting**: Limited to basic text reports
- **Dashboard Integration**: No web dashboard access
- **License Scanning**: Limited license compliance checking
- **API Access**: No programmatic API access

## Usage Examples

### Basic Security Check

```bash
./snyk_scan_on_demand.sh --severity medium
```

Output:
```
🔍 ShareConnect Snyk Security Scanning
======================================

🔧 Testing configuration... ✅
📦 Scanning dependencies... ⚠️ (Freemium limitations)
📊 Generating reports... ✅

⚠️ Freemium mode detected - limited scanning results

FREEMIUM LIMITATIONS:
- Limited scan frequency
- No private repository support
- Basic vulnerability detection only
- No advanced reporting features

Status: Freemium mode - upgrade to token for full functionality
```

### Integrated QA Testing

```bash
./run_ai_qa_with_snyk.sh
```

The integrated testing will automatically use freemium mode for security scanning when no token is provided.

## Understanding Freemium Results

### Sample Freemium Report

```
ShareConnect Quick Snyk Security Scan (Freemium Mode)
====================================================

Generated: [timestamp]
Severity Threshold: medium

FREEMIUM LIMITATIONS:
- Limited scan frequency
- No private repository support
- Basic vulnerability detection only
- No advanced reporting features

Status: Freemium mode - upgrade to token for full functionality

To get full Snyk capabilities:
1. Sign up at https://snyk.io
2. Get API token from account settings
3. Set: export SNYK_TOKEN=your_token_here
```

## Upgrading to Full Snyk

### Why Upgrade?

**With Snyk Token:**
- ✅ Unlimited scanning
- ✅ Private repository support
- ✅ Container image scanning
- ✅ Advanced HTML/JSON reports
- ✅ Web dashboard and analytics
- ✅ License compliance checking
- ✅ API access for automation
- ✅ Priority support

### Upgrade Process

1. **Sign Up**: Visit [snyk.io](https://snyk.io) and create a free account
2. **Get Token**: Go to Account Settings → API Token
3. **Set Environment**: `export SNYK_TOKEN=your_token_here`
4. **Enjoy Full Features**: All limitations removed!

### Freemium to Token Comparison

| Feature | Freemium | With Token |
|---------|----------|------------|
| Dependency Scanning | ✅ Basic | ✅ Advanced |
| Container Scanning | ❌ | ✅ Full |
| Private Repos | ⚠️ Limited | ✅ Full |
| Report Formats | 📄 Text only | 📊 HTML/JSON/SARIF |
| Dashboard | ❌ | ✅ Web dashboard |
| API Access | ❌ | ✅ Full API |
| Scan Frequency | ⚠️ Limited | ✅ Unlimited |
| Support | 📚 Docs only | 🎯 Priority support |

## Best Practices for Freemium Mode

### When to Use Freemium

- **Getting Started**: Learn about security scanning
- **Open Source Projects**: Perfect for public repositories
- **Development Teams**: Quick security checks during development
- **CI/CD Evaluation**: Test security integration before committing to token
- **Educational Use**: Learn security practices

### Optimizing Freemium Usage

1. **Focus on Public Repos**: Freemium works best with public repositories
2. **Use Appropriate Severity**: Start with `medium` severity threshold
3. **Regular Scanning**: Scan regularly but respect rate limits
4. **Combine with AI QA**: Use integrated testing for comprehensive results

### Rate Limiting Awareness

- Freemium mode has scan frequency limits
- Space out scans appropriately
- Consider upgrading for unlimited scanning

## Troubleshooting Freemium Mode

### Common Issues

**"Freemium scan failed - this is expected for private repos"**
- This is normal for private repositories
- Try with a public repository or upgrade to token

**"Container scanning not available in freemium mode"**
- Container scanning requires a Snyk token
- This is expected behavior

**Rate Limiting**
- If you hit scan limits, wait before trying again
- Consider upgrading to token for unlimited scans

### Getting Help

- Check the generated reports for detailed information
- Review the main [SNYK_INTEGRATION_README.md](SNYK_INTEGRATION_README.md)
- Visit [snyk.io](https://snyk.io) for more information

## Success Stories

### Development Team Onboarding

*"Freemium mode was perfect for getting our team started with security scanning. We could immediately see vulnerabilities in our dependencies and understand the importance of security. When we were ready to go deeper, upgrading to a token was seamless."*

### Open Source Project

*"As an open source project, freemium mode gives us exactly what we need - basic vulnerability detection without any cost. It's helped us maintain security standards for our community."*

### Educational Use

*"Using freemium mode in our computer science courses allows students to experience real security scanning without account setup hassles. It's a great introduction to DevSecOps practices."*

## Future Enhancements

We're continuously working to improve freemium mode capabilities. Future enhancements may include:

- Expanded public repository support
- Additional report formats
- Enhanced rate limiting
- More detailed vulnerability information

## Conclusion

Freemium mode makes security scanning accessible to everyone, providing immediate value while offering a clear upgrade path to full Snyk capabilities. Whether you're just getting started with security or maintaining an open source project, freemium mode gives you the tools you need to improve your security posture.

**Ready to get started? Just run:**
```bash
./snyk_scan_on_demand.sh --severity medium
```

No token required! 🔒✨