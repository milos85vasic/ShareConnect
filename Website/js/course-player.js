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
