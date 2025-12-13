#!/bin/bash

# ShareConnect Video Course Integration Script
# Embeds professional video content and creates interactive course platform

set -e

echo "🎬 Integrating Professional Video Courses into ShareConnect Website"
echo "======================================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Website directory
WEBSITE_DIR="/home/milosvasic/Projects/ShareConnect/Website"
VIDEO_DIR="/home/milosvasic/Projects/ShareConnect/VideoProduction"

# Function to create video course pages
create_course_pages() {
    echo -e "${BLUE}Creating video course pages...${NC}"
    
    # Course overview page
    cat > "${WEBSITE_DIR}/courses.html" << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ShareConnect Video Courses - Professional Training</title>
    <meta name="description" content="Master ShareConnect with comprehensive video courses. Professional training for 20+ connectors, installation guides, and advanced features.">
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://vjs.zencdn.net/8.6.1/video-js.css" rel="stylesheet">
    <link rel="stylesheet" href="styles.css">
    <link rel="stylesheet" href="styles-enhanced-additions.css">
    
    <style>
        .courses-hero {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 80px 0;
            text-align: center;
        }
        
        .course-player {
            background: #000;
            border-radius: 16px;
            overflow: hidden;
            margin: 32px 0;
        }
        
        .course-progress {
            background: #f3f4f6;
            height: 8px;
            border-radius: 4px;
            overflow: hidden;
            margin: 16px 0;
        }
        
        .progress-bar {
            height: 100%;
            background: linear-gradient(90deg, #3b82f6, #1d4ed8);
            transition: width 0.3s ease;
        }
        
        .lesson-navigation {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 32px;
            padding: 24px;
            background: #f9fafb;
            border-radius: 12px;
        }
        
        .nav-button {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 12px 24px;
            background: #3b82f6;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .nav-button:hover {
            background: #2563eb;
            transform: translateY(-1px);
        }
        
        .nav-button:disabled {
            background: #9ca3af;
            cursor: not-allowed;
            transform: none;
        }
        
        .transcript-section {
            background: #f9fafb;
            border-radius: 12px;
            padding: 24px;
            margin-top: 32px;
        }
        
        .transcript-text {
            font-family: 'Inter', monospace;
            line-height: 1.6;
            color: #374151;
            max-height: 300px;
            overflow-y: auto;
        }
        
        .course-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 24px;
            margin: 32px 0;
        }
        
        .stat-card {
            background: white;
            padding: 24px;
            border-radius: 12px;
            text-align: center;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
        }
        
        .stat-number {
            font-size: 2rem;
            font-weight: 800;
            color: #3b82f6;
            display: block;
        }
        
        .stat-label {
            color: #6b7280;
            font-size: 0.875rem;
            margin-top: 4px;
        }
        
        @media (max-width: 768px) {
            .lesson-navigation {
                flex-direction: column;
                gap: 16px;
            }
            
            .course-stats {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <!-- Theme Toggle -->
    <button id="theme-toggle" class="theme-toggle" aria-label="Toggle theme">
        <i class="fas fa-moon"></i>
        <i class="fas fa-sun"></i>
    </button>

    <!-- Header -->
    <header class="header">
        <nav class="nav">
            <div class="nav-container">
                <a href="index.html" class="logo">
                     <div class="logo-icon">
                         <img src="assets/logo_transparent.png" alt="ShareConnect Logo" class="logo-image">
                     </div>
                    <span>ShareConnect</span>
                </a>

                <div class="nav-menu">
                    <a href="index.html" class="nav-link">Home</a>
                    <a href="courses.html" class="nav-link active">Courses</a>
                    <a href="products.html" class="nav-link">Products</a>
                    <a href="manuals.html" class="nav-link">Manuals</a>
                    <a href="https://github.com/yourusername/ShareConnect" class="nav-link github">
                        <i class="fab fa-github"></i>
                        GitHub
                    </a>
                </div>

                <button class="mobile-menu-toggle" aria-label="Toggle menu">
                    <i class="fas fa-bars"></i>
                </button>
            </div>
        </nav>
    </header>

    <!-- Hero Section -->
    <section class="courses-hero">
        <div class="container">
            <h1>Master ShareConnect with Professional Video Courses</h1>
            <p>Comprehensive training for 20+ professional connectors, installation guides, and advanced features</p>
            
            <div class="course-stats">
                <div class="stat-card">
                    <span class="stat-number">2</span>
                    <div class="stat-label">Complete Courses</div>
                </div>
                <div class="stat-card">
                    <span class="stat-number">18</span>
                    <div class="stat-label">Total Minutes</div>
                </div>
                <div class="stat-card">
                    <span class="stat-number">20+</span>
                    <div class="stat-label">Connectors Covered</div>
                </div>
                <div class="stat-card">
                    <span class="stat-number">100%</span>
                    <div class="stat-label">Free Access</div>
                </div>
            </div>
        </div>
    </section>

    <!-- Course Player -->
    <section class="course-player-section">
        <div class="container">
            <div class="course-player">
                <video
                    id="course-video"
                    class="video-js vjs-default-skin vjs-big-play-centered"
                    controls
                    preload="auto"
                    width="100%"
                    height="auto"
                    poster="assets/courses/course-poster.jpg"
                    data-setup='{}'>
                    <source src="assets/courses/installation-setup-overview.mp4" type="video/mp4">
                    <track kind="captions" src="assets/courses/installation-setup-captions.vtt" srclang="en" label="English" default>
                    Your browser does not support the video tag.
                </video>
            </div>
            
            <div class="course-progress">
                <div class="progress-bar" id="course-progress-bar" style="width: 0%"></div>
            </div>
            
            <div class="course-info">
                <h2 id="course-title">Installation & Setup Course</h2>
                <p id="course-description">Complete guide to installing ShareConnect and setting up your first connectors. Perfect for beginners and professionals alike.</p>
                
                <div class="course-lessons" id="course-lessons">
                    <h3>Course Content</h3>
                    <div class="lesson-list">
                        <div class="lesson-item active" data-lesson="1" data-time="0:00">
                            <i class="fas fa-play-circle"></i>
                            <span>Lesson 1: ShareConnect Overview (10 min)</span>
                            <span class="lesson-duration">10:00</span>
                        </div>
                        <div class="lesson-item" data-lesson="2" data-time="10:00">
                            <i class="fas fa-play-circle"></i>
                            <span>Lesson 2: System Requirements (8 min)</span>
                            <span class="lesson-duration">8:00</span>
                        </div>
                        <div class="lesson-item" data-lesson="3" data-time="18:00">
                            <i class="fas fa-lock"></i>
                            <span>Lesson 3: Advanced Configuration (Coming Soon)</span>
                            <span class="lesson-duration">12:00</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="lesson-navigation">
                <button class="nav-button" id="prev-lesson" disabled>
                    <i class="fas fa-chevron-left"></i>
                    Previous Lesson
                </button>
                
                <div class="lesson-info">
                    <span id="current-lesson">Lesson 1 of 3</span>
                    <span id="lesson-progress">0% Complete</span>
                </div>
                
                <button class="nav-button" id="next-lesson">
                    Next Lesson
                    <i class="fas fa-chevron-right"></i>
                </button>
            </div>
        </div>
    </section>

    <!-- Transcript Section -->
    <section class="transcript-section">
        <div class="container">
            <h3>Video Transcript</h3>
            <div class="transcript-text" id="course-transcript">
                <p><strong>Welcome to the ShareConnect Installation and Setup course.</strong></p>
                <p>I'm excited to guide you through one of the most powerful media sharing ecosystems available today. ShareConnect consists of 20 specialized Android applications, each designed to connect with specific types of services.</p>
                <p>In this comprehensive course, you'll learn how to install, configure, and master all 20+ connectors, from torrent clients like qBittorrent and Transmission to media servers like Plex and Jellyfin.</p>
                <p>By the end of this course, you'll be able to share media seamlessly across your entire self-hosted ecosystem with just one tap.</p>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="footer-content">
                <div class="footer-section">
                    <h4>Learning</h4>
                    <ul>
                        <li><a href="courses.html">Video Courses</a></li>
                        <li><a href="manuals.html">User Manuals</a></li>
                        <li><a href="docs/api.html">API Documentation</a></li>
                        <li><a href="docs/troubleshooting.html">Troubleshooting</a></li>
                    </ul>
                </div>
                
                <div class="footer-section">
                    <h4>Products</h4>
                    <ul>
                        <li><a href="products.html#shareconnector">ShareConnector</a></li>
                        <li><a href="products.html#qbitconnect">qBitConnect</a></li>
                        <li><a href="products.html#plexconnect">PlexConnect</a></li>
                        <li><a href="products.html">All Products</a></li>
                    </ul>
                </div>
                
                <div class="footer-section">
                    <h4>Community</h4>
                    <ul>
                        <li><a href="https://github.com/yourusername/ShareConnect">GitHub</a></li>
                        <li><a href="https://discord.gg/shareconnect">Discord</a></li>
                        <li><a href="https://reddit.com/r/ShareConnect">Reddit</a></li>
                        <li><a href="blog.html">Blog</a></li>
                    </ul>
                </div>
            </div>
            
            <div class="footer-bottom">
                <div class="footer-brand">
                    <img src="assets/logo_transparent.png" alt="ShareConnect" class="footer-logo">
                    <span>ShareConnect</span>
                </div>
                <div class="footer-legal">
                    <p>&copy; 2025 ShareConnect. All rights reserved.</p>
                    <p>Professional media sharing platform with 20+ connectors</p>
                </div>
            </div>
        </div>
    </footer>

    <!-- Scripts -->
    <script src="https://vjs.zencdn.net/8.6.1/video.min.js"></script>
    <script src="js/course-player.js"></script>
    <script src="js/main-enhanced.js"></script>
    
    <script>
        // Initialize course player
        document.addEventListener('DOMContentLoaded', function() {
            initializeCoursePlayer();
        });
        
        function initializeCoursePlayer() {
            const player = videojs('course-video', {
                controls: true,
                autoplay: false,
                preload: 'auto',
                fluid: true,
                playbackRates: [0.5, 0.75, 1, 1.25, 1.5, 2],
                plugins: {
                    hotkeys: {
                        enableVolumeScroll: false
                    }
                }
            });
            
            // Track progress
            player.on('timeupdate', function() {
                const progress = (player.currentTime() / player.duration()) * 100;
                document.getElementById('course-progress-bar').style.width = progress + '%';
                
                // Save progress to localStorage
                localStorage.setItem('course-progress', progress);
            });
            
            // Restore previous progress
            const savedProgress = localStorage.getItem('course-progress');
            if (savedProgress) {
                const duration = player.duration();
                if (duration > 0) {
                    player.currentTime((savedProgress / 100) * duration);
                }
            }
            
            // Lesson navigation
            const lessons = document.querySelectorAll('.lesson-item');
            const prevButton = document.getElementById('prev-lesson');
            const nextButton = document.getElementById('next-lesson');
            let currentLesson = 1;
            
            function updateLesson(lessonNumber) {
                lessons.forEach((lesson, index) => {
                    lesson.classList.toggle('active', index + 1 === lessonNumber);
                });
                
                currentLesson = lessonNumber;
                
                // Update button states
                prevButton.disabled = currentLesson === 1;
                nextButton.disabled = currentLesson === lessons.length;
                
                // Update lesson info
                document.getElementById('current-lesson').textContent = `Lesson ${currentLesson} of ${lessons.length}`;
                
                // Update video source based on lesson
                const lessonData = lessons[lessonNumber - 1].dataset;
                if (lessonData.time) {
                    player.currentTime(parseTime(lessonData.time));
                }
            }
            
            // Event listeners
            prevButton.addEventListener('click', () => {
                if (currentLesson > 1) {
                    updateLesson(currentLesson - 1);
                }
            });
            
            nextButton.addEventListener('click', () => {
                if (currentLesson < lessons.length) {
                    updateLesson(currentLesson + 1);
                }
            });
            
            lessons.forEach((lesson, index) => {
                lesson.addEventListener('click', () => {
                    updateLesson(index + 1);
                });
            });
            
            // Initialize
            updateLesson(1);
        }
        
        function parseTime(timeString) {
            const parts = timeString.split(':');
            return parseInt(parts[0]) * 60 + parseInt(parts[1]);
        }
    </script>
</body>
</html>
EOF

    echo -e "${GREEN}  ✅ Course pages created${NC}"
}

# Function to create video player JavaScript
create_video_player_js() {
    local js_file="${WEBSITE_DIR}/js/course-player.js"
    
    echo -e "${BLUE}Creating video player JavaScript...${NC}"
    
    cat > "$js_file" << 'EOF'
/**
 * ShareConnect Course Player
 * Professional video course platform with progress tracking
 */

class CoursePlayer {
    constructor() {
        this.player = null;
        this.currentCourse = 'installation-setup';
        this.currentLesson = 1;
        this.progress = {};
        this.courses = {
            'installation-setup': {
                title: 'Installation & Setup Course',
                description: 'Complete guide to installing ShareConnect and setting up your first connectors.',
                duration: '18 minutes',
                lessons: [
                    {
                        id: 1,
                        title: 'ShareConnect Overview',
                        duration: '10:00',
                        description: 'Introduction to the ShareConnect ecosystem and 20+ professional connectors',
                        videoSrc: 'assets/courses/installation-setup-lesson-01.mp4',
                        transcript: 'Welcome to the ShareConnect Installation and Setup course. I\'m excited to guide you through one of the most powerful media sharing ecosystems available today. ShareConnect consists of 20 specialized Android applications, each designed to connect with specific types of services...'
                    },
                    {
                        id: 2,
                        title: 'System Requirements',
                        duration: '8:00',
                        description: 'Learn about system requirements, dependencies, and preparation for all 20+ connectors',
                        videoSrc: 'assets/courses/installation-setup-lesson-02.mp4',
                        transcript: 'Before we begin installing ShareConnect, let\'s review the system requirements for all 20+ connectors. Each connector has specific requirements, but there are some common prerequisites...'
                    }
                ]
            },
            'advanced-features': {
                title: 'Advanced Features Course',
                description: 'Deep dive into advanced ShareConnect features and enterprise deployment.',
                duration: '45 minutes',
                lessons: [
                    {
                        id: 1,
                        title: 'Multi-Server Management',
                        duration: '15:00',
                        description: 'Managing multiple servers across 20+ connectors efficiently',
                        videoSrc: 'assets/courses/advanced-features-lesson-01.mp4',
                        transcript: 'Advanced ShareConnect features include comprehensive multi-server management across all 20+ connectors...'
                    },
                    {
                        id: 2,
                        title: 'Automation & Workflows',
                        duration: '20:00',
                        description: 'Setting up automated workflows and advanced configurations',
                        videoSrc: 'assets/courses/advanced-features-lesson-02.mp4',
                        transcript: 'Automation is key to maximizing productivity with ShareConnect\'s 20+ connectors...'
                    },
                    {
                        id: 3,
                        title: 'Enterprise Deployment',
                        duration: '10:00',
                        description: 'Deploying ShareConnect in enterprise environments',
                        videoSrc: 'assets/courses/advanced-features-lesson-03.mp4',
                        transcript: 'Enterprise deployment of ShareConnect requires careful planning for all 20+ connectors...'
                    }
                ]
            }
        };
        
        this.init();
    }
    
    init() {
        this.setupVideoPlayer();
        this.setupProgressTracking();
        this.setupLessonNavigation();
        this.setupCourseNavigation();
        this.setupKeyboardShortcuts();
        this.setupAccessibilityFeatures();
        this.loadProgress();
    }
    
    setupVideoPlayer() {
        const videoElement = document.getElementById('course-video');
        if (!videoElement) return;
        
        this.player = videojs(videoElement, {
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
        
        // Event listeners
        this.player.on('timeupdate', () => this.updateProgress());
        this.player.on('ended', () => this.handleLessonComplete());
        this.player.on('loadedmetadata', () => this.restoreLessonProgress());
        
        // Custom quality selector
        this.setupQualitySelector();
    }
    
    setupQualitySelector() {
        // Add quality selector to player
        const qualityButton = this.player.controlBar.addChild('button', {
            className: 'vjs-quality-button',
            controlText: 'Quality',
            clickHandler: () => this.toggleQualityMenu()
        });
        
        qualityButton.addClass('vjs-quality-button');
        qualityButton.el().innerHTML = '<i class="fas fa-cog"></i>';
    }
    
    setupProgressTracking() {
        // Progress bar update
        this.player.on('timeupdate', () => {
            const currentTime = this.player.currentTime();
            const duration = this.player.duration();
            
            if (duration > 0) {
                const progress = (currentTime / duration) * 100;
                this.updateProgressBar(progress);
                this.saveLessonProgress(progress);
            }
        });
    }
    
    setupLessonNavigation() {
        const prevButton = document.getElementById('prev-lesson');
        const nextButton = document.getElementById('next-lesson');
        const lessonItems = document.querySelectorAll('.lesson-item');
        
        prevButton?.addEventListener('click', () => this.previousLesson());
        nextButton?.addEventListener('click', () => this.nextLesson());
        
        lessonItems.forEach((item, index) => {
            item.addEventListener('click', () => this.goToLesson(index + 1));
        });
    }
    
    setupCourseNavigation() {
        // Course tabs or dropdown
        const courseTabs = document.querySelectorAll('.course-tab');
        courseTabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const courseId = tab.dataset.course;
                this.switchCourse(courseId);
            });
        });
    }
    
    setupKeyboardShortcuts() {
        document.addEventListener('keydown', (e) => {
            if (!this.player) return;
            
            switch(e.key) {
                case ' ':
                    e.preventDefault();
                    this.togglePlayPause();
                    break;
                case 'ArrowLeft':
                    e.preventDefault();
                    this.seek(-10);
                    break;
                case 'ArrowRight':
                    e.preventDefault();
                    this.seek(10);
                    break;
                case 'ArrowUp':
                    e.preventDefault();
                    this.changeVolume(0.1);
                    break;
                case 'ArrowDown':
                    e.preventDefault();
                    this.changeVolume(-0.1);
                    break;
                case 'f':
                    e.preventDefault();
                    this.toggleFullscreen();
                    break;
                case 'n':
                    e.preventDefault();
                    this.nextLesson();
                    break;
                case 'p':
                    e.preventDefault();
                    this.previousLesson();
                    break;
            }
        });
    }
    
    setupAccessibilityFeatures() {
        // Screen reader announcements
        this.setupScreenReaderSupport();
        
        // Focus management
        this.setupFocusManagement();
        
        // Captions support
        this.setupCaptions();
    }
    
    // Navigation methods
    goToLesson(lessonNumber) {
        if (lessonNumber < 1 || lessonNumber > this.getCurrentCourse().lessons.length) {
            return;
        }
        
        this.currentLesson = lessonNumber;
        const lesson = this.getCurrentLesson();
        
        this.loadLesson(lesson);
        this.updateLessonUI();
        this.saveProgress();
        
        // Announce to screen readers
        this.announceToScreenReader(`Loading lesson ${lessonNumber}: ${lesson.title}`);
    }
    
    nextLesson() {
        if (this.currentLesson < this.getCurrentCourse().lessons.length) {
            this.goToLesson(this.currentLesson + 1);
        }
    }
    
    previousLesson() {
        if (this.currentLesson > 1) {
            this.goToLesson(this.currentLesson - 1);
        }
    }
    
    switchCourse(courseId) {
        if (!this.courses[courseId]) return;
        
        this.currentCourse = courseId;
        this.currentLesson = 1;
        
        this.loadCourse(this.courses[courseId]);
        this.saveProgress();
        
        // Update URL without reload
        this.updateCourseURL(courseId);
    }
    
    // UI update methods
    updateLessonUI() {
        const lessonItems = document.querySelectorAll('.lesson-item');
        const currentLessonElement = document.getElementById('current-lesson');
        const lessonProgressElement = document.getElementById('lesson-progress');
        
        // Update lesson list
        lessonItems.forEach((item, index) => {
            item.classList.toggle('active', index + 1 === this.currentLesson);
        });
        
        // Update lesson info
        if (currentLessonElement) {
            currentLessonElement.textContent = `Lesson ${this.currentLesson} of ${this.getCurrentCourse().lessons.length}`;
        }
        
        // Update navigation buttons
        this.updateNavigationButtons();
        
        // Update progress
        const lessonProgress = this.getLessonProgress();
        if (lessonProgressElement) {
            lessonProgressElement.textContent = `${Math.round(lessonProgress)}% Complete`;
        }
    }
    
    updateNavigationButtons() {
        const prevButton = document.getElementById('prev-lesson');
        const nextButton = document.getElementById('next-lesson');
        
        if (prevButton) {
            prevButton.disabled = this.currentLesson === 1;
        }
        
        if (nextButton) {
            nextButton.disabled = this.currentLesson === this.getCurrentCourse().lessons.length;
        }
    }
    
    updateProgressBar(progress) {
        const progressBar = document.getElementById('course-progress-bar');
        if (progressBar) {
            progressBar.style.width = progress + '%';
        }
    }
    
    // Player control methods
    togglePlayPause() {
        if (this.player.paused()) {
            this.player.play();
        } else {
            this.player.pause();
        }
    }
    
    seek(seconds) {
        const currentTime = this.player.currentTime();
        const newTime = Math.max(0, Math.min(currentTime + seconds, this.player.duration()));
        this.player.currentTime(newTime);
    }
    
    changeVolume(delta) {
        const currentVolume = this.player.volume();
        const newVolume = Math.max(0, Math.min(currentVolume + delta, 1));
        this.player.volume(newVolume);
    }
    
    toggleFullscreen() {
        if (this.player.isFullscreen()) {
            this.player.exitFullscreen();
        } else {
            this.player.requestFullscreen();
        }
    }
    
    // Progress management
    updateProgress() {
        const currentTime = this.player.currentTime();
        const duration = this.player.duration();
        
        if (duration > 0) {
            const progress = (currentTime / duration) * 100;
            this.updateProgressBar(progress);
        }
    }
    
    saveLessonProgress(progress) {
        const courseProgress = this.progress[this.currentCourse] || {};
        courseProgress[this.currentLesson] = {
            progress: progress,
            timestamp: new Date().toISOString()
        };
        
        this.progress[this.currentCourse] = courseProgress;
        this.saveProgress();
    }
    
    restoreLessonProgress() {
        const courseProgress = this.progress[this.currentCourse] || {};
        const lessonProgress = courseProgress[this.currentLesson];
        
        if (lessonProgress && lessonProgress.progress > 0) {
            const duration = this.player.duration();
            const resumeTime = (lessonProgress.progress / 100) * duration;
            
            // Only restore if significant progress (more than 5%)
            if (lessonProgress.progress > 5) {
                this.player.currentTime(resumeTime);
                this.showResumeNotification(lessonProgress.progress);
            }
        }
    }
    
    handleLessonComplete() {
        this.markLessonComplete();
        this.showCompletionNotification();
        
        // Auto-advance to next lesson after delay
        if (this.currentLesson < this.getCurrentCourse().lessons.length) {
            setTimeout(() => {
                this.nextLesson();
            }, 3000);
        } else {
            this.handleCourseComplete();
        }
    }
    
    markLessonComplete() {
        const courseProgress = this.progress[this.currentCourse] || {};
        courseProgress[this.currentLesson] = {
            progress: 100,
            completed: true,
            completedAt: new Date().toISOString()
        };
        
        this.progress[this.currentCourse] = courseProgress;
        this.saveProgress();
    }
    
    handleCourseComplete() {
        this.showCourseCompletionNotification();
        
        // Update course completion in UI
        this.updateCourseCompletionUI();
        
        // Suggest next course
        this.suggestNextCourse();
    }
    
    // Data persistence
    saveProgress() {
        try {
            localStorage.setItem('shareconnect-course-progress', JSON.stringify(this.progress));
        } catch (error) {
            console.warn('Failed to save course progress:', error);
        }
    }
    
    loadProgress() {
        try {
            const saved = localStorage.getItem('shareconnect-course-progress');
            if (saved) {
                this.progress = JSON.parse(saved);
            }
        } catch (error) {
            console.warn('Failed to load course progress:', error);
        }
    }
    
    // Helper methods
    getCurrentCourse() {
        return this.courses[this.currentCourse];
    }
    
    getCurrentLesson() {
        const course = this.getCurrentCourse();
        return course.lessons[this.currentLesson - 1];
    }
    
    getLessonProgress() {
        const courseProgress = this.progress[this.currentCourse] || {};
        const lessonProgress = courseProgress[this.currentLesson];
        return lessonProgress ? lessonProgress.progress : 0;
    }
    
    loadLesson(lesson) {
        // Update video source
        this.player.src(lesson.videoSrc);
        
        // Update transcript
        this.updateTranscript(lesson.transcript);
        
        // Update title and description
        this.updateLessonInfo(lesson);
    }
    
    loadCourse(course) {
        // Update course title and description
        document.getElementById('course-title').textContent = course.title;
        document.getElementById('course-description').textContent = course.description;
        
        // Load first lesson
        this.loadLesson(course.lessons[0]);
        
        // Update lesson list
        this.updateLessonList(course.lessons);
        
        // Update UI
        this.updateLessonUI();
    }
    
    updateTranscript(transcript) {
        const transcriptElement = document.getElementById('course-transcript');
        if (transcriptElement) {
            transcriptElement.innerHTML = `<p>${transcript}</p>`;
        }
    }
    
    updateLessonInfo(lesson) {
        // Update lesson-specific information
        const titleElement = document.getElementById('lesson-title');
        const descriptionElement = document.getElementById('lesson-description');
        
        if (titleElement) titleElement.textContent = lesson.title;
        if (descriptionElement) descriptionElement.textContent = lesson.description;
    }
    
    updateLessonList(lessons) {
        const lessonList = document.querySelector('.lesson-list');
        if (!lessonList) return;
        
        lessonList.innerHTML = lessons.map((lesson, index) => `
            <div class="lesson-item ${index === 0 ? 'active' : ''}" data-lesson="${lesson.id}" data-time="${index * 600}">
                <i class="fas fa-play-circle"></i>
                <span>${lesson.title} (${lesson.duration})</span>
                <span class="lesson-duration">${lesson.duration}</span>
            </div>
        `).join('');
        
        // Re-attach event listeners
        this.setupLessonNavigation();
    }
    
    updateCourseURL(courseId) {
        const url = new URL(window.location);
        url.searchParams.set('course', courseId);
        window.history.pushState({ course: courseId }, '', url);
    }
    
    updateCourseCompletionUI() {
        // Update course card to show completion
        const courseCard = document.querySelector(`[data-course="${this.currentCourse}"]`);
        if (courseCard) {
            courseCard.classList.add('completed');
        }
    }
    
    suggestNextCourse() {
        const courses = Object.keys(this.courses);
        const currentIndex = courses.indexOf(this.currentCourse);
        
        if (currentIndex < courses.length - 1) {
            const nextCourse = courses[currentIndex + 1];
            this.showNextCourseSuggestion(nextCourse);
        }
    }
    
    // Notification methods
    showResumeNotification(progress) {
        this.showNotification(`Resuming from ${Math.round(progress)}%`, 'info');
    }
    
    showCompletionNotification() {
        this.showNotification('Lesson completed! Great job!', 'success');
    }
    
    showCourseCompletionNotification() {
        this.showNotification('Course completed! Congratulations!', 'success');
    }
    
    showNextCourseSuggestion(nextCourse) {
        const course = this.courses[nextCourse];
        this.showNotification(`Ready for "${course.title}"?`, 'info', [
            {
                text: 'Start Next Course',
                action: () => this.switchCourse(nextCourse)
            }
        ]);
    }
    
    showNotification(message, type = 'info', actions = []) {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas fa-${this.getNotificationIcon(type)}"></i>
                <span>${message}</span>
            </div>
            ${actions.length > 0 ? `
                <div class="notification-actions">
                    ${actions.map(action => `
                        <button class="notification-action" onclick="${action.action}">
                            ${action.text}
                        </button>
                    `).join('')}
                </div>
            ` : ''}
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
    
    // Accessibility
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
    
    setupFocusManagement() {
        // Ensure focus is managed properly during navigation
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Tab' && e.target.closest('.lesson-item')) {
                // Trap focus in lesson list
                this.trapFocusInLessonList(e);
            }
        });
    }
    
    setupCaptions() {
        // Ensure captions are available and properly configured
        const tracks = this.player.textTracks();
        if (tracks.length > 0) {
            // Enable captions by default
            tracks[0].mode = 'showing';
        }
    }
    
    trapFocusInLessonList(event) {
        const lessonItems = document.querySelectorAll('.lesson-item');
        const firstItem = lessonItems[0];
        const lastItem = lessonItems[lessonItems.length - 1];
        
        if (event.shiftKey) {
            if (document.activeElement === firstItem) {
                lastItem.focus();
                event.preventDefault();
            }
        } else {
            if (document.activeElement === lastItem) {
                firstItem.focus();
                event.preventDefault();
            }
        }
    }
}

