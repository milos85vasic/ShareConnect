// ShareConnect - Modern Website JavaScript

class ShareConnectWebsite {
    constructor() {
        this.init();
    }

    init() {
        this.setupTheme();
        this.setupNavigation();
        this.setupAnimations();
        this.setupModals();
        this.setupToasts();
        this.setupScrollEffects();
        this.setupMobileMenu();
    }

    // Theme Management
    setupTheme() {
        const themeToggle = document.getElementById('theme-toggle');
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        const savedTheme = localStorage.getItem('theme');

        // Set initial theme
        const initialTheme = savedTheme || (prefersDark ? 'dark' : 'light');
        this.setTheme(initialTheme);

        // Theme toggle button
        themeToggle.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            this.setTheme(newTheme);
        });

        // Listen for system theme changes
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
            if (!localStorage.getItem('theme')) {
                this.setTheme(e.matches ? 'dark' : 'light');
            }
        });
    }

    setTheme(theme) {
        // Add transition class to body for smooth theme change
        document.body.classList.add('theme-transitioning');

        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('theme', theme);

        // Update theme toggle icon with animation
        const themeToggle = document.getElementById('theme-toggle');
        const sunIcon = themeToggle.querySelector('.fa-sun');
        const moonIcon = themeToggle.querySelector('.fa-moon');

        if (theme === 'dark') {
            sunIcon.style.display = 'block';
            moonIcon.style.display = 'block'; // Show both during transition
            setTimeout(() => {
                moonIcon.style.display = 'none';
            }, 150);
        } else {
            sunIcon.style.display = 'block';
            moonIcon.style.display = 'block'; // Show both during transition
            setTimeout(() => {
                sunIcon.style.display = 'none';
            }, 150);
        }

        // Remove transition class after animation
        setTimeout(() => {
            document.body.classList.remove('theme-transitioning');
        }, 300);

        // Show theme change toast with icon
        const themeIcon = theme === 'dark' ? 'fa-moon' : 'fa-sun';
        this.showToast(`Switched to ${theme} theme`, 'success', 3000, `<i class="fas ${themeIcon}"></i> Theme Changed`);
    }

    // Navigation
    setupNavigation() {
        const navLinks = document.querySelectorAll('.nav-link:not(.github)');
        const sections = document.querySelectorAll('section[id]');

        // Smooth scrolling for nav links
        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const targetId = link.getAttribute('href').substring(1);
                const targetSection = document.getElementById(targetId);

                if (targetSection) {
                    const offsetTop = targetSection.offsetTop - 80; // Account for fixed header
                    window.scrollTo({
                        top: offsetTop,
                        behavior: 'smooth'
                    });
                }
            });
        });

        // Update active nav link on scroll
        window.addEventListener('scroll', () => {
            let current = '';
            sections.forEach(section => {
                const sectionTop = section.offsetTop - 100;
                if (window.pageYOffset >= sectionTop) {
                    current = section.getAttribute('id');
                }
            });

            navLinks.forEach(link => {
                link.classList.remove('active');
                if (link.getAttribute('href').substring(1) === current) {
                    link.classList.add('active');
                }
            });
        });
    }

    // Animations
    setupAnimations() {
        // Intersection Observer for fade-in animations
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, observerOptions);

        // Observe elements for animation
        document.querySelectorAll('.feature-card, .connector-category, .testimonial-card, .step').forEach(el => {
            el.style.opacity = '0';
            el.style.transform = 'translateY(30px)';
            el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            observer.observe(el);
        });

        // Stagger animations for feature cards
        document.querySelectorAll('.feature-card').forEach((card, index) => {
            card.style.transitionDelay = `${index * 0.1}s`;
        });
    }

    // Modal System
    setupModals() {
        const modalOverlay = document.getElementById('modal-overlay');
        const modal = document.getElementById('modal');
        const modalTitle = document.getElementById('modal-title');
        const modalBody = document.getElementById('modal-body');

        // Modal trigger buttons (you can add data-modal attributes to buttons)
        document.addEventListener('click', (e) => {
            if (e.target.closest('[data-modal]')) {
                e.preventDefault();
                const button = e.target.closest('[data-modal]');
                const modalType = button.getAttribute('data-modal');

                this.openModal(modalType);
            }
        });

        // Close modal
        document.addEventListener('click', (e) => {
            if (e.target.closest('.modal-close') || e.target === modalOverlay) {
                this.closeModal();
            }
        });

        // ESC key to close
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal.classList.contains('active')) {
                this.closeModal();
            }
        });
    }

    openModal(type, data = {}) {
        const modalOverlay = document.getElementById('modal-overlay');
        const modal = document.getElementById('modal');
        const modalTitle = document.getElementById('modal-title');
        const modalBody = document.getElementById('modal-body');

        // Add jumping class for animation
        modal.classList.add('jumping');

        // Set modal content based on type
        switch(type) {
            case 'share-connect':
                modalTitle.innerHTML = '<i class="fas fa-rocket"></i> What is ShareConnect?';
                modalBody.innerHTML = `
                    <div class="modal-hero">
                        <div class="modal-icon">
                            <i class="fas fa-share-alt"></i>
                        </div>
                        <h3>Revolutionary Media Sharing</h3>
                    </div>
                    <p>ShareConnect is a revolutionary Android application that transforms how you share media links across your favorite services.</p>
                    <div class="modal-features">
                        <div class="modal-feature">
                            <i class="fas fa-bolt"></i>
                            <span>One-Tap Sharing</span>
                        </div>
                        <div class="modal-feature">
                            <i class="fas fa-brain"></i>
                            <span>Smart Detection</span>
                        </div>
                        <div class="modal-feature">
                            <i class="fas fa-shield-alt"></i>
                            <span>Secure & Private</span>
                        </div>
                    </div>
                    <p>Instead of manually copying and pasting URLs between different apps, ShareConnect acts as an intelligent bridge that automatically routes your media to the perfect destination.</p>
                    <div class="modal-cta">
                        <a href="products.html" class="btn btn-primary">Explore Products</a>
                    </div>
                `;
                break;
            case 'connector-detail':
                const connectorName = data.name || 'Connector';
                const connectorDesc = data.description || 'Description coming soon...';
                modalTitle.innerHTML = `<i class="fas fa-plug"></i> ${connectorName}`;
                modalBody.innerHTML = `
                    <div class="connector-modal">
                        <div class="connector-header">
                            <div class="connector-icon ${data.category || ''}">
                                <i class="fas ${data.icon || 'fa-plug'}"></i>
                            </div>
                            <div class="connector-info">
                                <h3>${connectorName}</h3>
                                <p>${connectorDesc}</p>
                            </div>
                        </div>
                        <div class="connector-details">
                            <h4>Key Features</h4>
                            <ul>
                                ${data.features ? data.features.map(feature => `<li><i class="fas fa-check"></i> ${feature}</li>`).join('') : '<li>Features coming soon...</li>'}
                            </ul>
                        </div>
                        <div class="connector-status">
                            <span class="status-badge ${data.status || 'status-success'}">${data.statusText || 'Available'}</span>
                        </div>
                    </div>
                `;
                break;
            case 'feature-detail':
                modalTitle.innerHTML = `<i class="fas ${data.icon || 'fa-star'}"></i> ${data.title || 'Feature'}`;
                modalBody.innerHTML = `
                    <div class="feature-modal">
                        <div class="feature-hero">
                            <div class="feature-icon-large">
                                <i class="fas ${data.icon || 'fa-star'}"></i>
                            </div>
                            <h3>${data.title || 'Feature'}</h3>
                        </div>
                        <p>${data.description || 'Description coming soon...'}</p>
                        <div class="feature-benefits">
                            <h4>Benefits</h4>
                            <ul>
                                ${data.benefits ? data.benefits.map(benefit => `<li>${benefit}</li>`).join('') : '<li>Benefits coming soon...</li>'}
                            </ul>
                        </div>
                    </div>
                `;
                break;
            case 'connectors':
                modalTitle.innerHTML = '<i class="fas fa-plug"></i> Understanding Connectors';
                modalBody.innerHTML = `
                    <div class="connectors-modal">
                        <p>Connectors are ShareConnect's secret sauce - specialized integrations for different types of services.</p>
                        <div class="connector-types">
                            <div class="connector-type">
                                <div class="type-icon torrent">
                                    <i class="fas fa-magnet"></i>
                                </div>
                                <div class="type-info">
                                    <h4>Torrent Connectors</h4>
                                    <p>qBittorrent, Transmission, uTorrent</p>
                                    <small>Professional torrent downloading</small>
                                </div>
                            </div>
                            <div class="connector-type">
                                <div class="type-icon media">
                                    <i class="fas fa-tv"></i>
                                </div>
                                <div class="type-info">
                                    <h4>Media Servers</h4>
                                    <p>Plex, Jellyfin, Emby</p>
                                    <small>Stream your media collection</small>
                                </div>
                            </div>
                            <div class="connector-type">
                                <div class="type-icon download">
                                    <i class="fas fa-download"></i>
                                </div>
                                <div class="type-info">
                                    <h4>Download Managers</h4>
                                    <p>JDownloader, YT-DLP, MeTube</p>
                                    <small>Professional download management</small>
                                </div>
                            </div>
                            <div class="connector-type">
                                <div class="type-icon cloud">
                                    <i class="fas fa-cloud"></i>
                                </div>
                                <div class="type-info">
                                    <h4>Cloud Storage</h4>
                                    <p>Nextcloud, Seafile, FileBrowser</p>
                                    <small>Secure cloud storage</small>
                                </div>
                            </div>
                        </div>
                        <p>Each connector is optimized for its specific service, ensuring the best possible experience.</p>
                    </div>
                `;
                break;
            default:
                modalTitle.innerHTML = '<i class="fas fa-info-circle"></i> Information';
                modalBody.innerHTML = '<p>Content coming soon...</p>';
        }

        modalOverlay.classList.add('active');
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';

        // Remove jumping class after animation
        setTimeout(() => {
            modal.classList.remove('jumping');
        }, 600);
    }

    closeModal() {
        const modalOverlay = document.getElementById('modal-overlay');
        const modal = document.getElementById('modal');

        modalOverlay.classList.remove('active');
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }

    // Toast Notifications
    setupToasts() {
        // Toast container is already in HTML
    }

    showToast(message, type = 'info', duration = 4000, title = '') {
        const toastContainer = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        const iconMap = {
            success: 'fa-check-circle',
            error: 'fa-exclamation-circle',
            warning: 'fa-exclamation-triangle',
            info: 'fa-info-circle'
        };

        const titleHtml = title ? `<div class="toast-title">${title}</div>` : '';

        toast.innerHTML = `
            <i class="fas ${iconMap[type] || iconMap.info}"></i>
            <div class="toast-content">
                ${titleHtml}
                <div class="toast-message">${message}</div>
            </div>
            <button class="toast-close" aria-label="Close notification">
                <i class="fas fa-times"></i>
            </button>
        `;

        // Add close button functionality
        const closeBtn = toast.querySelector('.toast-close');
        closeBtn.addEventListener('click', () => {
            this.removeToast(toast);
        });

        toastContainer.appendChild(toast);

        // Trigger animation
        setTimeout(() => toast.classList.add('show'), 100);

        // Auto remove
        const autoRemoveTimer = setTimeout(() => {
            this.removeToast(toast);
        }, duration);

        // Store timer for potential clearing
        toast._autoRemoveTimer = autoRemoveTimer;

        // Pause auto-remove on hover
        toast.addEventListener('mouseenter', () => {
            clearTimeout(toast._autoRemoveTimer);
        });

        toast.addEventListener('mouseleave', () => {
            toast._autoRemoveTimer = setTimeout(() => {
                this.removeToast(toast);
            }, 1000); // Shorter delay after hover
        });

        return toast;
    }

    removeToast(toast) {
        toast.classList.remove('show');
        setTimeout(() => {
            if (toast.parentNode) {
                toast.remove();
            }
        }, 300);
    }

    // Scroll Effects
    setupScrollEffects() {
        let lastScrollTop = 0;
        const header = document.querySelector('.header');

        window.addEventListener('scroll', () => {
            const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

            // Header styling on scroll
            if (scrollTop > 50) {
                header.classList.add('scrolled');
            } else {
                header.classList.remove('scrolled');
            }

            // Header hide/show on scroll (only on mobile)
            if (window.innerWidth <= 768) {
                if (scrollTop > lastScrollTop && scrollTop > 100) {
                    // Scrolling down
                    header.style.transform = 'translateY(-100%)';
                } else {
                    // Scrolling up
                    header.style.transform = 'translateY(0)';
                }
            } else {
                header.style.transform = 'translateY(0)';
            }

            lastScrollTop = scrollTop;

            // Parallax effect for hero background
            const hero = document.querySelector('.hero');
            if (hero) {
                const scrolled = scrollTop * 0.3;
                hero.style.backgroundPosition = `center ${scrolled}px`;
            }
        });
    }

    // Mobile Menu
    setupMobileMenu() {
        const mobileToggle = document.querySelector('.mobile-menu-toggle');
        const navMenu = document.querySelector('.nav-menu.mobile');
        const overlay = document.querySelector('.mobile-menu-overlay');

        if (mobileToggle && navMenu) {
            mobileToggle.addEventListener('click', () => {
                navMenu.classList.toggle('active');
                overlay.classList.toggle('active');
                mobileToggle.classList.toggle('active');
                document.body.style.overflow = navMenu.classList.contains('active') ? 'hidden' : '';
            });

            // Close mobile menu when clicking overlay
            if (overlay) {
                overlay.addEventListener('click', () => {
                    navMenu.classList.remove('active');
                    overlay.classList.remove('active');
                    mobileToggle.classList.remove('active');
                    document.body.style.overflow = '';
                });
            }

            // Close mobile menu on nav link click
            navMenu.querySelectorAll('.nav-link').forEach(link => {
                link.addEventListener('click', () => {
                    navMenu.classList.remove('active');
                    overlay.classList.remove('active');
                    mobileToggle.classList.remove('active');
                    document.body.style.overflow = '';
                });
            });
        }
    }
}

