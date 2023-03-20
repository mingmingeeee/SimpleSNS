package com.min.sns.fixture;

import com.min.sns.model.entity.PostEntity;
import com.min.sns.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId, Integer userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUserName(userName);

        PostEntity result = new PostEntity();
        result.setId(postId);
        result.setUser(user);

        return result;
    }

}
