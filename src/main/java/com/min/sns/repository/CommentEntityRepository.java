package com.min.sns.repository;

import com.min.sns.model.entity.CommentEntity;
import com.min.sns.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findAllByPost(PostEntity post, Pageable pageable);

    @Transactional
    @Modifying // 삭제 관련 query이기 때문
    @Query("UPDATE CommentEntity entity SET entity.deletedAt = NOW() where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);
}