// Demo functionality
function setupDemoFeatures() {
    // Add demo modal triggers
    document.querySelectorAll('.btn').forEach(btn => {
        if (btn.textContent.includes('Learn More') || btn.textContent.includes('Explore')) {
            btn.setAttribute('data-modal', 'share-connect');
        }
    });

    // Add connector demo triggers
    document.querySelectorAll('.connector-category').forEach((category, index) => {
        if (!category.classList.contains('featured')) {
            category.addEventListener('click', () => {
                const categoryName = category.querySelector('h3').textContent;
                const connectorData = getConnectorData(categoryName);
                window.shareConnect.openModal('connector-detail', connectorData);
            });
        }
    });

    // Add feature card interactions
    document.querySelectorAll('.feature-card').forEach((card, index) => {
        card.addEventListener('click', () => {
            const featureTitle = card.querySelector('h3').textContent;
            const featureData = getFeatureData(featureTitle);
            window.shareConnect.openModal('feature-detail', featureData);
        });
    });

    // Add floating card interactions
    document.querySelectorAll('.floating-card').forEach(card => {
        card.addEventListener('click', () => {
            const cardText = card.querySelector('span').textContent;
            window.shareConnect.showToast(`Learn more about ${cardText}!`, 'info');
            // Add jumping animation
            card.style.animation = 'none';
            setTimeout(() => {
                card.style.animation = 'float 4s ease-in-out infinite, scaleIn 0.5s ease-out';
            }, 10);
        });
    });
}

