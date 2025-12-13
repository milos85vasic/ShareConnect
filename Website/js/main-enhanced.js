/**
 * ShareConnect Enhanced Website JavaScript
 * Professional-grade interactive functionality and modern web features
 */

class ShareConnectWebsite {
    constructor() {
        this.init();
    }

    init() {
        this.setupThemeToggle();
        this.setupMobileMenu();
        this.setupVideoPlayer();
        this.setupInteractiveDemo();
        this.setupCourseEnrollment();
        this.setupSmoothScrolling();
        this.setupScrollAnimations();
        this.setupIntersectionObserver();
        this.setupPerformanceOptimizations();
        this.setupAccessibilityFeatures();
        this.initializeComponents();
    }

    // ==========================================================================
    // Theme Management
    // ==========================================================================

    setupThemeToggle() {
        const themeToggle = document.getElementById('theme-toggle');
        if (!themeToggle) return;

        // Check for saved theme preference or default to light mode
        const savedTheme = localStorage.getItem('theme') || 'light';
        this.setTheme(savedTheme);

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
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('theme', theme);
        
        // Update theme toggle icon
        const themeToggle = document.getElementById('theme-toggle');
        if (themeToggle) {
            const moonIcon = themeToggle.querySelector('.fa-moon');
            const sunIcon = themeToggle.querySelector('.fa-sun');
            
            if (theme === 'dark') {
                moonIcon.style.display = 'none';
                sunIcon.style.display = 'block';
            } else {
                moonIcon.style.display = 'block';
                sunIcon.style.display = 'none';
            }
        }

        // Dispatch custom event for theme change
        window.dispatchEvent(new CustomEvent('themeChanged', { detail: { theme } }));
    }

    // ==========================================================================
    // Mobile Menu
    // ==========================================================================

