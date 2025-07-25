<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
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
  <link rel="stylesheet" href="/css/articles/detail.css">

  <script>
    let articleId;

    // 게시글 수정 페이지로 이동하는 함수
    function updateReq() {
      console.log("수정 요청");
      window.location.href = "/view/articles/update/" + articleId;
    }

    // 게시글 목록 페이지로 이동하는 함수
    function listReq() {
      console.log("목록 요청");
      window.location.href = "/view/articles/paging?page=${page}";
    }

    // 게시글 삭제 요청 함수
    function deleteReq() {
      console.log("삭제 요청");
      $.ajax({
        url: "/api/v1/articles/" + articleId,
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        type: "DELETE",
        success: function (response) {
          alert("게시글이 삭제되었습니다.");
          window.location.href = "/view/articles/paging";
        },
        error: function (error) {
          alert("삭제 중 오류가 발생했습니다: " + error);
        }
      });
    }

    // HTML 인코딩 함수 추가
    function escapeHtml(text) {
      if (!text) return '';
      return text.replace(/[&<>"']/g, function (match) {
        const escapeMap = {
          '&': '&amp;',
          '<': '&lt;',
          '>': '&gt;',
          '"': '&quot;',
          "'": '&#039;'
        };
        return escapeMap[match];
      });
    }

    document.addEventListener("DOMContentLoaded", function () {
      const pathSegments = window.location.pathname.split("/");
      const articleIndex = pathSegments.indexOf("articles");
      if (articleIndex !== -1 && pathSegments.length > articleIndex + 1) {
        articleId = pathSegments[articleIndex + 1];
      } else {
        console.error("articleId를 URL에서 추출하지 못했습니다.");
      }

      $.ajax({
        url: "/api/v1/articles/" + articleId,
        type: 'GET',
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('jwt')
        },
        success: function(response) {
          const article = response.data;
          document.getElementById("articleId").textContent = article.id || '';
          document.getElementById("title").textContent = article.title || '';
          document.getElementById("author").textContent = article.writer || '';
          document.getElementById("content").textContent = article.content || '';
          document.getElementById("viewsCount").textContent = article.viewsCount || '0';
          document.getElementById("createdAt").textContent = article.formattedCreatedAt || '';

          if (article.isAuthor === true) {
            document.getElementById("updateBtn").style.display = "inline-block";
            document.getElementById("deleteBtn").style.display = "inline-block";
          }
        },
        error: function() {
          console.error("게시글 정보를 불러오는데 실패했습니다.");
        }
      });
      loadLikeCount();
      loadComments();
    });

    var loggedInUserNickname = '<c:out value="${member.nickname}"/>';

    var currentPage = 0;
    var pageSize = 3;
    var isFetchingComments = false;
    var hasMoreComments = true;

    // 댓글 작성
    function commentWrite() {
      var contents = $('#contents').val();
      if (!contents) {
        alert('댓글 내용을 입력해주세요.');
        return;
      }

      $.ajax({
        url: "/api/v1/articles/" + articleId + "/comments",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
          content: contents,
          parentId: null
        }),
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
        success: function() {
          $('#contents').val('');
          $("#comment-list table tbody").empty();
          currentPage = 0;
          hasMoreComments = true;
          loadComments(); // 다시 불러오기
        },
        error: function(xhr) {
          alert('댓글 작성 실패: ' + xhr.responseText);
        }
      });
    }

    // 댓글 목록 불러오기
    function loadComments() {
      if (isFetchingComments || !hasMoreComments) return;

      isFetchingComments = true;

      $.ajax({
        url: "/api/public/articles/" + articleId + "/comments",
        type: "GET",
        data: {
          offset: currentPage * pageSize,
          limit: pageSize
        },
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
        success: function(response) {
          const comments = response.data;

          console.log("불러온 댓글 데이터:", comments);

          if (comments.length > 0) {
            let commentsHtml = '';
            comments.forEach(function(comment) {
              const rendered = renderComment(comment, 0);
              console.log("렌더링된 HTML:", rendered);
              commentsHtml += rendered;
            });

            console.log("최종 누적된 commentsHtml:", commentsHtml);
            $("#comment-list table tbody").append(commentsHtml);
            currentPage++;

            // 무한스크롤 여부 설정
            if (response.hasMoreComments) {
              $("#moreCommentsIndicator").show();
            } else {
              hasMoreComments = false;
              $("#moreCommentsIndicator").hide();
            }
          } else {
            hasMoreComments = false;
            $("#moreCommentsIndicator").hide();
          }

          isFetchingComments = false;
        },
        error: function(xhr) {
          alert('댓글 목록 로딩 실패: ' + xhr.responseText);
          isFetchingComments = false;
        }
      });
    }

    function renderComment(comment, depth) {
      const commentId = comment.id;
      const numericDepth = parseInt(depth, 10);
      const calculatedIndent = isNaN(numericDepth) ? 0 : numericDepth * 40;
      const indentStyle = "style=\"padding-left: " + calculatedIndent + "px !important;\"";

      let html = '' +
              '<tr id="comment-row-' + commentId + '">' +
              '<td ' + indentStyle + '>' +
              '<div class="comment-row d-flex justify-content-between align-items-center">' +
              '<div class="comment-content">' +
              '<strong>' + escapeHtml(comment.writer || '익명') + '</strong>: ' + escapeHtml(comment.content || '') +
              '</div>' +
              '<div class="comment-actions">' +
              '<button class="btn btn-outline-primary btn-sm ml-2" onclick="showReplyInput(' + commentId + ')">답글</button>' +
              '<button class="btn btn-outline-danger btn-sm ml-1" onclick="deleteComment(' + commentId + ')">삭제</button>' +
              '</div>' +
              '</div>' +
              '<div id="reply-input-' + commentId + '" class="mt-2" style="display: none;">' +
              '<div class="input-group input-group-sm">' +
              '<input type="text" id="reply-content-' + commentId + '" class="form-control" placeholder="답글을 입력하세요..." />' +
              '<div class="input-group-append">' +
              '<button class="btn btn-secondary" onclick="submitReply(' + commentId + ')">작성</button>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="comment-date text-muted small mt-1">' +
              formatDateTime(comment.createdAt) +
              '</div>' +
              '</td>' +
              '</tr>';

      if (comment.children && comment.children.length > 0) {
        comment.children.forEach(child => {
          html += renderComment(child, numericDepth + 1);
        });
      } else {
        console.log("대댓글 없음");
      }


      return html;
    }

    // 무한 스크롤
    $(window).scroll(function() {
      if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
        loadComments();
      }
    });

    function showReplyInput(commentId) {
      $("#reply-input-" + commentId).toggle();
    }

    function submitReply(parentId) {
      const content = $("#reply-content-" + parentId).val();
      if (!content) {
        alert("답글 내용을 입력해주세요.");
        return;
      }

      $.ajax({
        url: "/api/v1/articles/" + articleId + "/comments",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ content: content, parentId: parentId }),
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwt") },
        success: function() {
          $("#comment-list table tbody").empty();
          currentPage = 0;
          hasMoreComments = true;
          loadComments(); // 전체 댓글 새로 불러오기
        },
        error: function(xhr) {
          alert("답글 작성 실패: " + xhr.responseText);
        }
      });
    }

    function deleteComment(commentId) {
      if (!confirm("댓글을 삭제하시겠습니까?")) return;

      $.ajax({
        url: "/api/v1/articles/" + articleId + "/comments/" + commentId,
        type: "DELETE",
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        success: function () {
          $("#comment-row-" + commentId).remove();
        },
        error: function (xhr) {
          alert("댓글 삭제 실패: " + (xhr.responseJSON?.message || xhr.responseText));
        }
      });
    }

    function formatDateTime(datetimeString) {
      const date = new Date(datetimeString);
      return date.toLocaleString("ko-KR", {
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit'
      });
    }



    function toggleLike() {
      $.ajax({
        url: "/api/v1/articles/" + articleId + "/likes",
        type: "POST",
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        success: function(response) {
          const likeStatus = response.data;

          if (likeStatus === 'LIKE_ADDED') {
            // 좋아요 상태로 버튼 UI 변경
            $("#like-button").addClass("liked");
          } else if (likeStatus === 'LIKE_REMOVED') {
            // 좋아요 해제 상태로 UI 복원
            $("#like-button").removeClass("liked");
          }

          loadLikeCount(); // 좋아요 수 최신화
        },
        error: function(xhr) {
          Swal.fire("오류", "좋아요 요청 실패: " + (xhr.responseJSON?.message || xhr.responseText), "error");
        }
      });
    }

    function loadLikeCount() {
      $.ajax({
        url: "/api/public/articles/" + articleId + "/likes",
        type: "GET",
        success: function(response) {
          $("#likesCount").text(response.data);
        },
        error: function(xhr) {
          console.error("좋아요 수 조회 실패:", xhr.responseText);
        }
      });
    }
  </script>