function getConnectorData(name) {
    const connectorMap = {
        'Torrent Clients': {
            name: 'Torrent Connectors',
            description: 'Professional torrent client integrations for seamless downloading',
            category: 'torrent',
            icon: 'fa-magnet',
            features: ['qBittorrent integration', 'Transmission support', 'uTorrent connectivity', 'Advanced queue management'],
            status: 'status-success',
            statusText: 'Production Ready'
        },
        'Media Servers': {
            name: 'Media Server Connectors',
            description: 'Stream your media collection from anywhere with professional server integrations',
            category: 'media',
            icon: 'fa-tv',
            features: ['Plex Media Server', 'Jellyfin streaming', 'Emby integration', 'Live TV support'],
            status: 'status-success',
            statusText: 'Production Ready'
        },
        'Download Managers': {
            name: 'Download Manager Connectors',
            description: 'Professional download management with support for 1800+ sites',
            category: 'download',
            icon: 'fa-download',
            features: ['JDownloader premium', 'YT-DLP integration', '1800+ sites support', 'Batch downloads'],
            status: 'status-success',
            statusText: 'Production Ready'
        },
        'Cloud Storage': {
            name: 'Cloud Storage Connectors',
            description: 'Secure and private cloud storage integrations',
            category: 'cloud',
            icon: 'fa-cloud',
            features: ['Nextcloud self-hosted', 'Seafile encrypted', 'FileBrowser interface', 'Multi-device sync'],
            status: 'status-success',
            statusText: 'Production Ready'
        },
        'Specialized Services': {
            name: 'Specialized Service Connectors',
            description: 'Advanced integrations for specific use cases and workflows',
            category: 'specialized',
            icon: 'fa-cogs',
            features: ['Syncthing P2P sync', 'Matrix messaging', 'Paperless document management', 'WireGuard VPN'],
            status: 'status-success',
            statusText: 'Production Ready'
        }
    };
    return connectorMap[name] || { name: name, description: 'Connector information coming soon...' };
}

