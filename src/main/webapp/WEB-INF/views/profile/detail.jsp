<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로필</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .profile-image {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid #e9ecef;
        }
    </style>
</head>
<body>
    <div class="container mt-4">
        <div class="row">
            <div class="col-md-8 mx-auto">
                <div class="card">
                    <div class="card-body">
                        <div class="text-center mb-4">
                            <div class="position-relative d-inline-block">
                                <img id="profileImage" 
                                     src="/images/default.png" 
                                     alt="프로필 이미지" 
                                     class="profile-image">
                                <button type="button" 
                                        class="btn btn-sm btn-primary position-absolute bottom-0 end-0 rounded-circle"
                                        onclick="document.getElementById('profileImageInput').click()">
                                    📷
                                </button>
                            </div>
                            <input type="file" 
                                   id="profileImageInput" 
                                   accept="image/*" 
                                   style="display: none;"
                                   onchange="uploadProfileImage(this)">
                        </div>

                        <div class="text-center mb-3">
                            <h4 id="nickname" class="mb-1">닉네임</h4>
                            <p id="joinedAt" class="text-muted">가입일: </p>
                        </div>

                        <div class="row text-center mb-4">
                            <div class="col-4">
                                <h5 id="followersCount" class="mb-0">0</h5>
                                <small class="text-muted">팔로워</small>
                            </div>
                            <div class="col-4">
                                <h5 id="followingsCount" class="mb-0">0</h5>
                                <small class="text-muted">팔로잉</small>
                            </div>
                            <div class="col-4">
                                <button id="followBtn" class="btn btn-primary btn-sm" onclick="toggleFollow()">
                                    팔로우
                                </button>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="bio" class="form-label">자기소개</label>
                            <textarea id="bio" 
                                      class="form-control" 
                                      rows="3" 
                                      placeholder="자기소개를 입력하세요..."></textarea>
                        </div>

                        <div class="text-center">
                            <button type="button" class="btn btn-success" onclick="updateProfile()">
                                프로필 수정
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
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
            
            fetch('/api/v1/profile/' + memberId, {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(response => {
                console.log('Response status:', response.status);
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('Profile data received:', data);
                if (data.status === 'SUCCESS' && data.data) {
                    currentProfile = data.data;
                    updateProfileDisplay();
                } else {
                    console.error('API returned error or no data:', data);
                    alert('프로필 데이터가 없습니다.');
                }
            })
            .catch(error => {
                console.error('프로필 로드 실패:', error);
                alert('프로필을 불러오는데 실패했습니다: ' + error.message);
            });
        }

        function updateProfileDisplay() {
            console.log('Updating profile display with:', currentProfile);
            console.log('Profile data keys:', Object.keys(currentProfile));
            
            // 안전하게 데이터 접근
            const nickname = currentProfile.nickname || '닉네임';
            const joinedAt = currentProfile.joinedAt || '';
            const followersCount = currentProfile.followersCount || 0;
            const followingsCount = currentProfile.followingsCount || 0;
            const bio = currentProfile.bio || '';
            const imageUrl = currentProfile.imageUrl;
            
            console.log('Setting values:', {nickname, joinedAt, followersCount, followingsCount, bio, imageUrl});
            
            document.getElementById('nickname').textContent = nickname;
            document.getElementById('joinedAt').textContent = '가입일: ' + joinedAt;
            document.getElementById('followersCount').textContent = followersCount;
            document.getElementById('followingsCount').textContent = followingsCount;
            document.getElementById('bio').value = bio;
            
            if (imageUrl && imageUrl !== '/images/default.png') {
                document.getElementById('profileImage').src = imageUrl;
            }
            
            const followBtn = document.getElementById('followBtn');
            if (currentProfile.isFollowing) {
                followBtn.textContent = '언팔로우';
                followBtn.className = 'btn btn-outline-primary btn-sm';
            } else {
                followBtn.textContent = '팔로우';
                followBtn.className = 'btn btn-primary btn-sm';
            }
        }

        function uploadProfileImage(input) {
            if (input.files && input.files[0]) {
                const file = input.files[0];
                
                if (file.size > 10 * 1024 * 1024) {
                    alert('파일 크기는 10MB를 초과할 수 없습니다.');
                    return;
                }
                
                const formData = new FormData();
                formData.append('file', file);
                const token = getAuthToken();
                
                fetch('/api/v1/profile/image', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + token
                    },
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'SUCCESS') {
                        document.getElementById('profileImage').src = data.data.imageUrl;
                        alert('프로필 이미지가 업데이트되었습니다.');
                    } else {
                        alert('이미지 업로드에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('이미지 업로드 실패:', error);
                    alert('이미지 업로드에 실패했습니다.');
                });
            }
        }

        function updateProfile() {
            const bio = document.getElementById('bio').value;
            const token = getAuthToken();
            
            console.log('Updating profile with bio:', bio);
            
            fetch('/api/v1/profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify({
                    bio: bio
                })
            })
            .then(response => {
                console.log('Update response status:', response.status);
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('Update response data:', data);
                if (data.status === 'SUCCESS') {
                    alert('프로필이 수정되었습니다.');
                    loadProfile();
                } else {
                    console.error('Update failed:', data);
                    alert('프로필 수정에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('프로필 수정 실패:', error);
                alert('프로필 수정에 실패했습니다: ' + error.message);
            });
        }

        function toggleFollow() {
            console.log('팔로우 토글');
        }

        function getCurrentMemberId() {
            const pathParts = window.location.pathname.split('/');
            const memberId = pathParts[pathParts.length - 1];
            console.log('Current member ID:', memberId);
            return memberId;
        }

        function getAuthToken() {
            return localStorage.getItem('jwt') || '';
        }
    </script>
</body>
</html>
