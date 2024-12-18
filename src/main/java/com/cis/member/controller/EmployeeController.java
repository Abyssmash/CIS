package com.cis.member.controller;

import com.cis.Pagination;
import com.cis.attendance.dto.AttendanceDTO;
import com.cis.attendance.service.IF_AttendanceService;
import com.cis.board.paging.PagingResponse;
import com.cis.board.service.IF_board_service;
import com.cis.board.vo.boardVO;
import com.cis.board.vo.searchDTO;
import com.cis.member.dto.*;
import com.cis.member.service.IF_MemberService;
import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.service.PersonalTaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
public class EmployeeController {

    @Autowired
    IF_MemberService memberService;
    private final IF_board_service ifboardservice;
    private final IF_AttendanceService attendanceService;
    private final PersonalTaskService personalTaskService;

    // 전체 로그인 (로그인 방식 선택.)
    @GetMapping(value="/")
    public String start_page() throws Exception{
        System.out.println("시작화면");
        return "total_login";
    }


    // ========================= 일반사원 ========================

    // 일반사원 로그인창으로 이동.
    @GetMapping(value="/employee_login")
    public String login() throws Exception {
        System.out.println("=== total_login 진입 ===");
        return "Employee/employee_login";
    }

    // 일반사원 로그인.
    @RequestMapping(value="employee_login_action")
    public String employee_login_action(HttpServletRequest request, Model model) throws Exception {
        System.out.println("=== Employee/employee_login 진입 ===");
        String userId = request.getParameter("emp_id");
        String userPass = request.getParameter("emp_pass");
        ManagerEmployeeDTO managerEmployeeDTO = memberService.login_employee_info(userId);
        String password = managerEmployeeDTO.getEmp_pass();
        if(userPass.equals(password)){
            HttpSession session = request.getSession(); // 세션 생성
            session.setAttribute("emp_name", managerEmployeeDTO.getEmp_name());
            session.setAttribute("employee_id", userId);
            session.setAttribute("emp_email", managerEmployeeDTO.getEmp_email());
            session.setAttribute("emp_dept", managerEmployeeDTO.getEmp_dept());
            session.setAttribute("emp_rank", managerEmployeeDTO.getEmp_rank());
            session.setMaxInactiveInterval(600); // 세션 유효 시간을 10 분으로 설정.

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
            List<AttendanceDTO> attendance_list = attendanceService.attendanceList(userId, pagination);
            // 개인업무
            List<PersonalTaskDTO> MainTasks = personalTaskService.getMainTasks(userId);

            model.addAttribute("attendance_list", attendance_list);
            model.addAttribute("boardvolist", boardvolist);
            model.addAttribute("boardvolistg", boardvolistg);
            model.addAttribute("MainTasks", MainTasks);

            return "main/emp_main";
        }
        return "Employee/employee_login";
    }


    // 로그아웃
    @RequestMapping(value="logout_btn")
    public String logout_btn(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        session.invalidate(); // 세션 비활성화.
        return "Employee/employee_login";
//        return "main/emp_main"; // 세션 비활성화 성공 여부 확인 코드.
    }

    // 뒤로가기
    @GetMapping(value="back_btn")
    public String back_btn(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        session.invalidate();
        return "total_login";
    }



    // 일반 사원 "사원가입" 화면으로 이동.
    @GetMapping(value="/join")
    public String join() throws Exception {
        return "Employee/join";
    }

    // 신규 사원 등록.
    @PostMapping(value="employee_join")
    public String add_new_employee(@ModelAttribute EmployeeDTO employeedto) throws Exception {
        System.out.println("=== Employee/join 진입 ===");
        System.out.println("입력받은 신규 사원의 정보 확인 : " + employeedto.toString());
        memberService.add_new_employee(employeedto);
        return "redirect:employee_login"; // 등록 후 로그인 화면으로 이동.
    }

