package com.cis.main.controller;

import com.cis.Pagination;
import com.cis.attendance.dto.AttendanceDTO;
import com.cis.attendance.service.IF_AttendanceService;
import com.cis.board.paging.PagingResponse;
import com.cis.board.service.IF_board_service;
import com.cis.board.vo.boardVO;
import com.cis.board.vo.searchDTO;
import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.service.PersonalTaskService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private IF_AttendanceService attendanceService;
    private final IF_board_service ifboardservice;
    private final PersonalTaskService personalTaskService;

    // 게시판, 근태관리, 개인업무 목록 조회, 직원 메인화면 페이지 이동
    @GetMapping(value = "emp_main")
    public String empMain(Model model,
                          HttpSession httpSession) throws Exception {
        Object login_emp = httpSession.getAttribute("employee_id");
        if (login_emp == null) return "total_login";

        // 메인화면 목록 리스트 출력하기
        // 자유게시판
        searchDTO params = new searchDTO();
        params.setPage(1);
        params.setRecordSize(3);
        params.setPageSize(1);
        PagingResponse<boardVO> boardvolist = ifboardservice.findAllPost_fr(params);
        // 공지사항
        searchDTO paramsg = new searchDTO();
        paramsg.setPage(1);
        paramsg.setRecordSize(3);
        paramsg.setPageSize(1);
        PagingResponse<boardVO> boardvolistg = ifboardservice.findAllPost(paramsg);
        // 근태관리
        Pagination pagination = new Pagination(6, 3, 1);
        pagination.setStartIndex(0);
        pagination.setPageSize(3);

        List<AttendanceDTO> attendance_list = attendanceService.attendanceList(login_emp, pagination);

        // 개인 업무
        List<PersonalTaskDTO> MainTasks = personalTaskService.getMainTasks((String) login_emp);

        model.addAttribute("attendance_list", attendance_list);
        model.addAttribute("boardvolist", boardvolist);
        model.addAttribute("boardvolistg", boardvolistg);
        model.addAttribute("MainTasks", MainTasks);


        return "main/emp_main";
    }

    // 관리자 메인화면 페이지 이동
    @GetMapping(value = "manager_main")
    public String managerMain() {
        return "main/manager_main";
    }

}
