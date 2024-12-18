package com.cis.member.repository;

import com.cis.member.dto.EmployeeDTO;
import com.cis.member.dto.ManagerDTO;
import com.cis.member.dto.ManagerEmployeeDTO;
import com.cis.member.dto.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Mapper
public interface IF_MemberDao {

    // -------------------- 일반사원 --------------------
    // 전체 사원 리스트 조회.
    public List<ManagerEmployeeDTO> selectAll(int startIndex, int pageSize) throws Exception;

    // 아이디 중복 체크.
    public int check_id(String id) throws Exception;

    // 신규 사원 등록.
    public void insert_employee(EmployeeDTO employeedto) throws Exception;

    // 관리자가 가지고 있는 전체 사원의 정보 조회.
    public List<ManagerDTO> select_all_manager_info(EmployeeDTO employeedto) throws Exception;

    // 로그인
    public EmployeeDTO selectOne(String id) throws Exception;

    // 전체 사원 리스트에서 검색한 결과.
    public List<ManagerEmployeeDTO> selectSearch(String input_name) throws Exception;

    // 전체 사원수 count (페이징)
    public int count_employee() throws Exception;

    // 부서 선택.
    public List<ManagerEmployeeDTO> select_dept_list( String department, int startIndex, int pageSize) throws Exception;

    // 재직상태 선택(select_all).
    public List<ManagerEmployeeDTO> select_work_status_list(String work_status, int startIndex, int pageSize) throws Exception;

    // 전체 사원 리스트에서 이름을 클릭해 한명에 사원에 모든 정보를 조회(select_one).
    public ManagerEmployeeDTO select_one_employee_info (String emp_name) throws Exception;

    // -------------------- 관리자 --------------------
    // 로그인한 사원에 전체정보 조회(select_all).
    public ManagerEmployeeDTO select_login_employee_info(String id) throws Exception;

    // 관라자 사원정보 보충(insert).
    public void modify_employee_info(ManagerEmployeeDTO managerEmployeeDTO) throws Exception;

    // 관리자가 정보를 추가할 사원들에 전체 리스트(select_all).
    public List<ManagerEmployeeDTO>select_manager_add_info(int startIndex, int pageSize) throws Exception;

    // 관리자가 정보를 추가할 한명에 사원에 정보를 조회(select_one).
    public ManagerEmployeeDTO select_one_employee_info_need_complete(String id) throws Exception;

    // 관리자가 보충한 사원 정보를 update(update).
    public void update_complete_employee_info(ManagerEmployeeDTO member) throws Exception;

    // 등록된 전체 사원에 정보 가져오기(select_all).
    public int total_employee_count() throws Exception;

    // 정보 추가가 필요한 사원의 전체 인원수
    public int total_count_need_add_info_employee() throws Exception;

    // 전체 사원리스트에서 콤보박스로 선택한 부서의 인원수
    public int total_selected_dept_employee_count(String dept) throws Exception;

    // 전체 사원리스트에서 콤보박스로 선택한 재직상태의 인원수
    public int total_selected_work_status_employee_count(String work_status) throws Exception;

    // 관리자 신규 사원 정보 등록
    public void manager_insert_new_employee_info(ManagerDTO member) throws Exception;

    // 관리자 신규 사원 정볼 등록시, employee 테이블에도 주민번호를 입력
    public void insert_new_employee_rrn_in_employee(String rrn, String r_num) throws Exception;

    // 사원이 정보를 완성할 필요가 있는 사원의 정보 리스트
    public List<ManagerEmployeeDTO> select_list_need_complete(int startIndex, int pageSize) throws Exception;

    public int count_employee_need_complete() throws Exception;

    // 관리자 사원 퇴사 처리_원진호_1213
    public void modify_employee_info(String rrn, String work_status) throws Exception;






}
