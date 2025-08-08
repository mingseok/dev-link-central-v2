<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Bootstrap CSS -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <!-- Popper.js -->
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/umd/popper.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 CSS and JS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/members/home.css">
    <title>프로젝트!</title>

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
                } catch (e) {
                    console.error("JWT 디코딩 오류:", e);
                }
            }
        });

        // 팔로워 리스트 페이지로 이동
        function followersView() {
            window.location.href = "/view/follow/followers";
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
<div class="note">
    <div class="menu-title">메뉴</div>
    <button onclick="logout()">로그아웃</button>
    <button onclick="followersView()">팔로워 리스트</button>
    <button onclick="profileView()">프로필</button>
    <button onclick="Article()">정보 게시판</button>
    <button onclick="feedView()">피드</button>
</div>
</body>
</html>
