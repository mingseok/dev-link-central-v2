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
  <link rel="stylesheet" href="/css/members/home.css">
  <title>프로젝트!</title>

  <script>
    function logout() {
      // JWT 토큰 삭제
      localStorage.removeItem('jwt');
      // 로그아웃 후 홈페이지로 리디렉션
      window.location.href = "/api/v1/view/members/signin";
    }

    function editProfile() {
      window.location.href = "/api/v1/view/member/edit-form";
    }

    function deletePage() {
      window.location.href = "/api/v1/view/member/delete-page";
    }

  </script>
</head>
<body>
<div class="note">
  <div class="menu-title">메뉴</div>
  <button onclick="logout()">로그아웃</button>
  <button onclick="deletePage()">회원탈퇴</button>
  <button onclick="editProfile()">회원수정</button>
  <button onclick="profileView()">프로필 & 친구 목록</button>
  <button onclick="studyRecruitmentArticlePaging()">스터디 모집 게시판</button>
  <button onclick="StudyGroupView()">그룹 관리</button>
  <button onclick="groupFeedView()">그룹 피드 (메인)</button>
  <button onclick="myFeed()">나의 피드</button>
</div>
</body>
</html>
