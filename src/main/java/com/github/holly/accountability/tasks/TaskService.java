package com.github.holly.accountability.tasks;

import kotlin.reflect.jvm.internal.impl.resolve.constants.FloatValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TaskService {

    public Float calculateTotal(List<TaskDto> taskList){
        Duration combinedTime = taskList.stream()
                .map(TaskDto::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        return (float) (((double) combinedTime.toMinutes() /60) * 13.0);
    }

    public Float calculateFromTaskDto(TaskDto taskDto){
        Duration time = taskDto.getDuration();
        return (float) (((double) time.toMinutes() /60) * 13.0);
    }

}
