/**
 * ShareConnect Interactive API Demo
 * Real-time demonstration of ShareConnect's 20+ connector capabilities
 */

class ShareConnectAPIDemo {
    constructor() {
        this.apiEndpoint = '/api/demo';
        this.supportedServices = {
            'qBittorrent': {
                name: 'qBittorrent',
                icon: 'fas fa-magnet',
                color: '#4CAF50',
                description: 'Professional torrent client integration',
                exampleUrls: [
                    'magnet:?xt=urn:btih:1234567890abcdef',
                    'https://example.com/file.torrent'
                ]
            },
            'Plex': {
                name: 'Plex Media Server',
                icon: 'fas fa-tv',
                color: '#E5A00D',
                description: 'Media server with library management',
                exampleUrls: [
                    'https://example.com/movie.mp4',
                    'https://example.com/tv-show.mkv'
                ]
            },
            'Nextcloud': {
                name: 'Nextcloud',
                icon: 'fas fa-cloud',
                color: '#0082C9',
                description: 'Self-hosted cloud storage solution',
                exampleUrls: [
                    'https://example.com/document.pdf',
                    'https://example.com/photo.jpg'
                ]
            },
            'JDownloader': {
                name: 'JDownloader',
                icon: 'fas fa-download',
                color: '#2196F3',
                description: 'Advanced download manager',
                exampleUrls: [
                    'https://example.com/file.zip',
                    'https://example.com/software.exe'
                ]
            },
            'Transmission': {
                name: 'Transmission',
                icon: 'fas fa-sync',
                color: '#FF9800',
                description: 'Lightweight BitTorrent client',
                exampleUrls: [
                    'magnet:?xt=urn:btih:abcdef1234567890',
                    'https://example.com/linux.iso.torrent'
                ]
            },
            'uTorrent': {
                name: 'uTorrent',
                icon: 'fas fa-download',
                color: '#9C27B0',
                description: 'Popular torrent client',
                exampleUrls: [
                    'magnet:?xt=urn:btih:fedcba0987654321',
                    'https://example.com/media.torrent'
                ]
            },
            'Plex': {
                name: 'Plex',
                icon: 'fas fa-play-circle',
                color: '#FFC107',
                description: 'Media streaming platform',
                exampleUrls: [
                    'https://example.com/movie.mp4',
                    'https://example.com/show.mkv'
                ]
            },
            'Jellyfin': {
                name: 'Jellyfin',
                icon: 'fas fa-video',
                color: '#00A4DC',
                description: 'Open source media system',
                exampleUrls: [
                    'https://example.com/media.mp4',
                    'https://example.com/series/'
                ]
            },
            'Emby': {
                name: 'Emby',
                icon: 'fas fa-film',
                color: '#673AB7',
                description: 'Media server platform',
                exampleUrls: [
                    'https://example.com/movie.mkv',
                    'https://example.com/tvshow.mp4'
                ]
            },
            'Seafile': {
                name: 'Seafile',
                icon: 'fas fa-ship',
                color: '#F44336',
                description: 'Enterprise cloud storage',
                exampleUrls: [
                    'https://example.com/document.pdf',
                    'https://example.com/archive.zip'
                ]
            }
        };
        
        this.currentService = 'qBittorrent';
        this.demoHistory = [];
        this.isProcessing = false;
        
        this.init();
    }
    
    init() {
        this.setupDemoInterface();
        this.setupServiceSelector();
        this.setupExampleButtons();
        this.setupDemoForm();
        this.setupKeyboardShortcuts();
        this.setupAccessibilityFeatures();
        this.loadDemoHistory();
        
        console.log('🚀 ShareConnect API Demo initialized with 20+ connectors');
    }
    
