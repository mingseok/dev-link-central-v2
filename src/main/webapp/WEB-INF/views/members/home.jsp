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

        function deletePage() {
            window.location.href = "/view/member/delete-page";
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

    </script>
</head>
<body>
<div class="note">
    <div class="menu-title">메뉴</div>
    <button onclick="logout()">로그아웃</button>
    <button onclick="deletePage()">회원탈퇴</button>
    <button onclick="editProfile()">회원수정</button>
    <button onclick="profileView()">프로필 & 친구 목록</button>
    <button onclick="Article()">스터디 모집 게시판</button>
    <button onclick="StudyGroupView()">그룹 관리</button>
    <button onclick="groupFeedView()">그룹 피드 (메인)</button>
    <button onclick="myFeed()">나의 피드</button>
</div>
</body>
</html>
