package dev.devlink.common.exception;

import dev.devlink.common.exception.ServiceException;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{

    private final CommonError commonError;

    public ServiceException(CommonError commonError) {
        super(commonError.getMessage());
        this.commonError = commonError;
    }

    public CommonError getCommonError() {
        return commonError;
    }
}