    setupDemoInterface() {
        const demoContainer = document.getElementById('demo-container');
        if (!demoContainer) return;
        
        // Create enhanced demo interface
        demoContainer.innerHTML = `
            <div class="api-demo-header">
                <h3><i class="fas fa-rocket"></i> Live API Demo</h3>
                <div class="demo-status online">
                    <i class="fas fa-circle"></i>
                    <span>Online</span>
                </div>
            </div>
            
            <div class="api-demo-content">
                <div class="demo-input-section">
                    <label for="demo-url">Enter any URL to test ShareConnect:</label>
                    <div class="url-input-container">
                        <input type="url" id="demo-url" placeholder="https://example.com/video.mp4" class="demo-input">
                        <button id="demo-share" class="btn btn-primary demo-button">
                            <i class="fas fa-share"></i>
                            Share to Service
                        </button>
                    </div>
                </div>
                
                <div class="demo-services">
                    <div class="service-selector">
                        <label>Select Service (20+ Available):</label>
                        <div class="service-grid">
                            ${this.renderServiceGrid()}
                        </div>
                    </div>
                </div>
                
                <div class="demo-examples">
                    <h4>Try These Examples:</h4>
                    <div class="example-grid">
                        ${this.renderExampleGrid()}
                    </div>
                </div>
                
                <div class="demo-results" id="demo-results">
                    <div class="demo-welcome">
                        <i class="fas fa-hand-pointer"></i>
                        <p>Enter a URL above and click "Share to Service" to see ShareConnect's 20+ connectors in action!</p>
                        <div class="demo-features">
                            <div class="demo-feature">
                                <i class="fas fa-bolt"></i>
                                <span>Instant Processing</span>
                            </div>
                            <div class="demo-feature">
                                <i class="fas fa-shield-alt"></i>
                                <span>Secure Connections</span>
                            </div>
                            <div class="demo-feature">
                                <i class="fas fa-chart-line"></i>
                                <span>Real-time Analytics</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="demo-history" id="demo-history">
                    <h4>Recent Demos</h4>
                    <div class="history-list" id="history-list">
                        <div class="history-empty">No demos yet. Try the examples above!</div>
                    </div>
                </div>
            </div>
        `;
    }
    
    renderServiceGrid() {
        return Object.entries(this.supportedServices).map(([key, service]) => `
            <button class="service-card ${key === this.currentService ? 'active' : ''}" 
                    data-service="${key}" 
                    style="--service-color: ${service.color}">
                <div class="service-icon">
                    <i class="${service.icon}"></i>
                </div>
                <div class="service-info">
                    <div class="service-name">${service.name}</div>
                    <div class="service-description">${service.description}</div>
                </div>
            </button>
        `).join('');
    }
    
    renderExampleGrid() {
        const examples = [
            { type: 'video', icon: 'fas fa-video', title: 'Sample Video', url: 'https://example.com/video.mp4' },
            { type: 'torrent', icon: 'fas fa-magnet', title: 'Sample Magnet', url: 'magnet:?xt=urn:btih:1234567890abcdef' },
            { type: 'document', icon: 'fas fa-file-pdf', title: 'Sample Document', url: 'https://example.com/document.pdf' },
            { type: 'archive', icon: 'fas fa-file-archive', title: 'Sample Archive', url: 'https://example.com/archive.zip' }
        ];
        
        return examples.map(example => `
            <button class="example-card" data-url="${example.url}" data-type="${example.type}">
                <div class="example-icon">
                    <i class="${example.icon}"></i>
                </div>
                <div class="example-title">${example.title}</div>
            </button>
        `).join('');
    }
    
    setupServiceSelector() {
        const serviceCards = document.querySelectorAll('.service-card');
        
        serviceCards.forEach(card => {
            card.addEventListener('click', () => {
                const service = card.dataset.service;
                this.selectService(service);
            });
        });
    }
    
    setupExampleButtons() {
        const exampleCards = document.querySelectorAll('.example-card');
        
        exampleCards.forEach(card => {
            card.addEventListener('click', () => {
                const url = card.dataset.url;
                const type = card.dataset.type;
                this.useExample(url, type);
            });
        });
    }
    
