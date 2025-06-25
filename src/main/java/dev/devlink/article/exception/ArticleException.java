package dev.devlink.article.exception;

import dev.devlink.common.exception.CommonError;
import dev.devlink.common.exception.ServiceException;
import lombok.Getter;

@Getter
public class ArticleException extends ServiceException {

    public ArticleException(CommonError commonError) {
        super(commonError);
    }
}
