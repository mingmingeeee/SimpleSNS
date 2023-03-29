package com.min.sns.repository;

import com.min.sns.model.entity.AlarmEntity;
import com.min.sns.model.entity.LikeEntity;
import com.min.sns.model.entity.PostEntity;
import com.min.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Long> {

    Optional<AlarmEntity> findByUserAndPost(UserEntity user, PostEntity post);

    // where * from "like" where post_id = 2
    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post =:post")
    Integer countByPost(@Param("post") PostEntity post);
}