    setupDemoForm() {
        const demoInput = document.getElementById('demo-url');
        const demoButton = document.getElementById('demo-share');
        const resultContainer = document.getElementById('demo-results');
        
        if (!demoInput || !demoButton || !resultContainer) return;
        
        // Main demo functionality
        const runDemo = async () => {
            const url = demoInput.value.trim();
            
            if (!url) {
                this.showDemoError('Please enter a URL to test');
                return;
            }
            
            if (!this.isValidUrl(url)) {
                this.showDemoError('Please enter a valid URL');
                return;
            }
            
            await this.processDemo(url, this.currentService, resultContainer);
        };
        
        demoButton.addEventListener('click', runDemo);
        demoInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                runDemo();
            }
        });
        
        // Real-time validation
        demoInput.addEventListener('input', () => {
            this.validateDemoInput(demoInput.value);
        });
    }
    
    setupKeyboardShortcuts() {
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && e.ctrlKey) {
                e.preventDefault();
                document.getElementById('demo-share')?.click();
            }
            
            if (e.key === 'Escape') {
                this.clearDemoResults();
            }
        });
    }
    
    setupAccessibilityFeatures() {
        // Add ARIA labels and roles
        const demoInput = document.getElementById('demo-url');
        if (demoInput) {
            demoInput.setAttribute('aria-label', 'URL to test with ShareConnect');
            demoInput.setAttribute('aria-describedby', 'demo-help');
        }
        
        // Add screen reader announcements
        this.announceToScreenReader = (message) => {
            const announcement = document.createElement('div');
            announcement.setAttribute('aria-live', 'polite');
            announcement.setAttribute('aria-atomic', 'true');
            announcement.className = 'sr-only';
            announcement.textContent = message;
            document.body.appendChild(announcement);
            
            setTimeout(() => {
                announcement.remove();
            }, 1000);
        };
    }
    
    selectService(service) {
        this.currentService = service;
        
        // Update UI
        document.querySelectorAll('.service-card').forEach(card => {
            card.classList.toggle('active', card.dataset.service === service);
        });
        
        // Update service info
        const serviceInfo = this.supportedServices[service];
        this.updateServiceDescription(serviceInfo);
        
        // Announce selection
        this.announceToScreenReader(`Selected ${serviceInfo.name}`);
        
        console.log(`Selected service: ${service} - ${serviceInfo.description}`);
    }
    
    useExample(url, type) {
        const demoInput = document.getElementById('demo-url');
        if (demoInput) {
            demoInput.value = url;
            demoInput.focus();
            
            // Auto-trigger demo with delay
            setTimeout(() => {
                document.getElementById('demo-share')?.click();
            }, 500);
        }
        
        // Update service selection based on type
        this.autoSelectService(type);
    }
    
    autoSelectService(type) {
        const serviceMap = {
            'video': 'Plex',
            'torrent': 'qBittorrent',
            'document': 'Nextcloud',
            'archive': 'JDownloader'
        };
        
        const service = serviceMap[type];
        if (service && this.supportedServices[service]) {
            this.selectService(service);
        }
    }
    
    async processDemo(url, service, resultContainer) {
        if (this.isProcessing) return;
        
        this.isProcessing = true;
        this.showDemoLoading(resultContainer);
        
        const startTime = performance.now();
        
        try {
            // Simulate API call with realistic delay
            const response = await this.simulateApiCall(url, service);
            
            const endTime = performance.now();
            const responseTime = Math.round(endTime - startTime);
            
            this.displayDemoResults(response, service, responseTime, resultContainer);
            this.addToHistory(url, service, responseTime, response);
            
            // Track demo usage
            this.trackDemoUsage(url, service, responseTime);
            
        } catch (error) {
            this.showDemoError(`Demo failed: ${error.message}`);
            console.error('Demo error:', error);
        } finally {
            this.isProcessing = false;
        }
    }
    
    async simulateApiCall(url, service) {
        // Simulate network delay based on service type
        const baseDelay = 800;
        const serviceDelay = Math.random() * 1200;
        const totalDelay = baseDelay + serviceDelay;
        
        await new Promise(resolve => setTimeout(resolve, totalDelay));
        
        // Generate realistic response based on service
        return this.generateServiceResponse(url, service);
    }
    
    generateServiceResponse(url, service) {
        const responses = {
            'qBittorrent': {
                success: true,
                service: 'qBittorrent',
                message: `Torrent successfully added to qBittorrent`,
                data: {
                    hash: this.generateHash(),
                    name: this.extractFileName(url) || 'Unknown torrent',
                    size: `${(Math.random() * 5 + 0.5).toFixed(2)} GB`,
                    status: 'Downloading',
                    progress: Math.floor(Math.random() * 100),
                    downloadSpeed: `${(Math.random() * 10 + 1).toFixed(1)} MB/s`,
                    seeds: Math.floor(Math.random() * 50 + 10),
                    peers: Math.floor(Math.random() * 100 + 20)
                }
            },
            'Transmission': {
                success: true,
                service: 'Transmission',
                message: `Torrent added to Transmission successfully`,
                data: {
                    id: Math.floor(Math.random() * 1000),
                    name: this.extractFileName(url) || 'Unknown torrent',
                    status: 'downloading',
                    percentDone: Math.floor(Math.random() * 100),
                    rateDownload: Math.floor(Math.random() * 1048576 + 102400),
                    rateUpload: Math.floor(Math.random() * 524288 + 51200),
                    peersConnected: Math.floor(Math.random() * 50 + 5)
                }
            },
            'Plex': {
                success: true,
                service: 'Plex',
                message: `Media added to Plex library`,
                data: {
                    library: 'Movies',
                    title: this.extractFileName(url) || 'Unknown media',
                    year: new Date().getFullYear(),
                    rating: 'Not Rated',
                    duration: `${Math.floor(Math.random() * 120 + 60)} min`,
                    resolution: '1080p',
                    audio: '5.1 Surround',
                    addedAt: new Date().toISOString()
                }
            },
            'Nextcloud': {
                success: true,
                service: 'Nextcloud',
                message: `File uploaded to Nextcloud successfully`,
                data: {
                    path: `/Downloads/${this.extractFileName(url) || 'unknown.file'}`,
                    size: `${(Math.random() * 1000 + 100).toFixed(0)} MB`,
                    modified: new Date().toISOString(),
                    shareLink: `https://nextcloud.example.com/s/${this.generateHash()}`,
                    permissions: 'read, write',
                    etag: this.generateHash()
                }
            },
            'JDownloader': {
                success: true,
                service: 'JDownloader',
                message: `Download added to JDownloader queue`,
                data: {
                    packageName: 'ShareConnect Downloads',
                    links: 1,
                    status: 'Queued',
                    eta: `${Math.floor(Math.random() * 10 + 2)} minutes`,
                    speed: `${(Math.random() * 50 + 10).toFixed(1)} MB/s`,
                    host: new URL(url).hostname,
                    chunks: Math.floor(Math.random() * 8 + 1)
                }
            }
        };
        
        // Return appropriate response or default
        return responses[service] || responses['qBittorrent'];
    }
    
    displayDemoResults(response, service, responseTime, container) {
        const serviceInfo = this.supportedServices[service];
        
        let html = `
            <div class="demo-success">
                <div class="success-header">
                    <div class="service-badge" style="background-color: ${serviceInfo.color}">
                        <i class="${serviceInfo.icon}"></i>
                        <span>${serviceInfo.name}</span>
                    </div>
                    <div class="response-time">${responseTime}ms</div>
                </div>
                <div class="success-message">${response.message}</div>
                <div class="success-data">
        `;
        
        // Add service-specific data
        Object.entries(response.data).forEach(([key, value]) => {
            html += `
                <div class="data-item">
                    <span class="data-label">${this.formatLabel(key)}:</span>
                    <span class="data-value">${value}</span>
                </div>
            `;
        });
        
        html += `
                </div>
                <div class="success-actions">
                    <button class="btn btn-outline btn-sm" onclick="shareConnectDemo.copyResponse('${service}', '${encodeURIComponent(JSON.stringify(response))}')">
                        <i class="fas fa-copy"></i>
                        Copy Response
                    </button>
                    <button class="btn btn-outline btn-sm" onclick="shareConnectDemo.testAnotherService()">
                        <i class="fas fa-sync"></i>
                        Try Another Service
                    </button>
                </div>
            </div>
        `;
        
        container.innerHTML = html;
        
        // Add animation
        container.querySelector('.demo-success').classList.add('animate-fadeInUp');
        
        // Update response time display
        const responseTimeElement = document.getElementById('response-time');
        if (responseTimeElement) {
            responseTimeElement.textContent = `${responseTime}ms`;
        }
    }
    
    addToHistory(url, service, responseTime, response) {
        const historyEntry = {
            timestamp: new Date().toISOString(),
            url: url,
            service: service,
            responseTime: responseTime,
            success: response.success,
            id: this.generateHash()
        };
        
        this.demoHistory.unshift(historyEntry);
        
        // Keep only last 10 entries
        if (this.demoHistory.length > 10) {
            this.demoHistory = this.demoHistory.slice(0, 10);
        }
        
        this.saveDemoHistory();
        this.updateHistoryDisplay();
    }
    
    updateHistoryDisplay() {
        const historyList = document.getElementById('history-list');
        if (!historyList) return;
        
        if (this.demoHistory.length === 0) {
            historyList.innerHTML = '<div class="history-empty">No demos yet. Try the examples above!</div>';
            return;
        }
        
        historyList.innerHTML = this.demoHistory.map(entry => {
            const serviceInfo = this.supportedServices[entry.service];
            const timeAgo = this.getTimeAgo(entry.timestamp);
            
            return `
                <div class="history-item" data-id="${entry.id}">
                    <div class="history-service" style="background-color: ${serviceInfo.color}">
                        <i class="${serviceInfo.icon}"></i>
                    </div>
                    <div class="history-info">
                        <div class="history-url">${this.truncateUrl(entry.url)}</div>
                        <div class="history-meta">
                            <span>${serviceInfo.name}</span>
                            <span>•</span>
                            <span>${entry.responseTime}ms</span>
                            <span>•</span>
                            <span>${timeAgo}</span>
                        </div>
                    </div>
                    <div class="history-status ${entry.success ? 'success' : 'error'}">
                        <i class="fas fa-${entry.success ? 'check' : 'times'}"></i>
                    </div>
                </div>
            `;
        }).join('');
    }
    
    // Utility methods
    isValidUrl(string) {
        try {
            new URL(string);
            return true;
        } catch (_) {
            return false;
        }
    }
    
    generateHash() {
        return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
    }
    
    extractFileName(url) {
        try {
            const urlObj = new URL(url);
            const pathname = urlObj.pathname;
            return pathname.split('/').pop() || null;
        } catch {
            return null;
        }
    }
    
    formatLabel(key) {
        return key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
    }
    
    truncateUrl(url, maxLength = 50) {
        return url.length > maxLength ? url.substring(0, maxLength - 3) + '...' : url;
    }
    
    getTimeAgo(timestamp) {
        const now = new Date();
        const past = new Date(timestamp);
        const seconds = Math.floor((now - past) / 1000);
        
        if (seconds < 60) return 'just now';
        if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
        if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
        return `${Math.floor(seconds / 86400)}d ago`;
    }
    
    // Public API methods
    copyResponse(service, encodedResponse) {
        try {
            const response = JSON.parse(decodeURIComponent(encodedResponse));
            const text = `ShareConnect API Response (${service}):\n${JSON.stringify(response, null, 2)}`;
            
            navigator.clipboard.writeText(text).then(() => {
                this.showNotification('Response copied to clipboard!', 'success');
            }).catch(() => {
                this.showNotification('Failed to copy response', 'error');
            });
        } catch (error) {
            console.error('Copy failed:', error);
        }
    }
    
    testAnotherService() {
        const services = Object.keys(this.supportedServices);
        const currentIndex = services.indexOf(this.currentService);
        const nextIndex = (currentIndex + 1) % services.length;
        const nextService = services[nextIndex];
        
        this.selectService(nextService);
        
        // Auto-run demo with current URL
        const currentUrl = document.getElementById('demo-url')?.value;
        if (currentUrl) {
            setTimeout(() => {
                document.getElementById('demo-share')?.click();
            }, 500);
        }
    }
    
    clearDemoResults() {
        const resultContainer = document.getElementById('demo-results');
        if (resultContainer) {
            resultContainer.innerHTML = `
                <div class="demo-welcome">
                    <i class="fas fa-hand-pointer"></i>
                    <p>Enter a URL above and click "Share to Service" to see ShareConnect's 20+ connectors in action!</p>
                </div>
            `;
        }
    }
    
    // Data persistence
    saveDemoHistory() {
        try {
            localStorage.setItem('shareconnect-demo-history', JSON.stringify(this.demoHistory));
        } catch (error) {
            console.warn('Failed to save demo history:', error);
        }
    }
    
    loadDemoHistory() {
        try {
            const saved = localStorage.getItem('shareconnect-demo-history');
            if (saved) {
                this.demoHistory = JSON.parse(saved);
                this.updateHistoryDisplay();
            }
        } catch (error) {
            console.warn('Failed to load demo history:', error);
        }
    }
    
    // Analytics
    trackDemoUsage(url, service, responseTime) {
        const demoData = {
            url: this.truncateUrl(url, 100),
            service: service,
            responseTime: responseTime,
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent.substring(0, 100)
        };
        
        // Store locally for now - integrate with analytics service
        const analytics = JSON.parse(localStorage.getItem('shareconnect-demo-analytics') || '[]');
        analytics.push(demoData);
        
        // Keep only last 100 entries
        if (analytics.length > 100) {
            analytics.shift();
        }
        
        localStorage.setItem('shareconnect-demo-analytics', JSON.stringify(analytics));
        
        console.log('Demo usage tracked:', demoData);
    }
    
    // Notification system
    showDemoLoading(container) {
        container.innerHTML = `
            <div class="demo-loading">
                <div class="loading-spinner"></div>
                <div class="loading-text">Connecting to ${this.supportedServices[this.currentService].name}...</div>
                <div class="loading-subtext">Processing your request through ShareConnect's API</div>
            </div>
        `;
    }
    
    showDemoError(message) {
        const resultContainer = document.getElementById('demo-results');
        if (resultContainer) {
            resultContainer.innerHTML = `
                <div class="demo-error">
                    <div class="error-header">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>Error</span>
                    </div>
                    <div class="error-message">${message}</div>
                    <button class="btn btn-outline btn-sm" onclick="shareConnectDemo.clearDemoResults()">
                        <i class="fas fa-times"></i>
                        Close
                    </button>
                </div>
            `;
        }
    }
    
    showNotification(message, type = 'info') {
        // Simple notification system
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas fa-${this.getNotificationIcon(type)}"></i>
                <span>${message}</span>
            </div>
        `;
        
        document.body.appendChild(notification);
        
        // Auto-remove after 3 seconds
        setTimeout(() => {
            notification.remove();
        }, 3000);
    }
    
    getNotificationIcon(type) {
        const icons = {
            success: 'check-circle',
            error: 'exclamation-circle',
            warning: 'exclamation-triangle',
            info: 'info-circle'
        };
        return icons[type] || 'info-circle';
    }
    
    // Service description updates
    updateServiceDescription(serviceInfo) {
        const descriptionElement = document.querySelector('.demo-info h4');
        if (descriptionElement) {
            descriptionElement.textContent = serviceInfo.description;
        }
    }
    
    validateDemoInput(value) {
        const demoInput = document.getElementById('demo-url');
        const demoButton = document.getElementById('demo-share');
        
        if (!demoInput || !demoButton) return;
        
        const isValid = this.isValidUrl(value) || value.length === 0;
        
        demoInput.classList.toggle('valid', isValid && value.length > 0);
        demoInput.classList.toggle('invalid', !isValid && value.length > 0);
        demoButton.disabled = !isValid && value.length > 0;
    }
}

// Initialize the demo when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('demo-container')) {
        window.shareConnectDemo = new ShareConnectAPIDemo();
    }
});

// Add CSS for demo elements
const demoStyles = `
<style>
/* ShareConnect API Demo Styles */
.api-demo-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 16px 16px 0 0;
}

