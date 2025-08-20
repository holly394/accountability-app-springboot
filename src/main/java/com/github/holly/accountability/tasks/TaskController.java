package com.github.holly.accountability.tasks;
import com.github.holly.accountability.relationships.Relationship;
import com.github.holly.accountability.relationships.RelationshipRepository;
import com.github.holly.accountability.relationships.RelationshipService;
import com.github.holly.accountability.relationships.RelationshipStatus;
import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserRepository;
import com.github.holly.accountability.wallet.Wallet;
import com.github.holly.accountability.wallet.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

//Backend controller to handle API calls from frontend for our task-related data

//Repositories: TaskRepository, UserRepository, WalletRepository, RelationshipRepository
//Services: TaskService, RelationshipService
//DTOs: convertTaskToDto (TaskData), taskCalculator, TaskEditRequest, TaskStatusDto

@Controller
@RequestMapping("/api/tasks")
@ResponseBody
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RelationshipService relationshipService;

    @GetMapping("")
    public Page<TaskData> getAllTasks(
            @AuthenticationPrincipal AccountabilitySessionUser user,
            @RequestParam(defaultValue = "") List<Long> userIds,
            @RequestParam(defaultValue = "APPROVED, PENDING, COMPLETED, IN_PROGRESS, REJECTED") List<TaskStatus> statuses,
            @PageableDefault(size = 20) Pageable pageable
            ){

        if (userIds.isEmpty()) {
            return taskRepository.findByUserId(user.getId(), statuses, pageable)
                    .map(taskService::convertTaskToDto);
        }

        return taskRepository.findByUserIdIn(userIds, statuses, pageable)
                .map(taskService::convertTaskToDto);
    }

    @GetMapping("/get-partner-tasks")
    public Page<TaskData> getPartnerTasks(@AuthenticationPrincipal AccountabilitySessionUser user,
                                          @RequestParam(defaultValue = "APPROVED, PENDING, COMPLETED, IN_PROGRESS, REJECTED") List<TaskStatus> statuses,
                                          @PageableDefault(size = 20) Pageable pageable) {

        Page<Relationship> relationships = relationshipRepository
                .getRelationshipsByUserIdAndStatusIgnoreDirection(user.getId(), List.of(RelationshipStatus.APPROVED), pageable);
        List<User> partners = relationshipService.deduplicateRelationshipsForUser(relationships.getContent(), user.getId());
        List<Long> partnerIds = partners.stream().map(User::getId).toList();

        return taskRepository.findByUserIdIn(partnerIds, statuses, pageable)
                .map(taskService::convertTaskToDto);
    }


    @GetMapping("/calculatePaymentCompleted")
    public TaskCalculator calculatePaymentCompleted(@AuthenticationPrincipal AccountabilitySessionUser user){
        TaskCalculator taskCalculator = new TaskCalculator();
        Double totalCompletedTasksInSeconds = taskRepository.getTotalSecondsWithEndTime(user.getId(), TaskStatus.COMPLETED);
        if (totalCompletedTasksInSeconds != null){
            Double completedEarnings = (totalCompletedTasksInSeconds/3600)*13;
            taskCalculator.setPayment(completedEarnings);
        }
        return taskCalculator;
    }

    @GetMapping("/calculatePaymentInProgress")
    public TaskCalculator calculatePaymentInProgress(@AuthenticationPrincipal AccountabilitySessionUser user){
        TaskCalculator taskCalculator = new TaskCalculator();
        Double totalInProgressTasksInSeconds = taskRepository.getTotalSecondsNoEndTime(user.getId(), TaskStatus.IN_PROGRESS);
        if (totalInProgressTasksInSeconds != null){
            Double completedEarnings = (totalInProgressTasksInSeconds/3600)*13;
            taskCalculator.setPayment(completedEarnings);
        }
        return taskCalculator;
    }

    @PostMapping("/add")
    public TaskData addTask(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestBody TaskEditRequest request){
        Task newTask = new Task();
        newTask.setDescription(request.getDescription());
        newTask.setUser(userRepository.getReferenceById(user.getId()));
        taskRepository.save(newTask);
        return taskService.convertTaskToDto(newTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        validateUserTask(taskId, user.getId());
        taskRepository.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}")
    public TaskData editTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal AccountabilitySessionUser user,
            @RequestBody TaskEditRequest request
    ){
        Task task = validateUserTask(taskId, user.getId());
        task.setDescription(request.getDescription());
        taskRepository.save(task);
        return taskService.convertTaskToDto(task);
    }

    @PostMapping("/{taskId}/start")
    public TaskData startTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        Task task = validateUserTask(taskId, user.getId());

        if(task.getTimeStart() != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task has already started.");
        }

        task.setTimeStart(LocalDateTime.now());
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);
        return taskService.convertTaskToDto(task);
    }

    @PostMapping("/{taskId}/end")
    public TaskData endTask(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long taskId){
        Task task = validateUserTask(taskId, user.getId());

        if(task.getTimeEnd() != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task has already ended.");
        }

        task.setTimeEnd(LocalDateTime.now());
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);


        return taskService.convertTaskToDto(task);
    }

    @PostMapping("/{taskId}/process")
    public TaskData updateTaskStatus(@AuthenticationPrincipal AccountabilitySessionUser user,
                                     @PathVariable Long taskId,
                                     @RequestBody TaskStatusDto newStatus){

        // FIXME: This should query the database for a task belonging to a partner with which the user has an approved relationship, not reload all relationships from the database
        List<Relationship> partnerRelationships = relationshipRepository.getApprovedRelationshipsByUserIdBothDirections(user.getId());
        List<User> cleanPartnerList = relationshipService.deduplicateRelationshipsForUser(partnerRelationships, user.getId());

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        boolean partnerTask = cleanPartnerList.stream()
                .anyMatch( partner -> (partner.getId()).equals(task.getUser().getId()));

        if (!partnerTask) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!task.getStatus().equals(TaskStatus.COMPLETED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (newStatus.getStatus() == TaskStatus.APPROVED) {
            task.setStatus(TaskStatus.APPROVED);
            TaskData taskDto = taskService.convertTaskToDto(task);
            Double payment = taskService.calculateFromTaskDto(taskDto);
            Wallet wallet = walletRepository.findByUserId(task.getUser().getId());
            wallet.addBalance(payment);
            walletRepository.save(wallet);
        }

        if (newStatus.getStatus() == TaskStatus.REJECTED) {
            task.setStatus(TaskStatus.REJECTED);
        }

        taskRepository.save(task);
        return taskService.convertTaskToDto(task);
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


}
