package com.min.sns.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class AlarmArgs {

    // 알람을 발생시킨 사람
    private Integer fromUserId;
    // 알람이 발생된 주체 아이디
    private Integer targetId;

}

// 필드들이 많아지면 컬럼으로 다 지정하는 것보다 args로 관리해주는 것이 더 효율적 (유연하게 대처하는 방법)