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
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/members/sign-in.css">
    <title>Dev Link Central - 로그인</title>

    <script>
        $(document).ready(function () {
            localStorage.removeItem("jwt");

            $("#loginButton").click(function (e) {
                e.preventDefault(); // 기본 form 제출을 방지

                var email = $("#email").val();
                var password = $("#password").val();

                // 로딩 효과 추가
                $("#loginButton").addClass("loading").html('<i class="fas fa-spinner fa-spin"></i> 로그인 중...');

                $.ajax({
                    type: "POST",
                    url: "/api/public/members/signin",
                    contentType: "application/json",
                    data: JSON.stringify({email: email, password: password}),
                    success: function (response) {
                        console.log("로그인 응답: ", response);
                        const accessToken = response?.data?.accessToken;

                        if (response.status === "SUCCESS" && accessToken) {
                            localStorage.setItem("jwt", accessToken);
                            window.location.href = "/view/members/home";
                        } else {
                            console.error("응답에서 accessToken을 찾을 수 없습니다.");
                            alert("로그인 실패: 응답에서 토큰을 찾을 수 없습니다.");
                        }
                    },
                    error: function (xhr) {
                        console.error("로그인 요청 실패: ", xhr);
                        alert("로그인 요청 실패");
                    },
                    complete: function() {
                        // 로딩 효과 제거
                        $("#loginButton").removeClass("loading").html('로그인하기');
                    }
                });
            });

            // 입력 필드 포커스 효과
            $('.form-control').on('focus', function() {
                $(this).parent().addClass('focused');
            });

            $('.form-control').on('blur', function() {
                if ($(this).val() === '') {
                    $(this).parent().removeClass('focused');
                }
            });
        });
    </script>

</head>
<body>
    <div class="signin-container">
        <div class="signin-left">
            <div class="brand-section">
                <i class="fas fa-code-branch brand-icon"></i>
                <h1 class="brand-title">Dev Link Central</h1>
                <p class="brand-subtitle">개발자들을 위한 소셜 플랫폼</p>
            </div>
            <div class="feature-list">
                <div class="feature-item">
                    <i class="fas fa-users"></i>
                    <span>개발자 네트워킹</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-share-alt"></i>
                    <span>지식 공유</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-project-diagram"></i>
                    <span>프로젝트 협업</span>
                </div>
            </div>
        </div>
        
        <div class="signin-right">
            <div class="form-container">
                <div class="form-header">
                    <h2 class="form-title">로그인</h2>
                    <p class="form-subtitle">계정에 로그인하세요</p>
                </div>
                
                <form id="loginForm" class="signin-form">
                    <div class="input-group">
                        <div class="input-wrapper">
                            <i class="fas fa-envelope input-icon"></i>
                            <input type="email" class="form-control" id="email" name="email" placeholder="이메일 주소" required>
                            <label for="email" class="floating-label">이메일</label>
                        </div>
                    </div>
                    
                    <div class="input-group">
                        <div class="input-wrapper">
                            <i class="fas fa-lock input-icon"></i>
                            <input type="password" class="form-control" id="password" name="password" placeholder="비밀번호" required>
                            <label for="password" class="floating-label">비밀번호</label>
                        </div>
                    </div>
                    
                    <button type="submit" class="btn btn-signin" id="loginButton">
                        로그인하기
                    </button>
                </form>
                
                <div class="form-footer">
                    <p class="signup-link">
                        아직 계정이 없으신가요? 
                        <a href="/view/members/signup" class="link-button">
                            회원가입하기
                        </a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>