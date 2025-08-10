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
      if (!validateForm()) {
        return;
      }

      const id = $("#id").val();
      const writerId = $("#writerId").val();
      const writer = $("#writer").val();
      const title = $("#title").val().trim();
      const content = $("#content").val().trim();
      const data = {
        id: id, writerId: writerId, writer: writer, title: title, content: content
      };

      const $button = $(".update-button");
      const originalText = $button.val();
      $button.prop('disabled', true).val('수정 중...');

      $.ajax({
        url: "/api/v1/articles/" + id,
        type: "PUT",
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        data: JSON.stringify(data),
        contentType: "application/json",
        success: function (response) {
          Swal.fire({
            icon: 'success',
            title: '수정 완료!',
            text: '게시글이 성공적으로 수정되었습니다.',
            confirmButtonColor: '#4facfe'
          }).then(() => {
            window.location.href = "/view/articles/" + id;
          });
        },
        error: function (xhr) {
          let errorMessage = "게시글 수정에 실패했습니다.";
          if (xhr.responseJSON && xhr.responseJSON.message) {
            errorMessage = xhr.responseJSON.message;
          }
          
          Swal.fire({
            icon: 'error',
            title: '오류 발생',
            text: errorMessage,
            confirmButtonColor: '#ff6b6b'
          });
          
          $button.prop('disabled', false).val(originalText);
        }
      });
    }

    function validateForm() {
      const title = $('#title').val().trim();
      const content = $('#content').val().trim();
      
      if (!title) {
        Swal.fire({
          icon: 'warning',
          title: '제목을 입력해주세요',
          text: '게시글 제목을 작성해주세요.',
          confirmButtonColor: '#4facfe'
        });
        return false;
      }
      
      if (title.length > 100) {
        Swal.fire({
          icon: 'warning',
          title: '제목이 너무 깁니다',
          text: '제목은 100자를 초과할 수 없습니다.',
          confirmButtonColor: '#4facfe'
        });
        return false;
      }
      
      if (!content) {
        Swal.fire({
          icon: 'warning',
          title: '내용을 입력해주세요',
          text: '게시글 내용을 작성해주세요.',
          confirmButtonColor: '#4facfe'
        });
        return false;
      }
      
      if (content.length > 2000) {
        Swal.fire({
          icon: 'warning',
          title: '내용이 너무 깁니다',
          text: '내용은 2000자를 초과할 수 없습니다.',
          confirmButtonColor: '#4facfe'
        });
        return false;
      }
      
      return true;
    }

    function home() {
      window.history.back();
    }
  </script>

  <script>
    $(document).ready(function () {
      const token = localStorage.getItem("jwt");
      const articleId = window.location.pathname.split("/").pop();

      if (!token) {
        Swal.fire({
          icon: 'error',
          title: '로그인이 필요합니다',
          text: '게시글을 수정하려면 로그인해주세요.',
          confirmButtonColor: '#4facfe'
        }).then(() => {
          window.location.href = "/view/members/signin";
        });
        return;
      }

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
          
          // 초기 글자 수 표시
          updateCharacterCount();
        },
        error: function (xhr) {
          Swal.fire({
            icon: 'error',
            title: '오류 발생',
            text: '게시글 정보를 불러오는데 실패했습니다.',
            confirmButtonColor: '#ff6b6b'
          }).then(() => {
            window.history.back();
          });
        }
      });

      // 글자 수 카운터
      $("#title").on('input', updateCharacterCount);
      $("#content").on('input', updateCharacterCount);
    });

    function updateCharacterCount() {
      const titleLength = $("#title").val().length;
      const contentLength = $("#content").val().length;
      
      const $titleCounter = $("#title-count");
      const $contentCounter = $("#content-count");
      
      $titleCounter.text(titleLength + "/100");
      $contentCounter.text(contentLength + "/2000");
      
      // 제목 글자 수 경고
      if (titleLength > 80) {
        $titleCounter.addClass("warning").removeClass("danger");
      } else {
        $titleCounter.removeClass("warning danger");
      }
      
      if (titleLength > 100) {
        $titleCounter.addClass("danger").removeClass("warning");
      }
      
      // 내용 글자 수 경고
      if (contentLength > 1800) {
        $contentCounter.addClass("warning").removeClass("danger");
      } else {
        $contentCounter.removeClass("warning danger");
      }
      
      if (contentLength > 2000) {
        $contentCounter.addClass("danger").removeClass("warning");
      }
    }
  </script>
</head>
<body>
<div class="container">
  <div class="form-container">
    <h3>📝 게시글 수정</h3>
    
    <div class="info-box">
      <strong>수정 안내:</strong> 스터디 정보를 명확하고 상세하게 수정하여 다른 개발자들에게 더 나은 정보를 제공해주세요.
    </div>

    <form name="updateForm">
      <input type="hidden" id="id" value="">
      <input type="hidden" id="writerId" value="">
      
      <div class="form-group">
        <label for="writer">작성자</label>
        <input type="text" id="writer" class="form-control" value="" readonly>
      </div>
      
      <div class="form-group">
        <label for="title">스터디 제목</label>
        <input type="text" id="title" class="form-control" value="" placeholder="스터디 제목을 입력하세요" maxlength="100">
        <div class="character-count">
          <span id="title-count">0/100</span>
        </div>
      </div>
      
      <div class="form-group">
        <label for="content">스터디 상세 내용</label>
        <textarea id="content" class="form-control" rows="8" placeholder="스터디에 대한 상세한 정보를 작성해주세요..." maxlength="2000"></textarea>
        <div class="character-count">
          <span id="content-count">0/2000</span>
        </div>
      </div>
      
      <div class="form-group button-container">
        <button type="button" class="menu-button" onclick="home()">
          ← 이전으로
        </button>
        <input type="button" class="update-button" value="✅ 수정 완료" onclick="updateArticle()">
      </div>
    </form>
  </div>
</div>
</body>
</html>