    // "일반사원 등록"에서 "취소"버튼 클릭.
    @GetMapping(value="back_employee_login_page")
    public String back_employee_login_page() throws Exception {
        return "Employee/employee_login";
    }



    // ========================= 관리자 ========================

    // "관리자 로그인" 선택시.
    @GetMapping(value="check_manager_pass")
    public String check_manager_pass()throws Exception {
        return "Manager/manager_pass_check";
    }

    // 관리자가 관리자 비밀번호를 입력 후 "확인" 버튼 클릭시.
    @PostMapping(value="check_input_manager_pass")
    public String manager(@RequestParam("manager_pass") String pass, HttpServletRequest request) throws Exception {
        System.out.println("=== Manager/manager_pass_check 진입 ===");
        // 비밀번호 일치 여부 체크
        boolean check_result = memberService.check_manager_pass(pass);
        if (check_result) {
            HttpSession session = request.getSession();
            session.setAttribute("admin","admin"); // 관리자 세션 추가
            String emp_id = (String) session.getAttribute("admin");
            String emp_name = ifboardservice.getNameById(emp_id);
            session.setAttribute("emp_name", emp_name);
            return "main/manager_main"; // 비밀번호 참 : 관리자 메인화면으로 이동.
        } else {
            return "redirect:check_manager_pass"; // 비밀번호 거짓 : 관리자 비밀번호 입력창으로 이동.
        }
    }

    // 관리자 메인페이지에서 "직원관리" 버튼 클릭시.
    //    public String manage_page(Model model, @ModelAttribute PageDTO pagedto) throws Exception{
    @GetMapping(value="manager_page")
    public String manage_page(@RequestParam(defaultValue = "1") int page, Model model) throws Exception{
        System.out.println("=== main/manager_main 진입 ===");
        int totalCount = memberService.total_count_number();
//        System.out.println("totalcount : " + totalCount + " / " + page);
        Pagination pagination = new Pagination(10, totalCount, page);
        int startIndex = pagination.getStartIndex();
        int pageSize = pagination.getPageSize();
        pagination.setSelectPage(page);
        // 전체 사원 리스트 받기.
        List<ManagerEmployeeDTO> total_employee_list = memberService.total_employee_list(startIndex, pageSize);
        for (ManagerEmployeeDTO member : total_employee_list) {
            System.out.println("사원 정보 : " + member.toString());
        }
        model.addAttribute("pagination", pagination);
        model.addAttribute("employee", total_employee_list);
//        return "Manager/total_list";
        return "Manager/total_list";
    }



