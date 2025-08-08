<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <!-- Bootstrap CSS -->
  <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
  <!-- jQuery library -->
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <!-- Popper.js -->
  <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/umd/popper.min.js"></script>
  <!-- Bootstrap JS -->
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
  <!-- SweetAlert2 CSS and JS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
  <!-- Custom CSS -->
  <link rel="stylesheet" href="/css/profile/detail.css">

  <script>
    const pathSegments = window.location.pathname.split("/");
    const memberIndex = pathSegments.indexOf("profile");
    const profileOwnerId = pathSegments[memberIndex + 1]; // 프로필 주인의 ID
    const jwt = localStorage.getItem('jwt');
    let currentUserId = null; // 현재 로그인한 사용자 ID
    let isMe = false;

    if (jwt) {
      try {
        const tokenPayload = JSON.parse(atob(jwt.split('.')[1]));
        currentUserId = tokenPayload.sub; // JWT 표준의 sub 클레임 사용
        console.log("Current User ID (JWT):", currentUserId);
      } catch (e) {
        console.error("JWT 디코딩 오류:", e);
      }
    } else {
      console.warn("JWT 없음: 로그인되지 않은 상태입니다.");
    }

    $(document).ready(function () {
      $.ajax({
        url: "/api/v1/profile/" + profileOwnerId,
        type: 'GET',
        headers: { 'Authorization': 'Bearer ' + jwt },
        success: function (response) {
          const data = response.data;
          
          // 서버에서 받은 프로필 데이터를 화면에 표시
          $("#nickname").text(data.nickname);
          $("#joinedAt").text(data.joinedAt);
          $("#followersCount").text(data.followersCount);
          $("#followingsCount").text(data.followingsCount);
          $("#bio-text").text(data.bio || '');
          
          const isFollowingBoolean = Boolean(data.isFollowing);
          isMe = (String(currentUserId) === String(data.memberId));

          if (!isMe) {
            // 다른 사람의 프로필이면 팔로우/언팔로우 버튼 표시
            const buttonText = isFollowingBoolean ? "언팔로우" : "팔로우";
            const buttonClass = isFollowingBoolean ? "btn-danger" : "btn-primary";
            console.log("Setting button text to:", buttonText);
            console.log("Setting button class to:", buttonClass);
            
            $("#follow-btn")
              .show()
              .text(buttonText)
              .removeClass("btn-primary btn-danger")
              .addClass(buttonClass)
              .off("click")
              .on("click", function () {
                const currentIsFollowing = $("#follow-btn").text() === "언팔로우";
                console.log("Button clicked - current state:", currentIsFollowing ? "following" : "not following");
                
                if (currentIsFollowing) {
                  unfollow();
                } else {
                  follow();
                }
              });
            $("#edit-bio-btn, #save-bio-btn").hide();
          } else {
            // 자신의 프로필이면 소개글 수정 버튼 표시
            $("#edit-bio-btn").show();
            $("#follow-btn").hide();
          }
        },
        error: function () {
          Swal.fire("오류", "프로필 정보를 불러올 수 없습니다.", "error");
        }
      });

      $("#edit-bio-btn").on("click", function () {
        const currentText = $("#bio-text").text();
        $("#bio-text").hide();
        $("#bio-input").val(currentText).show();
        $("#save-bio-btn").show();
        $(this).hide();
      });

      $("#save-bio-btn").on("click", function () {
        const newBio = $("#bio-input").val();
        $.ajax({
          url: "/api/v1/profile",
          type: "PUT",
          contentType: "application/json",
          data: JSON.stringify({ bio: newBio }),
          headers: { 'Authorization': 'Bearer ' + jwt },
          success: function () {
            $("#bio-text").text(newBio).show();
            $("#bio-input").hide();
            $("#save-bio-btn").hide();
            $("#edit-bio-btn").show();
          },
          error: function (xhr) {
            Swal.fire("오류", "소개글 저장 실패: " + (xhr.responseJSON?.message || xhr.responseText), "error");
          }
        });
      });
    });

    function follow() {
      console.log("팔로우 요청 시작 - targetId:", profileOwnerId);
      
      // 중복 요청 방지
      $("#follow-btn").prop('disabled', true);
      
      $.ajax({
        url: "/api/v1/follows",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ followeeId: parseInt(profileOwnerId) }),
        headers: { 'Authorization': 'Bearer ' + jwt },
        success: function (response) {
          console.log("팔로우 성공:", response);
          $("#follow-btn")
            .text("언팔로우")
            .removeClass("btn-primary")
            .addClass("btn-danger")
            .prop('disabled', false);
          
          const currentCount = parseInt($("#followersCount").text()) || 0;
          $("#followersCount").text(currentCount + 1);
        },
        error: function (xhr, status, error) {
          console.error("팔로우 실패:", xhr);
          console.error("Status:", status);
          console.error("Error:", error);
          console.error("Response Text:", xhr.responseText);
          
          $("#follow-btn").prop('disabled', false);
          
          let errorMessage = "팔로우 요청에 실패했습니다.";
          if (xhr.responseJSON && xhr.responseJSON.message) {
            errorMessage = xhr.responseJSON.message;
          }
          
          Swal.fire("오류", errorMessage, "error");
        }
      });
    }

    function unfollow() {
      console.log("언팔로우 요청 시작 - targetId:", profileOwnerId);
      
      // 중복 요청 방지
      $("#follow-btn").prop('disabled', true);
      
      $.ajax({
        url: "/api/v1/follows/" + profileOwnerId,
        type: "DELETE",
        headers: { 'Authorization': 'Bearer ' + jwt },
        success: function (response) {
          console.log("언팔로우 성공:", response);
          $("#follow-btn")
            .text("팔로우")
            .removeClass("btn-danger")
            .addClass("btn-primary")
            .prop('disabled', false);
          
          const currentCount = parseInt($("#followersCount").text()) || 0;
          $("#followersCount").text(Math.max(0, currentCount - 1));
        },
        error: function (xhr, status, error) {
          console.error("언팔로우 실패:", xhr);
          console.error("Status:", status);
          console.error("Error:", error);
          console.error("Response Text:", xhr.responseText);
          
          $("#follow-btn").prop('disabled', false);
          
          let errorMessage = "언팔로우 요청에 실패했습니다.";
          if (xhr.responseJSON && xhr.responseJSON.message) {
            errorMessage = xhr.responseJSON.message;
          }
          
          Swal.fire("오류", errorMessage, "error");
        }
      });
    }
  </script>
</head>

<body>
<div class="profile-container">
  <div class="profile-header">
    <h2 id="nickname">닉네임</h2>
    <button id="follow-btn" class="btn btn-primary" style="display: none;">팔로우</button>
  </div>

  <p> 가입일: <span id="joinedAt"></span></p>

  <!-- 팔로워/팔로잉 수 -->
  <div class="stats">
    <span>👥 팔로워: <span id="followersCount">0</span></span>
    <span>🤝 팔로잉: <span id="followingsCount">0</span></span>
  </div>

  <!-- 소개글 영역 -->
  <div class="bio-box">
    <label for="bio">💭 소개글</label>
    <p id="bio-text"></p>
    <textarea id="bio-input" rows="4" class="form-control" style="display:none;" placeholder="자신을 소개해보세요..."></textarea>
    <button id="save-bio-btn" class="btn btn-primary" style="display:none;">저장</button>
    <button id="edit-bio-btn" class="btn btn-secondary" style="display:none;">소개글 수정</button>
  </div>

  <div class="back-button-container">
    <button onclick="history.back()" class="btn btn-secondary back-btn">
      ← 뒤로가기
    </button>
  </div>
</div>
</body>
</html>