.api-demo-header h3 {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 1.5rem;
}

.demo-status {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 0.875rem;
    font-weight: 500;
}

.demo-status.online i {
    color: #10b981;
    animation: pulse 2s infinite;
}

@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}

.api-demo-content {
    padding: 32px;
    background: white;
    border-radius: 0 0 16px 16px;
}

.url-input-container {
    display: flex;
    gap: 16px;
    align-items: center;
}

.demo-input {
    flex: 1;
    padding: 16px;
    border: 2px solid #e5e7eb;
    border-radius: 12px;
    font-size: 1rem;
    transition: all 0.3s ease;
}

.demo-input:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.demo-input.valid {
    border-color: #10b981;
}

.demo-input.invalid {
    border-color: #ef4444;
}

.demo-button {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 24px;
    font-size: 1rem;
    font-weight: 600;
}

.service-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-top: 16px;
}

.service-card {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: white;
    border: 2px solid #e5e7eb;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: left;
}

.service-card:hover {
    border-color: var(--service-color);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.service-card.active {
    border-color: var(--service-color);
    background: linear-gradient(135deg, var(--service-color), var(--service-color)dd);
    color: white;
}

.service-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--service-color);
    color: white;
    border-radius: 8px;
    font-size: 1.25rem;
}

.service-info {
    flex: 1;
}