    // 전체사원 리스트에서 콤보박스 + 사원이름검색.
    @PostMapping(value="manager_page")
//    public String search(@RequestParam("search_employee") String input_name, Model model) throws Exception{
    public String search(@RequestParam(defaultValue = "1") int page , @RequestParam("select_big_part") String big_part, @RequestParam("department") String department, @RequestParam("select_work_status") String work_status, @RequestParam("search_employee") String input_name , Model model) throws Exception{
//        System.out.println("!!!!!!!");
        int totalCount = 0;
        if(input_name.isEmpty()){
            if(big_part.equals("select_dept") && work_status.equals("select_status")){
//                System.out.println("*** EmployeeController_콤보박스 선택한 부서명 : " + department);
                int total_count_dept = memberService.total_count_selected_dept_employee(department);
//                System.out.println("=== EmployeeController_부서선택 인원수 : " + total_count_dept);
                totalCount = total_count_dept;
            }else if(big_part.equals("select_work_status") && department.equals("select_dept")){
//                System.out.println("*** EmployeeController_콤보박스 선택한 재직상태 : " + work_status);
                int total_count_work_status = memberService.total_count_selected_work_status_employee(work_status);
//                System.out.println("=== EmployeeController_재직상태 선택 인원수 : " + total_count_work_status);
                totalCount = total_count_work_status;
            }
        }
//        System.out.println("EmployeeController_부서 선택한 인원수 count : " + totalCount);
        Pagination pagination = new Pagination(10, totalCount, page);
        int startIndex = pagination.getStartIndex();
        int pageSize = pagination.getPageSize();
//        System.out.println("### EmployeeController_시작 인덱스 / 페이지 사이즈 : " + startIndex + " / " + pageSize);
        pagination.setSelectPage(page);
        List<ManagerEmployeeDTO> list = null;
        // 검색창에 입력한 내용이 없을때.
        if(input_name.isEmpty()){
            if(big_part.equals("select_dept") && work_status.equals("select_status")){
                // 부서 선택.
                list = memberService.total_dept_list( department, startIndex, pageSize);
                for(ManagerEmployeeDTO member : list){
                    System.out.println("EmployeeController_부서 선택한 사원 정보 : " + member.toString());
                }
            }else if(big_part.equals("select_work_status") && department.equals("select_dept")){
                // 재직상태 선택.
                list = memberService.total_work_status_list( work_status,startIndex, pageSize);
                for(ManagerEmployeeDTO member : list){
                    System.out.println("EmployeeController_재직상태 선택한 사원 정보 : " + member.toString());
                }
            }
        }else{
            // 검색창에 입력한 내용이 있을때.
            list = memberService.total_search_employee_list(input_name);
            for(ManagerEmployeeDTO member : list){
                System.out.println("EmployeeController_검색한 사원 정보 : " + member.toString());
            }
        }
        System.out.println("EmployeeController_pagination 정보 : " + pagination);
        for(ManagerEmployeeDTO member : list){
            System.out.println("EmployeeController_화면에 출력할 리스트 내용 finall check : " + member.toString());
        }
        model.addAttribute("pagination", pagination);
        model.addAttribute("employee",list);
        return "Manager/total_list";
    }


    // 전체사원 리스트에서 사원이름 클릭시.
    @GetMapping(value="manager_modify_employee_info")
    public String employee_info(@RequestParam("emp_name") String click_id, Model model) throws Exception{
        System.out.println("@@@@@@@");
        // 이름이 "click_id"인 사원에 모든 정보 조회.
        ManagerEmployeeDTO member = memberService.one_employee_info(click_id);
        if(member.getEmp_id().equals("111")){
            member.setEmp_id("");
        }
        model.addAttribute("one_employee_info", member);
        return "Manager/manager_modify_employee_info";
    }

    // "관리자-사원 정보 수정" 에서 "확인"클릭시.
    @PostMapping(value="send_modify_employee_info")
    public String changed_employee_info(@ModelAttribute ManagerEmployeeDTO member) throws Exception{
        System.out.println("#######");
        memberService.modify_employee_info(member);
        return "redirect:manager_page";
    }

    // "관리자-사원 정보 수정" 부서 콤보박스 구현.
    @ModelAttribute("departmentCodes")
    public List<DepartmentCode> departmentCodes() {
        List<DepartmentCode> departmentCodes = new ArrayList<>();
        departmentCodes.add(new DepartmentCode("business", "영업팀"));
        departmentCodes.add(new DepartmentCode("program", "개발팀"));
        departmentCodes.add(new DepartmentCode("technology", "기술팀"));
        departmentCodes.add(new DepartmentCode("planning", "기획팀"));
        departmentCodes.add(new DepartmentCode("accounting", "회계팀"));
        departmentCodes.add(new DepartmentCode("human_resources", "인사팀"));
        return departmentCodes;
    }

    // 관리자가 직원 정보를 완성시킬 수 있게하는 정보 입력 화면으로 이동.
//    @GetMapping(value="manager_complete_employee_info")
//    @PostMapping(value="manager_complete_employee_info")
//    public String add_complete_employee_info(@RequestParam("emp_id") String id, Model model) throws Exception{
//        System.out.println("%%%%%%%");
//        System.out.println("33333 : " + id);
//        System.out.println("111111");
//        ManagerEmployeeDTO one_employee_info = memberService.select_one_employee_info_need_complete(id);
//        System.out.println("22222 : " + one_employee_info.toString());
//        model.addAttribute("need_complete_one_employee_info", one_employee_info);
//        System.out.println("%%%%%%%----");
//        return "Manager/add_new_employee_info";
//    }

