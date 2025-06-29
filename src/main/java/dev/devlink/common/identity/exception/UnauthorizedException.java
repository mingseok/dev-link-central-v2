package dev.devlink.common.identity.exception;

import dev.devlink.common.exception.ErrorCode;
import dev.devlink.common.exception.ServiceException;

public class UnauthorizedException extends ServiceException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
