package com.min.sns.model.entity;

import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Data
@Table(name = "\"comment\"", indexes = {
        @Index(name = "post_id_idx", columnList = "post_id")
}) // 색인을 만들어서 찾으면 빠름
@SQLDelete(sql = "UPDATE \"comment\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column(name = "comment")
    private String comment;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    @PrePersist // 저장 전
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate // 업데이트 전
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static CommentEntity of(PostEntity postEntity, UserEntity userEntity, String comment) {
        CommentEntity entity = new CommentEntity();
        entity.setPost(postEntity);
        entity.setUser(userEntity);
        entity.setComment(comment);

        return entity;
    }


}
