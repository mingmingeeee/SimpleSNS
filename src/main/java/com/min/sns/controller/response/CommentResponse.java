package com.min.sns.controller.response;

import com.min.sns.model.Comment;
import com.min.sns.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Integer id;

    private String comment;

    private String userName;

    private Integer postId;

    private Timestamp registeredAt;

    private Timestamp updatedAt;


    public static CommentResponse fromComment(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getUserName(),
                comment.getPostId(),
                comment.getRegisteredAt(),
                comment.getUpdatedAt()
        );
    }

}
