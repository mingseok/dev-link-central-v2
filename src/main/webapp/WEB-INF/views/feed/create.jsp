<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
  <link rel="stylesheet" href="/css/feed/create.css">

  <title>피드 작성</title>

  <script>
    const jwt = localStorage.getItem('jwt');
    
    // JWT 토큰 확인
    if (!jwt) {
      alert("로그인이 필요합니다.");
      window.location.href = "/view/members/signin";
    }

    function submitFeed() {
      const content = $("#feed-content").val().trim();
      const imageFile = $("#feed-image")[0].files[0];
      
      if (!content) {
        Swal.fire("알림", "피드 내용을 입력해주세요.", "warning");
        return;
      }

      if (content.length > 1000) {
        Swal.fire("알림", "피드 내용은 1000자를 초과할 수 없습니다.", "warning");
        return;
      }

      // 이미지가 있는 경우 FormData 사용, 없는 경우 JSON 사용
      if (imageFile) {
        const formData = new FormData();
        formData.append('content', content);
        formData.append('image', imageFile);

        $.ajax({
          url: "/api/v1/feeds",
          type: "POST",
          data: formData,
          processData: false,
          contentType: false,
          headers: { 'Authorization': 'Bearer ' + jwt },
          success: function (response) {
            alert('피드 작성 완료!');
            window.location.href = "/view/feeds";
          },
          error: function (xhr, status, error) {
            let errorMessage = "피드 작성에 실패했습니다.";
            if (xhr.responseJSON && xhr.responseJSON.message) {
              errorMessage = xhr.responseJSON.message;
            }
            
            Swal.fire("오류", errorMessage, "error");
          }
        });
      } else {
        $.ajax({
          url: "/api/v1/feeds",
          type: "POST",
          contentType: "application/json",
          data: JSON.stringify({ content: content }),
          headers: { 'Authorization': 'Bearer ' + jwt },
          success: function (response) {
            alert('피드 작성 완료!');
            window.location.href = "/view/feeds";
          },
          error: function (xhr, status, error) {
            let errorMessage = "피드 작성에 실패했습니다.";
            if (xhr.responseJSON && xhr.responseJSON.message) {
              errorMessage = xhr.responseJSON.message;
            }
            
            Swal.fire("오류", errorMessage, "error");
          }
        });
      }
    }

    function goBack() {
      window.location.href = "/view/feeds";
    }

    function goHome() {
      window.location.href = "/view/members/home";
    }

    // 글자 수 카운터
    $(document).ready(function() {
      $("#feed-content").on('input', function() {
        const currentLength = $(this).val().length;
        $("#char-count").text(currentLength + "/1000");
        
        if (currentLength > 1000) {
          $("#char-count").addClass("text-danger");
        } else {
          $("#char-count").removeClass("text-danger");
        }
      });

      // 이미지 파일 선택 이벤트
      $("#feed-image").on('change', function() {
        const file = this.files[0];
        if (file) {
          // 파일 크기 체크 (5MB 제한)
          if (file.size > 5 * 1024 * 1024) {
            Swal.fire("알림", "이미지 파일은 5MB를 초과할 수 없습니다.", "warning");
            $(this).val('');
            return;
          }

          // 이미지 미리보기
          const reader = new FileReader();
          reader.onload = function(e) {
            $("#preview-img").attr('src', e.target.result);
            $("#image-preview").show();
          };
          reader.readAsDataURL(file);
        }
      });

      // 이미지 제거
      $("#remove-image").on('click', function() {
        $("#feed-image").val('');
        $("#image-preview").hide();
      });
    });
  </script>
</head>

<body>
<div class="create-container">
  <!-- 헤더 -->
  <div class="header">
    <h1>✏️ 피드 작성</h1>
    <div class="header-actions">
      <button onclick="goBack()" class="btn btn-secondary back-btn">
        ← 뒤로가기
      </button>
      <button onclick="goHome()" class="btn btn-outline-secondary home-btn">
        🏠 홈으로
      </button>
    </div>
  </div>

  <!-- 피드 작성 폼 -->
  <div class="create-form">
    <div class="form-group">
      <label for="feed-content" class="form-label">무슨 일이 일어나고 있나요?</label>
      <textarea 
        id="feed-content" 
        class="form-control" 
        rows="8" 
        placeholder="오늘 있었던 일, 생각, 느낌을 자유롭게 공유해보세요..."
        maxlength="1000"></textarea>
      <div class="char-counter">
        <small id="char-count" class="text-muted">0/1000</small>
      </div>
    </div>

    <div class="form-group">
      <label for="feed-image" class="form-label">이미지 추가 (선택사항)</label>
      <input type="file" id="feed-image" class="form-control" accept="image/*">
      <div id="image-preview" class="mt-3" style="display: none;">
        <img id="preview-img" src="" alt="미리보기" style="max-width: 300px; max-height: 200px; border-radius: 8px;">
        <button type="button" id="remove-image" class="btn btn-sm btn-outline-danger mt-2">이미지 제거</button>
      </div>
    </div>
    
    <div class="form-actions">
      <button type="button" onclick="submitFeed()" class="btn btn-primary submit-btn">
        📤 피드 게시
      </button>
      <button type="button" onclick="goBack()" class="btn btn-outline-secondary cancel-btn">
        취소
      </button>
    </div>
  </div>
</div>
</body>
</html>
