package com.cis.personal_task.controller;

import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.dto.TaskFileDTO;
import com.cis.personal_task.service.PersonalTaskService;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.http.HttpStatus;
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
    public String sendTask(@RequestBody PersonalTaskDTO taskDTO) {
        //try {
            personalTaskService.sendPersonalTask(taskDTO);
            //return ResponseEntity.ok("Task sent successfully.");
        //} catch (IllegalStateException e) {
        //   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to send task: " + e.getMessage());
        //} catch (Exception e) {
        //    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send task: " + e.getMessage());
        // }
    }

    // 받은 업무 리스트 조회 (Thymeleaf View)
    @GetMapping("/received-view")
    public String getReceivedTasksView(Model model) {
        String receiveId = (String) session.getAttribute("emp_id");
        //if (receiveId == null) {
        //    throw new IllegalStateException("로그인되지 않았습니다.");
        //}
        //try {
            List<PersonalTaskDTO> receivedTasks = personalTaskService.getReceivedTasks(receive_id);
            model.addAttribute("receivedTasks", receivedTasks);
            return "receivedTasks"; // Thymeleaf 템플릿 이름
        //} catch (Exception e) {
        //   throw new IllegalStateException("받은 업무를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        //}
    }

    // 업무 상태별 업무 목록 조회 (전체보기, 진행중인 업무, 완료한 업무)
    @GetMapping
    public String getTasksByStatus(
            @RequestParam(required = false) String status,  // status 파라미터를 선택적으로 변경
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        int size = 6;  // 한 페이지에 보여줄 업무 개수

        List<PersonalTaskDTO> tasks;

        // status가 null이거나 "전체"인 경우 전체 업무 조회
        if (status == null || "전체".equals(status)) {
            tasks = personalTaskService.getAllTasks(page, size);
        } else {
            tasks = personalTaskService.getTasksByStatus(status, page, size);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage", page);
        model.addAttribute("status", status);
        return "task_list";  // Thymeleaf 템플릿 이름
    }

    // 업무 상태 완료 처리
    @PostMapping("/{taskId}/complete")
    public String completeTask(@PathVariable int task_num, String task_status) {
        personalTaskService.updateTaskStatus(task_num, task_status);
        return "redirect:/tasks?status=진행&page=1";  // 완료된 후 진행 중인 업무로 리다이렉트
    }

    // 파일 업로드
    @PostMapping("/{taskNum}/upload")
    public String uploadFiles(@PathVariable int task_num, @RequestParam("files") List<MultipartFile> files) {
        if (files.size() > 3) {
            return ("최대 3개의 파일만 업로드 가능합니다.");
        }
        //try {
            personalTaskService.saveTaskFiles(task_num, files);
            return ("Files uploaded successfully.");
        //} catch (IllegalArgumentException e) {
        //    return ResponseEntity.badRequest().body("Invalid file input: " + e.getMessage());
        //} catch (Exception e) {
        //    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        //}
    }

    // 업무 상세보기
    @GetMapping("/detail/{taskId}")
    public String getTaskDetails(@PathVariable int task_num, Model model) {
        try {
            // 업무 상세 정보 가져오기
            PersonalTaskDTO taskDetail = personalTaskService.getTaskDetails(task_num);
            // 업무 파일 목록 가져오기
            List<TaskFileDTO> taskFiles = personalTaskService.getTaskFiles(task_num);

            // 모델에 데이터 추가
            model.addAttribute("taskDetail", taskDetail);
            model.addAttribute("taskFiles", taskFiles);

            return "personal_task/task_detail"; // Thymeleaf 템플릿 파일 경로
        } catch (Exception e) {
            // 예외 처리 (업무가 없거나 오류 발생 시)
            model.addAttribute("errorMessage", "업무를 찾을 수 없습니다.");
            return "error";
        }
    }

    // 받은 업무 리스트 조회 (페이징 처리)
    @GetMapping("/received")
    public List<PersonalTaskDTO> getReceivedTasksWithPagination(
            @RequestParam(defaultValue = "1") int page,  // 기본 페이지는 1
            @RequestParam(defaultValue = "10") int size   // 기본 페이지 크기는 10
    ) {
        //String receiveId = (String) session.getAttribute("emp_id");
        //if (receiveId == null) {
        //    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        //}

        //try {
            List<PersonalTaskDTO> receivedTasks = personalTaskService.getReceivedTasksWithPagination(receiveId, page, size);
            return ResponseEntity.ok(receivedTasks);
        //} catch (Exception e) {
        //    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        //}
    }

    // 업무 상태 업데이트 API
    @PatchMapping("/{taskNum}/status")
    public String updateTaskStatus(@PathVariable int task_num, @RequestParam String task_status) {
        //if (!task_status.equals("진행") && !task_status.equals("완료")) {
        //    return ("유효하지 않은 상태 값입니다.");
        //}
        //try {
            personalTaskService.updateTaskStatus(task_num, task_status);
            return ("업무 상태가 성공적으로 업데이트되었습니다.");
        //} catch (Exception e) {
        //    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업무 상태 업데이트 중 오류가 발생했습니다.");
        //}
    //}
}
