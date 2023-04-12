package com.min.sns.model.event;

import com.min.sns.model.AlarmArgs;
import com.min.sns.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmEvent {

    private Integer receiveUserId;
    private AlarmType alarmType;
    private AlarmArgs args;

}
