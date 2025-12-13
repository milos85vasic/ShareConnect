// ShareConnect Marketing Analytics Tracking
// Enhanced tracking for campaign performance

// Course enrollment tracking
function trackCourseEnrollment(courseId, lessonId, userId) {
    gtag('event', 'course_enrollment', {
        'course_id': courseId,
        'lesson_id': lessonId,
        'user_id': userId,
        'enrollment_date': new Date().toISOString()
    });
    
    // Store in localStorage for persistence
    localStorage.setItem('course_progress_' + courseId, JSON.stringify({
        courseId: courseId,
        lessonId: lessonId,
        enrollmentDate: new Date().toISOString(),
        progress: 0
    }));
}

// Video engagement tracking
function trackVideoProgress(videoId, progress, duration, userId) {
    gtag('event', 'video_progress', {
        'video_id': videoId,
        'progress_percent': Math.round((progress / duration) * 100),
        'duration_seconds': duration,
        'completion_rate': progress / duration,
        'user_id': userId
    });
}

// Social sharing tracking
function trackSocialShare(platform, contentType, url) {
    gtag('event', 'social_share', {
        'platform': platform,
        'content_type': contentType,
        'share_url': url,
        'timestamp': new Date().toISOString()
    });
}

// Email engagement tracking
function trackEmailEngagement(emailType, action, userId) {
    gtag('event', 'email_engagement', {
        'email_type': emailType,
        'action': action,
        'user_id': userId,
        'timestamp': new Date().toISOString()
    });
}

// Conversion funnel tracking
function trackConversionStep(step, userId, value) {
    gtag('event', 'conversion_step', {
        'conversion_step': step,
        'user_id': userId,
        'step_value': value,
        'timestamp': new Date().toISOString()
    });
}
