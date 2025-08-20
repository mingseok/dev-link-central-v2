package dev.devlink.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("성공 응답을 빈 데이터로 생성할 수 있다")
    void successEmpty_Success() {
        // when
        ApiResponse<Void> response = ApiResponse.successEmpty();

        // then
        assertThat(response.getStatus()).isEqualTo(ApiResponse.Status.SUCCESS);
        assertThat(response.getData()).isNull();
        assertThat(response.getMessage()).isEqualTo("요청에 성공하였습니다.");
    }

    @Test
    @DisplayName("성공 응답을 데이터와 함께 생성할 수 있다")
    void success_WithData_Success() {
        // given
        String testData = "테스트 데이터";

        // when
        ApiResponse<String> response = ApiResponse.success(testData);

        // then
        assertThat(response.getStatus()).isEqualTo(ApiResponse.Status.SUCCESS);
        assertThat(response.getData()).isEqualTo(testData);
        assertThat(response.getMessage()).isEqualTo("요청에 성공하였습니다.");
    }

    @Test
    @DisplayName("객체 데이터로 성공 응답을 생성할 수 있다")
    void success_WithObjectData_Success() {
        // given
        TestData testData = new TestData("이름", 25);

        // when
        ApiResponse<TestData> response = ApiResponse.success(testData);

        // then
        assertThat(response.getStatus()).isEqualTo(ApiResponse.Status.SUCCESS);
        assertThat(response.getData()).isEqualTo(testData);
        assertThat(response.getData().getName()).isEqualTo("이름");
        assertThat(response.getData().getAge()).isEqualTo(25);
        assertThat(response.getMessage()).isEqualTo("요청에 성공하였습니다.");
    }

    @Test
    @DisplayName("실패 응답을 생성할 수 있다")
    void failure_Success() {
        // given
        String errorMessage = "요청 처리 중 오류가 발생했습니다.";

        // when
        ApiResponse<Void> response = ApiResponse.failure(errorMessage);

        // then
        assertThat(response.getStatus()).isEqualTo(ApiResponse.Status.FAIL);
        assertThat(response.getData()).isNull();
        assertThat(response.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("빌더 패턴으로 API 응답을 생성할 수 있다")
    void builder_Success() {
        // given
        String customMessage = "커스텀 메시지";
        Integer data = 12345;

        // when
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .status(ApiResponse.Status.SUCCESS)
                .data(data)
                .message(customMessage)
                .build();

        // then
        assertThat(response.getStatus()).isEqualTo(ApiResponse.Status.SUCCESS);
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("Status 열거형이 올바르게 동작한다")
    void status_EnumValues_Correct() {
        // when & then
        assertThat(ApiResponse.Status.SUCCESS).isNotNull();
        assertThat(ApiResponse.Status.FAIL).isNotNull();
        assertThat(ApiResponse.Status.values()).hasSize(2);
    }

    // 테스트용 내부 클래스
    private static class TestData {
        private final String name;
        private final int age;

        public TestData(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
