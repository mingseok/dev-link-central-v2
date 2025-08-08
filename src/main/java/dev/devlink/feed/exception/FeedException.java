package dev.devlink.feed.exception;

import dev.devlink.common.exception.ServiceException;

public class FeedException extends ServiceException {

    public FeedException(FeedError feedError) {
        super(feedError);
    }
}
