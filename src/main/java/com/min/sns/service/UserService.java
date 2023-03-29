package com.min.sns.service;

import com.min.sns.exception.ErrorCode;
import com.min.sns.exception.SnsApplicationException;
import com.min.sns.model.Alarm;
import com.min.sns.model.User;
import com.min.sns.model.entity.AlarmEntity;
import com.min.sns.model.entity.UserEntity;
import com.min.sns.repository.AlarmRepository;
import com.min.sns.repository.UserEntityRepository;
import com.min.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmRepository alarmRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private long expiredTimeMs;

    public User loadUserByUserName(String userMame) {
        return userEntityRepository.findByUserName(userMame).map(User::fromEntity).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userMame))
        );
    }

    @Transactional
    public User join(String userName, String password) {
        // 회원 가입 하려는 userName으로 회원가입된 user가 있는지 체크
        userEntityRepository.findByUserName(userName)
                        .ifPresent(it -> {
                            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
                        });

        // 회원가입 진행
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));

        return User.fromEntity(userEntity);
    }

    public String login(String userName, String password) {

        // 회원 가입 여부 체크
        UserEntity userEntity =
                userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 비밀번호 체크
        if(!encoder.matches(password, userEntity.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);

        return token;
    }

    // alram return
    public Page<Alarm> alarmList(String userName, Pageable pageable) {
        UserEntity userEntity =
                userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        return alarmRepository.findAllByUser(userEntity, pageable).map(Alarm::fromEntity);
    }
}
