<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dev Link Central - 회원가입</title>
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/members/sign-up.css">

    <script>
        $(document).ready(function() {
            // 입력 필드 포커스 효과
            $('.form-control').on('focus', function() {
                $(this).closest('.input-wrapper').addClass('focused');
            });

            $('.form-control').on('blur', function() {
                if ($(this).val() === '') {
                    $(this).closest('.input-wrapper').removeClass('focused');
                }
            });

            // 실시간 유효성 검사
            $('#loginId').on('input', function() {
                validateLoginId($(this).val());
            });

            $('#password').on('input', function() {
                validatePassword($(this).val());
                if ($('#passwordConfirm').val()) {
                    validatePasswordConfirm($('#passwordConfirm').val());
                }
            });

            $('#passwordConfirm').on('input', function() {
                validatePasswordConfirm($(this).val());
            });

            $('#nickname').on('input', function() {
                validateNickname($(this).val());
            });

            $('#email').on('input', function() {
                validateEmail($(this).val());
            });

            // 폼 제출 시 로딩 효과
            $('#signUpForm').on('submit', function(e) {
                e.preventDefault();
                
                if (validateForm()) {
                    $('#submitBtn').addClass('loading').html('<i class="fas fa-spinner fa-spin"></i> 계정 생성 중...');
                    
                    // 폼 데이터를 JSON으로 변환
                    const formData = {
                        name: $('#loginId').val(),
                        nickname: $('#nickname').val(),
                        email: $('#email').val(),
                        password: $('#password').val(),
                        confirmPassword: $('#passwordConfirm').val()
                    };
                    
                    // AJAX 요청
                    $.ajax({
                        type: "POST",
                        url: "/api/public/members",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                        success: function(response) {
                            console.log("회원가입 성공: ", response);
                            alert("회원가입이 완료되었습니다!");
                            window.location.href = "/view/members/signin";
                        },
                        error: function(xhr) {
                            console.error("회원가입 실패: ", xhr);
                            let errorMessage = "회원가입에 실패했습니다.";
                            
                            if (xhr.responseJSON && xhr.responseJSON.message) {
                                errorMessage = xhr.responseJSON.message;
                            }
                            
                            alert(errorMessage);
                        },
                        complete: function() {
                            // 로딩 효과 제거
                            $('#submitBtn').removeClass('loading').html('계정 만들기');
                        }
                    });
                }
            });
        });

        function validateLoginId(value) {
            const messageEl = $('#loginIdMessage');
            if (value.length < 2) {
                showValidation('loginId', false, '이름은 2자 이상이어야 합니다.');
                return false;
            }
            showValidation('loginId', true, '올바른 이름입니다.');
            return true;
        }

        function validatePassword(value) {
            const messageEl = $('#passwordMessage');
            if (value.length < 8) {
                showValidation('password', false, '비밀번호는 8자 이상이어야 합니다.');
                return false;
            }
            showValidation('password', true, '사용 가능한 비밀번호입니다.');
            return true;
        }

        function validatePasswordConfirm(value) {
            const password = $('#password').val();
            if (value !== password) {
                showValidation('passwordConfirm', false, '비밀번호가 일치하지 않습니다.');
                return false;
            }
            showValidation('passwordConfirm', true, '비밀번호가 일치합니다.');
            return true;
        }

        function validateNickname(value) {
            if (value.length < 2) {
                showValidation('nickname', false, '닉네임은 2자 이상이어야 합니다.');
                return false;
            }
            showValidation('nickname', true, '사용 가능한 닉네임입니다.');
            return true;
        }

        function validateEmail(value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                showValidation('email', false, '올바른 이메일 형식이 아닙니다.');
                return false;
            }
            showValidation('email', true, '사용 가능한 이메일입니다.');
            return true;
        }

        function showValidation(fieldId, isValid, message) {
            const field = $('#' + fieldId);
            const messageEl = $('#' + fieldId + 'Message');
            
            field.removeClass('is-valid is-invalid');
            field.addClass(isValid ? 'is-valid' : 'is-invalid');
            
            messageEl.removeClass('valid-message invalid-message');
            messageEl.addClass(isValid ? 'valid-message' : 'invalid-message');
            messageEl.text(message);
        }

        function validateForm() {
            const loginId = $('#loginId').val();
            const password = $('#password').val();
            const passwordConfirm = $('#passwordConfirm').val();
            const nickname = $('#nickname').val();
            const email = $('#email').val();
            const agreeTerms = $('#agreeTerms').is(':checked');

            const validations = [
                validateLoginId(loginId),
                validatePassword(password),
                validatePasswordConfirm(passwordConfirm),
                validateNickname(nickname),
                validateEmail(email)
            ];

            if (!agreeTerms) {
                alert('이용약관에 동의해주세요.');
                return false;
            }

            return validations.every(v => v);
        }
    </script>
