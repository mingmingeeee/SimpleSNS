package com.min.sns.service;

import com.min.sns.exception.ErrorCode;
import com.min.sns.exception.SnsApplicationException;
import com.min.sns.model.AlarmArgs;
import com.min.sns.model.AlarmType;
import com.min.sns.model.entity.AlarmEntity;
import com.min.sns.model.entity.UserEntity;
import com.min.sns.repository.AlarmRepository;
import com.min.sns.repository.EmitterRepository;
import com.min.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final static long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;
    private final AlarmRepository alarmRepository;
    private final UserEntityRepository userEntityRepository;

    public void send(AlarmType type, AlarmArgs arg, Integer receiverUserId) {
        // alarm save
        // (post를 작성한 사람) ,,,, AlarmArgs(like 단 사람, postId)
        UserEntity user = userEntityRepository.findById(receiverUserId).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND));
        AlarmEntity alarm = alarmRepository.save(AlarmEntity.of(user, type, arg));

        // 특정 브라우저에게 알람을 보내줘야 함
        emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
            try{
                sseEmitter.send(sseEmitter.event().id(alarm.getId().toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(receiverUserId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }

    // 알람 connect
    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(userId, sseEmitter);

        // 끝났을 때는
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));

        // timeout일 때
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        // 전송
        try {
            // id: 몇 번째 event 인지 발급하기
            // name: event name

            sseEmitter.send(sseEmitter.event().id("").name(ALARM_NAME).data("connect complete"));
        } catch (IOException exception) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }

}