    setupMobileMenu() {
        const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');
        const mobileMenu = document.querySelector('.nav-menu.mobile');
        const overlay = document.querySelector('.mobile-menu-overlay');

        if (!mobileMenuToggle || !mobileMenu || !overlay) return;

        const toggleMobileMenu = () => {
            const isOpen = mobileMenu.classList.contains('active');
            
            if (isOpen) {
                this.closeMobileMenu(mobileMenu, overlay);
            } else {
                this.openMobileMenu(mobileMenu, overlay);
            }
        };

        mobileMenuToggle.addEventListener('click', toggleMobileMenu);
        overlay.addEventListener('click', () => this.closeMobileMenu(mobileMenu, overlay));

        // Close menu when clicking on links
        mobileMenu.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => this.closeMobileMenu(mobileMenu, overlay));
        });

        // Close menu on escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && mobileMenu.classList.contains('active')) {
                this.closeMobileMenu(mobileMenu, overlay);
            }
        });

        // Close menu on window resize if above mobile breakpoint
        window.addEventListener('resize', () => {
            if (window.innerWidth > 768 && mobileMenu.classList.contains('active')) {
                this.closeMobileMenu(mobileMenu, overlay);
            }
        });
    }

    openMobileMenu(menu, overlay) {
        menu.classList.add('active');
        overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
        
        // Add focus management
        const firstLink = menu.querySelector('a');
        if (firstLink) {
            firstLink.focus();
        }
    }

    closeMobileMenu(menu, overlay) {
        menu.classList.remove('active');
        overlay.classList.remove('active');
        document.body.style.overflow = '';
        
        // Return focus to toggle button
        const toggleButton = document.querySelector('.mobile-menu-toggle');
        if (toggleButton) {
            toggleButton.focus();
        }
    }

    // ==========================================================================
    // Video Player Integration
    // ==========================================================================

    setupVideoPlayer() {
        // Initialize Video.js players
        const videoPlayers = document.querySelectorAll('video');
        
        videoPlayers.forEach(video => {
            if (video.classList.contains('video-js')) {
                this.initializeVideoJS(video);
            }
        });

        // Setup video modal functionality
        this.setupVideoModal();
        
        // Setup video course functionality
        this.setupVideoCourses();
    }

    initializeVideoJS(videoElement) {
        if (typeof videojs === 'undefined') return;

        const player = videojs(videoElement, {
            controls: true,
            autoplay: false,
            preload: 'auto',
            fluid: true,
            responsive: true,
            playbackRates: [0.5, 0.75, 1, 1.25, 1.5, 2],
            plugins: {
                hotkeys: {
                    enableVolumeScroll: false,
                    enableModifiersForNumbers: false
                }
            }
        });

        // Add custom event listeners
        player.ready(() => {
            console.log('Video.js player ready:', player.id());
            
            // Track video progress
            player.on('timeupdate', () => {
                const currentTime = player.currentTime();
                const duration = player.duration();
                
                if (duration > 0) {
                    const progress = (currentTime / duration) * 100;
                    this.saveVideoProgress(player.id(), progress);
                }
            });

            // Handle video completion
            player.on('ended', () => {
                this.handleVideoComplete(player.id());
            });

            // Restore previous progress
            this.restoreVideoProgress(player);
        });

        return player;
    }

    setupVideoModal() {
        const modalTriggers = document.querySelectorAll('[data-video-modal]');
        
        modalTriggers.forEach(trigger => {
            trigger.addEventListener('click', (e) => {
                e.preventDefault();
                const videoId = trigger.getAttribute('data-video-modal');
                this.openVideoModal(videoId);
            });
        });
    }

    openVideoModal(videoId) {
        // Create modal if it doesn't exist
        let modal = document.getElementById('video-modal');
        
        if (!modal) {
            modal = this.createVideoModal();
        }

        // Load video content
        this.loadVideoInModal(videoId, modal);
        
        // Show modal
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
        
        // Focus management
        const closeButton = modal.querySelector('.modal-close');
        if (closeButton) {
            closeButton.focus();
        }
    }

    createVideoModal() {
        const modal = document.createElement('div');
        modal.id = 'video-modal';
        modal.className = 'video-modal';
        modal.innerHTML = `
            <div class="modal-overlay"></div>
            <div class="modal-content">
                <button class="modal-close" aria-label="Close modal">
                    <i class="fas fa-times"></i>
                </button>
                <div class="modal-video-container">
                    <video id="modal-video" class="video-js vjs-default-skin" controls preload="auto">
                        <source src="" type="video/mp4">
                        Your browser does not support the video tag.
                    </video>
                </div>
                <div class="modal-video-info">
                    <h3 class="modal-video-title"></h3>
                    <p class="modal-video-description"></p>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        // Setup modal controls
        const overlay = modal.querySelector('.modal-overlay');
        const closeButton = modal.querySelector('.modal-close');
        
        overlay.addEventListener('click', () => this.closeVideoModal());
        closeButton.addEventListener('click', () => this.closeVideoModal());

        // Close on escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal.classList.contains('active')) {
                this.closeVideoModal();
            }
        });

        return modal;
    }

    loadVideoInModal(videoId, modal) {
        const videoElement = modal.querySelector('#modal-video');
        const titleElement = modal.querySelector('.modal-video-title');
        const descriptionElement = modal.querySelector('.modal-video-description');
        
        // Get video data (you would replace this with actual data)
        const videoData = this.getVideoData(videoId);
        
        if (videoData) {
            titleElement.textContent = videoData.title;
            descriptionElement.textContent = videoData.description;
            
            // Set video source
            const source = videoElement.querySelector('source');
            source.src = videoData.src;
            
            // Initialize Video.js
            if (typeof videojs !== 'undefined') {
                const player = videojs(videoElement);
                player.src(videoData.src);
                player.ready(() => {
                    player.play();
                });
            }
        }
    }

    closeVideoModal() {
        const modal = document.getElementById('video-modal');
        if (!modal) return;

        // Stop video playback
        const videoElement = modal.querySelector('#modal-video');
        if (videoElement && typeof videojs !== 'undefined') {
            const player = videojs(videoElement);
            player.pause();
            player.dispose();
        }

        modal.classList.remove('active');
        document.body.style.overflow = '';
    }

    setupVideoCourses() {
        const watchButtons = document.querySelectorAll('.btn-watch');
        
        watchButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const courseId = button.getAttribute('data-course');
                this.enrollInCourse(courseId);
            });
        });

        // Setup course progress tracking
        this.setupCourseProgress();
    }

    setupCourseProgress() {
        // Load saved progress
        const savedProgress = this.getSavedCourseProgress();
        
        // Update UI with progress
        Object.keys(savedProgress).forEach(courseId => {
            this.updateCourseProgressUI(courseId, savedProgress[courseId]);
        });
    }

    // ==========================================================================
    // Interactive Demo
    // ==========================================================================

    setupInteractiveDemo() {
        const demoInput = document.getElementById('demo-url');
        const demoButton = document.getElementById('demo-share');
        const serviceButtons = document.querySelectorAll('.service-btn');
        const exampleButtons = document.querySelectorAll('.example-btn');
        const resultContainer = document.getElementById('demo-result');

        if (!demoInput || !demoButton || !resultContainer) return;

        let selectedService = 'qBittorrent';

        // Service selection
        serviceButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                serviceButtons.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                selectedService = btn.getAttribute('data-service');
                
                // Update demo description
                this.updateDemoDescription(selectedService);
            });
        });

        // Example buttons
        exampleButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const url = btn.getAttribute('data-url');
                demoInput.value = url;
                demoInput.focus();
                
                // Auto-trigger demo
                setTimeout(() => {
                    demoButton.click();
                }, 300);
            });
        });

        // Main demo functionality
        demoButton.addEventListener('click', async () => {
            const url = demoInput.value.trim();
            
            if (!url) {
                this.showDemoError('Please enter a URL to test');
                return;
            }

            if (!this.isValidUrl(url)) {
                this.showDemoError('Please enter a valid URL');
                return;
            }

            await this.runDemo(url, selectedService, resultContainer);
        });

        // Real-time validation
        demoInput.addEventListener('input', () => {
            this.validateDemoInput(demoInput.value);
        });

        // Initialize demo description
        this.updateDemoDescription(selectedService);
    }

    async runDemo(url, service, resultContainer) {
        // Show loading state
        this.showDemoLoading(resultContainer);
        
        const startTime = performance.now();
        
        try {
            // Simulate API call (replace with actual API integration)
            const response = await this.simulateApiCall(url, service);
            
            const endTime = performance.now();
            const responseTime = Math.round(endTime - startTime);
            
            this.displayDemoResults(response, service, responseTime, resultContainer);
            
            // Track demo usage
            this.trackDemoUsage(url, service, responseTime);
            
        } catch (error) {
            this.showDemoError(`Demo failed: ${error.message}`);
            console.error('Demo error:', error);
        }
    }

    async simulateApiCall(url, service) {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 800 + Math.random() * 1200));
        
        // Generate mock response based on service
        return this.generateMockResponse(url, service);
    }

    generateMockResponse(url, service) {
        const responses = {
            'qBittorrent': {
                success: true,
                message: `Torrent added successfully to qBittorrent`,
                data: {
                    hash: this.generateHash(),
                    name: this.extractFileName(url) || 'Unknown torrent',
                    size: `${(Math.random() * 5 + 0.5).toFixed(2)} GB`,
                    status: 'Downloading',
                    progress: Math.floor(Math.random() * 100)
                }
            },
            'Plex': {
                success: true,
                message: `Media added to Plex library`,
                data: {
                    library: 'Movies',
                    title: this.extractFileName(url) || 'Unknown media',
                    year: new Date().getFullYear(),
                    rating: 'Not Rated',
                    duration: `${Math.floor(Math.random() * 120 + 60)} min`
                }
            },
            'Nextcloud': {
                success: true,
                message: `File uploaded to Nextcloud`,
                data: {
                    path: `/Downloads/${this.extractFileName(url) || 'unknown.file'}`,
                    size: `${(Math.random() * 1000 + 100).toFixed(0)} MB`,
                    modified: new Date().toISOString(),
                    shareLink: `https://nextcloud.example.com/s/${this.generateHash()}`
                }
            },
            'JDownloader': {
                success: true,
                message: `Download added to JDownloader queue`,
                data: {
                    packageName: 'ShareConnect Downloads',
                    links: 1,
                    status: 'Queued',
                    eta: `${Math.floor(Math.random() * 10 + 2)} minutes`
                }
            }
        };

        return responses[service] || responses['qBittorrent'];
    }

    displayDemoResults(response, service, responseTime, container) {
        const responseTimeElement = document.getElementById('response-time');
        if (responseTimeElement) {
            responseTimeElement.textContent = responseTime;
        }

        let html = '';
        
        if (response.success) {
            html = `
                <div class="demo-success">
                    <div class="success-header">
                        <i class="fas fa-check-circle"></i>
                        <span>Success!</span>
                    </div>
                    <div class="success-message">${response.message}</div>
                    <div class="success-data">
                        ${Object.entries(response.data).map(([key, value]) => `
                            <div class="data-item">
                                <span class="data-label">${this.formatLabel(key)}:</span>
                                <span class="data-value">${value}</span>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
        } else {
            html = `
                <div class="demo-error">
                    <div class="error-header">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>Error</span>
                    </div>
                    <div class="error-message">${response.message}</div>
                </div>
            `;
        }

        container.innerHTML = html;
        
        // Add animation
        container.querySelector('.demo-success, .demo-error').classList.add('animate-fadeInUp');
    }

    updateDemoDescription(service) {
        const descriptions = {
            'qBittorrent': 'Share torrent files and magnet links directly to your qBittorrent client',
            'Plex': 'Add media files to your Plex library for instant streaming',
            'Nextcloud': 'Upload files directly to your Nextcloud storage',
            'JDownloader': 'Queue downloads in JDownloader with premium support'
        };

        // Update description text if element exists
        const descriptionElement = document.querySelector('.demo-info h4');
        if (descriptionElement) {
            descriptionElement.textContent = descriptions[service] || 'Share content to your selected service';
        }
    }

    // ==========================================================================
    // Course Enrollment & Progress
    // ==========================================================================

    setupCourseEnrollment() {
        const enrollButtons = document.querySelectorAll('.btn-enroll, .btn-notify');
        
        enrollButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const courseId = button.getAttribute('data-course') || 'installation-setup';
                const action = button.classList.contains('btn-notify') ? 'notify' : 'enroll';
                
                if (action === 'notify') {
                    this.showNotificationModal(courseId);
                } else {
                    this.enrollInCourse(courseId);
                }
            });
        });
    }

    enrollInCourse(courseId) {
        // Check if user is already enrolled
        if (this.isUserEnrolled(courseId)) {
            this.showNotification('Already enrolled in this course!', 'info');
            return;
        }

        // Simulate enrollment process
        this.showEnrollmentModal(courseId);
        
        // Track enrollment
        this.trackEnrollment(courseId);
    }

    showEnrollmentModal(courseId) {
        const modal = this.createEnrollmentModal(courseId);
        document.body.appendChild(modal);
        
        // Show modal with animation
        setTimeout(() => {
            modal.classList.add('active');
        }, 100);
    }

    createEnrollmentModal(courseId) {
        const modal = document.createElement('div');
        modal.className = 'enrollment-modal';
        modal.innerHTML = `
            <div class="modal-overlay"></div>
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Enroll in Course</h3>
                    <button class="modal-close" aria-label="Close modal">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="course-preview">
                        <img src="assets/courses/${courseId}-thumb.jpg" alt="Course preview">
                        <div class="course-info">
                            <h4>${this.getCourseTitle(courseId)}</h4>
                            <p>${this.getCourseDescription(courseId)}</p>
                        </div>
                    </div>
                    <form class="enrollment-form">
                        <div class="form-group">
                            <label for="enroll-name">Full Name</label>
                            <input type="text" id="enroll-name" required>
                        </div>
                        <div class="form-group">
                            <label for="enroll-email">Email Address</label>
                            <input type="email" id="enroll-email" required>
                        </div>
                        <div class="form-group">
                            <label class="checkbox-label">
                                <input type="checkbox" id="enroll-newsletter">
                                Subscribe to course updates and new releases
                            </label>
                        </div>
                        <button type="submit" class="btn btn-primary btn-large">
                            <i class="fas fa-graduation-cap"></i>
                            Enroll Now
                        </button>
                    </form>
                </div>
            </div>
        `;

        // Setup form submission
        const form = modal.querySelector('.enrollment-form');
        const closeButton = modal.querySelector('.modal-close');
        const overlay = modal.querySelector('.modal-overlay');

        form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.processEnrollment(courseId, form);
        });

        closeButton.addEventListener('click', () => this.closeEnrollmentModal(modal));
        overlay.addEventListener('click', () => this.closeEnrollmentModal(modal));

        return modal;
    }

    processEnrollment(courseId, form) {
        const formData = new FormData(form);
        const enrollmentData = {
            courseId,
            name: formData.get('enroll-name'),
            email: formData.get('enroll-email'),
            newsletter: formData.get('enroll-newsletter') === 'on',
            timestamp: new Date().toISOString()
        };

        // Simulate API call
        setTimeout(() => {
            this.saveEnrollment(enrollmentData);
            this.showNotification('Successfully enrolled! Check your email for course access.', 'success');
            this.closeEnrollmentModal(form.closest('.enrollment-modal'));
            
            // Update UI
            this.updateEnrollmentUI(courseId);
        }, 1500);
    }

    closeEnrollmentModal(modal) {
        modal.classList.remove('active');
        setTimeout(() => {
            modal.remove();
        }, 300);
    }

    // ==========================================================================
    // Smooth Scrolling & Navigation
    // ==========================================================================

    setupSmoothScrolling() {
        // Smooth scroll for anchor links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                
                if (target) {
                    const headerOffset = 80;
                    const elementPosition = target.getBoundingClientRect().top;
                    const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

                    window.scrollTo({
                        top: offsetPosition,
                        behavior: 'smooth'
                    });
                }
            });
        });

        // Scroll to top functionality
        this.setupScrollToTop();
    }

    setupScrollToTop() {
        const scrollToTopButton = document.createElement('button');
        scrollToTopButton.className = 'scroll-to-top';
        scrollToTopButton.innerHTML = '<i class="fas fa-arrow-up"></i>';
        scrollToTopButton.setAttribute('aria-label', 'Scroll to top');
        document.body.appendChild(scrollToTopButton);

        // Show/hide button based on scroll position
        window.addEventListener('scroll', () => {
            if (window.pageYOffset > 300) {
                scrollToTopButton.classList.add('visible');
            } else {
                scrollToTopButton.classList.remove('visible');
            }
        });

        // Smooth scroll to top
        scrollToTopButton.addEventListener('click', () => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }

    // ==========================================================================
    // Scroll Animations & Intersection Observer
    // ==========================================================================

    setupScrollAnimations() {
        // Enhanced scroll animations with Intersection Observer
        const animatedElements = document.querySelectorAll('[data-animate]');
        
        if ('IntersectionObserver' in window) {
            const animationObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const animation = entry.target.getAttribute('data-animate');
                        entry.target.classList.add('animate-' + animation);
                        animationObserver.unobserve(entry.target);
                    }
                });
            }, {
                threshold: 0.1,
                rootMargin: '0px 0px -50px 0px'
            });

            animatedElements.forEach(element => {
                animationObserver.observe(element);
            });
        }
    }

    setupIntersectionObserver() {
        // Progress indicators for sections
        const sections = document.querySelectorAll('section');
        
        if ('IntersectionObserver' in window) {
            const sectionObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const sectionId = entry.target.id;
                        this.updateNavigationIndicator(sectionId);
                        this.trackSectionView(sectionId);
                    }
                });
            }, {
                threshold: 0.5
            });

            sections.forEach(section => {
                sectionObserver.observe(section);
            });
        }
    }

    updateNavigationIndicator(sectionId) {
        const navLinks = document.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href') === `#${sectionId}`) {
                link.classList.add('active');
            }
        });
    }

    trackSectionView(sectionId) {
        const viewData = {
            sectionId,
            timestamp: new Date().toISOString(),
            scrollDepth: this.getScrollDepth()
        };
        
        this.storeData('sectionViews', viewData);
    }

    getScrollDepth() {
        const scrollTop = window.pageYOffset;
        const docHeight = document.documentElement.scrollHeight - window.innerHeight;
        return Math.round((scrollTop / docHeight) * 100);
    }

    // ==========================================================================
    // Performance Optimizations
    // ==========================================================================

    setupPerformanceOptimizations() {
        // Lazy loading for images
        this.setupLazyLoading();
        
        // Debounced scroll events
        this.setupDebouncedScroll();
        
        // Resource preloading
        this.setupResourcePreloading();
        
        // Connection quality detection
        this.setupConnectionQualityDetection();
    }

    setupLazyLoading() {
        const lazyImages = document.querySelectorAll('img[data-src]');
        
        if ('IntersectionObserver' in window) {
            const imageObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const img = entry.target;
                        img.src = img.dataset.src;
                        img.classList.remove('lazy');
                        imageObserver.unobserve(img);
                    }
                });
            });

            lazyImages.forEach(img => {
                imageObserver.observe(img);
            });
        } else {
            // Fallback for browsers without Intersection Observer
            lazyImages.forEach(img => {
                img.src = img.dataset.src;
            });
        }
    }

    setupDebouncedScroll() {
        let scrollTimeout;
        
        window.addEventListener('scroll', () => {
            clearTimeout(scrollTimeout);
            scrollTimeout = setTimeout(() => {
                this.handleScrollEnd();
            }, 150);
        });
    }

    handleScrollEnd() {
        // Trigger any scroll-end specific functionality
        this.updateScrollProgress();
    }

    updateScrollProgress() {
        const scrollProgress = document.querySelector('.scroll-progress');
        if (!scrollProgress) return;

        const scrollTop = window.pageYOffset;
        const docHeight = document.documentElement.scrollHeight - window.innerHeight;
        const scrollPercent = (scrollTop / docHeight) * 100;
        
        scrollProgress.style.width = scrollPercent + '%';
    }

    setupResourcePreloading() {
        // Preload critical resources
        const criticalResources = [
            '/assets/videos/shareconnect-overview.mp4',
            '/assets/fonts/inter.woff2'
        ];

        criticalResources.forEach(resource => {
            const link = document.createElement('link');
            link.rel = 'preload';
            link.href = resource;
            link.as = resource.includes('.mp4') ? 'video' : 'font';
            if (resource.includes('.woff2')) {
                link.crossOrigin = 'anonymous';
            }
            document.head.appendChild(link);
        });
    }

    setupConnectionQualityDetection() {
        if ('connection' in navigator) {
            const connection = navigator.connection;
            
            const updateConnectionInfo = () => {
                const effectiveType = connection.effectiveType;
                const saveData = connection.saveData;
                
                // Adjust quality based on connection
                if (effectiveType === '2g' || saveData) {
                    document.body.classList.add('low-bandwidth');
                } else {
                    document.body.classList.remove('low-bandwidth');
                }
            };

            connection.addEventListener('change', updateConnectionInfo);
            updateConnectionInfo(); // Initial check
        }
    }

    // ==========================================================================
    // Accessibility Features
    // ==========================================================================

    setupAccessibilityFeatures() {
        // Keyboard navigation
        this.setupKeyboardNavigation();
        
        // Focus management
        this.setupFocusManagement();
        
        // Screen reader announcements
        this.setupScreenReaderSupport();
        
        // High contrast mode detection
        this.setupHighContrastMode();
        
        // Reduced motion preference
        this.setupReducedMotion();
    }

    setupKeyboardNavigation() {
        // Skip to main content link
        const skipLink = document.createElement('a');
        skipLink.href = '#main-content';
        skipLink.className = 'skip-link';
        skipLink.textContent = 'Skip to main content';
        document.body.insertBefore(skipLink, document.body.firstChild);

        // Enhanced keyboard navigation for interactive elements
        document.querySelectorAll('.btn, .nav-link, .service-btn').forEach(element => {
            element.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    element.click();
                }
            });
        });
    }

    setupFocusManagement() {
        // Trap focus in modals
        const modals = document.querySelectorAll('.modal, .enrollment-modal');
        
        modals.forEach(modal => {
            modal.addEventListener('keydown', (e) => {
                if (e.key === 'Tab') {
                    this.trapFocus(e, modal);
                }
            });
        });
    }

    trapFocus(event, container) {
        const focusableElements = container.querySelectorAll(
            'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );
        
        const firstElement = focusableElements[0];
        const lastElement = focusableElements[focusableElements.length - 1];
        
        if (event.shiftKey) {
            if (document.activeElement === firstElement) {
                lastElement.focus();
                event.preventDefault();
            }
        } else {
            if (document.activeElement === lastElement) {
                firstElement.focus();
                event.preventDefault();
            }
        }
    }

    setupScreenReaderSupport() {
        // Create live region for announcements
        const liveRegion = document.createElement('div');
        liveRegion.setAttribute('aria-live', 'polite');
        liveRegion.setAttribute('aria-atomic', 'true');
        liveRegion.className = 'sr-only';
        document.body.appendChild(liveRegion);
        
        this.announceToScreenReader = (message) => {
            liveRegion.textContent = message;
            setTimeout(() => {
                liveRegion.textContent = '';
            }, 1000);
        };
    }

    setupHighContrastMode() {
        // Detect high contrast mode
        const checkHighContrast = () => {
            const div = document.createElement('div');
            div.style.color = 'rgb(255, 255, 255)';
            document.body.appendChild(div);
            const color = window.getComputedStyle(div).color;
            document.body.removeChild(div);
            
            if (color === 'rgb(255, 255, 255)') {
                document.body.classList.add('high-contrast');
            }
        };

        checkHighContrast();
        
        // Also check for Windows high contrast mode
        if (window.matchMedia('(-ms-high-contrast: active)').matches) {
            document.body.classList.add('high-contrast');
        }
    }

    setupReducedMotion() {
        const mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)');
        
        const handleReducedMotion = (e) => {
            if (e.matches) {
                document.body.classList.add('reduced-motion');
                // Disable animations
                document.documentElement.style.setProperty('--transition-fast', '0ms');
                document.documentElement.style.setProperty('--transition-normal', '0ms');
                document.documentElement.style.setProperty('--transition-slow', '0ms');
            } else {
                document.body.classList.remove('reduced-motion');
                // Re-enable animations
                document.documentElement.style.removeProperty('--transition-fast');
                document.documentElement.style.removeProperty('--transition-normal');
                document.documentElement.style.removeProperty('--transition-slow');
            }
        };

        mediaQuery.addEventListener('change', handleReducedMotion);
        handleReducedMotion(mediaQuery);
    }

    // ==========================================================================
    // Utility Methods
    // ==========================================================================

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

    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas fa-${this.getNotificationIcon(type)}"></i>
                <span>${message}</span>
            </div>
            <button class="notification-close" aria-label="Close notification">
                <i class="fas fa-times"></i>
            </button>
        `;

        document.body.appendChild(notification);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            this.removeNotification(notification);
        }, 5000);

        // Manual close
        notification.querySelector('.notification-close').addEventListener('click', () => {
            this.removeNotification(notification);
        });

        // Screen reader announcement
        if (this.announceToScreenReader) {
            this.announceToScreenReader(message);
        }
    }

    removeNotification(notification) {
        notification.classList.add('removing');
        setTimeout(() => {
            notification.remove();
        }, 300);
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

    // ==========================================================================
    // Data Management
    // ==========================================================================

    saveVideoProgress(videoId, progress) {
        const progressData = this.getStoredData('videoProgress') || {};
        progressData[videoId] = {
            progress: progress,
            timestamp: new Date().toISOString()
        };
        this.storeData('videoProgress', progressData);
    }

    restoreVideoProgress(player) {
        const videoId = player.id();
        const progressData = this.getStoredData('videoProgress') || {};
        
        if (progressData[videoId] && progressData[videoId].progress > 0) {
            const savedProgress = progressData[videoId].progress;
            const shouldResume = confirm(`Resume from ${Math.round(savedProgress)}%?`);
            
            if (shouldResume) {
                const duration = player.duration();
                const resumeTime = (duration * savedProgress) / 100;
                player.currentTime(resumeTime);
            }
        }
    }

    handleVideoComplete(videoId) {
        this.showNotification('Video completed! Great job!', 'success');
        
        // Mark as completed in course progress
        this.updateCourseProgress(videoId, 100);
        
        // Suggest next video
        this.suggestNextVideo(videoId);
    }

    getVideoData(videoId) {
        // Mock video data - replace with actual data source
        const videoData = {
            'shareconnect-overview': {
                title: 'ShareConnect Overview',
                description: 'Comprehensive introduction to the ShareConnect ecosystem',
                src: 'assets/videos/shareconnect-overview.mp4',
                duration: '2:30'
            },
            'installation-setup': {
                title: 'Installation & Setup',
                description: 'Step-by-step guide to installing ShareConnect',
                src: 'assets/videos/installation-setup.mp4',
                duration: '8:00'
            }
        };
        
        return videoData[videoId] || null;
    }

    getCourseTitle(courseId) {
        const titles = {
            'installation-setup': 'Installation & Setup Course',
            'advanced-features': 'Advanced Features Course'
        };
        return titles[courseId] || 'ShareConnect Course';
    }

    getCourseDescription(courseId) {
        const descriptions = {
            'installation-setup': 'Complete guide to installing ShareConnect and setting up your first connectors.',
            'advanced-features': 'Deep dive into advanced ShareConnect features and enterprise deployment.'
        };
        return descriptions[courseId] || 'Professional ShareConnect training course.';
    }

    // ==========================================================================
    // Analytics & Tracking
    // ==========================================================================

    trackDemoUsage(url, service, responseTime) {
        // Track demo usage for analytics
        const demoData = {
            url,
            service,
            responseTime,
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent
        };
        
        // Store locally for now - integrate with analytics service
        this.storeData('demoUsage', demoData);
        
        // Console log for development
        console.log('Demo usage tracked:', demoData);
    }

    trackEnrollment(courseId) {
        const enrollmentData = {
            courseId,
            timestamp: new Date().toISOString(),
            referrer: document.referrer,
            userAgent: navigator.userAgent
        };
        
        this.storeData('enrollment', enrollmentData);
        console.log('Enrollment tracked:', enrollmentData);
    }

    trackSectionView(sectionId) {
        const viewData = {
            sectionId,
            timestamp: new Date().toISOString(),
            scrollDepth: this.getScrollDepth()
        };
        
        this.storeData('sectionViews', viewData);
    }

    getScrollDepth() {
        const scrollTop = window.pageYOffset;
        const docHeight = document.documentElement.scrollHeight - window.innerHeight;
        return Math.round((scrollTop / docHeight) * 100);
    }

    // ==========================================================================
    // Local Storage Helpers
    // ==========================================================================

    storeData(key, data) {
        try {
            localStorage.setItem(key, JSON.stringify(data));
        } catch (error) {
            console.warn('Failed to store data:', error);
        }
    }

    getStoredData(key) {
        try {
            const stored = localStorage.getItem(key);
            return stored ? JSON.parse(stored) : null;
        } catch (error) {
            console.warn('Failed to retrieve data:', error);
            return null;
        }
    }

    // ==========================================================================
    // Component Initialization
    // ==========================================================================

    initializeComponents() {
        // Initialize any additional components
        this.initializeTooltips();
        this.initializeDropdowns();
        this.initializeTabs();
    }

    initializeTooltips() {
        const tooltipTriggers = document.querySelectorAll('[data-tooltip]');
        
        tooltipTriggers.forEach(trigger => {
            const tooltipText = trigger.getAttribute('data-tooltip');
            const tooltip = document.createElement('div');
            tooltip.className = 'tooltip';
            tooltip.textContent = tooltipText;
            tooltip.setAttribute('role', 'tooltip');
            
            trigger.appendChild(tooltip);
            
            trigger.addEventListener('mouseenter', () => {
                tooltip.classList.add('visible');
            });
            
            trigger.addEventListener('mouseleave', () => {
                tooltip.classList.remove('visible');
            });
        });
    }

    initializeDropdowns() {
        const dropdowns = document.querySelectorAll('.dropdown');
        
        dropdowns.forEach(dropdown => {
            const trigger = dropdown.querySelector('.dropdown-trigger');
            const menu = dropdown.querySelector('.dropdown-menu');
            
            if (trigger && menu) {
                trigger.addEventListener('click', (e) => {
                    e.preventDefault();
                    dropdown.classList.toggle('active');
                });
                
                // Close dropdown when clicking outside
                document.addEventListener('click', (e) => {
                    if (!dropdown.contains(e.target)) {
                        dropdown.classList.remove('active');
                    }
                });
            }
        });
    }

    initializeTabs() {
        const tabContainers = document.querySelectorAll('.tabs');
        
        tabContainers.forEach(container => {
            const tabs = container.querySelectorAll('.tab');
            const panels = container.querySelectorAll('.tab-panel');
            
            tabs.forEach((tab, index) => {
                tab.addEventListener('click', () => {
                    // Remove active class from all tabs and panels
                    tabs.forEach(t => t.classList.remove('active'));
                    panels.forEach(p => p.classList.remove('active'));
                    
                    // Add active class to clicked tab and corresponding panel
                    tab.classList.add('active');
                    panels[index].classList.add('active');
                });
            });
        });
    }
}

// ==========================================================================
// Additional Utility Functions
// ==========================================================================

// Debounce function for performance optimization
function debounce(func, wait, immediate) {
    let timeout;
    return function executedFunction() {
        const context = this;
        const args = arguments;
        
        const later = function() {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        
        const callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        
        if (callNow) func.apply(context, args);
    };
}

// Throttle function for scroll events
function throttle(func, limit) {
    let inThrottle;
    return function() {
        const args = arguments;
        const context = this;
        if (!inThrottle) {
            func.apply(context, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// ==========================================================================
// Initialize Website
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
    new ShareConnectWebsite();
});

// Handle page visibility changes
document.addEventListener('visibilitychange', () => {
    if (document.hidden) {
        // Pause any playing videos
        const videos = document.querySelectorAll('video');
        videos.forEach(video => {
            if (!video.paused) {
                video.pause();
            }
        });
    }
});

// Handle beforeunload to save state
window.addEventListener('beforeunload', () => {
    // Save any unsaved data
    if (window.shareConnectWebsite) {
        // Save current state
        console.log('Saving application state...');
    }
});

// Export for global access
window.ShareConnectWebsite = ShareConnectWebsite;