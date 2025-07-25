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
  <link rel="stylesheet" href="/css/members/sign-in.css">
  <title>dev-link-central</title>

  <script>
    $(document).ready(function () {
      localStorage.removeItem("jwt");

      $("#loginButton").click(function (e) {
        e.preventDefault(); // 기본 form 제출을 방지

        var email = $("#email").val();
        var password = $("#password").val();

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
          }
        });
      });
    });
  </script>

</head>
<body>
<div class="form-container">
  <h2 class="text-center mb-4">Sign-In</h2>
  <form id="loginForm">
    <div class="form-group">
      <label for="email" class="form-label">이메일:</label>
      <input type="email" class="form-control" id="email" name="email" required>
    </div>
    <div class="form-group">
      <label for="password" class="form-label">패스워드:</label>
      <input type="password" class="form-control" id="password" name="password" required>
    </div>
    <button type="submit" class="btn btn-primary btn-block" id="loginButton">로그인하기</button>
  </form>
  <p class="text-center mt-4">
    처음이신가요? <a href="/view/members/signup" class="link-button">회원가입하기</a>
  </p>
</div>
</body>
</html>
