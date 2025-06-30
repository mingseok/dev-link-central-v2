<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
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
  <link rel="stylesheet" href="/css/articles/paging.css">

  <script>
    $(document).ready(function() {
      // 로컬 스토리지에서 JWT 토큰 확인
      const token = localStorage.getItem('jwt');
      if (token) {
        // 토큰이 존재하면 글작성 버튼 표시
        $('#writeButton').show();
      } else {
        // 토큰이 없으면 글작성 버튼 숨김
        $('#writeButton').hide();
      }
    });

    function saveReq() {
      window.location.href = "/api/v1/view/articles/save";
    }
  </script>

  <script>
    $(document).ready(function() {
      // 링크 클릭 이벤트를 잡아 AJAX 요청으로 변환
      $('a.article-link').click(function(e) {
        e.preventDefault(); // 기본 링크 동작 방지
        var href = $(this).attr('href');
        var token = localStorage.getItem('jwt'); // 로컬 스토리지에서 JWT 토큰 가져오기

        $.ajax({
          url: href,
          headers: {'Authorization': 'Bearer ' + token}, // JWT 토큰을 HTTP 헤더에 포함
          success: function(data) {
            console.log("성공")
            window.location.href = href;
          },
          error: function(xhr, status, error) {
            console.error('Error: ' + error);
          }
        });
      });
    });

    function home() {
      window.location.href = "/api/v1/view/members/home";
    }
  </script>
</head>
<body>
<div class="container">
  <div class="header">
    <div class="title">스터디 모집 게시판</div>
    <div class="header-actions">
<%--      <% if ((Boolean) request.getAttribute("isAuthenticated")) { %>--%>
<%--      <button onclick="home()" style="margin-left: 10px;">이전</button>--%>
<%--      <button onclick="saveReq()" style="margin-left: 8px;">글작성</button>--%>
<%--      <% } %>--%>
      <button onclick="home()" style="margin-left: 10px;">나가기</button>
      <button id="writeButton" onclick="saveReq()" style="margin-left: 8px;">글작성</button>
    </div>
  </div>

  <table>
    <thead>
    <tr>
      <th>글번호</th>
      <th>작성자</th>
      <th>스터디 제목</th>
      <th>작성일</th>
    </tr>
    </thead>
    <tbody>
    <c:if test="${not empty articleList}">
      <c:forEach items="${articleList}" var="article" varStatus="status">
        <tr>
          <td>${article.id}</td>
          <td><a href="/api/v1/view/profile/view?memberId=${article.writerId}">${article.writer}</a></td>
          <td><a class="article-link" href="/api/v1/view/articles/${article.id}?page=${articlePage.number + 1}">${article.title}</a></td>
          <td>${article.formattedCreatedAt}</td>
        </tr>
      </c:forEach>
    </c:if>
    <c:if test="${empty articleList}">
      <tr>
        <td colspan="4">게시글이 없습니다.</td>
      </tr>
    </c:if>
    </tbody>
  </table>

  <nav class="pagination">
    <a href="/api/v1/view/articles/paging?page=0">처음</a>
    <!-- 이전 페이지 링크 -->
    <c:choose>
      <c:when test="${articlePage.number eq 0}">
        <span>이전</span>
      </c:when>
      <c:otherwise>
        <a href="/api/v1/view/articles/paging?page=${articlePage.number - 1}">이전</a>
      </c:otherwise>
    </c:choose>

    <c:forEach begin="${startPage}" end="${endPage}" var="page">
      <c:choose>
        <c:when test="${page eq articlePage.number + 1}">
          <span class="active">${page}</span>
        </c:when>
        <c:otherwise>
          <!-- page-1 을 서버로 넘겨야 Pageable이 정상 동작 -->
          <a href="/api/v1/view/articles/paging?page=${page - 1}">${page}</a>
        </c:otherwise>
      </c:choose>
    </c:forEach>

    <!-- 다음 페이지 링크 -->
    <c:choose>
      <c:when test="${articlePage.number + 1 eq articlePage.totalPages}">
        <span>다음</span>
      </c:when>
      <c:otherwise>
        <a href="/api/v1/view/articles/paging?page=${articlePage.number + 1}">다음</a>
      </c:otherwise>
    </c:choose>

    <!-- 마지막 페이지 링크 -->
    <a href="/api/v1/view/articles/paging?page=${articlePage.totalPages - 1}">마지막</a>
  </nav>
</div>
</body>
