package com.shareconnect.jdownloaderconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "server_profiles")
@Serializable
data class ServerProfile(
    @PrimaryKey
    val id: String,
    val name: String,
    val serverUrl: String,
    val username: String? = null,
    val password: String? = null,
    val isDefault: Boolean = false,
    val serviceType: ServiceType = ServiceType.JDOWNLOADER,
    val connectionTimeout: Int = 30,
    val readTimeout: Int = 60,
    val enabled: Boolean = true,
    val lastConnected: Long? = null
)

@Serializable
enum class ServiceType {
    // Current Connectors
    JDOWNLOADER, QBITTORRENT, TRANSMISSION, UTORRENT,

    // Download Managers
    MOTRIX, FILECENTIPEDE, XDM, PERSEPOLIS,

    // Cloud Storage & File Sharing
    NEXTCLOUD, SYNCTHING, SEAFILE, MINIO,

    // Media Servers & Management
    PLEX, JELLYFIN, EMBY, IMMICH, PHOTOPRISM,

    // WebDAV & Cloud Storage
    OWNCLOUD, WEBDAV, DROPBOX_API, GOOGLE_DRIVE_API,

    // FTP/SFTP Servers
    VSFTPD, PROFTPD, PURE_FTPD, FILEZILLA_SERVER,

    // Archive & Compression
    SEVENZIP, WINRAR, RARLAB, PEAZIP,

    // Backup Solutions
    DUPLICATI, BORG_BACKUP, RESTIC, KOPIA,

    // Media Processing
    HANDBRAKE, FFMPEG, MAKEMKV, MKVTOOLNIX,

    // YouTube & Video Downloaders
    YOUTUBE_DL, YT_DLP, TUBEARCHIVIST, NEWPIPE,

    // File Browsers & Managers
    FILEBROWSER, KRUSADER, DOUBLE_COMMANDER, FAR_MANAGER,

    // NAS Systems
    OPENMEDIAVAULT, TRUE_NAS, UNRAID, ROCKSTOR,

    // Git & Version Control
    GOGS, GITEA, GITLAB_CE, FORGEJO,

    // Note-taking & Knowledge Management
    TRILLIUM, SIYUAN, JOPLIN, LOGSEQ,

    // Monitoring & System Management
    NETDATA, ZABBIX, NAGIOS, PROMETHEUS,

    // Container & Orchestration
    PORTAINER, DOCKER_COMPOSE, KUBERNETES_DASHBOARD,

    // Database Management
    ADMINER, PHPMYADMIN, PGADMIN, MONGODB_COMPASS,

    // Web Servers & Proxies
    NGINX_PROXY_MANAGER, TRAEFIK, HA_PROXY, APACHE,

    // Communication & Collaboration
    MATRIX, MATTERMOST, ROCKET_CHAT, ZULIP,

    // Password Managers
    VAULTWARDEN, BITWARDEN_RS, KEEPASSXC,

    // RSS & News Aggregation
    MINIFLUX, FRESHRSS, TTRSS, NEWSBOAT,

    // Email Servers
    MAILCOW, MAILU, POSTE_IO, MAILPILE,

    // Calendar & Contacts
    BAICAL, RADICALE, DAVICAL,

    // Home Automation
    HOME_ASSISTANT, OPENHAB, DOMOTICZ,

    // VPN & Security
    WIREGUARD, OPENVPN, IPSEC, TAILSCALE,

    // DNS & Network Services
    PIHOLE, ADGUARD_HOME, UNBOUND,

    // Game Servers
    MINECRAFT_SERVER, VALHEIM_SERVER, SAMP_SERVER,

    // Development Tools
    GITEA_ACTIONS, DRONE_CI, WOODPECKER_CI,

    // Analytics & Dashboards
    GRAFANA, KIBANA, METABASE,

    // Document Management
    PAPERLESS_NG, DOCUSEAL, ONLYOFFICE,

    // Music & Audio
    NAVIDROME, AIRSONIC, FUNKWHALE,

    // Video Streaming
    JELLYFIN_VUE, PLEX_WEB, EMBY_WEB,

    // Social & Communication
    MASTODON, PEERTUBE, PIXELFED,

    // Finance & Budgeting
    FIREFLY_III, ACTUAL_BUDGET,

    // Task Management
    VIKTOR, TASKWARRIOR_GUI,

    // Custom/Generic
    GENERIC_WEBDAV, GENERIC_FTP, GENERIC_HTTP_API
}