function getFeatureData(title) {
    const featureMap = {
        'One-Tap Sharing': {
            title: 'One-Tap Sharing',
            icon: 'fa-bolt',
            description: 'Share any media link with a single tap. ShareConnect automatically detects the content type and routes it to the perfect connector for the job.',
            benefits: [
                'Eliminates copy-paste frustration',
                'Intelligent content recognition',
                'Seamless workflow integration',
                'Works with any Android app'
            ]
        },
        'Secure & Private': {
            title: 'Secure & Private',
            icon: 'fa-shield-alt',
            description: 'Your data stays secure with enterprise-grade encryption. ShareConnect never stores your personal information or media content.',
            benefits: [
                'End-to-end encryption',
                'No data storage on servers',
                'Privacy-first design',
                'Open source transparency'
            ]
        },
        'Universal Connectors': {
            title: 'Universal Connectors',
            icon: 'fa-plug',
            description: 'Connect to 20+ services including torrent clients, media servers, cloud storage, and download managers. All working together seamlessly.',
            benefits: [
                '20+ service integrations',
                'Consistent user experience',
                'Extensible architecture',
                'Community-driven additions'
            ]
        },
        'Cross-Platform': {
            title: 'Cross-Platform',
            icon: 'fa-mobile-alt',
            description: 'Works on Android devices with a beautiful, intuitive interface. Designed for speed and simplicity across all screen sizes.',
            benefits: [
                'Native Android experience',
                'Responsive design',
                'Material Design 3',
                'Optimized performance'
            ]
        },
        'Smart Detection': {
            title: 'Smart Detection',
            icon: 'fa-brain',
            description: 'Advanced AI-powered content recognition automatically chooses the best connector for each media type and service.',
            benefits: [
                'Automatic content analysis',
                'Context-aware routing',
                'Learning algorithms',
                'Reduces user decision fatigue'
            ]
        },
        'Lightning Fast': {
            title: 'Lightning Fast',
            icon: 'fa-clock',
            description: 'Optimized for performance with minimal battery impact. ShareConnect works instantly without slowing down your device.',
            benefits: [
                'Minimal resource usage',
                'Instant response times',
                'Battery optimized',
                'Background processing'
            ]
        }
    };
    return featureMap[title] || { title: title, description: 'Feature information coming soon...' };
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.shareConnect = new ShareConnectWebsite();
    setupDemoFeatures();

    // Welcome toast
    setTimeout(() => {
        window.shareConnect.showToast('Welcome to ShareConnect! Explore our revolutionary media sharing platform.', 'success');
    }, 1000);
});

// Service Worker registration (for PWA features)
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        // Register service worker for offline functionality
        // navigator.serviceWorker.register('/sw.js');
    });
}