</head>
<body>
    <div class="signup-container">
        <div class="signin-left">
            <div class="brand-section">
                <i class="fas fa-users-cog brand-icon"></i>
                <h1 class="brand-title">Join Dev Community</h1>
                <p class="brand-subtitle">개발자들과 함께 성장하세요</p>
            </div>
            <div class="benefit-list">
                <div class="benefit-item">
                    <i class="fas fa-rocket"></i>
                    <div class="benefit-content">
                        <h3>빠른 성장</h3>
                        <p>실무 경험과 지식을 공유하며 함께 성장</p>
                    </div>
                </div>
                <div class="benefit-item">
                    <i class="fas fa-handshake"></i>
                    <div class="benefit-content">
                        <h3>네트워킹</h3>
                        <p>전국의 개발자들과 연결되어 인사이트 공유</p>
                    </div>
                </div>
                <div class="benefit-item">
                    <i class="fas fa-lightbulb"></i>
                    <div class="benefit-content">
                        <h3>아이디어 교환</h3>
                        <p>창의적인 프로젝트와 솔루션 발견</p>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="signin-right">
            <div class="form-container">
                <div class="form-header">
                    <h2 class="form-title">계정 만들기</h2>
                    <p class="form-subtitle">개발자 커뮤니티에 참여하세요</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-triangle"></i>
                        ${error}
                    </div>
                </c:if>
                
                <form id="signUpForm" class="signup-form">
                    <div class="form-row">
                        <div class="input-group">
                            <div class="input-wrapper">
                                <i class="fas fa-user input-icon"></i>
                                <input type="text" 
                                       id="loginId" 
                                       name="name" 
                                       class="form-control"
                                       placeholder="이름"
                                       value="${param.name}"
                                       required>
                                <label for="loginId" class="floating-label">이름</label>
                            </div>
                            <div class="validation-message" id="loginIdMessage"></div>
                        </div>
                        
                        <div class="input-group">
                            <div class="input-wrapper">
                                <i class="fas fa-signature input-icon"></i>
                                <input type="text" 
                                       id="nickname" 
                                       name="nickname" 
                                       class="form-control"
                                       placeholder="닉네임"
                                       value="${param.nickname}"
                                       required>
                                <label for="nickname" class="floating-label">닉네임</label>
                            </div>
                            <div class="validation-message" id="nicknameMessage"></div>
                        </div>
                    </div>
                    
                    <div class="input-group">
                        <div class="input-wrapper">
                            <i class="fas fa-envelope input-icon"></i>
                            <input type="email" 
                                   id="email" 
                                   name="email" 
                                   class="form-control"
                                   placeholder="이메일 주소"
                                   value="${param.email}"
                                   required>
                            <label for="email" class="floating-label">이메일</label>
                        </div>
                        <div class="validation-message" id="emailMessage"></div>
                    </div>
                    
                    <div class="form-row">
                        <div class="input-group">
                            <div class="input-wrapper">
                                <i class="fas fa-lock input-icon"></i>
                                <input type="password" 
                                       id="password" 
                                       name="password" 
                                       class="form-control"
                                       placeholder="비밀번호"
                                       required>
                                <label for="password" class="floating-label">비밀번호</label>
                            </div>
                            <div class="validation-message" id="passwordMessage"></div>
                        </div>
                        
                        <div class="input-group">
                            <div class="input-wrapper">
                                <i class="fas fa-lock input-icon"></i>
                                <input type="password" 
                                       id="passwordConfirm" 
                                       name="confirmPassword" 
                                       class="form-control"
                                       placeholder="비밀번호 확인"
                                       required>
                                <label for="passwordConfirm" class="floating-label">비밀번호 확인</label>
                            </div>
                            <div class="validation-message" id="passwordConfirmMessage"></div>
                        </div>
                    </div>
                    
                    <div class="terms-section">
                        <label class="checkbox-container">
                            <input type="checkbox" id="agreeTerms" name="agreeTerms" required>
                            <span class="checkmark"></span>
                            <span class="terms-text">
                                <a href="/terms" target="_blank">이용약관</a> 및 
                                <a href="/privacy" target="_blank">개인정보처리방침</a>에 동의합니다
                            </span>
                        </label>
                    </div>
                    
                    <button type="submit" class="btn-signup" id="submitBtn">
                        계정 만들기
                    </button>
                </form>
                
                <div class="form-footer">
                    <p class="signin-link">
                        이미 계정이 있으신가요? 
                        <a href="/view/members/signin" class="link-button">
                            로그인하기
                        </a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>