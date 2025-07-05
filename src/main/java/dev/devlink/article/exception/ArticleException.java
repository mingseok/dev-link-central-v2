package dev.devlink.article.exception;

import dev.devlink.common.exception.CommonError;
import dev.devlink.common.exception.ServiceException;

public class ArticleException extends ServiceException {

    public ArticleException(CommonError commonError) {
        super(commonError);
    }
}
