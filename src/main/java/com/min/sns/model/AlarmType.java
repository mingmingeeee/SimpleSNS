package com.min.sns.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;

@RequiredArgsConstructor
@Getter
public enum AlarmType {

    NEW_COMMENT_ON_POST("new comment!"),
    NEW_LIKE_ONE_POST("new like!"),
    ;

    private final String alarmText;

}
