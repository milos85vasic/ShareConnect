# uTorrentConnect

uTorrentConnect is an Android application which allows you to remotely control [uTorrent BitTorrent client](https://www.utorrent.com).
It works through the uTorrent Web UI (so remote access must be enabled in uTorrent preferences).
Access is obtained from Android and supported the integrate with [ShareConnect](https://github.com/vasic-digital/ShareConnect) application.

This connector is based on the TransmissionConnect codebase and has been adapted to work with uTorrent's Web API.

## Features

- Remote torrent management (add, remove, start, stop, pause)
- View torrent details, files, peers, and trackers
- Multiple server support
- Torrent filtering and sorting
- Background notifications for completed torrents
- Support for .torrent files and magnet links
- Material Design UI with dark mode support

## Requirements

- Android 8.0+ (API 26+)
- uTorrent with WebUI enabled
- Network access to uTorrent server

## Configuration

1. Enable WebUI in uTorrent:
   - Go to Options > Preferences > Advanced > Web UI
   - Check "Enable Web UI"
   - Set username and password
   - Note the port number (default: 8080)

2. Configure uTorrentConnect:
   - Open the app
   - Add a new server
   - Enter server details (host, port, username, password)
   - Save and connect

## Integration with ShareConnect

uTorrentConnect integrates with ShareConnect to provide seamless torrent management across different BitTorrent clients. It supports profile-based configuration and synchronization with uTorrent instances.

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
