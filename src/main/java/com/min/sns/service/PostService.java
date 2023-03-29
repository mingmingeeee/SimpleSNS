package com.min.sns.service;

import com.min.sns.exception.ErrorCode;
import com.min.sns.exception.SnsApplicationException;
import com.min.sns.model.AlarmArgs;
import com.min.sns.model.AlarmType;
import com.min.sns.model.Comment;
import com.min.sns.model.Post;
import com.min.sns.model.entity.*;
import com.min.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public void create(String title, String body, String userName) {

        // 1. user find
        UserEntity userEntity = getUserOrException(userName);
        
        // 2. post save
        postEntityRepository.save(PostEntity.of(title, body, userEntity));

    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = getUserOrException(userName);

        // 1. post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        // 2. post permission (수정한 사람이 로그인한 사람인지)
        if(postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(String userName, Integer postId) {
        UserEntity userEntity = getUserOrException(userName);

        // 1. post exist
        PostEntity postEntity = getPostOrException(postId);

        // 2. post permission (수정한 사람이 로그인한 사람인지)
        if(postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = getUserOrException(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        // 1. post exist
        PostEntity postEntity = getPostOrException(postId);

        // 2. user exist
        UserEntity userEntity = getUserOrException(userName);

        // 3. check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already liked post %d", userName, postId));
        });

        // 4. like.save
        likeEntityRepository.save(LikeEntity.of(postEntity, userEntity));

        // (post를 작성한 사람) ,,,, AlarmArgs(like 단 사람, postId)
        alarmRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ONE_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));

    }

    public int likeCount(Integer postId) {
        // 1. post exist
        PostEntity postEntity = getPostOrException(postId);

        // 2. count like
        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        PostEntity postEntity = getPostOrException(postId);
        UserEntity userEntity = getUserOrException(userName);

        // comment save
        commentEntityRepository.save(CommentEntity.of(postEntity, userEntity, comment));

        // (post를 작성한 사람) ,,,, AlarmArgs(comment 단 사람, postId)
        alarmRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
    }

    private PostEntity getPostOrException(Integer postId) {
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    private UserEntity getUserOrException(String userName) {
        return userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

}
