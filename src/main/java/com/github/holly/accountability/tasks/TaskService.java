package com.github.holly.accountability.tasks;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TaskService {

    public Double calculateTotal(List<TaskData> taskList){
        Duration combinedTime = taskList.stream()
                .map(TaskData::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        return combinedTime.toMinutes() /60d * 13.0d;
    }

    public Double calculateFromTaskDto(TaskData taskDto){
        Duration time = taskDto.getDuration();
        return time.toMinutes() /60d * 13.0d;
    }

    public TaskData convertTaskToDto(Task task){
        TaskData taskDto = new TaskData();
        taskDto.setId(task.getId());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
        taskDto.setUserId(task.getUser().getId());
        taskDto.setUserName(task.getUser().getUsername());
        if (task.getTimeStart() != null) {
            Duration duration;
            if(task.getTimeEnd() == null){
                duration = Duration.between(task.getTimeStart(), LocalDateTime.now());
            } else {
                duration = Duration.between(task.getTimeStart(), task.getTimeEnd());
            }
            taskDto.setDuration(duration);
            long HH = duration.toHours();
            long MM = duration.toMinutesPart();
            long SS = duration.toSecondsPart();
            String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
            taskDto.setDurationString(timeInHHMMSS);
        }

        return taskDto;
    }
}
