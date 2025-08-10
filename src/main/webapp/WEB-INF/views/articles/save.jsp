<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- SweetAlert2 CSS and JS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/articles/save.css">

    <title>글 작성 - DevLink Central</title>

    <script>
        function goBack() {
            window.history.back();
        }

        $(document).ready(function () {
            const token = localStorage.getItem('jwt');

            if (!token) {
                alert("로그인이 필요합니다.");
                window.location.href = "/view/members/signin";
                return;
            }

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
                    Swal.fire({
                        icon: 'error',
                        title: '오류 발생',
                        text: '사용자 정보를 불러오는데 실패했습니다.',
                        confirmButtonColor: '#ff416c'
                    });
                }
            });

            // 글자 수 카운터
            $("#title").on('input', function() {
                const currentLength = $(this).val().length;
                const $counter = $("#title-count");
                $counter.text(currentLength + "/100");
                
                if (currentLength > 80) {
                    $counter.addClass("warning");
                } else {
                    $counter.removeClass("warning");
                }
                
                if (currentLength > 100) {
                    $counter.addClass("danger").removeClass("warning");
                } else {
                    $counter.removeClass("danger");
                }
            });

            $("#content").on('input', function() {
                const currentLength = $(this).val().length;
                const $counter = $("#content-count");
                $counter.text(currentLength + "/2000");
                
                if (currentLength > 1800) {
                    $counter.addClass("warning");
                } else {
                    $counter.removeClass("warning");
                }
                
                if (currentLength > 2000) {
                    $counter.addClass("danger").removeClass("warning");
                } else {
                    $counter.removeClass("danger");
                }
            });

            // 폼 검증
            function validateForm() {
                const title = $('#title').val().trim();
                const content = $('#content').val().trim();
                
                if (!title) {
                    Swal.fire({
                        icon: 'warning',
                        title: '제목을 입력해주세요',
                        text: '글 제목을 작성해주세요.',
                        confirmButtonColor: '#667eea'
                    });
                    return false;
                }
                
                if (title.length > 100) {
                    Swal.fire({
                        icon: 'warning',
                        title: '제목이 너무 깁니다',
                        text: '제목은 100자를 초과할 수 없습니다.',
                        confirmButtonColor: '#667eea'
                    });
                    return false;
                }
                
                if (!content) {
                    Swal.fire({
                        icon: 'warning',
                        title: '내용을 입력해주세요',
                        text: '글 내용을 작성해주세요.',
                        confirmButtonColor: '#667eea'
                    });
                    return false;
                }
                
                if (content.length > 2000) {
                    Swal.fire({
                        icon: 'warning',
                        title: '내용이 너무 깁니다',
                        text: '내용은 2000자를 초과할 수 없습니다.',
                        confirmButtonColor: '#667eea'
                    });
                    return false;
                }
                
                return true;
            }

            // 글 등록
            $("#saveArticleBtn").on("click", function (e) {
                e.preventDefault();

                if (!validateForm()) {
                    return;
                }

                const $button = $(this);
                const originalText = $button.text();
                $button.prop('disabled', true).text('등록 중...');

                const formData = {
                    writer: $('#writer').val(),
                    title: $('#title').val().trim(),
                    content: $('#content').val().trim()
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
                        Swal.fire({
                            icon: 'success',
                            title: '글 작성 완료!',
                            text: '글이 성공적으로 게시되었습니다.',
                            confirmButtonColor: '#43e97b'
                        }).then(() => {
                            window.location.href = "/view/articles/paging";
                        });
                    },
                    error: function (xhr) {
                        let errorMessage = "글 작성에 실패했습니다.";
                        if (xhr.responseJSON && xhr.responseJSON.message) {
                            errorMessage = xhr.responseJSON.message;
                        }
                        
                        Swal.fire({
                            icon: 'error',
                            title: '오류 발생',
                            text: errorMessage,
                            confirmButtonColor: '#ff416c'
                        });
                        
                        $button.prop('disabled', false).text(originalText);
                    }
                });
            });
        });
    </script>

</head>
<body>
<div class="container">
    <div class="form-container">
        <div class="header">
            <h1>📝 새 글 작성</h1>
            <p>스터디 모집이나 정보를 공유해보세요</p>
        </div>

        <div class="form-note">
            <strong>작성 가이드:</strong> 명확한 제목과 상세한 내용으로 다른 개발자들에게 유용한 정보를 제공해주세요.
        </div>

        <form id="articleForm">
            <div class="author-info">
                <div class="form-group">
                    <label for="writer" class="form-label">작성자</label>
                    <input type="text" id="writer" class="form-control" name="writer" readonly>
                </div>
            </div>

            <div class="form-group">
                <label for="title" class="form-label">제목</label>
                <input type="text" id="title" class="form-control" name="title" placeholder="글 제목을 입력하세요" maxlength="100" required>
                <div class="character-count">
                    <span id="title-count">0/100</span>
                </div>
            </div>

            <div class="form-group">
                <label for="content" class="form-label">내용</label>
                <textarea id="content" class="form-control" name="content" placeholder="글 내용을 상세히 작성해주세요..." maxlength="2000" required></textarea>
                <div class="character-count">
                    <span id="content-count">0/2000</span>
                </div>
            </div>

            <div class="button-group">
                <button type="button" onclick="goBack()" class="btn btn-secondary">
                    ← 목록으로
                </button>
                <button type="button" id="saveArticleBtn" class="btn btn-primary">
                    ✅ 글 등록하기
                </button>
            </div>
        </form>
    </div>
</div>
</body>
</html>