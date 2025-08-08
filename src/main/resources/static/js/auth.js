class AuthManager {
    static TOKEN_KEY = 'jwt';
    
    static setToken(token) {
        if (token) {
            localStorage.setItem(this.TOKEN_KEY, token);
        }
    }
    
    static getToken() {
        return localStorage.getItem(this.TOKEN_KEY);
    }
    
    static removeToken() {
        localStorage.removeItem(this.TOKEN_KEY);
    }
    
    static isAuthenticated() {
        return !!this.getToken();
    }
    
    static getAuthHeaders() {
        const token = this.getToken();
        return token ? {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        } : {
            'Content-Type': 'application/json'
        };
    }
    
    static async makeAuthenticatedRequest(url, options = {}) {
        const headers = {
            ...this.getAuthHeaders(),
            ...(options.headers || {})
        };
        
        const config = {
            ...options,
            headers,
            credentials: 'include'
        };
        
        try {
            const response = await fetch(url, config);
            
            if (response.status === 401) {
                this.handleUnauthorized();
                throw new Error('인증이 필요합니다.');
            }
            
            return response;
        } catch (error) {
            console.error('API 요청 실패:', error);
            throw error;
        }
    }
    
    static handleUnauthorized() {
        this.removeToken();
        alert('로그인이 필요합니다.');
        window.location.href = '/login';
    }
}

// 전역에서 사용할 수 있도록 window 객체에 추가
window.AuthManager = AuthManager;
