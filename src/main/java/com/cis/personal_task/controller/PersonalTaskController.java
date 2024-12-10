package com.cis.personal_task.controller;

import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.dto.TaskFileDTO;
import com.cis.personal_task.service.PersonalTaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class PersonalTaskController {

    private final PersonalTaskService personalTaskService;

    public PersonalTaskController(PersonalTaskService personalTaskService) {
        this.personalTaskService = personalTaskService;
    }

    // 업무 전송 처리
    @PostMapping("/send")
    public ResponseEntity<String> sendTask(@RequestBody PersonalTaskDTO taskDTO, HttpSession session) {
        try {
            personalTaskService.sendPersonalTask(taskDTO);
            return ResponseEntity.ok("업무 보내기를 완료했습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("업무 보내기에 실패했습니다.: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업무 보내기에 실패했습니다.: " + e.getMessage());
        }
    }

    // 받은 업무 리스트 조회 (Thymeleaf View)
    @GetMapping("/received-view")
    public String getReceivedTasksView(Model model, HttpSession session) {
        String receiveId = (String) session.getAttribute("emp_id");
        if (receiveId == null) {
            throw new IllegalStateException("로그인되지 않았습니다.");
        }
        try {
            List<PersonalTaskDTO> receivedTasks = personalTaskService.getReceivedTasks(receiveId);
            model.addAttribute("receivedTasks", receivedTasks);
            return "receivedTasks"; // Thymeleaf 템플릿 이름
        } catch (Exception e) {
            throw new IllegalStateException("받은 업무를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 업무 상태별 업무 목록 조회
    @GetMapping
    public String getTasksByStatus(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        int size = 6;

        List<PersonalTaskDTO> tasks;

        if (status == null || "전체".equals(status)) {
            tasks = personalTaskService.getAllTasks(page, size);
        } else {
            tasks = personalTaskService.getTasksByStatus(status, page, size);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage", page);
        model.addAttribute("status", status);
        return "task_list";
    }

    // 업무 상태 완료 처리
    @PostMapping("/{taskNum}/complete")
    public String completeTask(@PathVariable int taskNum) {
        personalTaskService.updateTaskStatus(taskNum, "완료");
        return "redirect:/tasks?status=진행&page=1";
    }

    // 파일 업로드
    @PostMapping("/{taskNum}/upload")
    public ResponseEntity<String> uploadFiles(@PathVariable int taskNum, @RequestParam("files") List<MultipartFile> files) {
        if (files.size() > 3) {
            return ResponseEntity.badRequest().body("최대 3개의 파일만 업로드 가능합니다.");
        }
        try {
            personalTaskService.saveTaskFiles(taskNum, files);
            return ResponseEntity.ok("파일을 성공적으로 업로드 했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("파일 업로드: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 업무 상세보기
    @GetMapping("/detail/{taskNum}")
    public String getTaskDetails(@PathVariable int taskNum, Model model) {
        try {
            PersonalTaskDTO taskDetail = personalTaskService.getTaskDetails(taskNum);
            List<TaskFileDTO> taskFiles = personalTaskService.getTaskFiles(taskNum);

            model.addAttribute("taskDetail", taskDetail);
            model.addAttribute("taskFiles", taskFiles);

            return "personal_task/task_detail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "업무를 찾을 수 없습니다.");
            return "error";
        }
    }

    // 받은 업무 리스트 조회 (페이징 처리)
    @GetMapping("/received")
    public ResponseEntity<List<PersonalTaskDTO>> getReceivedTasksWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        String receiveId = (String) session.getAttribute("emp_id");
        if (receiveId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            List<PersonalTaskDTO> receivedTasks = personalTaskService.getReceivedTasksWithPagination(receiveId, page, size);
            return ResponseEntity.ok(receivedTasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 업무 상태 업데이트 API
    @PatchMapping("/{taskNum}/status")
    public ResponseEntity<String> updateTaskStatus(@PathVariable int taskNum, @RequestParam String taskStatus) {
        if (!"진행".equals(taskStatus) && !"완료".equals(taskStatus)) {
            return ResponseEntity.badRequest().body("유효하지 않은 상태 값입니다.");
        }
        try {
            personalTaskService.updateTaskStatus(taskNum, taskStatus);
            return ResponseEntity.ok("업무 상태가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업무 상태 업데이트 중 오류가 발생했습니다.");
        }
    }
}
