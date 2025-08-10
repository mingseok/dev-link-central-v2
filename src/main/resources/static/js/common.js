/**
 * 공통 JavaScript 유틸리티 함수들
 */

// 마우스 추적 효과
document.addEventListener('DOMContentLoaded', function() {
    // 마우스 위치 추적하여 배경 효과 적용
    document.addEventListener('mousemove', function(e) {
        const mouseX = (e.clientX / window.innerWidth) * 100;
        const mouseY = (e.clientY / window.innerHeight) * 100;
        
        document.documentElement.style.setProperty('--mouse-x', mouseX + '%');
        document.documentElement.style.setProperty('--mouse-y', mouseY + '%');
    });

    // 인터랙티브 배경 효과
    const interactiveBgs = document.querySelectorAll('.interactive-bg');
    interactiveBgs.forEach(bg => {
        bg.addEventListener('mousemove', function(e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            
            const after = this.querySelector('::after') || this;
            after.style.left = x + 'px';
            after.style.top = y + 'px';
        });
    });

    // 페이지 로드 애니메이션
    addPageLoadAnimation();
    
    // 스크롤 애니메이션
    addScrollAnimations();
    
    // 버튼 클릭 효과
    addButtonEffects();
    
    // 카드 호버 효과
    addCardEffects();
});

/**
 * 페이지 로드 애니메이션 추가
 */
function addPageLoadAnimation() {
    const animatedElements = document.querySelectorAll('.fade-in-up, .card, .btn');
    
    animatedElements.forEach((element, index) => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(30px)';
        
        setTimeout(() => {
            element.style.transition = 'all 0.8s cubic-bezier(0.4, 0, 0.2, 1)';
            element.style.opacity = '1';
            element.style.transform = 'translateY(0)';
        }, index * 100);
    });
}

/**
 * 스크롤 애니메이션 추가
 */
function addScrollAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                entry.target.classList.add('animated');
            }
        });
    }, observerOptions);

    // 스크롤 애니메이션 대상 요소들
    const scrollElements = document.querySelectorAll('.scroll-animate');
    scrollElements.forEach(element => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(30px)';
        element.style.transition = 'all 0.8s cubic-bezier(0.4, 0, 0.2, 1)';
        observer.observe(element);
    });
}

/**
 * 버튼 클릭 효과 추가
 */
function addButtonEffects() {
    const buttons = document.querySelectorAll('.btn');
    
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            // 리플 효과
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.classList.add('ripple');
            
            this.appendChild(ripple);
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
}

/**
 * 카드 호버 효과 추가
 */
function addCardEffects() {
    const cards = document.querySelectorAll('.card');
    
    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px) scale(1.02)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
}

/**
 * 부드러운 스크롤 함수
 */
function smoothScrollTo(element) {
    element.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
    });
}

/**
 * 토스트 알림 표시
 */
