# GiteaConnect User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Getting Started](#getting-started)
4. [Authentication](#authentication)
5. [Repository Management](#repository-management)
6. [Issues & Pull Requests](#issues--pull-requests)
7. [Releases](#releases)
8. [Settings](#settings)
9. [Troubleshooting](#troubleshooting)
10. [FAQ](#faq)

## Introduction

GiteaConnect is a native Android client for Gitea, the lightweight self-hosted Git service. Manage repositories, issues, pull requests, and releases from your Android device.

### Key Features
- **Repository Management**: Browse, create, fork, and star repositories
- **Issue Tracking**: Create, view, and manage issues
- **Pull Requests**: Review and merge pull requests
- **Release Management**: View and create releases
- **Code Browsing**: View commits and repository contents
- **Multi-Account**: Connect to multiple Gitea servers
- **ShareConnect Integration**: Share repositories and releases

## Installation

### From Google Play Store
1. Open Google Play Store
2. Search for "GiteaConnect"
3. Tap "Install"
4. Wait for installation to complete
5. Open the app

### From F-Droid
1. Open F-Droid app store
2. Search for "GiteaConnect"
3. Tap "Install"
4. Follow installation prompts

### From APK
1. Download the latest APK from [GitHub Releases](https://github.com/shareconnect/giteaconnect/releases)
2. Enable "Install from unknown sources" in Settings
3. Install the APK file
4. Grant necessary permissions

## Getting Started

### First Launch
1. Open GiteaConnect after installation
2. Complete the onboarding process:
   - Introduction to features
   - Server setup instructions
   - Permission requests
3. Add your Gitea server

### Required Permissions
- **Internet Access**: For connecting to Gitea server
- **Network State**: To check connectivity
- **Storage Access**: For repository cloning (future feature)
- **Notification Access**: For issue/PR notifications

## Authentication

### Creating API Token

Before using GiteaConnect, create an API token:

1. Log in to your Gitea instance web interface
2. Go to Settings → Applications → Manage Access Tokens
3. Enter token name: "GiteaConnect Android"
4. Select scopes (permissions):
   - `repo`: Repository access
   - `user`: User information
   - `issue`: Issue management
   - `notification`: Notifications
5. Click "Generate Token"
6. **Important**: Copy the token immediately (shown only once)

### Adding Gitea Server

1. Open GiteaConnect
2. Tap "Add Server"
3. Enter server details:
   - **Server URL**: `https://gitea.example.com`
   - **API Token**: Paste token from above
4. Tap "Connect"
5. Server is added and authentication verified

### Server Configuration

**Server Settings:**
- **Server Name**: Friendly display name
- **Server URL**: Full URL including https://
- **API Token**: Authentication token
- **Default Branch**: Usually "main" or "master"

**Connection:**
- App verifies token on connection
- Retrieves user information
- Lists accessible repositories

### Managing Multiple Servers

1. Go to Settings → Accounts
2. View all Gitea servers
3. Tap server to:
   - Switch to this account
   - Edit settings
   - Remove account
4. Long-press to set as default

## Repository Management

### Browsing Repositories

**Your Repositories:**
1. Home screen shows your repositories
2. Sort by:
   - Recently updated
   - Name (A-Z)
   - Stars
   - Creation date
3. Filter by:
   - Public/Private
   - Forked
   - Mirrored

**Repository Information:**
- Name and description
- Owner and visibility (public/private)
- Star, fork, and watcher counts
- Open issues and pull requests
- Last update time
- Default branch

### Viewing Repository Details

1. Tap any repository
2. View tabs:
   - **Code**: Browse files and folders
   - **Issues**: View and create issues
   - **Pull Requests**: Review PRs
   - **Commits**: View commit history
   - **Releases**: View releases and downloads
   - **Info**: Repository information

### Creating Repository

1. Tap "+" button
2. Select "New Repository"
3. Enter details:
   - **Name**: Repository name (required)
   - **Description**: Brief description
   - **Visibility**: Public or Private
   - **Initialize**: Add README, .gitignore, License
4. Tap "Create"
5. Repository is created

**Repository Options:**
- Name must be unique for your account
- Choose visibility carefully
- Initialize with README for easy start
- Can change visibility later

### Forking Repository

1. Open repository
2. Tap menu → "Fork"
3. Choose destination:
   - Your account
   - An organization (if member)
4. Wait for fork to complete
5. Forked repository appears in your list

### Starring Repositories

**Star a Repository:**
1. Open repository
2. Tap star icon ⭐
3. Repository added to starred

**View Starred:**
1. Go to menu → "Starred"
2. See all starred repositories
3. Un-star by tapping icon again

### Deleting Repository

1. Open repository
2. Tap menu → "Settings"
3. Scroll to "Danger Zone"
4. Tap "Delete Repository"
5. Enter repository name to confirm
6. Tap "Delete" (permanent!)

## Issues & Pull Requests

### Managing Issues

**Viewing Issues:**
1. Open repository → Issues tab
2. Filter by state:
   - Open
   - Closed
   - All
3. Sort by:
   - Newest
   - Oldest
   - Most commented

**Issue Details:**
- Title and description
- Author and assignees
- Labels and milestone
- Comments
- State (open/closed)
- Creation and update dates

**Creating Issue:**
1. Repository → Issues → "+"
2. Enter information:
   - **Title**: Brief, descriptive
   - **Description**: Detailed explanation
   - **Assignees**: Who should work on it
   - **Labels**: Categorize (bug, enhancement, etc.)
   - **Milestone**: Associate with milestone
3. Tap "Create"

**Issue Template:**
```markdown
## Description
Brief description of the issue

## Steps to Reproduce
1. Step one
2. Step two
3. Step three

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: Android 14
- Device: Pixel 7
- App Version: 1.0.0
```

**Closing Issues:**
1. Open issue
2. Tap "Close Issue"
3. Optionally add comment
4. Confirm

**Reopening Issues:**
1. Open closed issue
2. Tap "Reopen Issue"
3. Issue state changes to open

**Adding Comments:**
1. Open issue
2. Scroll to comment box
3. Write comment (Markdown supported)
4. Tap "Comment"

### Managing Pull Requests

**Viewing Pull Requests:**
1. Repository → Pull Requests tab
2. Filter by state: Open, Closed, Merged
3. View PR information:
   - Source and target branches
   - Changes summary
   - Merge status
   - Review state

**Creating Pull Request:**
1. Repository → Pull Requests → "+"
2. Select branches:
   - **Base**: Target branch (e.g., main)
   - **Compare**: Source branch (e.g., feature-x)
3. Enter details:
   - **Title**: Descriptive title
   - **Description**: Explain changes
4. Review file changes
5. Tap "Create Pull Request"

**Pull Request Template:**
```markdown
## Changes
Summary of changes made

## Related Issues
Closes #123

## Checklist
- [ ] Code follows style guidelines
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] No breaking changes

## Screenshots
(if applicable)
```

**Reviewing Pull Requests:**
1. Open PR
2. View tabs:
   - **Conversation**: Comments and activity
   - **Commits**: List of commits
   - **Files Changed**: Diff view
3. Add review comments
4. Approve or request changes

**Merging Pull Requests:**
1. Open PR (must have merge permissions)
2. Ensure:
   - No conflicts
   - Reviews approved
   - CI checks passed
3. Choose merge method:
   - **Merge Commit**: Creates merge commit
   - **Squash**: Combines into one commit
   - **Rebase**: Replays commits on base
4. Tap "Merge"
5. Confirm

**Merge Methods Explained:**
- **Merge Commit**: Preserves all commits + merge commit
- **Squash**: Cleaner history, one commit
- **Rebase**: Linear history, no merge commit

### Labels and Milestones

**Using Labels:**
- Organize issues and PRs
- Filter by label
- Common labels: bug, enhancement, documentation
- Custom labels with colors

**Milestones:**
- Group related issues/PRs
- Track progress toward goals
- Set due dates
- View completion percentage

## Releases

### Viewing Releases

1. Repository → Releases tab
2. See all releases listed:
   - Tag name and version
   - Release title and notes
   - Author and date
   - Download assets

**Release Information:**
- Tag name (version)
- Target branch/commit
- Release notes
- Pre-release or stable
- Attached files (APKs, ZIPs, etc.)
- Download counts

### Downloading Release Assets

1. Open release
2. Scroll to "Assets" section
3. Tap file to download:
   - APK files
   - ZIP/TAR archives
   - Documentation
4. File saves to Downloads folder

**Asset Types:**
- Source code (auto-generated ZIP/TAR)
- Custom uploads (APKs, binaries)
- Checksums and signatures

### Creating Release

1. Repository → Releases → "+"
2. Enter release information:
   - **Tag**: Version tag (e.g., v1.0.0)
   - **Title**: Release name
   - **Description**: Release notes
   - **Target**: Branch or commit
   - **Type**: Stable or Pre-release
3. Upload files (optional):
   - Tap "Add Files"
   - Select from storage
4. Preview release notes
5. Tap "Publish Release"

**Release Notes Template:**
```markdown
## What's New in v1.0.0

### Features
- Added dark mode support
- Implemented search functionality
- New dashboard layout

### Bug Fixes
- Fixed crash on startup
- Resolved memory leak
- Corrected date formatting

### Downloads
- [app-release.apk](link)
- [app-release.aab](link)

Full Changelog: v0.9.0...v1.0.0
```

### Deleting Releases

1. Open release
2. Tap menu → "Delete"
3. Confirm deletion
4. Release and tag removed

## Settings

### App Settings

**Appearance:**
- Theme: Light, Dark, Auto
- Language: Select app language
- List Density: Compact or comfortable
- Font Size: Adjust text size

**Notifications:**
- Issue Comments: Get notified
- Pull Request Reviews: Notify on reviews
- Mentions: When you're @mentioned
- Repository Stars: When repo is starred
- Sound: Enable notification sound

**Cache:**
- Repository Cache: Cache repo data
- Image Cache: Cache avatars
- Cache Size: Maximum cache storage
- Clear Cache: Remove cached data

### Account Settings

1. Go to Settings → Accounts
2. Select account
3. Configure:
   - Display name
   - Email (read-only)
   - Default repository visibility
   - Starred repositories sync

### Server Settings

**Connection:**
- Server URL
- API Token management
- Connection timeout
- Auto-reconnect on network change

**Preferences:**
- Default branch
- Preferred merge method
- Issue template
- PR template

## Troubleshooting

### Connection Issues

**Cannot Connect to Server:**
1. Verify server URL is correct
2. Check internet connection
3. Ensure Gitea server is online
4. Try accessing via web browser
5. Check firewall/network settings

**SSL Certificate Errors:**
1. Verify HTTPS is configured correctly
2. Check certificate validity
3. For self-signed certificates:
   - Import certificate on device
   - Or use HTTP (not recommended)

**Authentication Failed:**
1. Verify API token is correct
2. Check token hasn't been revoked
3. Ensure token has required scopes
4. Generate new token if needed
5. Re-add server with new token

### Repository Issues

**Repositories Not Showing:**
1. Pull down to refresh
2. Check repository permissions
3. Verify you have access
4. Try searching for repository name

**Cannot Create Repository:**
1. Check repository name is unique
2. Verify you have create permissions
3. Check disk quota on server
4. Try different name/settings

### Issue/PR Problems

**Cannot Create Issue:**
1. Verify repository allows issues
2. Check you have permissions
3. Ensure required fields filled
4. Try simpler description first

**Cannot Merge PR:**
1. Check merge conflicts
2. Verify merge permissions
3. Ensure CI checks pass
4. Try different merge method

### App Crashes

1. Update to latest version
2. Clear app cache
3. Clear app data (signs you out)
4. Reinstall app
5. Report crash with logs

## FAQ

### General Questions

**Q: What is Gitea?**
A: Gitea is a lightweight, self-hosted Git service similar to GitHub, written in Go.

**Q: Is GiteaConnect free?**
A: Yes, GiteaConnect is free and open-source.

**Q: Can I use this with GitHub or GitLab?**
A: No, GiteaConnect is specifically for Gitea. GitHub and GitLab have their own apps.

**Q: Do I need my own Gitea server?**
A: Yes, you need access to a Gitea server (self-hosted or hosted by provider).

### Authentication

**Q: Why do I need an API token?**
A: API tokens are more secure than passwords and can be scoped to specific permissions.

**Q: How do I create an API token?**
A: Log in to Gitea web interface → Settings → Applications → Generate Token.

**Q: Can I use multiple tokens?**
A: Yes, you can create multiple tokens for different devices/purposes.

**Q: What scopes should I select?**
A: For full functionality: repo, user, issue, notification.

### Repositories

**Q: Can I clone repositories?**
A: Direct cloning in-app is planned for future release. Use share to external Git client.

**Q: How do I delete a forked repository?**
A: Same as regular repo: Menu → Settings → Delete Repository.

**Q: Can I transfer repository ownership?**
A: Yes, in Gitea web interface. Not yet in GiteaConnect.

**Q: What's the difference between Watch, Star, and Fork?**
A: **Watch**: Get notifications. **Star**: Bookmark/like. **Fork**: Create your own copy.

### Issues & PRs

**Q: Can I assign issues to others?**
A: Yes, if you have permissions. Select from collaborators list.

**Q: How do I close multiple issues?**
A: Must close individually. Bulk operations planned for future.

**Q: Can I merge PRs from the app?**
A: Yes, if you have merge permissions and no conflicts exist.

**Q: What's the difference between merge methods?**
A: See [Merging Pull Requests](#merging-pull-requests) section above.

### Releases

**Q: Who can create releases?**
A: Users with push access to the repository.

**Q: Can I edit a release after publishing?**
A: Yes, open release → Menu → Edit.

**Q: How do I make a pre-release?**
A: When creating release, check "This is a pre-release" option.

**Q: Can I upload any file type?**
A: Yes, but check Gitea server's file size limits.

### Technical

**Q: What Android version is required?**
A: Android 9.0 (API 28) or higher.

**Q: What Gitea version is supported?**
A: Gitea 1.18.0 and later recommended.

**Q: Does this work on tablets?**
A: Yes, optimized for both phones and tablets.

**Q: Can I use this offline?**
A: Limited. Cached data viewable, but operations require connection.

### Getting Help

**Q: Where can I report bugs?**
A: GitHub: github.com/shareconnect/giteaconnect/issues

**Q: Is there a user community?**
A: Yes, ShareConnect Discord and Gitea community forum.

**Q: How do I request features?**
A: Submit feature requests on GitHub or vote on existing ones.

## Additional Resources

### Official Links
- **Website**: [shareconnect.org/giteaconnect](https://shareconnect.org/giteaconnect)
- **GitHub**: [github.com/shareconnect/giteaconnect](https://github.com/shareconnect/giteaconnect)
- **Documentation**: [docs.shareconnect.org/giteaconnect](https://docs.shareconnect.org/giteaconnect)

### Gitea Resources
- **Gitea**: [gitea.io](https://gitea.io)
- **Gitea Docs**: [docs.gitea.io](https://docs.gitea.io)
- **Gitea Discourse**: [discourse.gitea.io](https://discourse.gitea.io)
- **Gitea GitHub**: [github.com/go-gitea/gitea](https://github.com/go-gitea/gitea)

### Community
- **Discord**: Join the ShareConnect Discord server
- **Reddit**: r/ShareConnect, r/Gitea
- **Twitter**: @ShareConnectApp

---

**Version**: 1.0.0
**Last Updated**: 2025-10-25
**License**: Open Source (GPL-3.0)

For the latest updates and detailed documentation, visit [shareconnect.org/docs](https://shareconnect.org/docs)
