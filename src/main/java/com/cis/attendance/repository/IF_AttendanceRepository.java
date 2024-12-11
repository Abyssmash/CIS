package com.cis.attendance.repository;

import com.cis.Pagination;
import com.cis.attendance.dto.AttendanceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface IF_AttendanceRepository {

    public void attendanceInsert(AttendanceDTO attendanceDTO) throws Exception;
    public List<AttendanceDTO> attendanceSelectAll(Object login_emp, Pagination pagination) throws Exception;
    public void attendanceUpdate(AttendanceDTO attendanceDTO);
    public int attendanceSelectAllCnt(Object login_emp) throws Exception;
    public int attendanceWorkStartCheck(Object login_emp, String now_date) throws Exception;
    public int attendanceWorkEndCheck(Object login_emp, String now_date) throws Exception;

}
