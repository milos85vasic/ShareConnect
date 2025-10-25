/* ShareConnect Website - Interactive JavaScript */

// ============================================
// GLOBAL STATE
// ============================================
const state = {
    scrollPosition: 0,
    isMenuOpen: false,
    activeCategory: 'all'
};

// ============================================
// DOM ELEMENTS
// ============================================
const elements = {
    navbar: document.getElementById('navbar'),
    navMenu: document.getElementById('nav-menu'),
    hamburger: document.getElementById('hamburger'),
    navLinks: document.querySelectorAll('.nav-link'),
    tabButtons: document.querySelectorAll('.tab-btn'),
    appCards: document.querySelectorAll('.app-card'),
    revealElements: document.querySelectorAll('.reveal, .reveal-left, .reveal-right, .reveal-scale')
};

// ============================================
// NAVIGATION
// ============================================

// Toggle mobile menu
function toggleMenu() {
    state.isMenuOpen = !state.isMenuOpen;
    elements.navMenu.classList.toggle('active');
    elements.hamburger.classList.toggle('active');

    // Prevent body scroll when menu is open
    document.body.style.overflow = state.isMenuOpen ? 'hidden' : '';
}

// Close menu when clicking a link
function closeMenuOnLinkClick() {
    if (state.isMenuOpen) {
        toggleMenu();
    }
}

// Handle navbar scroll behavior
function handleNavbarScroll() {
    const currentScroll = window.pageYOffset;

    if (currentScroll > 100) {
        elements.navbar.classList.add('scrolled');
    } else {
        elements.navbar.classList.remove('scrolled');
    }

    state.scrollPosition = currentScroll;
}

// Smooth scroll to section
function smoothScrollTo(target) {
    const element = document.querySelector(target);
    if (element) {
        const offsetTop = element.offsetTop - 80; // Account for fixed navbar
        window.scrollTo({
            top: offsetTop,
            behavior: 'smooth'
        });
    }
}

// ============================================
// SCROLL REVEAL ANIMATIONS
// ============================================
function revealOnScroll() {
    const windowHeight = window.innerHeight;
    const revealPoint = 150;

    elements.revealElements.forEach(element => {
        const elementTop = element.getBoundingClientRect().top;

        if (elementTop < windowHeight - revealPoint) {
            element.classList.add('active');
        }
    });
}

// ============================================
// APP FILTERING
// ============================================
function filterApps(category) {
    state.activeCategory = category;

    // Update active tab button
    elements.tabButtons.forEach(btn => {
        btn.classList.remove('active');
        if (btn.dataset.category === category) {
            btn.classList.add('active');
        }
    });

    // Filter app cards
    elements.appCards.forEach(card => {
        const cardCategory = card.dataset.category;

        if (category === 'all' || cardCategory === category) {
            card.style.display = 'block';
            // Trigger animation
            card.style.animation = 'none';
            setTimeout(() => {
                card.style.animation = '';
            }, 10);
        } else {
            card.style.display = 'none';
        }
    });
}

// ============================================
// PARALLAX EFFECTS
// ============================================
function handleParallax() {
    const scrolled = window.pageYOffset;
    const parallaxElements = document.querySelectorAll('.parallax');

    parallaxElements.forEach(element => {
        const speed = element.dataset.speed || 0.5;
        const yPos = -(scrolled * speed);
        element.style.transform = `translateY(${yPos}px)`;
    });
}

// ============================================
// INTERSECTION OBSERVER FOR LAZY LOADING
// ============================================
function initIntersectionObserver() {
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');

                // Lazy load images if they have data-src
                if (entry.target.dataset.src) {
                    entry.target.src = entry.target.dataset.src;
                }
            }
        });
    }, observerOptions);

    // Observe all elements with lazy-load class
    document.querySelectorAll('.lazy-load').forEach(el => {
        observer.observe(el);
    });
}

// ============================================
// THEME TOGGLE (if implemented)
// ============================================
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';

    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
}

// ============================================
// STATS COUNTER ANIMATION
// ============================================
function animateCounter(element, target, duration = 2000) {
    const start = 0;
    const increment = target / (duration / 16);
    let current = start;

    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            element.textContent = target;
            clearInterval(timer);
        } else {
            element.textContent = Math.floor(current);
        }
    }, 16);
}