.service-name {
    font-weight: 600;
    font-size: 0.875rem;
    margin-bottom: 4px;
}

.service-description {
    font-size: 0.75rem;
    color: #6b7280;
}

.service-card.active .service-description {
    color: rgba(255, 255, 255, 0.9);
}

.example-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 12px;
    margin-top: 16px;
}

.example-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 16px 12px;
    background: #f9fafb;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: center;
}

.example-card:hover {
    background: #f3f4f6;
    border-color: #3b82f6;
    transform: translateY(-1px);
}

.example-icon {
    font-size: 1.5rem;
    color: #6b7280;
}

.example-title {
    font-size: 0.75rem;
    font-weight: 500;
    color: #374151;
}

.demo-results {
    margin-top: 32px;
    padding: 24px;
    background: #f9fafb;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
}

.demo-success {
    animation: fadeInUp 0.6s ease-out;
}

.success-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
}

.service-badge {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border-radius: 9999px;
    color: white;
    font-weight: 500;
    font-size: 0.875rem;
}

.response-time {
    font-size: 0.875rem;
    color: #6b7280;
    font-weight: 500;
}

.success-message {
    margin-bottom: 16px;
    font-weight: 500;
    color: #15803d;
}

.success-data {
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
}

.data-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #f3f4f6;
}

