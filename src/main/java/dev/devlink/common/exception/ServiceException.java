package dev.devlink.common.exception;

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
