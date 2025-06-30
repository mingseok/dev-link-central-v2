<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
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
    <link rel="stylesheet" href="/css/articles/save.css">

    <script>
        function home() {
            window.history.back();
        }
    </script>

    <script>
        $(document).ready(function () {
            const token = localStorage.getItem('jwt');

            // 닉네임 불러오기
            $.ajax({
                url: "/api/v1/members/self",
                type: "GET",
                headers: {
                    'Authorization': 'Bearer ' + token
                },
                success: function (res) {
                    $('#writer').val(res.data.nickname);
                },
                error: function () {
                    alert('실패');
                }
            });

            // 글 등록
            $("#saveArticleBtn").on("click", function (e) {
                e.preventDefault();

                const formData = {
                    writer: $('#writer').val(),
                    title: $('#title').val(),
                    content: $('#content').val()
                };

                $.ajax({
                    url: "/api/v1/articles",
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(formData),
                    headers: {
                        'Authorization': 'Bearer ' + token
                    },
                    success: function (response) {
                        console.log('성공:', response);
                        alert('글이 성공적으로 작성되었습니다.');
                        window.location.href = "/api/v1/view/articles/paging?page=0&sort=id,desc";
                    },
                    error: function (xhr) {
                        Swal.fire('오류', '글 작성 실패: ' + xhr.responseText, 'error');
                    }
                });
            });
        });
    </script>

</head>
<body>
<div class="form-container">
    <div class="form-title">스터디 모집 글 작성</div>
    <form id="articleForm" action="/api/v1/articles" method="post">
        <label for="writer">작성자:</label>
        <input type="text" id="writer" name="writer" readonly><br>

        <label for="title">스터디 제목:</label>
        <input type="text" id="title" name="title"><br>

        <label for="content">스터디 상세 내용 작성:</label>
        <textarea id="content" name="content" cols="30" rows="10"></textarea><br>

        <div class="button-container">
            <button type="button" onclick="home()">이전으로</button>
            <button type="button" id="saveArticleBtn">등록하기</button>
        </div>
    </form>
</div>
</body>
</html>
