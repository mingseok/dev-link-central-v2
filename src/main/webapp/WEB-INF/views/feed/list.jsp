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
  <link rel="stylesheet" href="/css/feed/list.css">

  <title>피드</title>

  <script>
    const jwt = localStorage.getItem('jwt');
    let currentUserId = null;

    // JWT에서 현재 로그인한 사용자 ID 추출
    if (jwt) {
      try {
        const tokenPayload = JSON.parse(atob(jwt.split('.')[1]));
        currentUserId = tokenPayload.sub;
        console.log("Current User ID:", currentUserId);
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
      loadFeeds();
    });

    function loadFeeds() {
      $.ajax({
        url: "/api/v1/feeds",
        type: 'GET',
        headers: { 'Authorization': 'Bearer ' + jwt },
        success: function (response) {
          const feeds = response.data || [];
          console.log("피드 목록:", feeds);
          console.log("현재 로그인 유저 ID:", currentUserId);

          let feedsHtml = '';
          if (feeds.length === 0) {
            feedsHtml = '' +
              '<div class="empty-state">' +
                '<div class="empty-icon">📝</div>' +
                '<h3>아직 피드가 없습니다</h3>' +
                '<p>첫 번째 피드를 작성해보세요!</p>' +
              '</div>';
          } else {
            feeds.forEach(function(feed) {
              console.log("피드 작성자 ID:", feed.writerId, "타입:", typeof feed.writerId);
              console.log("현재 유저 ID:", currentUserId, "타입:", typeof currentUserId);
              console.log("isMyFeed:", feed.isMyFeed);
              console.log("isLiked:", feed.isLiked);
              console.log("likeCount:", feed.likeCount);
              
              // 현재 로그인한 유저가 작성한 피드인지 확인
              const isMyFeed = String(feed.writerId) === String(currentUserId);
              
              let deleteButtonHtml = '';
              if (isMyFeed) {
                deleteButtonHtml = '<button class="btn btn-outline-danger btn-sm ml-2" onclick="deleteFeed(' + feed.feedId + ')">🗑️ 삭제</button>';
                console.log("삭제 버튼 추가됨 - 피드 ID:", feed.feedId);
              } else {
                console.log("삭제 버튼 숨김 - 다른 유저의 피드");
              }

              // 좋아요 버튼
              const likeButtonClass = feed.isLiked ? 'btn-danger' : 'btn-outline-danger';
              const likeButtonText = feed.isLiked ? '❤️' : '🤍';
              
              feedsHtml += '' +
                '<div class="feed-card" data-feed-id="' + feed.feedId + '">' +
                  '<div class="feed-header">' +
                    '<div class="feed-author">' +
                      '<h5 class="author-name">' + feed.writerNickname + '</h5>' +
                      '<span class="feed-date">' + feed.createdAt + '</span>' +
                    '</div>' +
                    '<div class="feed-actions">' +
                      deleteButtonHtml +
                    '</div>' +
                  '</div>' +
                  '<div class="feed-content">' +
                    '<p>' + feed.content + '</p>' +
                  '</div>' +
                  '<div class="feed-footer">' +
                    '<div class="like-section">' +
                      '<button class="btn ' + likeButtonClass + ' btn-sm like-btn" onclick="likeFeed(' + feed.feedId + ')">' +
                        likeButtonText +
                      '</button>' +
                      '<span class="like-count" id="like-count-' + feed.feedId + '">' + feed.likeCount + '</span>' +
                    '</div>' +
                  '</div>' +
                '</div>';
            });
          }

          $("#feeds-list").html(feedsHtml);
        },
        error: function (xhr) {
          console.error("피드 목록 로딩 실패:", xhr);
          Swal.fire("오류", "피드 목록을 불러올 수 없습니다.", "error");
        }
      });
    }

    function createFeed() {
      window.location.href = "/view/feeds/create";
    }

    function deleteFeed(feedId) {
      Swal.fire({
        title: '피드를 삭제하시겠습니까?',
        text: "삭제된 피드는 복구할 수 없습니다.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
      }).then((result) => {
        if (result.isConfirmed) {
          $.ajax({
            url: "/api/v1/feeds/" + feedId,
            type: "DELETE",
            headers: { 'Authorization': 'Bearer ' + jwt },
            success: function () {
              Swal.fire({
                title: "피드 삭제 완료",
                icon: "success",
                timer: 1500,
                showConfirmButton: false
              }).then(() => {
                location.reload();
              });
            },
            error: function (xhr) {
              console.error("피드 삭제 실패:", xhr);
              Swal.fire("오류", "피드 삭제에 실패했습니다.", "error");
            }
          });
        }
      });
    }

    function likeFeed(feedId) {
      $.ajax({
        url: "/api/v1/feeds/" + feedId + "/like",
        type: "POST",
        headers: { 'Authorization': 'Bearer ' + jwt },
        success: function (response) {
          console.log("좋아요 처리 성공:", response);
          
          const data = response.data;
          const isLiked = data.isLiked;
          const likeCount = data.likeCount;
          
          // 서버 응답을 기반으로 UI 업데이트
          const likeBtn = $(`.feed-card[data-feed-id="${feedId}"] .like-btn`);
          const likeCountSpan = $("#like-count-" + feedId);
          
          if (isLiked) {
            // 좋아요 상태
            likeBtn.removeClass('btn-outline-danger').addClass('btn-danger').text('❤️');
          } else {
            // 좋아요 취소 상태
            likeBtn.removeClass('btn-danger').addClass('btn-outline-danger').text('🤍');
          }
          
          // 실제 서버에서 받은 카운트로 업데이트
          likeCountSpan.text(likeCount);
        },
        error: function (xhr) {
          console.error("좋아요 처리 실패:", xhr);
          Swal.fire("오류", "좋아요 처리에 실패했습니다.", "error");
        }
      });
    }

    function goHome() {
      window.location.href = "/view/members/home";
    }
  </script>
</head>

<body>
<div class="feed-container">
  <!-- 헤더 -->
  <div class="header">
    <h1>📰 피드</h1>
    <div class="header-actions">
      <button onclick="createFeed()" class="btn btn-primary create-btn">
        ✏️ 피드 작성
      </button>
      <button onclick="goHome()" class="btn btn-secondary home-btn">
        🏠 홈으로
      </button>
    </div>
  </div>

  <!-- 피드 리스트 -->
  <div id="feeds-list" class="feeds-list">
    <!-- 피드 목록이 여기에 동적으로 로드됩니다 -->
    <div class="loading-state">
      <div class="spinner"></div>
      <p>피드를 불러오는 중...</p>
    </div>
  </div>
</div>
</body>
</html>
