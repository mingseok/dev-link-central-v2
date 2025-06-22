package dev.devlink.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    private final Status status;
    private final T data;
    private final String message;

    private static final String DEFAULT_SUCCESS_MESSAGE = "요청에 성공하였습니다.";

    public static ApiResponse<Void> successEmpty() {
        return ApiResponse.<Void>builder()
                .status(Status.SUCCESS)
                .data(null)
                .message(DEFAULT_SUCCESS_MESSAGE)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(Status.SUCCESS)
                .data(data)
                .message(DEFAULT_SUCCESS_MESSAGE)
                .build();
    }

    public static ApiResponse<Void> failure(String message) {
        return ApiResponse.<Void>builder()
                .status(Status.FAIL)
                .data(null)
                .message(message)
                .build();
    }

    public enum Status {
        SUCCESS, FAIL
    }
}
