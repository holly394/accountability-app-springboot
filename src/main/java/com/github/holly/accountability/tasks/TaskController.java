package com.github.holly.accountability.tasks;
import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.UserRepository;
import com.github.holly.accountability.wallet.Wallet;
import com.github.holly.accountability.wallet.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/tasks")
@ResponseBody
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WalletRepository walletRepository;

    @GetMapping("")
    public List<TaskDto> getAllTasks(@AuthenticationPrincipal AccountabilitySessionUser user){

        List<Task> taskListToConvert = taskRepository.findByUserId(user.getId());
        return taskListToConvert.stream()
                .map(this::convertTaskToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/calculatePaymentCompleted")
    public TaskCalculator calculatePaymentCompleted(@AuthenticationPrincipal AccountabilitySessionUser user){
        if(taskRepository.findCompleted(user.getId())==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        List<Task> taskList = taskRepository.findCompleted(user.getId());
        List<TaskDto> taskDtoList = taskList.stream()
                .map(this::convertTaskToDto)
                .toList();
        TaskCalculator taskCalculator = new TaskCalculator();
        Float total = taskService.calculateTotal(taskDtoList);
        taskCalculator.setPayment(total);

        return taskCalculator;
    }

    @GetMapping("/calculatePaymentInProgress")
    public TaskCalculator calculatePaymentInProgress(@AuthenticationPrincipal AccountabilitySessionUser user){
        if(taskRepository.findApproved(user.getId()) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }

        List<Task> taskList = taskRepository.findInProgress(user.getId());
        List<TaskDto> taskDtoList = taskList.stream()
                .map(this::convertTaskToDto)
                .toList();

        Float total = taskService.calculateTotal(taskDtoList);
        TaskCalculator taskCalculator = new TaskCalculator();
        taskCalculator.setPayment(total);

        return taskCalculator;
    }


    @PostMapping("/add")
    public TaskDto addTask(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestBody TaskEditRequest request){
        Task newTask = new Task();
        newTask.setDescription(request.getDescription());
        newTask.setUser(userRepository.getReferenceById(user.getId()));
        taskRepository.save(newTask);
        return convertTaskToDto(newTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        validateUserTask(taskId, user.getId());
        taskRepository.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}")
    public TaskDto editTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal AccountabilitySessionUser user,
            @RequestBody TaskEditRequest request
    ){
        Task task = validateUserTask(taskId, user.getId());
        task.setDescription(request.getDescription());
        taskRepository.save(task);
        return convertTaskToDto(task);
    }

    @PostMapping("/{taskId}/start")
    public TaskDto startTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        Task task = validateUserTask(taskId, user.getId());

        if(task.getTimeStart() != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task has already started.");
        }

        task.setTimeStart(LocalDateTime.now());
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);
        return convertTaskToDto(task);
    }

    @PostMapping("/{taskId}/end")
    public TaskDto endTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        Task task = validateUserTask(taskId, user.getId());

        if(task.getTimeEnd() != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task has already ended.");
        }

        task.setTimeEnd(LocalDateTime.now());
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);

        //move later to approved tasks once user relationships are established
        TaskDto taskDto = convertTaskToDto(task);
        Float payment = taskService.calculateFromTaskDto(taskDto);
        Wallet wallet = walletRepository.findByUserId(user.getId());
        wallet.addBalance(payment);
        walletRepository.save(wallet);
        //

        return convertTaskToDto(task);
    }


    private Task validateUserTask(Long taskId, Long userId){
        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task does not exist."));
        if(!task.getUser().getId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task does not exist.");
        }
        return task;
    }

    private TaskDto convertTaskToDto(Task task){
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
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
