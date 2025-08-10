<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/members/home.css">
    <title>Dev Link Central - 홈</title>

    <script>
        function logout() {
            // JWT 토큰 삭제
            localStorage.removeItem('jwt');
            // 로그아웃 후 홈페이지로 리디렉션
            window.location.href = "/view/members/signin";
        }

        function editProfile() {
            window.location.href = "/view/member/edit-form";
        }

        // 게시판 링크
        function Article() {
            window.location.href = "/view/articles/paging";
        }

        function ArticlePaging() {
            const token = localStorage.getItem('jwt');
            if (!token) {
                alert("로그인이 필요합니다.");
                window.location.href = "/view/members/signin";
                return;
            }

            $.ajax({
                url: "/view/articles/save",
                type: "GET",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", "Bearer " + token);
                },
                success: function (response) {
                    // 받아온 JSP 페이지 HTML을 현재 body에 삽입
                    document.body.innerHTML = response;

                    // 폼 버튼 이벤트 바인딩 (동적으로 삽입된 버튼에 이벤트 등록)
                    $("#saveArticleBtn").on("click", function (e) {
                        e.preventDefault();

                        const formData = {
                            writer: $('input[name="writer"]').val(),
                            title: $('input[name="title"]').val(),
                            content: $('textarea[name="content"]').val()
                        };

                        $.ajax({
                            url: "/api/v1/articles",  // 실제 글 저장 URL
                            type: "POST",
                            contentType: "application/json",
                            data: JSON.stringify(formData),
                            beforeSend: function (xhr) {
                                xhr.setRequestHeader("Authorization", "Bearer " + token);
                            },
                            success: function () {
                                alert("글이 등록되었습니다.");
                                window.location.href = "/view/articles/paging";
                            },
                            error: function (xhr) {
                                console.error("글 등록 실패:", xhr.responseText);
                                alert("글 등록에 실패했습니다.");
                            }
                        });
                    });
                },
                error: function (xhr) {
                    console.error("페이지 접근 실패:", xhr.responseText);
                    alert("접근 권한이 없습니다. 다시 로그인해주세요.");
                    window.location.href = "/view/members/signin";
                }
            });
        }

        // 프로필 링크를 현재 사용자의 프로필로 동적 설정
        $(document).ready(function() {
            const jwt = localStorage.getItem('jwt');
            if (jwt) {
                try {
                    const tokenPayload = JSON.parse(atob(jwt.split('.')[1]));
                    const currentUserId = tokenPayload.sub;
                    $('#profile-link').attr('href', `/view/profile/${currentUserId}`);
                    
                    // 사용자 이름 표시
                    if (tokenPayload.name) {
                        $('#userName').text(tokenPayload.name);
                    }
                } catch (e) {
                    console.error("JWT 디코딩 오류:", e);
                }
            }
        });

        // 팔로워 리스트 페이지로 이동
        function followersView() {
            window.location.href = "/view/follow/followers";
        }

        // 팔로잉 리스트 페이지로 이동
        function followingView() {
            window.location.href = "/view/follow/following";
        }

        // 피드 페이지로 이동
        function feedView() {
            window.location.href = "/view/feeds";
        }

        // 내 프로필 페이지로 이동
        function profileView() {
            const jwt = localStorage.getItem('jwt');
            if (jwt) {
                try {
                    const tokenPayload = JSON.parse(atob(jwt.split('.')[1]));
                    const currentUserId = tokenPayload.sub;
                    window.location.href = "/view/profile/" + currentUserId;
                } catch (e) {
                    console.error("JWT 디코딩 오류:", e);
                    alert("프로필 페이지로 이동할 수 없습니다.");
                }
            } else {
                alert("로그인이 필요합니다.");
                window.location.href = "/view/members/signin";
            }
        }
    </script>
</head>
<body>
    <div class="home-container">
        <div class="home-left">
            <div class="welcome-section">
                <i class="fas fa-home welcome-icon"></i>
                <h1 class="welcome-title">Welcome Back!</h1>
                <p class="welcome-subtitle">안녕하세요, <span id="userName">개발자</span>님</p>
            </div>
            <div class="stats-section">
                <div class="stat-item">
                    <i class="fas fa-code"></i>
                    <div class="stat-content">
                        <h3>프로젝트</h3>
                        <p>개발 여정을 함께해요</p>
                    </div>
                </div>
                <div class="stat-item">
                    <i class="fas fa-users"></i>
                    <div class="stat-content">
                        <h3>커뮤니티</h3>
                        <p>개발자들과 소통하세요</p>
                    </div>
                </div>
                <div class="stat-item">
                    <i class="fas fa-chart-line"></i>
                    <div class="stat-content">
                        <h3>성장</h3>
                        <p>지속적인 학습과 발전</p>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="home-right">
            <div class="dashboard-container">
                <div class="dashboard-header">
                    <h2 class="dashboard-title">대시보드</h2>
                    <p class="dashboard-subtitle">원하는 메뉴를 선택하세요</p>
                </div>
                
                <div class="menu-grid">
                    <button onclick="profileView()" class="menu-item">
                        <i class="fas fa-user"></i>
                        <span>내 프로필</span>
                    </button>

                    <button onclick="followingView()" class="menu-item">
                        <i class="fas fa-user-plus"></i>
                        <span>팔로워 리스트</span>
                    </button>

                    <button onclick="Article()" class="menu-item">
                        <i class="fas fa-newspaper"></i>
                        <span>정보 게시판</span>
                    </button>

                    <button onclick="feedView()" class="menu-item">
                        <i class="fas fa-stream"></i>
                        <span>피드</span>
                    </button>
                </div>
                
                <div class="dashboard-footer">
                    <button onclick="logout()" class="logout-btn">
                        <i class="fas fa-sign-out-alt"></i>
                        로그아웃
                    </button>
                </div>
            </div>
        </div>
    </div>
</body>
</html>