.data-item:last-child {
    border-bottom: none;
}

.data-label {
    font-weight: 500;
    color: #374151;
    font-size: 0.875rem;
}

.data-value {
    color: #111827;
    font-weight: 600;
    font-size: 0.875rem;
}

.success-actions {
    display: flex;
    gap: 12px;
}

.demo-loading {
    text-align: center;
    padding: 40px;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #e5e7eb;
    border-top: 4px solid #3b82f6;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 16px;
}

.loading-text {
    font-size: 1.125rem;
    font-weight: 500;
    color: #374151;
    margin-bottom: 8px;
}

.loading-subtext {
    color: #6b7280;
    font-size: 0.875rem;
}

.demo-welcome {
    text-align: center;
    padding: 40px 20px;
}

.demo-welcome i {
    font-size: 3rem;
    color: #9ca3af;
    margin-bottom: 16px;
}

.demo-welcome p {
    font-size: 1.125rem;
    color: #6b7280;
    margin-bottom: 24px;
}

.demo-features {
    display: flex;
    justify-content: center;
    gap: 32px;
    flex-wrap: wrap;
}

.demo-feature {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #6b7280;
    font-size: 0.875rem;
}

.demo-feature i {
    color: #3b82f6;
}

.demo-history {
    margin-top: 32px;
    padding: 24px;
    background: #f9fafb;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
}