// Initialize course player when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('course-video')) {
        window.coursePlayer = new CoursePlayer();
    }
});
EOF

    echo -e "${GREEN}  ✅ Video player JavaScript created${NC}"
}

# Function to create video assets directory structure
create_video_assets_structure() {
    echo -e "${BLUE}Creating video assets directory structure...${NC}"
    
    # Create directories for video content
    mkdir -p "${WEBSITE_DIR}/assets/courses"
    mkdir -p "${WEBSITE_DIR}/assets/videos"
    mkdir -p "${WEBSITE_DIR}/assets/courses/captions"
    
    # Create placeholder files for video content
    touch "${WEBSITE_DIR}/assets/courses/.gitkeep"
    touch "${WEBSITE_DIR}/assets/videos/.gitkeep"
    touch "${WEBSITE_DIR}/assets/courses/captions/.gitkeep"
    
    echo -e "${GREEN}  ✅ Video assets structure created${NC}"
}

# Function to create video integration documentation
create_video_integration_docs() {
    echo -e "${BLUE}Creating video integration documentation...${NC}"
    
    cat > "${WEBSITE_DIR}/docs/video-integration.md" << 'EOF'
# ShareConnect Video Course Integration Guide

## Overview
This guide covers the integration of professional video courses into the ShareConnect website, including video hosting, player setup, and course management.

## Video Requirements

### Technical Specifications
- **Format**: MP4 (H.264 codec)
- **Resolution**: 1920x1080 (1080p) recommended
- **Frame Rate**: 30fps
- **Bitrate**: 5-8 Mbps for 1080p
- **Audio**: AAC, 48kHz, 192kbps

### File Naming Convention
```
{course-id}-lesson-{lesson-number}.mp4
{course-id}-overview.mp4
{course-id}-captions.vtt
```

Example:
```
installation-setup-lesson-01.mp4
installation-setup-overview.mp4
installation-setup-captions.vtt
```

## Directory Structure
```
assets/
├── courses/
│   ├── {course-id}-lesson-01.mp4
│   ├── {course-id}-lesson-02.mp4
│   ├── {course-id}-overview.mp4
│   └── captions/
│       ├── {course-id}-captions.vtt
│       └── {course-id}-lesson-01-captions.vtt
└── videos/
    ├── shareconnect-overview.mp4
    └── shareconnect-overview-captions.vtt
```

## Video Player Features

### Core Functionality
- Responsive video player with Video.js
- Multiple playback speeds (0.5x - 2x)
- Progress tracking and resume functionality
- Keyboard shortcuts
- Fullscreen support
- Closed captions

### Advanced Features
- Lesson navigation
- Course progress tracking
- Transcript display
- Quality selection
- Mobile optimization
- Accessibility support

## Implementation

### 1. Video Files
Place your video files in the appropriate directories:
```bash
# Course videos
assets/courses/installation-setup-lesson-01.mp4
assets/courses/installation-setup-lesson-02.mp4

# Overview video
assets/videos/shareconnect-overview.mp4
```

### 2. Captions
Create WebVTT caption files:
```vtt
WEBVTT

00:00:00.000 --> 00:00:05.000
Welcome to the ShareConnect Installation and Setup course.

00:00:05.000 --> 00:00:10.000
I'm excited to guide you through one of the most powerful media sharing ecosystems.
```

### 3. Course Configuration
Update course data in `js/course-player.js`:
```javascript
this.courses = {
    'installation-setup': {
        title: 'Installation & Setup Course',
        description: 'Complete guide to installing ShareConnect...',
        lessons: [
            {
                id: 1,
                title: 'ShareConnect Overview',
                duration: '10:00',
                videoSrc: 'assets/courses/installation-setup-lesson-01.mp4',
                transcript: 'Welcome to the ShareConnect...'
            }
        ]
    }
};
```

## Customization

### Styling
Customize video player appearance in `styles-enhanced-additions.css`:
```css
.video-js {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.vjs-big-play-button {
    background: rgba(59, 130, 246, 0.9) !important;
    border-radius: 50% !important;
}
```

### Player Configuration
Modify player settings in the initialization:
```javascript
this.player = videojs(videoElement, {
    controls: true,
    autoplay: false,
    preload: 'auto',
    fluid: true,
    playbackRates: [0.5, 0.75, 1, 1.25, 1.5, 2]
});
```

## Testing

### Video Playback
1. Test video loading and playback
2. Verify all playback speeds work
3. Check fullscreen functionality
4. Test keyboard shortcuts
5. Verify captions display correctly

### Progress Tracking
1. Play video and verify progress bar updates
2. Navigate away and return to test resume functionality
3. Complete a lesson and verify completion tracking
4. Switch between lessons and courses

### Responsive Design
1. Test on desktop (various screen sizes)
2. Test on tablet devices
3. Test on mobile phones
4. Verify touch controls work properly

### Accessibility
1. Test with screen readers
2. Verify keyboard navigation works
3. Check color contrast ratios
4. Test with reduced motion preferences

## Analytics Integration

### Progress Tracking
Track user progress for analytics:
```javascript
// Save progress to localStorage
localStorage.setItem('shareconnect-course-progress', JSON.stringify(progress));

// Track completion events
gtag('event', 'course_lesson_complete', {
    'course_id': courseId,
    'lesson_id': lessonId,
    'progress': progress
});
```

### Video Analytics
Monitor video engagement:
```javascript
this.player.on('timeupdate', () => {
    const progress = (this.player.currentTime() / this.player.duration()) * 100;
    
    // Send analytics event
    analytics.track('Video Progress', {
        course: this.currentCourse,
        lesson: this.currentLesson,
        progress: progress
    });
});
```

## Performance Optimization

### Video Compression
- Use modern codecs (H.264, H.265)
- Optimize bitrate for web delivery
- Create multiple quality versions
- Enable adaptive streaming if possible

### Loading Optimization
- Lazy load video players
- Preload critical video content
- Use CDN for video delivery
- Implement progressive download

## Troubleshooting

### Common Issues
1. **Video not loading**: Check file paths and formats
2. **Captions not displaying**: Verify WebVTT syntax
3. **Progress not saving**: Check localStorage permissions
4. **Mobile playback issues**: Ensure proper encoding

### Browser Compatibility
- Test across major browsers (Chrome, Firefox, Safari, Edge)
- Verify mobile browser support
- Check for codec compatibility issues

## Future Enhancements

### Planned Features
- Adaptive bitrate streaming
- Offline video support
- Advanced analytics dashboard
- Social sharing of progress
- Gamification elements
- Multi-language support
- Advanced search functionality

### Technical Improvements
- Implement HLS/DASH streaming
- Add video thumbnail generation
- Create video processing pipeline
- Develop mobile app integration
EOF

    echo -e "${GREEN}  ✅ Video integration documentation created${NC}"
}

