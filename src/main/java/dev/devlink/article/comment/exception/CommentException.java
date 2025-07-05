package dev.devlink.article.comment.exception;

import dev.devlink.common.exception.CommonError;
import dev.devlink.common.exception.ServiceException;

public class CommentException extends ServiceException {

    public CommentException(CommonError commonError) {
        super(commonError);
    }
}
