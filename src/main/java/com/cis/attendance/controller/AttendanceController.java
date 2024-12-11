package com.cis.attendance.controller;

import com.cis.Pagination;
import com.cis.attendance.dto.AttendanceDTO;
import com.cis.attendance.service.IF_AttendanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AttendanceController {

    @Autowired
    private IF_AttendanceService attendanceService;

    // 근태관리 목록 조회, 근태관리 페이지 이동
    @GetMapping(value = "attendance")
    public String attendance(@RequestParam(defaultValue = "1") int page,
                             Model model,
                             HttpSession httpSession) throws Exception {
        Object login_emp = httpSession.getAttribute("employee_id");
        if (login_emp == null) return "total_login";

        int totalListCnt = attendanceService.attendanceListCnt(login_emp);

        Pagination pagination = new Pagination(6, totalListCnt, page);
        pagination.setSelectPage(page);

        List<AttendanceDTO> attendance_list = attendanceService.attendanceList(login_emp, pagination);

        model.addAttribute("attendance_list", attendance_list);
        model.addAttribute("pagination", pagination);

        return "attendance/attendance";
    }

    // 출근
    @PostMapping(value = "go_to_work")
    public String workStart(@ModelAttribute AttendanceDTO attendanceDTO,
                            HttpSession httpSession) throws Exception {
        Object login_emp = httpSession.getAttribute("employee_id");
        if (login_emp == null) return "total_login";

        attendanceDTO.setEmp_id((String)login_emp);

        attendanceService.workStart(attendanceDTO);

        return "redirect:attendance";
    }

    // 퇴근
    @PostMapping(value = "leave_work")
    public String workEnd(@ModelAttribute AttendanceDTO attendanceDTO,
                          HttpSession httpSession) throws Exception {
        Object login_emp = httpSession.getAttribute("employee_id");
        if (login_emp == null) return "total_login";

        attendanceDTO.setEmp_id((String)login_emp);

        if (attendanceDTO.getSignificant().equals("퇴근")) attendanceDTO.setSignificant(null);

        attendanceService.attendanceMod(attendanceDTO);

        return "redirect:attendance";
    }

    // 오늘 날짜의 근무 기록 검증
    @GetMapping(value = "attendance/work_start/check")
    @ResponseBody
    public int attendanceWorkStartCheck(@RequestParam("now_date") String now_date, HttpSession httpSession) throws Exception {
        Object login_emp = httpSession.getAttribute("employee_id");
        if (login_emp == null) return 0;

        return attendanceService.attendanceWorkStartCheck(login_emp, now_date);
    }

    // 오늘 날짜의 퇴근 기록 검증
    @GetMapping(value = "attendance/work_end/check")
    @ResponseBody
    public int attendanceWorkEndCheck(@RequestParam("now_date") String now_date, HttpSession httpSession) throws Exception {
        Object login_emp = httpSession.getAttribute("employee_id");
        if (login_emp == null) return 0;

        return attendanceService.attendanceWorkEndCheck(login_emp, now_date);
    }
}
