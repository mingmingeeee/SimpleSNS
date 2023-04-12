package com.min.sns.controller;

import com.min.sns.controller.request.UserJoinRequest;
import com.min.sns.controller.request.UserLoginRequest;
import com.min.sns.controller.response.AlarmResponse;
import com.min.sns.controller.response.Response;
import com.min.sns.controller.response.UserJoinResponse;
import com.min.sns.controller.response.UserLoginResponse;
import com.min.sns.exception.ErrorCode;
import com.min.sns.exception.SnsApplicationException;
import com.min.sns.model.User;
import com.min.sns.service.AlarmService;
import com.min.sns.service.UserService;
import com.min.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AlarmService alarmService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        User user = userService.join(request.getName(), request.getPassword());

        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        log.info("login - controller");
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class).orElseThrow(() -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,
                "{Casting to User class failed}"));
        return Response.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }

    // SseEmitter 사용
    // Front EventSource => header 지원 X 라서 path에 넣음
    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(Authentication authentication) {

        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class).orElseThrow(() -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,
                "{Casting to User class failed}"));

        return alarmService.connectAlarm(user.getId());

    }

}

/*
*
* userEntity가 아니라 userId로 가져오기
* Entity하면 join이 발생하니까!
*
* */