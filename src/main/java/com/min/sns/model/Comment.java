package com.min.sns.model;

import com.min.sns.model.entity.CommentEntity;
import com.min.sns.model.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Comment {

    private Integer id;
    private String comment;

    private Integer postId;

    private String userName;

    private Timestamp registeredAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    public static Comment fromEntity(CommentEntity entity) {

        return new Comment(
                entity.getId(),
                entity.getComment(),
                entity.getPost().getId(),
                entity.getUser().getUserName(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );

    }

}