</head>
<body>

<div class="container">
  <div class="header">
    <span class="title">스터디 모집 상세보기</span>
    <div class="button-group">
      <button id="listBtn" onclick="listReq()">목록</button>
      <button id="updateBtn" style="display: none;" onclick="updateReq()">수정</button>
      <button id="deleteBtn" style="display: none;" onclick="deleteReq()">삭제</button>
    </div>

    <button id="requestJoinStudyGroup" style="display:none;">스터디 그룹 가입 요청</button>
  </div>
  <div class="detail-grid">
    <div>
      <label for="articleId">글 번호:</label>
      <div class="detail-box" id="articleId"></div>
    </div>
    <div>
      <label for="title">스터디 제목:</label>
      <div class="detail-box" id="title"></div>
    </div>
    <div>
      <label for="author">작성자:</label>
      <div class="detail-box" id="author"></div>
    </div>
  </div>
  <label for="content">스터디 상세 내용:</label>
  <div class="content-box" id="content"></div>
  <div class="stats">
    <div class="like">
      <button onclick="toggleLike()">좋아요</button>
      <span id="likesCount">0</span>
    </div>
    <div class="views">
      조회수: <span id="viewsCount">000</span>
    </div>
    <div class="date">
      작성일: <span id="createdAt"></span>
    </div>
    <div class="data">
      <div id="articleData"></div>
    </div>
  </div>

  <hr size="10"/>

  <!-- 댓글 제목 -->
  <div class="comment-title">댓글</div>

  <!-- 댓글 목록 -->
  <div id="comment-list" class="comment-list">
    <table>
      <thead>
      <tr></tr>
      </thead>
      <tbody>
      <!-- 댓글이 여기 삽입됩니다 -->
      </tbody>
    </table>
  </div>

  <!-- 더 보기 표시 -->
  <div id="moreCommentsIndicator">
    더 많은 댓글이 있습니다.
  </div>

  <!-- 댓글 작성 부분 -->
  <div id="comment-write" class="comment-write">
    <input type="text" id="contents" placeholder="댓글을 입력하세요..." />
    <button id="comment-write-btn" onclick="commentWrite()">댓글 작성</button>
  </div>
</div>
</body>
</html>
