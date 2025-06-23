package dev.devlink.member.exception;

import dev.devlink.common.exception.CommonError;
import dev.devlink.common.exception.ServiceException;
import lombok.Getter;

@Getter
public class MemberException extends ServiceException {

    public MemberException(CommonError commonError) {
        super(commonError);
    }
}