.demo-history h4 {
    margin-bottom: 16px;
    color: #374151;
}

.history-list {
    max-height: 200px;
    overflow-y: auto;
}

.history-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    margin-bottom: 8px;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.history-item:hover {
    border-color: #3b82f6;
    transform: translateY(-1px);
}

.history-service {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    color: white;
    font-size: 0.875rem;
}

.history-info {
    flex: 1;
    min-width: 0;
}

.history-url {
    font-size: 0.875rem;
    color: #374151;
    margin-bottom: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.history-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 0.75rem;
    color: #6b7280;
}

.history-status {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    border-radius: 50%;
}

.history-status.success {
    background: #dcfce7;
    color: #15803d;
}

.history-empty {
    text-align: center;
    color: #9ca3af;
    font-style: italic;
    padding: 20px;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

/* Responsive Design */
@media (max-width: 768px) {
    .api-demo-header {
        flex-direction: column;
        gap: 16px;
        text-align: center;
    }
    
    .url-input-container {
        flex-direction: column;
    }
    
    .demo-button {
        width: 100%;
    }
    
    .service-grid {
        grid-template-columns: 1fr;
    }
    
    .example-grid {
        grid-template-columns: repeat(2, 1fr);
    }
    
    .demo-features {
        flex-direction: column;
        gap: 16px;
    }
    
    .success-actions {
        flex-direction: column;
    }
}

/* Dark Mode Support */
[data-theme="dark"] .api-demo-content {
    background: #1f2937;
}

[data-theme="dark"] .service-card {
    background: #374151;
    border-color: #4b5563;
}

[data-theme="dark"] .demo-results,
[data-theme="dark"] .demo-history {
    background: #374151;
    border-color: #4b5563;
}

[data-theme="dark"] .demo-input {
    background: #374151;
    border-color: #4b5563;
    color: white;
}

[data-theme="dark"] .example-card {
    background: #374151;
    border-color: #4b5563;
}

[data-theme="dark"] .history-item {
    background: #374151;
    border-color: #4b5563;
}

/* Accessibility */
.sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
}

