<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <!-- Bootstrap CSS -->
  <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
  <!-- Font Awesome for icons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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
  <link rel="stylesheet" href="/css/profile/following.css">

  <title>Dev Link Central - 팔로잉</title>

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
        Swal.fire({
          icon: 'error',
          title: '인증 오류',
          text: '토큰이 유효하지 않습니다. 다시 로그인해주세요.',
          confirmButtonColor: '#4facfe'
        }).then(() => {
          localStorage.removeItem('jwt');
          window.location.href = "/view/members/signin";
        });
      }
    } else {
      console.warn("JWT 없음: 로그인되지 않은 상태입니다.");
      Swal.fire({
        icon: 'warning',
        title: '로그인 필요',
        text: '로그인이 필요한 서비스입니다.',
        confirmButtonColor: '#4facfe'
      }).then(() => {
        window.location.href = "/view/members/signin";
      });
    }

    $(document).ready(function () {
      loadFollowing();
    });

    function loadFollowing() {
      $.ajax({
        url: "/api/v1/follows/following",
        type: 'GET',
        headers: { 'Authorization': 'Bearer ' + jwt },
        success: function (response) {
          const following = response.data || [];
          console.log("팔로잉 목록:", following);

          let followingHtml = '';
          if (following.length === 0) {
            followingHtml = '' +
              '<div class="empty-state">' +
                '<div class="empty-icon">' +
                  '<i class="fas fa-user-plus"></i>' +
                '</div>' +
                '<h3>아직 팔로잉한 사용자가 없습니다</h3>' +
                '<p>다른 개발자들을 팔로우하여 네트워크를 확장해보세요!</p>' +
              '</div>';
          } else {
            following.forEach(function(user) {
              let joinedAtText = user.joinedAt || '정보 없음';
              
              followingHtml += '' +
                '<div class="following-card" data-following-id="' + user.memberId + '">' +
                  '<div class="following-avatar">' +
                    '<div class="avatar-circle">' +
                      '<i class="fas fa-user"></i>' +
                    '</div>' +
                    '<div class="status-badge"></div>' +
                  '</div>' +
                  '<div class="following-info">' +
                    '<h4 class="following-name">' + user.nickname + '</h4>' +
                    '<p class="following-join-date">' +
                      '<i class="fas fa-calendar-alt"></i>' +
                      '<span>' + joinedAtText + '</span>' +
                    '</p>' +
                  '</div>' +
                  '<div class="following-actions">' +
                    '<button class="btn btn-view" onclick="viewProfile(' + user.memberId + ')">' +
                      '<i class="fas fa-eye"></i>' +
                      '<span>프로필 보기</span>' +
                    '</button>' +
                    '<button class="btn btn-unfollow" onclick="unfollow(' + user.memberId + ')">' +
                      '<i class="fas fa-user-minus"></i>' +
                      '<span>언팔로우</span>' +
                    '</button>' +
                  '</div>' +
                '</div>';
            });
          }

          $("#following-list").html(followingHtml);
        },
        error: function (xhr) {
          console.error("팔로잉 목록 로딩 실패:", xhr);
          $("#following-list").html('' +
            '<div class="error-state">' +
              '<div class="error-icon">' +
                '<i class="fas fa-exclamation-triangle"></i>' +
              '</div>' +
              '<h3>오류가 발생했습니다</h3>' +
              '<p>팔로잉 목록을 불러오는 중 문제가 발생했습니다.</p>' +
              '<button class="btn btn-retry" onclick="loadFollowing()">' +
                '<i class="fas fa-redo"></i>' +
                '<span>다시 시도</span>' +
              '</button>' +
            '</div>'
          );
        }
      });
    }

    function viewProfile(memberId) {
      window.location.href = "/view/profile/" + memberId;
    }

    function unfollow(followeeId) {
      console.log("언팔로우 요청 - followeeId:", followeeId);

      Swal.fire({
        title: '언팔로우 확인',
        text: '정말로 언팔로우 하시겠습니까?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#ff4757',
        cancelButtonColor: '#4facfe',
        confirmButtonText: '언팔로우',
        cancelButtonText: '취소'
      }).then((result) => {
        if (result.isConfirmed) {
          // 버튼 로딩 상태
          const button = $(`[data-following-id="${followeeId}"] .btn-unfollow`);
          const originalHtml = button.html();
          button.html('<i class="fas fa-spinner fa-spin"></i><span>처리 중...</span>').prop('disabled', true);

          $.ajax({
            url: "/api/v1/follows/" + followeeId,
            type: "DELETE",
            headers: { 'Authorization': 'Bearer ' + jwt },
            success: function (response) {
              Swal.fire({
                icon: 'success',
                title: '언팔로우 완료',
                text: '언팔로우 처리되었습니다.',
                confirmButtonColor: '#4facfe'
              }).then(() => {
                location.reload();
              });
            },
            error: function (xhr, status, error) {
              console.error("언팔로우 실패:", xhr);

              let errorMessage = "언팔로우 요청에 실패했습니다.";
              if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
              }

              Swal.fire({
                icon: 'error',
                title: '언팔로우 실패',
                text: errorMessage,
                confirmButtonColor: '#4facfe'
              });
              
              button.html(originalHtml).prop('disabled', false);
            }
          });
        }
      });
    }
  </script>
</head>

<body>
  <div class="following-container">
    <!-- 헤더 섹션 -->
    <div class="header-section">
      <div class="header-left">
        <div class="brand-info">
          <i class="fas fa-user-plus brand-icon"></i>
          <div class="header-text">
            <h1 class="page-title">팔로잉</h1>
            <p class="page-subtitle">내가 팔로우하는 사용자들</p>
          </div>
        </div>
      </div>
      <div class="header-right">
        <button onclick="history.back()" class="btn btn-back">
          <i class="fas fa-arrow-left"></i>
          <span>뒤로가기</span>
        </button>
      </div>
    </div>

    <!-- 팔로잉 리스트 컨테이너 -->
    <div class="content-section">
      <div id="following-list" class="following-list">
        <!-- 로딩 상태 -->
        <div class="loading-state">
          <div class="loading-spinner">
            <i class="fas fa-spinner fa-spin"></i>
          </div>
          <h3>팔로잉 목록을 불러오는 중...</h3>
          <p>잠시만 기다려주세요</p>
        </div>
      </div>
    </div>
  </div>
</body>
</html>