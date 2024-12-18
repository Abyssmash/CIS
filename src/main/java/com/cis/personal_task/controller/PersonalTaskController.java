package com.cis.personal_task.controller;

import com.cis.member.repository.IF_MemberDao;
import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.exception.FileUploadException;
import com.cis.personal_task.repository.TaskFileRepository;
import com.cis.personal_task.service.PersonalTaskService;
import com.cis.personal_task.service.TaskFileService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PersonalTaskController {
    private final PersonalTaskService personalTaskService;
    private final IF_MemberDao memberDao;
    private final TaskFileService taskFileService;
    private final TaskFileRepository taskFileRepository;

    public PersonalTaskController(PersonalTaskService personalTaskService, IF_MemberDao memberDao, TaskFileService taskFileService
    , TaskFileRepository taskFileRepository) {
        this.personalTaskService = personalTaskService;
        this.memberDao = memberDao;
        this.taskFileService = taskFileService;
        this.taskFileRepository = taskFileRepository;
    }
    private String fileUploadDirectory = "C:/CIS/properties";  // 업로드된 파일이 저장된 디렉토리

    // 메인 화면 (업무 리스트 + 페이징)
    @GetMapping("/tasks")
    public String showMainPage(
            @RequestParam(value = "task_status", required = false, defaultValue = "전체보기") String task_status,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {

        // 페이지 당 보여줄 항목 수 설정
        int pageSize = 5;
        PageHelper.startPage(page, pageSize);

        // 상태에 따른 업무 리스트 가져오기
        List<PersonalTaskDTO> tasks = personalTaskService.getTasksByStatus(task_status);

        // 페이지 정보 생성
        PageInfo<PersonalTaskDTO> pageInfo = new PageInfo<>(tasks);

        // 모델에 데이터 추가
        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage", pageInfo.getPageNum());
        model.addAttribute("totalPages", pageInfo.getPages());
        model.addAttribute("task_status", task_status);

        return "personal_task/task_main";
    }

    // 업무 전송 페이지
    @GetMapping("/tasks/send")
    public String sendTaskForm() {
        return "personal_task/task_send"; // 업무 전송 폼 페이지
    }

    // 업무 전송 처리
    @PostMapping("/tasks/send")
    public String sendTask(PersonalTaskDTO personalTaskDTO,
                           @RequestParam(value = "task_files", required = false) List<MultipartFile> files,
                           HttpSession session, Model model) throws FileUploadException {

        // 세션에서 로그인한 사용자의 ID 가져오기
        String directiveId = (String) session.getAttribute("userId");
        personalTaskDTO.setDirective_id(directiveId); // 업무 발신자 설정

        // 받는 사람의 ID 유효성 검사
        if (!personalTaskService.checkReceiveId(personalTaskDTO.getReceive_id())) {
            model.addAttribute("error", "받는 사람의 ID가 유효하지 않습니다.");
            return "personal_task/task_send"; // 오류 메시지 반환
        }

        // 파일이 없을 경우에도 전송 가능하도록 처리
        if (files == null || files.isEmpty()) {
            files = new ArrayList<>(); // 빈 리스트로 초기화
        }

        // 업무 삽입
        personalTaskService.sendTask(personalTaskDTO, files);

        return "redirect:personal_task/task_main"; // 업무 목록 페이지로 리디렉션
    }
    @RequestMapping("/tasks/checkReceiveId")
    @ResponseBody
    public Map<String, Object> checkReceiveId(@RequestParam("receiveId") String receiveId) {
        System.out.println("Received ID: " + receiveId);  // 확인용 로그
        boolean isValid = personalTaskService.checkReceiveId(receiveId);
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);
        return response;
    }

    @GetMapping("/task_detail/{task_num}")
    public String getTaskDetail(@PathVariable("task_num") int taskNum, Model model) {
        PersonalTaskDTO taskDetail = personalTaskService.getTaskDetail(taskNum);
        if (taskDetail != null) {
            model.addAttribute("task", taskDetail);
            return "personal_task/task_detail";
        } else {
            return "error"; // 업무가 없을 경우 오류 페이지
        }
    }
    @PostMapping("/tasks/complete")
    public String completeTasks(@RequestParam List<Integer> task_ids, Model model) {
        for (Integer taskNum : task_ids) {
            personalTaskService.updateTaskStatusToComplete(taskNum);
        }
        model.addAttribute("message", "업무가 완료되었습니다.");
        return "personal_task/task_main"; // task_complete.html 뷰로 반환
    }

//    @RequestMapping(value = "/sendTask", method = RequestMethod.POST)
//    public String sendTask(PersonalTaskDTO personalTaskDTO, List<MultipartFile> files) {
//        // 업무 전송 로직 호출
//        personalTaskService.sendTask(personalTaskDTO, files);
//        return "redirect:/taskList";  // 작업 목록으로 리디렉션
//    }
}

