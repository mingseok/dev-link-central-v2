<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dev Link Central - 프로필</title>
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/profile/detail.css">

    <script>
        let currentProfile = {};
        
        document.addEventListener('DOMContentLoaded', function() {
            // JWT 토큰 확인
            const token = getAuthToken();
            console.log('JWT Token:', token ? 'exists' : 'missing');
            
            if (!token) {
                alert('로그인이 필요합니다.');
                window.location.href = '/view/members/signin';
                return;
            }
            
            loadProfile();
        });

        function loadProfile() {
            const memberId = getCurrentMemberId();
            const token = getAuthToken();
            
            console.log('Loading profile for member ID:', memberId);
            console.log('API URL:', '/api/v1/profile/' + memberId);
            
            if (!memberId || memberId === 'undefined') {
                console.error('Invalid member ID:', memberId);
                alert('잘못된 사용자 ID입니다.');
                return;
            }
            
            fetch('/api/v1/profile/' + memberId, {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(response => {
                console.log('Response status:', response.status);
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log('Profile data:', data);
                
                if (data.status === 'SUCCESS') {
                    currentProfile = data.data;
                    displayProfile(currentProfile);
                    updateFollowButton();
                } else {
                    console.error('프로필 로드 실패:', data.message ? data.message : 'Unknown error');
                    alert('프로필을 불러올 수 없습니다: ' + (data.message ? data.message : 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('프로필 로드 에러:', error);
                alert('프로필을 불러오는 중 오류가 발생했습니다: ' + error.message);
            });
        }

        function displayProfile(profile) {
            console.log('Displaying profile data:', profile);
            
            // 기본값 설정
            const profileData = {
                nickname: profile.nickname ? profile.nickname : '닉네임 없음',
                bio: profile.bio ? profile.bio : '',
                followersCount: profile.followersCount ? profile.followersCount : 0,
                followingsCount: profile.followingsCount ? profile.followingsCount : 0,
                joinedAt: profile.joinedAt ? profile.joinedAt : new Date().toISOString(),
                imageUrl: profile.imageUrl ? profile.imageUrl : '/images/default.png'
            };

            console.log('Profile image URL from server:', profile.imageUrl);
            console.log('Final profile image URL:', profileData.imageUrl);
            console.log('Follow status from server:', profile.isFollowing);

            // DOM 업데이트
            document.getElementById('nickname').textContent = profileData.nickname;
            document.getElementById('bio').value = profileData.bio;
            document.getElementById('followersCount').textContent = profileData.followersCount;
            document.getElementById('followingsCount').textContent = profileData.followingsCount;
            document.getElementById('profileImage').src = profileData.imageUrl;
            
            // 팔로우 버튼 상태 설정 (ProfileResponse의 isFollowing 사용)
            setFollowButtonState(profile.isFollowing);
            
            // 가입일 포맷팅
            const joinDate = new Date(profileData.joinedAt);
            document.getElementById('joinedAt').textContent = 
                '가입일: ' + joinDate.toLocaleDateString('ko-KR');
        }

        function getCurrentMemberId() {
            // URL에서 프로필 ID 추출
            const pathSegments = window.location.pathname.split('/');
            return pathSegments[pathSegments.length - 1];
        }

        function getAuthToken() {
            return localStorage.getItem('jwt');
        }

        function getCurrentUserId() {
            const token = getAuthToken();
            if (token) {
                try {
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    return payload.sub;
                } catch (e) {
                    console.error('JWT 디코딩 오류:', e);
                }
            }
            return null;
        }

        function updateFollowButton() {
            const currentUserId = getCurrentUserId();
            const profileUserId = getCurrentMemberId();
            const followBtn = document.getElementById('followBtn');
            
            if (currentUserId === profileUserId) {
                followBtn.style.display = 'none';
                return;
            }
            
            // ProfileResponse의 isFollowing을 사용하므로 별도 API 호출 불필요
        }

        function setFollowButtonState(isFollowing) {
            const followBtn = document.getElementById('followBtn');
            if (isFollowing) {
                followBtn.textContent = '언팔로우';
                followBtn.classList.add('following');
            } else {
                followBtn.textContent = '팔로우';
                followBtn.classList.remove('following');
            }
        }

        function toggleFollow() {
            const token = getAuthToken();
            const targetUserId = getCurrentMemberId();
            const followBtn = document.getElementById('followBtn');
            const isFollowing = followBtn.classList.contains('following');
            
            if (isFollowing) {
                // 언팔로우
                fetch("/api/v1/follows/" + targetUserId, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.status === 'SUCCESS') {
                        setFollowButtonState(false);
                        updateFollowerCounts();
                    } else {
                        alert('언팔로우에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('언팔로우 오류:', error);
                    alert('언팔로우 중 오류가 발생했습니다.');
                });
            } else {
                // 팔로우
                fetch("/api/v1/follows", {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + token,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ followeeId: parseInt(targetUserId) })
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.status === 'SUCCESS') {
                        setFollowButtonState(true);
                        updateFollowerCounts();
                    } else {
                        alert('팔로우에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('팔로우 오류:', error);
                    alert('팔로우 중 오류가 발생했습니다.');
                });
            }
        }

        function updateProfile() {
            const bio = document.getElementById('bio').value;
            const token = getAuthToken();
            
            fetch("/api/v1/profile", {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ bio: bio })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.status === 'SUCCESS') {
                    alert('프로필이 성공적으로 업데이트되었습니다.');
                    // 전체 프로필을 다시 로드하지 않고, 현재 자기소개 값만 유지
                    // 이미지는 그대로 유지됨
                } else {
                    alert('프로필 업데이트에 실패했습니다: ' + (data.message ? data.message : 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('프로필 업데이트 오류:', error);
                alert('프로필 업데이트 중 오류가 발생했습니다: ' + error.message);
            });
        }

        function uploadProfileImage(input) {
            if (input.files && input.files[0]) {
                const formData = new FormData();
                formData.append('file', input.files[0]);  // 서버에서 기대하는 'file' 파라미터명 사용
                
                const token = getAuthToken();
                
                fetch("/api/v1/profile/image", {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + token
                    },
                    body: formData
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.status === 'SUCCESS') {
                        // imageUrl 키로 이미지 URL 가져오기
                        const imageUrl = data.data.imageUrl;
                        document.getElementById('profileImage').src = imageUrl;
                        alert('프로필 이미지가 성공적으로 업데이트되었습니다.');
                    } else {
                        alert('프로필 이미지 업데이트에 실패했습니다: ' + (data.message ? data.message : 'Unknown error'));
                    }
                })
                .catch(error => {
                    console.error('프로필 이미지 업로드 오류:', error);
                    alert('프로필 이미지 업로드 중 오류가 발생했습니다: ' + error.message);
                });
            }
        }

        function updateFollowerCounts() {
            const memberId = getCurrentMemberId();
            const token = getAuthToken();
            
            if (!memberId || memberId === 'undefined') {
                console.error('Invalid member ID for follower count update');
                return;
            }
            
            fetch('/api/v1/profile/' + memberId, {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.status === 'SUCCESS') {
                    // 팔로워 수와 팔로우 상태만 업데이트하고 다른 정보는 유지
                    const profile = data.data;
                    document.getElementById('followersCount').textContent = profile.followersCount ? profile.followersCount : 0;
                    document.getElementById('followingsCount').textContent = profile.followingsCount ? profile.followingsCount : 0;
                    
                    // 팔로우 상태도 함께 업데이트
                    setFollowButtonState(profile.isFollowing);
                    
                    // 이미지와 자기소개는 건드리지 않음
                } else {
                    console.error('팔로워 수 업데이트 실패:', data.message);
                }
            })
            .catch(error => {
                console.error('팔로워 수 업데이트 오류:', error);
            });
        }

        function goBack() {
            window.history.back();
        }
    </script>
</head>
<body>
    <div class="profile-container">
        <div class="profile-left">
            <div class="profile-info-section">
                <div class="profile-image-container">
                    <img id="profileImage" 
                         src="/images/default.png" 
                         alt="프로필 이미지" 
                         class="profile-image">
                    <button type="button" 
                            class="image-upload-btn"
                            onclick="document.getElementById('profileImageInput').click()"
                            title="프로필 이미지 변경">
                        <i class="fas fa-camera"></i>
                    </button>
                    <input type="file" 
                           id="profileImageInput" 
                           accept="image/*" 
                           style="display: none;"
                           onchange="uploadProfileImage(this)">
                </div>
                
                <div class="profile-details">
                    <h1 id="nickname">닉네임</h1>
                    <p id="joinedAt" class="join-date">가입일: </p>
                    
                    <div class="profile-stats">
                        <div class="stat-item">
                            <span class="stat-number" id="followersCount">0</span>
                            <span class="stat-label">팔로워</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-number" id="followingsCount">0</span>
                            <span class="stat-label">팔로잉</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="bio-section">
                <label for="bio" class="bio-label">자기소개</label>
                <textarea id="bio" 
                          class="bio-textarea"
                          placeholder="자기소개를 입력하세요..."
                          rows="4"></textarea>
            </div>
        </div>
        
        <div class="profile-right">
            <div class="content-container">
                <div class="content-header">
                    <h2 class="content-title">프로필 관리</h2>
                    <div class="action-buttons">
                        <button id="followBtn" class="btn btn-follow" onclick="toggleFollow()">
                            <i class="fas fa-user-plus"></i>
                            팔로우
                        </button>
                        <button type="button" class="btn btn-edit" onclick="updateProfile()">
                            <i class="fas fa-edit"></i>
                            프로필 수정
                        </button>
                        <button type="button" class="btn btn-back" onclick="goBack()">
                            <i class="fas fa-arrow-left"></i>
                            뒤로가기
                        </button>
                    </div>
                </div>
                
                <div class="profile-info-card">
                    <div class="info-item">
                        <i class="fas fa-info-circle"></i>
                        <div class="info-content">
                            <h3>프로필 정보</h3>
                            <p>개인 정보를 확인하고 수정할 수 있습니다.</p>
                        </div>
                    </div>
                    <div class="info-item">
                        <i class="fas fa-users"></i>
                        <div class="info-content">
                            <h3>팔로워 관리</h3>
                            <p>팔로워와 팔로잉 관계를 관리할 수 있습니다.</p>
                        </div>
                    </div>
                    <div class="info-item">
                        <i class="fas fa-shield-alt"></i>
                        <div class="info-content">
                            <h3>개인정보 보호</h3>
                            <p>안전하게 개인정보가 보호됩니다.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>