    // 관리자 사원정보 수정후 "확인"클릭
    @PostMapping(value = "manager_complete_employee_info")
    public String manager_mod_info(@RequestParam("resident_num") String rrn, @RequestParam("work_status") String work_status) throws Exception{
        System.out.println("EmployeeController_rrn : " + rrn);
        System.out.println("EmployeeController_work_status : " + work_status);
        memberService.manager_modify_info(rrn,work_status);
        return "redirect:manager_page";
    }

    // 관리자가 입력후 보충된 사원에 정보.
    @PostMapping(value="add_new_employee")
    public String complete_info(@ModelAttribute ManagerEmployeeDTO member) throws Exception{
//        System.out.println("^^^^^^^");
        System.out.println("관리자가 입력한 정보 : " + member.toString());
        memberService.complete_info(member);
//        System.out.println("^^^^^^^----");
        return "redirect:manager_page";
    }

    // 관리자 페이지 "뒤로가기"버튼 구현.
    @GetMapping( value="manager_back")
    public String back(HttpServletRequest request) throws Exception{
        HttpSession session = request.getSession();
        session.invalidate(); // 관리자 로그아웃. (뒤로가기)
        return "Manager/manager_pass_check";
    }


    // ===================================================
    // 관리자는 등록했는데, 사원 본인은 자신에 정보를 등록하지 않는 사원에 명단.

    // 관리자 신규 사원 등록
    @PostMapping(value="manager_input_new_employe_info")
    public String add_new_employee(@ModelAttribute ManagerDTO member) throws Exception{
        System.out.println("EmployeeController_관리자가 신규 등록한 사원의 정보 : " + member.toString());
        memberService.add_new_employee_info(member);
        System.out.println("EmployeeController_rrn : " + member.getResident_num());
        int num = (int)(Math.random()*8999)+1000;
        String r_num = String.valueOf(num);
        System.out.println("생성한 랜덤 숫자 : " + r_num);
        String rrn = member.getResident_num();
        memberService.add_new_employee_rrn_in_employee(rrn,r_num);
        return "main/manager_main";
    }

    // ----------------
    @GetMapping(value="show_list_need_complete")
    public String show_list(Model model, @RequestParam(defaultValue = "1") int page) throws Exception{
        int totalCount = memberService.total_count_employee_need_complete();
        Pagination pagination = new Pagination(10,totalCount, page);
        int startIndex = pagination.getStartIndex();
        int pageSize = pagination.getPageSize();
        pagination.setSelectPage(page);
        System.out.println("EmployeeController_정보 보충이 필요한 사원의 인원수 : " + totalCount);
        List<ManagerEmployeeDTO> list = memberService.employee_need_complete(startIndex, pageSize);
        for(ManagerEmployeeDTO member : list){
            System.out.println("EmployeeController_need_complete : " + member.toString());
        }
        System.out.println("pagination : " + pagination);
        model.addAttribute("pagination", pagination);
        model.addAttribute("list", list);
        return "Manager/employee_list_info_need_complete"; // 미완성 사원 리스트 페이지.
    }


    @GetMapping(value="manager_add_new_emp")
    public String add()throws Exception{
        return "Manager/manager_add_new_employee_info";
    }

    // 관리자 사원 퇴사처리_원진호_1213
    @GetMapping(value="manager_main_page")
    public String manager_main() throws Exception{
        return "redirect:manager_main";
    }

    // 일반사원 로그인 에서 "뒤로가기" 버튼 클릭.
    @GetMapping(value="main_login")
    public String main_login() throws Exception{
        return "total_login";
    }





}
