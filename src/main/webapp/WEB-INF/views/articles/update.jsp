<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>update</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
  <link rel="stylesheet" href="/css/articles/update.css">

  <script>
    function updateArticle() {
      const id = $("#id").val();
      const writerId = $("#writerId").val();
      const writer = $("#writer").val();
      const title = $("#title").val();
      const content = $("#content").val();
      const data = {
        id: id, writerId: writerId, writer: writer, title: title, content: content
      };

      $.ajax({
        url: "/api/v1/articles/" + id,
        type: "PUT",
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        data: JSON.stringify(data),
        contentType: "application/json",
        success: function (response) {
          console.log("글이 업데이트되었습니다.");
          <%--window.location.href = `/api/v1/view/articles/paging?page=${page}`;--%>
          window.location.href = "/view/articles/" + id;
        },
        error: function (error) {
          console.error("글 업데이트 중 오류가 발생했습니다.", error);
          alert("글 업데이트 중 오류가 발생했습니다. " + error.responseText);
        }
      });
    }

    function home() {
      window.history.back();
    }
  </script>

  <script>
    $(document).ready(function () {
      const token = localStorage.getItem("jwt");
      const articleId = window.location.pathname.split("/").pop();

      $.ajax({
        url: "/api/v1/articles/" + articleId,
        type: "GET",
        headers: {
          'Authorization': 'Bearer ' + token
        },
        success: function (res) {
          const article = res.data;
          $("#id").val(article.id);
          $("#writerId").val(article.writerId);
          $("#writer").val(article.writer);
          $("#title").val(article.title);
          $("#content").val(article.content);
        },
        error: function () {
          alert("글 정보를 불러오는 데 실패했습니다.");
        }
      });
    });
  </script>
</head>
<body>
<div class="container">
  <h3 class="text-center mb-4" style="font-weight: 600;">게시글 수정</h3>
  <form name="updateForm">
    <input type="hidden" id="id" value="">
    <input type="hidden" id="writerId" value="">
    <div class="form-group">
      <label for="writer">작성자:</label>
      <input type="text" id="writer" class="form-control" value="" readonly>
    </div>
    <div class="form-group">
      <label for="title">스터디 제목:</label>
      <input type="text" id="title" class="form-control" value="">
    </div>
    <div class="form-group">
      <label for="content">스터디 상세 내용:</label>
      <textarea id="content" class="form-control" rows="5"></textarea>
    </div>
    <div class="form-group button-container">
      <button type="button" class="menu-button" onclick="home()">이전으로</button>
      <input type="button" class="update-button" value="수정하기" onclick="updateArticle()">
    </div>
  </form>
</div>
</body>
</html>