# Function to create placeholder video content
create_placeholder_videos() {
    echo -e "${BLUE}Creating placeholder video content...${NC}"
    
    # Create placeholder video info files
    cat > "${WEBSITE_DIR}/assets/courses/video-content-info.md" << 'EOF'
# Video Content Information

## Required Video Files

### Installation & Setup Course
1. **installation-setup-lesson-01.mp4** (10 minutes)
   - ShareConnect ecosystem overview
   - 20+ connector introduction
   - Use case demonstrations

2. **installation-setup-lesson-02.mp4** (8 minutes)
   - System requirements for all 20+ connectors
   - Installation process
   - Initial configuration

3. **installation-setup-overview.mp4** (2.5 minutes)
   - Course introduction and preview
   - What students will learn

### Advanced Features Course (Coming Soon)
1. **advanced-features-lesson-01.mp4** (15 minutes)
   - Multi-server management
   - Advanced configuration

2. **advanced-features-lesson-02.mp4** (20 minutes)
   - Automation and workflows
   - Enterprise features

3. **advanced-features-lesson-03.mp4** (10 minutes)
   - Enterprise deployment
   - Scaling considerations

### Overview Video
1. **shareconnect-overview.mp4** (2.5 minutes)
   - Homepage hero video
   - Professional introduction
   - Ecosystem showcase

## Caption Files
Each video should have corresponding WebVTT caption files for accessibility.

## Production Notes
- Use professional recording equipment
- Ensure consistent audio quality
- Include B-roll footage of app interfaces
- Add professional transitions and graphics
- Include call-to-action elements

## Upload Instructions
1. Convert videos to MP4 format with H.264 codec
2. Create WebVTT caption files
3. Optimize for web delivery (multiple bitrates if possible)
4. Upload to appropriate directories:
   - Course videos: `assets/courses/`
   - Overview video: `assets/videos/`
   - Captions: `assets/courses/captions/`

## Video Hosting Alternatives
If self-hosting is not desired, consider:
- YouTube (with unlisted videos)
- Vimeo (professional hosting)
- AWS S3 with CloudFront
- Other CDN solutions
EOF

    echo -e "${GREEN}  ✅ Placeholder video content info created${NC}"
}

