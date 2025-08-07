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
  <link rel="stylesheet" href="/css/profile/followers.css">

  <title>팔로워 리스트</title>

  <script>
    const jwt = localStorage.getItem('jwt');
    let currentUserId = null;

    // JWT에서 현재 로그인한 사용자 ID 추출
    if (jwt) {
      try {
        const tokenPayload = JSON.parse(atob(jwt.split('.')[1]));
        currentUserId = tokenPayload.sub;
        console.log("Current User ID:", currentUserId);
        console.log("JWT Token:", jwt.substring(0, 50) + "...");
      } catch (e) {
        console.error("JWT 디코딩 오류:", e);
        alert("토큰이 유효하지 않습니다. 다시 로그인해주세요.");
        localStorage.removeItem('jwt');
        window.location.href = "/view/members/signin";
      }
    } else {
      console.warn("JWT 없음: 로그인되지 않은 상태입니다.");
      alert("로그인이 필요합니다.");
      window.location.href = "/view/members/signin";
    }

    $(document).ready(function () {
      loadFollowers();
    });

    function loadFollowers() {
      $.ajax({
        url: "/api/v1/follows/followers",
        type: 'GET',
        headers: { 'Authorization': 'Bearer ' + jwt },
        success: function (response) {
          const followers = response.data || [];
          console.log("팔로워 목록:", followers);
          console.log("첫 번째 팔로워 데이터:", followers[0]);

          let followersHtml = '';
          if (followers.length === 0) {
            followersHtml = '' +
              '<div class="empty-state">' +
                '<div class="empty-icon">👥</div>' +
                '<h3>아직 팔로워가 없습니다</h3>' +
                '<p>다른 사용자들과 소통해보세요!</p>' +
              '</div>';
          } else {
            followers.forEach(function(follower) {
              let followBackDisplay = follower.isFollowing ? 'none' : 'inline-block';
              let unfollowDisplay = follower.isFollowing ? 'inline-block' : 'none';
              let joinedAtText = follower.joinedAt || '정보 없음';
              
              followersHtml += '' +
                '<div class="follower-card" data-follower-id="' + follower.memberId + '">' +
                  '<div class="follower-info">' +
                    '<div class="follower-details">' +
                      '<h4 class="follower-name">' + follower.nickname + '</h4>' +
                      '<p class="follower-join-date">📅 ' + joinedAtText + '</p>' +
                    '</div>' +
                  '</div>' +
                  '<div class="follower-actions">' +
                    '<button class="btn btn-primary" onclick="viewProfile(' + follower.memberId + ')">' +
                      '👤 프로필 보기' +
                    '</button>' +
                    '<button class="btn btn-success follow-back-btn" onclick="followBack(' + follower.memberId + ')" style="display: ' + followBackDisplay + ';">' +
                      '✅ 팔로우 수락' +
                    '</button>' +
                    '<button class="btn btn-outline-secondary unfollow-btn" onclick="unfollow(' + follower.memberId + ')" style="display: ' + unfollowDisplay + ';">' +
                      '❌ 언팔로우' +
                    '</button>' +
                  '</div>' +
                '</div>';
            });
          }

          $("#followers-list").html(followersHtml);
        },
        error: function (xhr) {
          console.error("팔로워 목록 로딩 실패:", xhr);
          Swal.fire("오류", "팔로워 목록을 불러올 수 없습니다.", "error");
        }
      });
    }

    function viewProfile(memberId) {
      window.location.href = "/view/profile/" + memberId;
    }

    function followBack(followeeId) {
      console.log("팔로우 요청 - followeeId:", followeeId);
      
      $.ajax({
        url: "/api/v1/follows",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ followeeId: followeeId }),
        headers: { 'Authorization': 'Bearer ' + jwt },
        success: function (response) {
          alert('팔로우 요청을 수락했습니다.');
          location.reload();
        },
        error: function (xhr, status, error) {
          console.error("팔로우 실패:", xhr);
          console.error("Status:", status);
          console.error("Error:", error);
          console.error("Response Text:", xhr.responseText);
          
          let errorMessage = "팔로우 요청에 실패했습니다.";
          if (xhr.responseJSON && xhr.responseJSON.message) {
            errorMessage = xhr.responseJSON.message;
          }
          
          Swal.fire("오류", errorMessage, "error");
        }
      });
    }

    function unfollow(followeeId) {
        console.log("언팔로우 요청 - followeeId:", followeeId);

        $.ajax({
          url: "/api/v1/follows/" + followeeId,
          type: "DELETE",
          headers: { 'Authorization': 'Bearer ' + jwt },
          success: function (response) {
            alert('언팔로우 처리되었습니다.');
            location.reload();
          },
          error: function (xhr, status, error) {
            console.error("언팔로우 실패:", xhr);
            console.error("Status:", status);
            console.error("Error:", error);
            console.error("Response Text:", xhr.responseText);

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
<div class="followers-container">
  <!-- 헤더 -->
  <div class="header">
    <h1>👥 팔로워 리스트</h1>
    <button onclick="history.back()" class="btn btn-secondary back-btn">
      ← 뒤로가기
    </button>
  </div>

  <!-- 팔로워 리스트 -->
  <div id="followers-list" class="followers-list">
    <!-- 팔로워 목록이 여기에 동적으로 로드됩니다 -->
    <div class="loading-state">
      <div class="spinner"></div>
      <p>팔로워 목록을 불러오는 중...</p>
    </div>
  </div>
</div>
</body>
</html>
