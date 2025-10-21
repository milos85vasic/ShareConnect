// Smooth scrolling for navigation links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Header background on scroll
window.addEventListener('scroll', () => {
    const header = document.querySelector('.header');
    if (window.scrollY > 100) {
        header.style.background = 'rgba(26, 26, 26, 0.98)';
        header.style.backdropFilter = 'blur(20px)';
    } else {
        header.style.background = 'rgba(26, 26, 26, 0.95)';
        header.style.backdropFilter = 'blur(20px)';
    }
});

// Animate elements on scroll
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
document.addEventListener('DOMContentLoaded', () => {
    const animatedElements = document.querySelectorAll('.health-card, .badge-category, .app-card');
    
    animatedElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(el);
    });
});

// Real-time metrics simulation (for demo purposes)
function updateMetrics() {
    const metrics = document.querySelectorAll('.metric-value');
    metrics.forEach(metric => {
        const currentValue = metric.textContent;
        if (currentValue.includes('%')) {
            // Simulate small fluctuations in percentages
            const baseValue = parseInt(currentValue);
            const fluctuation = Math.random() * 2 - 1; // -1 to +1
            const newValue = Math.max(95, Math.min(100, baseValue + fluctuation));
            metric.textContent = `${Math.round(newValue)}%`;
        }
    });
}

// Update metrics every 30 seconds (for demo)
setInterval(updateMetrics, 30000);

// Add loading animation for health cards
function addLoadingAnimation() {
    const healthCards = document.querySelectorAll('.health-card');
    healthCards.forEach(card => {
        const loadingBar = document.createElement('div');
        loadingBar.className = 'loading-bar';
        loadingBar.style.cssText = `
            position: absolute;
            bottom: 0;
            left: 0;
            height: 3px;
            background: var(--primary);
            border-radius: 0 0 16px 16px;
            width: 0%;
            transition: width 2s ease;
        `;
        card.style.position = 'relative';
        card.appendChild(loadingBar);
        
        // Animate loading bar
        setTimeout(() => {
            loadingBar.style.width = '100%';
        }, 100);
    });
}

// Initialize loading animation
document.addEventListener('DOMContentLoaded', addLoadingAnimation);

// Add hover effects for interactive elements
document.querySelectorAll('.badge-item, .app-card').forEach(element => {
    element.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-2px)';
        this.style.boxShadow = '0 8px 25px rgba(0, 0, 0, 0.15)';
    });
    
    element.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0)';
        this.style.boxShadow = 'none';
    });
});

// Theme toggle functionality (optional)
function initThemeToggle() {
    const themeToggle = document.createElement('button');
    themeToggle.innerHTML = '<i class="fas fa-moon"></i>';
    themeToggle.className = 'theme-toggle';
    themeToggle.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: var(--gradient);
        border: none;
        border-radius: 50%;
        width: 50px;
        height: 50px;
        color: white;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.2rem;
        z-index: 1000;
        transition: all 0.3s ease;
    `;
    
    document.body.appendChild(themeToggle);
    
    themeToggle.addEventListener('click', () => {
        document.body.classList.toggle('light-theme');
        const icon = themeToggle.querySelector('i');
        if (document.body.classList.contains('light-theme')) {
            icon.className = 'fas fa-sun';
        } else {
            icon.className = 'fas fa-moon';
        }
    });
}

// Uncomment to enable theme toggle
// initThemeToggle();

// Performance monitoring
window.addEventListener('load', () => {
    const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
    console.log(`Page loaded in ${loadTime}ms`);
});

// Error handling for images
document.querySelectorAll('img').forEach(img => {
    img.addEventListener('error', function() {
        this.style.display = 'none';
    });
});