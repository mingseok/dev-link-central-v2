package dev.devlink.member.exception;

import dev.devlink.common.exception.CommonError;
import dev.devlink.common.exception.ServiceException;

public class MemberException extends ServiceException {

    public MemberException(CommonError commonError) {
        super(commonError);
    }
}