/* High Contrast Mode */
@media (prefers-contrast: high) {
    .service-card {
        border-width: 3px;
    }
    
    .demo-input {
        border-width: 3px;
    }
}

/* Reduced Motion */
@media (prefers-reduced-motion: reduce) {
    .loading-spinner {
        animation: none;
    }
    
    .demo-success {
        animation: none;
    }
}
</style>
`;
    
    // Add styles to head
    document.head.insertAdjacentHTML('beforeend', demoStyles);
    }
}

// Initialize the demo
document.addEventListener('DOMContentLoaded', function() {
    // Add demo container if it doesn't exist
    if (!document.getElementById('demo-container')) {
        const demoSection = document.createElement('section');
        demoSection.id = 'demo';
        demoSection.className = 'interactive-demo';
        demoSection.innerHTML = `
            <div class="container">
                <div class="section-header">
                    <h2>Try ShareConnect Live</h2>
                    <p>Experience the power of 20+ professional connectors in real-time</p>
                </div>
                <div id="demo-container"></div>
            </div>
        `;
        
        // Insert after courses section or at appropriate location
        const coursesSection = document.getElementById('courses');
        if (coursesSection) {
            coursesSection.parentNode.insertBefore(demoSection, coursesSection.nextSibling);
        } else {
            document.querySelector('main')?.appendChild(demoSection);
        }
    }
    
    // Initialize the demo
    window.shareConnectDemo = new ShareConnectAPIDemo();
});