# Main execution
echo -e "${BLUE}Starting video course integration process...${NC}"

# Create necessary directories
mkdir -p "${WEBSITE_DIR}/scripts"
mkdir -p "${WEBSITE_DIR}/docs"

# Execute functions
create_course_pages
create_video_player_js
create_video_assets_structure
create_video_integration_docs
create_placeholder_videos

# Summary
echo -e "${GREEN}=======================================================================${NC}"
echo -e "${GREEN}✅ Video course integration completed successfully!${NC}"
echo -e "${GREEN}=======================================================================${NC}"
echo ""
echo "📊 Summary of created content:"
echo "  • Professional course platform with Video.js integration"
echo "  • Interactive lesson navigation and progress tracking"
echo "  • Comprehensive video player with accessibility features"
echo "  • Course management system with multiple courses support"
echo "  • Documentation for video integration and production"
echo "  • Placeholder content and production guidelines"
echo ""
echo "🎬 Files created:"
echo "  • ${WEBSITE_DIR}/courses.html - Main course platform"
echo "  • ${WEBSITE_DIR}/js/course-player.js - Video player functionality"
echo "  • ${WEBSITE_DIR}/docs/video-integration.md - Integration guide"
echo "  • ${WEBSITE_DIR}/assets/courses/ - Video content directory"
echo "  • ${WEBSITE_DIR}/assets/courses/video-content-info.md - Production guide"
echo ""
echo "🎯 Next steps for video production:"
echo "  1. Record professional video content using OBS setup"
echo "  2. Create WebVTT caption files for accessibility"
echo "  3. Optimize videos for web delivery"
echo "  4. Upload videos to appropriate directories"
echo "  5. Test video playback across devices and browsers"
echo ""
echo "💡 Professional video production pipeline is ready!"
echo "   Use the OBS configuration from VideoProduction/setup/setup-obs.sh"
echo "   Follow the teleprompter scripts in VideoProduction/courses/"
echo ""
echo -e "${BLUE}🚀 Phase 5 Video Integration is now complete and ready for content!${NC}"