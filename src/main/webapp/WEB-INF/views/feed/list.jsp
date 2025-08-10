<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>피드</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/feed/list.css">
    <link rel="stylesheet" href="/css/feed-comment.css">
    <!-- SweetAlert2 CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
</head>
<body>
    <div class="container mt-4">
        <div class="header">
            <h1>📰 피드</h1>
            <div class="header-actions">
                <button onclick="createFeed()" class="btn btn-primary">
                    ✏️ 피드 작성
                </button>
                <button onclick="goHome()" class="btn btn-secondary">
                    🏠 홈으로
                </button>
            </div>
        </div>

        <div id="feeds-list">
            <div class="text-center">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">피드를 불러오는 중...</p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- SweetAlert2 JS -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Feed Comment Manager -->
    <script src="/js/feed-comment.js"></script>
    <script>
        const jwt = localStorage.getItem('jwt');
        let currentUserId = null;

        if (jwt) {
            try {
                const tokenPayload = JSON.parse(atob(jwt.split('.')[1]));
                currentUserId = tokenPayload.sub;
                // 전역 변수로 설정하여 FeedCommentManager에서 사용할 수 있도록 함
                window.jwt = jwt;
                window.currentUserId = currentUserId;
            } catch (e) {
                console.error("JWT 디코딩 오류:", e);
                alert("토큰이 유효하지 않습니다. 다시 로그인해주세요.");
                localStorage.removeItem('jwt');
                window.location.href = "/view/members/signin";
            }
        } else {
            alert("로그인이 필요합니다.");
            window.location.href = "/view/members/signin";
        }

        document.addEventListener('DOMContentLoaded', function() {
            loadFeeds();
        });

        function loadFeeds() {
            fetch('/api/v1/feeds', {
                headers: {
                    'Authorization': 'Bearer ' + jwt
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'SUCCESS') {
                    displayFeeds(data.data);
                } else {
                    throw new Error('피드 로딩 실패');
                }
            })
            .catch(error => {
                console.error('피드 로딩 실패:', error);
                document.getElementById('feeds-list').innerHTML = 
                    '<div class="alert alert-danger">피드를 불러오는데 실패했습니다.</div>';
            });
        }

        function displayFeeds(feeds) {
            const feedsList = document.getElementById('feeds-list');
            
            if (feeds.length === 0) {
                feedsList.innerHTML = 
                    '<div class="empty-state">' +
                        '<div style="font-size: 60px;">📝</div>' +
                        '<h3>아직 피드가 없습니다</h3>' +
                        '<p>첫 번째 피드를 작성해보세요!</p>' +
                    '</div>';
                return;
            }

            let feedsHtml = '';
            feeds.forEach(feed => {
                const isMyFeed = String(feed.writerId) === String(currentUserId);
                
                // 프로필 이미지
                let profileImageHtml = '';
                if (feed.writerProfileImageUrl && feed.writerProfileImageUrl !== '/images/default.png') {
                    profileImageHtml = '<img src="' + feed.writerProfileImageUrl + '" alt="프로필 이미지" class="profile-image">';
                } else {
                    profileImageHtml = '<img src="/images/default.png" alt="기본 프로필 이미지" class="profile-image">';
                }

                // 피드 이미지
                let feedImageHtml = '';
                if (feed.imageUrl) {
                    feedImageHtml = '<div class="feed-image"><img src="' + feed.imageUrl + '" alt="피드 이미지"></div>';
                }

                // 삭제 버튼
                let deleteButtonHtml = '';
                if (isMyFeed) {
                    deleteButtonHtml = '<button class="btn btn-outline-danger btn-sm" onclick="deleteFeed(' + feed.feedId + ')">🗑️ 삭제</button>';
                }

                // 좋아요 버튼
                const likeButtonClass = feed.liked ? 'btn-danger' : 'btn-outline-danger';
                const likeButtonText = feed.liked ? '❤️' : '🤍';

                feedsHtml += 
                    '<div class="feed-card" data-feed-id="' + feed.feedId + '">' +
                        '<div class="feed-header">' +
                            '<div class="author-profile" onclick="goToProfile(' + feed.writerId + ')" style="cursor: pointer;">' +
                                profileImageHtml +
                                '<div>' +
                                    '<h5 class="author-name">' + feed.writerNickname + '</h5>' +
                                    '<span class="feed-date">' + feed.createdAt + '</span>' +
                                '</div>' +
                            '</div>' +
                            '<div class="feed-actions">' +
                                deleteButtonHtml +
                            '</div>' +
                        '</div>' +
                        '<div class="feed-content">' +
                            '<p>' + feed.content + '</p>' +
                            feedImageHtml +
                        '</div>' +
                        '<div class="feed-footer">' +
                            '<div class="feed-stats">' +
                                '<div class="like-section">' +
                                    '<button class="btn ' + likeButtonClass + ' btn-sm like-btn" data-liked="' + feed.liked + '" onclick="likeFeed(' + feed.feedId + ')">' +
                                        likeButtonText +
                                    '</button>' +
                                    '<span class="like-count" id="like-count-' + feed.feedId + '">' + feed.likeCount + '</span>' +
                                '</div>' +
                                '<div class="comment-section">' +
                                    '<button class="btn btn-outline-secondary btn-sm comment-btn show-comments-btn" data-feed-id="' + feed.feedId + '">' +
                                        '💬 댓글 ' + feed.commentCount +
                                    '</button>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                        '<div class="comments-section" id="comments-' + feed.feedId + '" style="display: none;">' +
                            '<form id="commentForm-' + feed.feedId + '" class="comment-form">' +
                                '<div class="input-group">' +
                                    '<textarea name="content" class="form-control" placeholder="댓글을 입력하세요..." rows="2" maxlength="500" required></textarea>' +
                                    '<button type="submit" class="btn btn-primary">작성</button>' +
                                '</div>' +
                            '</form>' +
                            '<div id="commentsContainer-' + feed.feedId + '" class="comments-list">' +
                                '<div class="text-center text-muted">댓글을 불러오는 중...</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>';
            });

            feedsList.innerHTML = feedsHtml;
        }

        function createFeed() {
            window.location.href = '/view/feeds/create';
        }

        function deleteFeed(feedId) {
            if (confirm('피드를 삭제하시겠습니까?')) {
                fetch('/api/v1/feeds/' + feedId, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': 'Bearer ' + jwt
                    }
                })
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'SUCCESS') {
                        alert('피드가 삭제되었습니다.');
                        loadFeeds();
                    } else {
                        alert('피드 삭제에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('피드 삭제 실패:', error);
                    alert('피드 삭제에 실패했습니다.');
                });
            }
        }

        function likeFeed(feedId) {
            // 즉시 UI 업데이트를 위해 버튼과 카운트 요소를 미리 찾기
            const likeBtn = document.querySelector('[data-feed-id="' + feedId + '"] .like-section .like-btn');
            const likeCount = document.getElementById('like-count-' + feedId);
            
            if (!likeBtn || !likeCount) {
                console.error('좋아요 버튼 또는 카운트 요소를 찾을 수 없습니다.');
                return;
            }

            // 중복 클릭 방지
            if (likeBtn.disabled) {
                return;
            }
            likeBtn.disabled = true;

            // 현재 상태 저장 (롤백용)
            const originalClass = likeBtn.className;
            const originalText = likeBtn.textContent;
            const originalCount = parseInt(likeCount.textContent);
            const originalLiked = likeBtn.getAttribute('data-liked') === 'true';

            // 현재 좋아요 상태 확인 (data 속성 사용)
            const isCurrentlyLiked = originalLiked;
            
            console.log('현재 좋아요 상태 (data-liked):', isCurrentlyLiked);
            console.log('버튼 텍스트:', likeBtn.textContent);

            // 즉시 UI 업데이트 (낙관적 업데이트)
            if (isCurrentlyLiked) {
                // 좋아요 취소
                likeBtn.className = 'btn btn-outline-danger btn-sm like-btn';
                likeBtn.textContent = '🤍';
                likeBtn.setAttribute('data-liked', 'false');
                likeCount.textContent = originalCount - 1;
            } else {
                // 좋아요 추가
                likeBtn.className = 'btn btn-danger btn-sm like-btn';
                likeBtn.textContent = '❤️';
                likeBtn.setAttribute('data-liked', 'true');
                likeCount.textContent = originalCount + 1;
            }

            fetch('/api/v1/feeds/' + feedId + '/like', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + jwt
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'SUCCESS') {
                    // 서버 응답에 따라 정확한 상태로 업데이트
                    console.log('서버 응답:', data.data);
                    // API 응답에서 liked 또는 isLiked 확인
                    const serverLiked = data.data.isLiked !== undefined ? data.data.isLiked : data.data.liked;
                    
                    if (serverLiked) {
                        likeBtn.className = 'btn btn-danger btn-sm like-btn';
                        likeBtn.textContent = '❤️';
                        likeBtn.setAttribute('data-liked', 'true');
                        console.log('data-liked를 true로 설정');
                    } else {
                        likeBtn.className = 'btn btn-outline-danger btn-sm like-btn';
                        likeBtn.textContent = '🤍';
                        likeBtn.setAttribute('data-liked', 'false');
                        console.log('data-liked를 false로 설정');
                    }
                    likeCount.textContent = data.data.likeCount;
                    console.log('최종 data-liked 상태:', likeBtn.getAttribute('data-liked'));
                } else {
                    // 실패 시 원래 상태로 롤백
                    likeBtn.className = originalClass;
                    likeBtn.textContent = originalText;
                    likeBtn.setAttribute('data-liked', originalLiked.toString());
                    likeCount.textContent = originalCount;
                    alert('좋아요 처리에 실패했습니다.');
                }
            })
            .catch(error => {
                // 에러 시 원래 상태로 롤백
                likeBtn.className = originalClass;
                likeBtn.textContent = originalText;
                likeBtn.setAttribute('data-liked', originalLiked.toString());
                likeCount.textContent = originalCount;
                console.error('좋아요 처리 실패:', error);
                alert('좋아요 처리에 실패했습니다.');
            })
            .finally(() => {
                // 버튼 다시 활성화
                likeBtn.disabled = false;
            });
        }

        function goHome() {
            window.location.href = '/view/members/home';
        }

        function goToProfile(userId) {
            window.location.href = '/view/profile/' + userId;
        }
    </script>
</body>
</html>
