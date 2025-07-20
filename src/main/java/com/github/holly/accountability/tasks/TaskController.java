package com.github.holly.accountability.tasks;
import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/tasks")
@ResponseBody
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public List<Task> getAllTasks(@AuthenticationPrincipal AccountabilitySessionUser user){
        return taskRepository.findByUserId(user.getId());
    }

    @PostMapping("/add")
    public Task addTask(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestBody TaskEditRequest request){
        Task newTask = new Task();
        newTask.setDescription(request.getDescription());
        newTask.setUser(userRepository.getReferenceById(user.getId()));
        return taskRepository.save(newTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        validateUserTask(taskId, user.getId());
        taskRepository.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}")
    public Task editTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal AccountabilitySessionUser user,
            @RequestBody TaskEditRequest request
    ){
        Task task = validateUserTask(taskId, user.getId());
        task.setDescription(request.getDescription());
        return taskRepository.save(task);
    }

    @PostMapping("/{taskId}/start")
    public Task startTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        Task task = validateUserTask(taskId, user.getId());
        task.setTimeStart(LocalDateTime.now());
        task.setStatus(TaskStatus.IN_PROGRESS);
        return task;
    }

    @PostMapping("/{taskId}/end")
    public Task endTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        Task task = validateUserTask(taskId, user.getId());
        task.setTimeEnd(LocalDateTime.now());
        task.setStatus(TaskStatus.COMPLETED);
        return task;
    }

    private Task validateUserTask(Long taskId, Long userId){
        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task does not exist."));
        if(!task.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("Task does not exist.");
        }
        return task;
    }

    private TaskDto convertTaskToDto(Task task){
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
        return taskDto;
    }

    private Duration totalTimeDuration(LocalDateTime startTime, LocalDateTime endTime){
        return Duration.between(startTime, endTime);
        //duration.getSeconds() for all in seconds
        //..toMinutes(), toHours(). all in each unit
        //..toHoursPart(), toMinutesPart() separated values
    }
}
