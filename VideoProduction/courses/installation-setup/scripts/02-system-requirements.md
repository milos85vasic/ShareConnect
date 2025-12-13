# Lesson 2: System Requirements
**Duration**: 8 minutes  
**Target Audience**: New users preparing for installation  
**Learning Objectives**: 
- Understand Android version and hardware requirements
- Learn about network configuration needs
- Identify security considerations
- Prepare the environment for installation

## Video Script

### [0:00-0:30] Introduction and Context

**VISUAL**: Clean, professional opening with system requirements checklist

**NARRATOR**:
"Before we install ShareConnect, let's make sure your system is properly configured. In this lesson, we'll cover the essential requirements for Android version, hardware specifications, network setup, and security considerations. Taking a few minutes to verify these requirements will ensure a smooth installation process."

**ON-SCREEN TEXT**: "System Requirements Checklist"

---

### [0:30-2:00] Android Version Requirements

**VISUAL**: Android version compatibility matrix with visual indicators

**NARRATOR**:
"ShareConnect supports Android 6.0 Marshmallow and higher, but we recommend Android 8.0 Oreo or later for optimal performance. Here's why the Android version matters:"

**VISUAL**: Feature comparison table showing Android 6.0 vs 8.0+ features

**NARRATOR**:
"Android 6.0 introduced the runtime permission model that ShareConnect uses for security. Android 8.0 added background execution limits that improve battery life, and Android 10+ provides enhanced privacy controls that work perfectly with our security framework."

**VISUAL**: Screen recording showing how to check Android version

**NARRATOR**:
"To check your Android version, go to Settings > About Phone > Android Version. If you're running Android 8.0 or later, you're all set. If you're on Android 6.0 or 7.0, ShareConnect will still work, but you might experience some limitations with advanced features."

**ON-SCREEN TEXT**: "Minimum: Android 6.0 • Recommended: Android 8.0+"

---

### [2:00-3:30] Hardware Recommendations

**VISUAL**: Hardware specification breakdown with visual icons

**NARRATOR**:
"Let's talk about hardware requirements. ShareConnect is designed to be efficient, but since you'll be managing multiple services, adequate hardware ensures smooth performance."

**VISUAL**: RAM requirements visualization

**NARRATOR**:
"For RAM, we recommend at least 2GB for basic usage, but 4GB or more is ideal if you'll be actively using multiple connectors simultaneously. The more RAM you have, the more smoothly ShareConnect can handle background sync operations and large file transfers."

**VISUAL**: Storage space breakdown

**NARRATOR**:
"Storage requirements vary based on your usage patterns. The ShareConnect apps themselves only need about 200MB total, but you'll want additional space for cached data, downloaded files, and offline content. We recommend at least 1GB of free storage for optimal operation."

**VISUAL**: Processor performance impact demonstration

**NARRATOR**:
"Processor speed affects how smoothly the user interface responds and how quickly sync operations complete. Any modern ARM processor from the last 5 years should handle ShareConnect well, but newer processors will provide snappier performance, especially when switching between multiple connector apps."

**ON-SCREEN TEXT**: "RAM: 2GB minimum, 4GB+ recommended • Storage: 1GB+ free space • Processor: Modern ARM CPU"

---

### [3:30-5:00] Network Configuration Needs

**VISUAL**: Network topology diagram showing typical home setup

**NARRATOR**:
"Network configuration is crucial for ShareConnect since it manages connections to your self-hosted services. Let's cover the essential network requirements."

**VISUAL**: Wi-Fi vs mobile data comparison

**NARRATOR**:
"Wi-Fi is strongly recommended for most ShareConnect operations, especially when dealing with large files or multiple simultaneous connections. While mobile data works, it can be expensive and slower for intensive operations like media streaming or large downloads."

**VISUAL**: Network speed requirements chart

**NARRATOR**:
"For network speed, we recommend at least 10 Mbps download and 5 Mbps upload for basic usage. However, if you'll be streaming media through PlexConnect or downloading large files, 25 Mbps or higher will provide a much better experience. The faster your connection, the more responsive your apps will feel."

**VISUAL**: Router configuration examples

**NARRATOR**:
"Your router should support standard protocols without restrictive firewalls. Most modern routers work fine, but if you're in a corporate environment or using advanced security settings, you might need to configure port forwarding or adjust firewall rules for specific connectors."

**ON-SCREEN TEXT**: "Wi-Fi Recommended • Speed: 10+ Mbps basic, 25+ Mbps optimal • Standard router protocols"

---

### [5:00-6:30] Security Considerations

**VISUAL**: Security layers visualization showing multiple protection levels

**NARRATOR**:
"Security is fundamental to ShareConnect's design. Let's review the security requirements and best practices for protecting your data and connections."

**VISUAL**: HTTPS vs HTTP comparison

**NARRATOR**:
"Always use HTTPS connections when possible. ShareConnect enforces encrypted connections by default, but your self-hosted services must also support SSL/TLS. If a service only supports HTTP, consider setting up a reverse proxy with SSL termination for security."

**VISUAL**: Authentication methods showcase

