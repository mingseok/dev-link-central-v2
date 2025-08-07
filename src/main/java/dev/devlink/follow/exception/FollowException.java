package dev.devlink.follow.exception;

import dev.devlink.common.exception.CommonError;
import dev.devlink.common.exception.ServiceException;

public class FollowException extends ServiceException {
    public FollowException(CommonError error) {
        super(error);
    }
}