function initCounters() {
    const counters = document.querySelectorAll('.stat-number');
    const observerOptions = {
        threshold: 0.5
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && !entry.target.classList.contains('counted')) {
                const target = parseInt(entry.target.textContent);
                entry.target.classList.add('counted');
                animateCounter(entry.target, target);
            }
        });
    }, observerOptions);

    counters.forEach(counter => observer.observe(counter));
}

// ============================================
// COPY TO CLIPBOARD
// ============================================
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showNotification('Copied to clipboard!', 'success');
    } catch (err) {
        console.error('Failed to copy:', err);
        showNotification('Failed to copy', 'error');
    }
}

// ============================================
// NOTIFICATIONS
// ============================================
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;

    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${type === 'success' ? '#14b8a6' : '#ef4444'};
        color: white;
        padding: 1rem 1.5rem;
        border-radius: 0.5rem;
        box-shadow: 0 10px 15px rgba(0, 0, 0, 0.3);
        z-index: 10000;
        animation: slideInRight 0.3s ease-out;
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'fadeOut 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// ============================================
// EXTERNAL LINKS
// ============================================
function initExternalLinks() {
    const externalLinks = document.querySelectorAll('a[href^="http"]');
    externalLinks.forEach(link => {
        if (!link.href.includes(window.location.hostname)) {
            link.setAttribute('target', '_blank');
            link.setAttribute('rel', 'noopener noreferrer');
        }
    });
}

// ============================================
// KEYBOARD NAVIGATION
// ============================================
function handleKeyboardNav(e) {
    // ESC key closes mobile menu
    if (e.key === 'Escape' && state.isMenuOpen) {
        toggleMenu();
    }

    // Ctrl/Cmd + K for search (if implemented)
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        // Open search modal
    }
}

// ============================================
// PERFORMANCE OPTIMIZATIONS
// ============================================

// Debounce function for performance
function debounce(func, wait = 20, immediate = false) {
    let timeout;
    return function() {
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

// Throttle function for performance
function throttle(func, limit = 20) {
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

// ============================================
// EVENT LISTENERS
// ============================================
function initEventListeners() {
    // Navigation
    if (elements.hamburger) {
        elements.hamburger.addEventListener('click', toggleMenu);
    }

    // Close menu on link click
    elements.navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = link.getAttribute('href');
            closeMenuOnLinkClick();
            smoothScrollTo(target);
        });
    });

    // App filtering
    elements.tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            filterApps(btn.dataset.category);
        });
    });

    // Scroll events (with throttling for performance)
    window.addEventListener('scroll', throttle(() => {
        handleNavbarScroll();
        revealOnScroll();
        // handleParallax(); // Uncomment if needed
    }, 20));

    // Keyboard navigation
    document.addEventListener('keydown', handleKeyboardNav);

    // Resize events (with debouncing)
    window.addEventListener('resize', debounce(() => {
        // Close mobile menu on resize to desktop
        if (window.innerWidth > 768 && state.isMenuOpen) {
            toggleMenu();
        }
    }, 250));
}

// ============================================
// PAGE VISIBILITY API
// ============================================
function handleVisibilityChange() {
    if (document.hidden) {
        // Pause animations or heavy operations
    } else {
        // Resume animations
    }
}

document.addEventListener('visibilitychange', handleVisibilityChange);

// ============================================
// INITIALIZATION
// ============================================
function init() {
    console.log('🔗 ShareConnect Website - Initializing...');

    // Check if essential elements exist
    if (!elements.navbar) {
        console.error('Essential elements not found');
        return;
    }

    // Initialize features
    initEventListeners();
    initExternalLinks();
    initIntersectionObserver();
    initCounters();

    // Initial calls
    handleNavbarScroll();
    revealOnScroll();

    // Load saved theme
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        document.documentElement.setAttribute('data-theme', savedTheme);
    }

    console.log('✅ ShareConnect Website - Ready!');
}

// ============================================
// START THE APP
// ============================================
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}

// ============================================
// EXPORTS (for module usage)
// ============================================
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        toggleMenu,
        filterApps,
        smoothScrollTo,
        copyToClipboard,
        showNotification
    };
}