**NARRATOR**:
"Strong authentication is essential. Use API tokens instead of passwords when available, enable two-factor authentication on your services, and consider using app passwords for automated connections. ShareConnect supports multiple authentication methods to match your security requirements."

**VISUAL**: PIN and biometric setup demonstration

**NARRATOR**:
"Take advantage of ShareConnect's built-in security features. Set up PIN protection or biometric authentication within the app, configure session timeouts, and use the security audit features to monitor access to your services."

**ON-SCREEN TEXT**: "HTTPS Required • Strong Authentication • PIN/Biometric Protection"

---

### [6:30-7:00] Pre-Installation Checklist

**VISUAL**: Interactive checklist that viewers can follow along

**NARRATOR**:
"Before we move to installation, let's run through a quick checklist. First, verify your Android version is 6.0 or higher. Second, ensure you have at least 2GB of RAM and 1GB of free storage. Third, confirm your network can handle the traffic you'll be generating."

**VISUAL**: Pre-installation setup steps

**NARRATOR**:
"Fourth, check that your self-hosted services are accessible and properly configured with SSL certificates. Fifth, gather your authentication credentials - API tokens, usernames, and passwords. Finally, make sure you have a backup plan in case something goes wrong during setup."

**ON-SCREEN TEXT**: "✓ Android Version • ✓ Hardware Specs • ✓ Network Ready • ✓ Services Configured • ✓ Credentials Ready • ✓ Backup Plan"

---

### [7:00-8:00] Common Issues and Prevention

**VISUAL**: Common problems prevention guide

**NARRATOR**:
"Let me share some common issues users encounter and how to prevent them. The most frequent problem is network connectivity - services that work on your local network might not be accessible from mobile. Test your service URLs from your phone's browser before installing ShareConnect."

**VISUAL**: SSL certificate troubleshooting

**NARRATOR**:
"SSL certificate issues are another common headache. Make sure your certificates are valid and not self-signed for production use. If you're using Let's Encrypt, verify automatic renewal is working properly. Invalid certificates will prevent ShareConnect from establishing secure connections."

**VISUAL**: Performance optimization tips

**NARRATOR**:
"Performance issues often stem from inadequate server resources. Monitor your self-hosted services during peak usage, ensure your servers have sufficient CPU and memory, and consider using CDNs or caching for better response times. A well-configured server means a smooth ShareConnect experience."

**ON-SCREEN TEXT**: "Test Network Access • Verify SSL Certificates • Monitor Server Resources"

---

### [7:30-8:00] Summary and Next Steps

**VISUAL**: Clean summary slide with key points

**NARRATOR**:
"You're now ready for installation! We've covered the Android version requirements, hardware recommendations, network configuration needs, and security considerations. Make sure you've completed the pre-installation checklist and tested your service accessibility."

**VISUAL**: Next lesson preview

**NARRATOR**:
"In our next lesson, we'll walk through the actual installation process step-by-step. I'll show you how to install ShareConnector, configure your first connector, and set up the synchronization system. We'll use real examples and demonstrate the complete setup process."

**ON-SCREEN TEXT**: "Ready for Installation? Next: Lesson 3 - ShareConnector Setup"

**NARRATOR**:
"Thanks for joining me for this system requirements overview. Taking time to prepare properly will ensure a smooth installation experience. See you in the next lesson where we start installing ShareConnect!"

---

## Visual Assets Needed

### System Requirements Graphics
- Android version compatibility matrix
- Hardware specification icons and charts
- Network topology diagrams
- Security layer visualization

### Technical Demonstrations
- Android version checking screenshots
- Network speed test demonstrations
- Router configuration examples
- SSL certificate validation steps

### User Interface Elements
- Checklist graphics and animations
- Warning and information icons
- Performance monitoring displays
- Security feature demonstrations

### Educational Graphics
- Requirement comparison charts
- Troubleshooting flow diagrams
- Best practices infographics
- Prevention tip callouts

## Recording Notes

### Audio Requirements
- Clear, professional narration suitable for technical content
- Slightly slower pace for complex technical information
- Consistent volume and quality throughout
- Technical terminology pronounced correctly

### Visual Requirements
- Clean, professional graphics suitable for technical documentation
- Clear screenshots and interface demonstrations
- Consistent color scheme matching ShareConnect branding
- Readable text overlays and annotations

### Technical Specifications
- Video codec: H.264
- Audio codec: AAC, 320kbps
- Frame rate: 30fps
- Aspect ratio: 16:9
- Resolution: 1080p minimum

## Assessment Questions

1. **What is the minimum Android version required for ShareConnect?**
   - A) Android 5.0
   - B) Android 6.0
   - C) Android 8.0
   - D) Android 10.0

2. **How much RAM is recommended for optimal ShareConnect performance?**
   - A) 1GB
   - B) 2GB
   - C) 4GB or more
   - D) 8GB

3. **Which network connection is strongly recommended for ShareConnect?**
   - A) Mobile data only
   - B) Wi-Fi
   - C) Ethernet only
   - D) Bluetooth tethering

4. **What security protocol should be used for ShareConnect connections?**
   - A) HTTP
   - B) FTP
   - C) HTTPS
   - D) Telnet

**Correct Answers**: 1-B, 2-C, 3-B, 4-C