<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- jQuery library -->
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <!-- SweetAlert2 CSS and JS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
  <!-- Font Awesome for icons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <!-- Custom CSS -->
  <link rel="stylesheet" href="/css/feed/create.css">

  <title>피드 작성 - Dev Link Central</title>

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

      // 버튼 로딩 상태로 변경
      const submitBtn = $('button[onclick="submitFeed()"]');
      const originalText = submitBtn.html();
      submitBtn.html('<i class="fas fa-spinner fa-spin"></i> 게시 중...').prop('disabled', true);

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
            Swal.fire("성공", "피드가 성공적으로 작성되었습니다!", "success").then(() => {
              window.location.href = "/view/feeds";
            });
          },
          error: function (xhr, status, error) {
            let errorMessage = "피드 작성에 실패했습니다.";
            if (xhr.responseJSON && xhr.responseJSON.message) {
              errorMessage = xhr.responseJSON.message;
            }
            
            Swal.fire("오류", errorMessage, "error");
          },
          complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
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
            Swal.fire("성공", "피드가 성공적으로 작성되었습니다!", "success").then(() => {
              window.location.href = "/view/feeds";
            });
          },
          error: function (xhr, status, error) {
            let errorMessage = "피드 작성에 실패했습니다.";
            if (xhr.responseJSON && xhr.responseJSON.message) {
              errorMessage = xhr.responseJSON.message;
            }
            
            Swal.fire("오류", errorMessage, "error");
          },
          complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
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

    // 글자 수 카운터 및 이미지 처리
    $(document).ready(function() {
      // 글자 수 카운터
      $("#feed-content").on('input', function() {
        const currentLength = $(this).val().length;
        const charCount = $("#char-count");
        charCount.text(currentLength + "/1000");
        
        if (currentLength > 800) {
          charCount.addClass("text-danger");
        } else {
          charCount.removeClass("text-danger");
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
            $("#file-label").removeClass("has-file").text("📷 이미지 선택하기 (최대 5MB)");
            $("#image-preview").hide();
            return;
          }

          // 이미지 타입 체크
          if (!file.type.startsWith('image/')) {
            Swal.fire("알림", "이미지 파일만 업로드 가능합니다.", "warning");
            $(this).val('');
            $("#file-label").removeClass("has-file").text("📷 이미지 선택하기 (최대 5MB)");
            $("#image-preview").hide();
            return;
          }

          // 파일 이름 표시
          $("#file-label").addClass("has-file").text("✅ " + file.name);

          // 이미지 미리보기
          const reader = new FileReader();
          reader.onload = function(e) {
            $("#preview-img").attr("src", e.target.result);
            $("#image-preview").show();
          };
          reader.readAsDataURL(file);
        }
      });

      // 이미지 제거 버튼
      $("#remove-image").on('click', function() {
        $("#feed-image").val('');
        $("#file-label").removeClass("has-file").text("📷 이미지 선택하기 (최대 5MB)");
        $("#image-preview").hide();
      });

      // 텍스트 영역 자동 높이 조절
      $("#feed-content").on('input', function() {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
      });
    });
  </script>

</head>

<body>
<div class="container">
  <div class="form-container">
    <div class="header">
      <h1>✏️ 피드 작성</h1>
      <p>오늘의 이야기를 공유해보세요</p>
    </div>

    <form id="feedForm">
      <div class="form-group">
        <label for="feed-content" class="form-label">내용</label>
        <textarea 
          id="feed-content" 
          class="form-control" 
          placeholder="무슨 일이 일어나고 있나요? 오늘의 생각과 느낌을 자유롭게 공유해보세요..."
          maxlength="1000"></textarea>
        <div class="character-count">
          <span id="char-count">0/1000</span>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">이미지 첨부 (선택사항)</label>
        <div class="file-input-wrapper">
          <input type="file" id="feed-image" class="file-input" accept="image/*">
          <label for="feed-image" class="file-input-label" id="file-label">
            📷 이미지 선택하기 (최대 5MB)
          </label>
        </div>
        
        <div id="image-preview" class="preview-container" style="display: none;">
          <img id="preview-img" src="" alt="미리보기" class="preview-image">
          <button type="button" id="remove-image" class="remove-image">
            ❌ 이미지 제거
          </button>
        </div>
      </div>
      
      <div class="button-group">
        <button type="button" onclick="submitFeed()" class="btn btn-primary">
          📤 피드 게시하기
        </button>
        <button type="button" onclick="goBack()" class="btn btn-secondary">
          ← 피드 목록으로
        </button>
      </div>
    </form>
  </div>
</div>
</body>
</html>