function showToast(message, type = 'info', duration = 3000) {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-content">
            <span class="toast-icon">${getToastIcon(type)}</span>
            <span class="toast-message">${message}</span>
            <button class="toast-close">&times;</button>
        </div>
    `;
    
    // 토스트 컨테이너가 없으면 생성
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    
    container.appendChild(toast);
    
    // 애니메이션
    setTimeout(() => toast.classList.add('show'), 100);
    
    // 자동 제거
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, duration);
    
    // 닫기 버튼
    toast.querySelector('.toast-close').addEventListener('click', () => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    });
}

/**
 * 토스트 아이콘 반환
 */
function getToastIcon(type) {
    const icons = {
        success: '✅',
        error: '❌',
        warning: '⚠️',
        info: 'ℹ️'
    };
    return icons[type] || icons.info;
}

/**
 * 로딩 스피너 표시/숨김
 */
function showLoading(element) {
    const spinner = document.createElement('div');
    spinner.className = 'loading-overlay';
    spinner.innerHTML = '<div class="spinner"></div>';
    
    if (element) {
        element.style.position = 'relative';
        element.appendChild(spinner);
    } else {
        document.body.appendChild(spinner);
    }
    
    return spinner;
}

function hideLoading(spinner) {
    if (spinner && spinner.parentNode) {
        spinner.remove();
    }
}

/**
 * 폼 검증 유틸리티
 */
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], textarea[required], select[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            showFieldError(input, '이 필드는 필수입니다.');
            isValid = false;
        } else {
            clearFieldError(input);
        }
    });
    
    return isValid;
}

function showFieldError(field, message) {
    clearFieldError(field);
    
    field.classList.add('error');
    
    const errorElement = document.createElement('div');
    errorElement.className = 'field-error';
    errorElement.textContent = message;
    
    field.parentNode.appendChild(errorElement);
}

function clearFieldError(field) {
    field.classList.remove('error');
    
    const errorElement = field.parentNode.querySelector('.field-error');
    if (errorElement) {
        errorElement.remove();
    }
}

/**
 * 디바운스 함수
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * 스로틀 함수
 */
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

/**
 * 이미지 지연 로딩
 */
function initLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');
    
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.classList.remove('lazy');
                observer.unobserve(img);
            }
        });
    });
    
    images.forEach(img => imageObserver.observe(img));
}

/**
 * 클립보드에 텍스트 복사
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showToast('클립보드에 복사되었습니다!', 'success');
        return true;
    } catch (err) {
        console.error('클립보드 복사 실패:', err);
        showToast('복사에 실패했습니다.', 'error');
        return false;
    }
}

/**
 * 날짜 포맷팅
 */
function formatDate(date, format = 'YYYY-MM-DD') {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    
    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes);
}

/**
 * 상대 시간 표시 (예: 3분 전, 1시간 전)
 */
function timeAgo(date) {
    const now = new Date();
    const diffInSeconds = Math.floor((now - new Date(date)) / 1000);
    
    const intervals = [
        { label: '년', seconds: 31536000 },
        { label: '달', seconds: 2592000 },
        { label: '일', seconds: 86400 },
        { label: '시간', seconds: 3600 },
        { label: '분', seconds: 60 },
        { label: '초', seconds: 1 }
    ];
    
    for (const interval of intervals) {
        const count = Math.floor(diffInSeconds / interval.seconds);
        if (count > 0) {
            return `${count}${interval.label} 전`;
        }
    }
    
    return '방금 전';
}

/**
 * 숫자 포맷팅 (천 단위 구분자)
 */
function formatNumber(num) {
    return new Intl.NumberFormat('ko-KR').format(num);
}

/**
 * 문자열 자르기 (말줄임표 추가)
 */
function truncateString(str, length) {
    if (str.length <= length) return str;
    return str.substring(0, length) + '...';
}

/**
 * 이메일 유효성 검사
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * 비밀번호 강도 검사
 */
function checkPasswordStrength(password) {
    let strength = 0;
    let feedback = [];
    
    if (password.length >= 8) strength++;
    else feedback.push('8자 이상이어야 합니다');
    
    if (/[a-z]/.test(password)) strength++;
    else feedback.push('소문자를 포함해야 합니다');
    
    if (/[A-Z]/.test(password)) strength++;
    else feedback.push('대문자를 포함해야 합니다');
    
    if (/[0-9]/.test(password)) strength++;
    else feedback.push('숫자를 포함해야 합니다');
    
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    else feedback.push('특수문자를 포함해야 합니다');
    
    const levels = ['매우 약함', '약함', '보통', '강함', '매우 강함'];
    
    return {
        score: strength,
        level: levels[strength] || levels[0],
        feedback: feedback
    };
}

/**
 * 파일 크기 포맷팅
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * 랜덤 색상 생성
 */
function getRandomColor() {
    const colors = [
        '#667eea', '#764ba2', '#ff6b6b', '#4ecdc4',
        '#45b7d1', '#96ceb4', '#ffeaa7', '#fab1a0',
        '#fd79a8', '#a29bfe', '#6c5ce7', '#74b9ff'
    ];
    return colors[Math.floor(Math.random() * colors.length)];
}

/**
 * 브라우저 지원 여부 확인
 */
function checkBrowserSupport() {
    const features = {
        localStorage: typeof Storage !== 'undefined',
        fetch: typeof fetch !== 'undefined',
        intersectionObserver: 'IntersectionObserver' in window,
        serviceWorker: 'serviceWorker' in navigator
    };
    
    return features;
}

// CSS 스타일 추가
const commonStyles = `
<style>
/* 리플 효과 */
.ripple {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.6);
    transform: scale(0);
    animation: ripple-animation 0.6s linear;
    pointer-events: none;
}

@keyframes ripple-animation {
    to {
        transform: scale(4);
        opacity: 0;
    }
}

/* 토스트 스타일 */
.toast-container {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 10000;
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.toast {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    padding: 16px 20px;
    box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
    border: 1px solid rgba(255, 255, 255, 0.18);
    transform: translateX(100%);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    max-width: 400px;
}

.toast.show {
    transform: translateX(0);
}

.toast-content {
    display: flex;
    align-items: center;
    gap: 12px;
}

.toast-icon {
    font-size: 18px;
    flex-shrink: 0;
}

.toast-message {
    flex: 1;
    color: #4a5568;
    font-weight: 600;
}

.toast-close {
    background: none;
    border: none;
    font-size: 20px;
    color: #718096;
    cursor: pointer;
    padding: 0;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.2s ease;
}

.toast-close:hover {
    background: rgba(113, 128, 150, 0.1);
    color: #4a5568;
}

.toast-success {
    border-left: 4px solid #43e97b;
}

.toast-error {
    border-left: 4px solid #ff416c;
}

.toast-warning {
    border-left: 4px solid #ffa726;
}

.toast-info {
    border-left: 4px solid #667eea;
}

/* 로딩 오버레이 */
.loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(5px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

/* 필드 에러 스타일 */
.field-error {
    color: #ff416c;
    font-size: 12px;
    margin-top: 5px;
    font-weight: 600;
    animation: fadeInUp 0.3s ease;
}

.form-control.error {
    border-color: #ff416c;
    box-shadow: 0 0 0 3px rgba(255, 65, 108, 0.1);
}

/* 지연 로딩 이미지 */
img.lazy {
    opacity: 0;
    transition: opacity 0.3s;
}

img.lazy.loaded {
    opacity: 1;
}
</style>
`;

// 스타일 추가
if (!document.querySelector('#common-styles')) {
    const styleElement = document.createElement('style');
    styleElement.id = 'common-styles';
    styleElement.innerHTML = commonStyles.replace(/<\/?style>/g, '');
    document.head.appendChild(styleElement);
}
