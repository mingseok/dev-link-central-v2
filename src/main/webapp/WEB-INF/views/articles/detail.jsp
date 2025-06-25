<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.devlink.article.controller.response.ArticleDetailsResponse" %>
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
    <% ArticleDetailsResponse article = (ArticleDetailsResponse) request.getAttribute("article"); %>
    var articleId = <%= article.getId() %>;

    // 게시글 수정 페이지로 이동하는 함수
    function updateReq() {
      console.log("수정 요청");
      window.location.href = "/api/v1/view/articles/update/" + articleId;
    }

    // 게시글 목록 페이지로 이동하는 함수
    function listReq() {
      console.log("목록 요청");
      window.location.href = "/api/v1/view/articles/paging?page=${page}";
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
          window.location.href = "/api/v1/view/articles/paging";
        },
        error: function (error) {
          alert("삭제 중 오류가 발생했습니다: " + error);
        }
      });
    }

    // HTML 인코딩 함수 추가
    function escapeHtml(text) {
      var map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
      };
      return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    }
  </script>

  <script>
    document.addEventListener("DOMContentLoaded", function () {
      const writer = "<%= article.getWriter() %>";
      const loggedInUser = '<c:out value="${member.nickname}" />';

      console.log("writer:", writer);
      console.log("loggedInUser:", loggedInUser);

      if (writer.trim() === loggedInUser.trim()) {
        document.getElementById("updateBtn").style.display = "inline-block";
        document.getElementById("deleteBtn").style.display = "inline-block";
      }
    });
  </script>

  <script type="text/javascript">
    var loggedInUserNickname = '<c:out value="${member.nickname}"/>';
  </script>

  <script>
    $.ajax({
      url: "/api/v1/members/me",
      headers: {
        'Authorization': 'Bearer ' + localStorage.getItem("jwt")
      },
      success: function(response) {
        const loggedInUserId = response.data.id;
        const articleWriterId = <%= article.getWriterId() %>;

        if (loggedInUserId === articleWriterId) {
          $("#updateBtn").show();
          $("#deleteBtn").show();
        }
      },
      error: function() {
        console.error("JWT 인증 사용자 조회 실패");
      }
    });
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
      <div class="detail-box" id="articleId"><%= article.getId() %></div>
    </div>
    <div>
      <label for="title">스터디 제목:</label>
      <div class="detail-box" id="title"><%= article.getTitle() %></div>
    </div>
    <div>
      <label for="author">작성자:</label>
      <div class="detail-box" id="author"><%= article.getWriter() %></div>
    </div>
  </div>
  <label for="content">스터디 상세 내용:</label>
  <div class="content-box" id="content"><%= article.getContent() %></div>
  <div class="stats">
    <div class="like">
      <button onclick="toggleLike()">좋아요</button>
      <span id="likesCount">0</span>
    </div>
    <div class="views">
      조회수: <span id="viewsCount">000</span>
    </div>
    <div class="date">
      작성일: <span>${article.formattedCreatedAt}</span>
    </div>
    <div class="data">
      <div id="articleData" data-article-id="<%= article.getId() %>"></div>
    </div>
  </div>

  <hr size="10"/>

  <div class="comment-title">댓글</div>

  <!-- 댓글 목록 출력 부분 -->
  <div id="comment-list" class="comment-list">
    <table>
      <thead>
      <tr>
      </tr>
      </thead>
      <tbody>
      <!-- 서버로부터 받은 댓글 데이터를 동적으로 삽입합니다. -->
      </tbody>
    </table>
  </div>

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
