class FeedCommentManager {
    constructor(feedId) {
        this.feedId = feedId;
        this.currentUserId = this.getCurrentUserId();
        this.isSubmitting = false; // 중복 제출 방지 플래그
        
        if (typeof AuthManager === 'undefined' && typeof jwt !== 'undefined') {
            this.jwt = jwt;
        }
        
        this.initializeEventListeners();
        this.loadComments();
    }

    getCurrentUserId() {
        return typeof currentUserId !== 'undefined' ? currentUserId : 1;
    }

    getAuthHeaders() {
        if (typeof AuthManager !== 'undefined') {
            return AuthManager.getAuthHeaders();
        } else if (this.jwt) {
            return {
                'Authorization': `Bearer ${this.jwt}`,
                'Content-Type': 'application/json'
            };
        } else {
            return {
                'Content-Type': 'application/json'
            };
        }
    }

    async makeRequest(url, options = {}) {
        if (typeof AuthManager !== 'undefined') {
            return AuthManager.makeAuthenticatedRequest(url, options);
        } else {
            const headers = {
                ...this.getAuthHeaders(),
                ...(options.headers || {})
            };
            
            const config = {
                ...options,
                headers,
                credentials: 'include'
            };
            
            const response = await fetch(url, config);
            return response;
        }
    }

    initializeEventListeners() {
        // 특정 피드ID에만 적용되는 이벤트 리스너
        $(`#commentForm-${this.feedId}`).off('submit').on('submit', (e) => {
            this.handleCommentSubmit(e);
        });

        $(`#comments-${this.feedId}`).off('submit', '.reply-form').on('submit', '.reply-form', (e) => {
            this.handleReplySubmit(e);
        });

        $(`#comments-${this.feedId}`).off('click', '.reply-btn').on('click', '.reply-btn', (e) => {
            const commentId = $(e.target).data('comment-id');
            this.showReplyForm(commentId);
        });

        $(`#comments-${this.feedId}`).off('click', '.delete-comment-btn').on('click', '.delete-comment-btn', (e) => {
            const commentId = $(e.target).data('comment-id');
            this.deleteComment(commentId);
        });

        $(`#comments-${this.feedId}`).off('click', '.cancel-reply-btn').on('click', '.cancel-reply-btn', (e) => {
            const commentId = $(e.target).data('comment-id');
            this.hideReplyForm(commentId);
        });
    }

    async loadComments() {
        try {
            const response = await this.makeRequest("/api/v1/feeds/" + this.feedId + "/comments");
            const result = await response.json();
            
            if (result.status === 'SUCCESS') {
                this.renderComments(result.data);
            } else {
                console.error('댓글 로드 실패:', result.message);
            }
        } catch (error) {
            console.error('댓글 로드 중 오류:', error);
        }
    }

    async handleCommentSubmit(e) {
        e.preventDefault();
        e.stopPropagation();
        
        if (this.isSubmitting) return;
        const form = e.target;
        const content = $(form).find('textarea[name="content"]').val().trim();
        
        if (!content) {
            Swal.fire("알림", "댓글 내용을 입력해주세요.", "warning");
            return;
        }

        this.isSubmitting = true;

        try {
            const response = await this.makeRequest("/api/v1/feeds/" + this.feedId + "/comments", {
                method: 'POST',
                body: JSON.stringify({
                    content: content,
                    parentId: null
                })
            });

            const result = await response.json();
            
            if (result.status === 'SUCCESS') {
                $(form)[0].reset();
                this.loadComments();
                
                Swal.fire({
                    title: "댓글 작성 완료",
                    icon: "success",
                    timer: 1000,
                    showConfirmButton: false
                });
            } else {
                Swal.fire("오류", result.message || "댓글 작성에 실패했습니다.", "error");
            }
        } catch (error) {
            console.error('댓글 작성 중 오류:', error);
            Swal.fire("오류", "댓글 작성 중 오류가 발생했습니다.", "error");
        } finally {
            this.isSubmitting = false;
        }
    }

    async handleReplySubmit(e) {
        e.preventDefault();
        e.stopPropagation();
        
        if (this.isSubmitting) return;
        
        const form = e.target;
        const content = $(form).find('textarea[name="content"]').val().trim();
        const parentId = $(form).find('input[name="parentId"]').val();
        
        if (!content) {
            Swal.fire("알림", "답글 내용을 입력해주세요.", "warning");
            return;
        }

        this.isSubmitting = true;

        try {
            const response = await this.makeRequest("/api/v1/feeds/" + this.feedId + "/comments", {
                method: 'POST',
                body: JSON.stringify({
                    content: content,
                    parentId: parseInt(parentId)
                })
            });

            const result = await response.json();
            
            if (result.status === 'SUCCESS') {
                $(form)[0].reset();
                this.hideReplyForm(parentId);
                this.loadComments();
                
                Swal.fire({
                    title: "답글 작성 완료",
                    icon: "success",
                    timer: 1000,
                    showConfirmButton: false
                });
            } else {
                Swal.fire("오류", result.message || "답글 작성에 실패했습니다.", "error");
            }
        } catch (error) {
            console.error('답글 작성 중 오류:', error);
            Swal.fire("오류", "답글 작성 중 오류가 발생했습니다.", "error");
        } finally {
            this.isSubmitting = false;
        }
    }

    async deleteComment(commentId) {
        const result = await Swal.fire({
            title: '댓글을 삭제하시겠습니까?',
            text: "삭제된 댓글은 복구할 수 없습니다.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d',
            confirmButtonText: '삭제',
            cancelButtonText: '취소'
        });

        if (!result.isConfirmed) {
            return;
        }

        try {
            const response = await this.makeRequest("/api/v1/feeds/" + this.feedId + "/comments/" + commentId, {
                method: 'DELETE'
            });

            const apiResult = await response.json();
            
            if (apiResult.status === 'SUCCESS') {
                this.loadComments();
                
                Swal.fire({
                    title: "댓글 삭제 완료",
                    icon: "success",
                    timer: 1000,
                    showConfirmButton: false
                });
            } else {
                Swal.fire("오류", apiResult.message || "댓글 삭제에 실패했습니다.", "error");
            }
        } catch (error) {
            console.error('댓글 삭제 중 오류:', error);
            Swal.fire("오류", "댓글 삭제 중 오류가 발생했습니다.", "error");
        }
    }

    renderComments(comments) {
        const container = $(`#commentsContainer-${this.feedId}`);
        if (!container.length) return;

        if (comments.length === 0) {
            container.html('<div class="comments-empty">아직 댓글이 없습니다.<br>첫 번째 댓글을 작성해보세요!</div>');
            return;
        }

        // 최신 댓글이 위에 오도록 정렬
        const sortedComments = [...comments].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

        let html = '';
        sortedComments.forEach(comment => {
            html += this.renderComment(comment, false);
        });
        
        container.html(html);
        
        // 댓글 수 업데이트
        const count = this.countTotalComments(comments);
        $(`.show-comments-btn[data-feed-id="${this.feedId}"]`).text(`💬 댓글 ${count}`);
    }

    renderComment(comment, isReply = false) {
        const isMyComment = String(comment.writerId) === String(this.currentUserId);
        console.log("댓글 렌더링: ", comment);
        console.log("writerId:", comment.writerId, "currentUserId:", this.currentUserId);

        let html = `<div class="comment ${isReply ? 'reply' : ''}" data-comment-id="${comment.id}">
            <div class="comment-header">
                <span class="comment-author">${this.escapeHtml(comment.writer)}</span>
                <span class="comment-time">${this.formatTime(comment.createdAt)}</span>
            </div>
            <div class="comment-content">${this.escapeHtml(comment.content)}</div>
            <div class="comment-actions">`;
                
        // 답글이 아닌 경우에만 답글 버튼 표시 (1뎁스 제한)
        if (!isReply) {
            html += `<button type="button" class="reply-btn" data-comment-id="${comment.id}">💬 답글</button>`;
        }
        
        if (isMyComment) {
            html += `<button type="button" class="delete-comment-btn" data-comment-id="${comment.id}">🗑️ 삭제</button>`;
        }
        
        html += `</div>`;

        // 답글이 아닌 경우에만 답글 작성 폼 표시 (1뎁스 제한)
        if (!isReply) {
            html += `
            <div class="reply-form-container" id="replyForm-${comment.id}" style="display: none;">
                <form class="reply-form">
                    <input type="hidden" name="parentId" value="${comment.id}">
                    <textarea name="content" placeholder="답글을 입력하세요..." rows="2" required></textarea>
                    <div class="reply-actions">
                        <button type="submit">답글 작성</button>
                        <button type="button" class="cancel-reply-btn" data-comment-id="${comment.id}">취소</button>
                    </div>
                </form>
            </div>`;
        }
        
        // 1뎁스 답글만 표시 (답글의 답글은 표시하지 않음)
        if (comment.children && comment.children.length > 0 && !isReply) {
            html += '<div class="replies">';
            comment.children.forEach(child => {
                html += this.renderComment(child, true);
            });
            html += '</div>';
        }
        
        html += '</div>';
        return html;
    }

    showReplyForm(commentId) {
        $('.reply-form-container').hide();
        
        const replyForm = $(`#replyForm-${commentId}`);
        replyForm.show();
        replyForm.find('textarea').focus();
    }

    hideReplyForm(commentId) {
        const replyForm = $(`#replyForm-${commentId}`);
        replyForm.hide();
        replyForm.find('textarea').val('');
    }



    countTotalComments(comments) {
        let count = 0;
        comments.forEach(comment => {
            count++; // 원댓글 카운트
            if (comment.children) {
                count += comment.children.length; // 1뎁스 답글만 카운트
            }
        });
        return count;
    }

    getCurrentUserNickname() {
        if (typeof window.currentUserNickname !== 'undefined') {
            return window.currentUserNickname;
        }
        return 'testUser';
    }

    formatTime(dateTimeString) {
        const date = new Date(dateTimeString);
        const now = new Date();
        const diffMs = now - date;
        const diffMins = Math.floor(diffMs / 60000);
        
        if (diffMins < 1) return '방금 전';
        if (diffMins < 60) return `${diffMins}분 전`;
        if (diffMins < 1440) return `${Math.floor(diffMins / 60)}시간 전`;
        return `${Math.floor(diffMins / 1440)}일 전`;
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

window.feedCommentManagers = new Map();

$(document).ready(function() {
    $(document).off('click', '.show-comments-btn').on('click', '.show-comments-btn', function(e) {
        e.preventDefault();
        e.stopPropagation();
        
        const feedId = $(this).data('feed-id');
        const commentsSection = $(`#comments-${feedId}`);
        
        if (commentsSection.is(':visible')) {
            commentsSection.hide();
            $(this).text($(this).text().replace('숨기기', '보기'));
        } else {
            commentsSection.show();
            $(this).text($(this).text().replace('보기', '숨기기'));
            
            if (!window.feedCommentManagers.has(feedId)) {
                const manager = new FeedCommentManager(feedId);
                window.feedCommentManagers.set(feedId, manager);
            }
        }
    });
});
