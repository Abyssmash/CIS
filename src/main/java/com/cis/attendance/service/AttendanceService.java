package com.cis.attendance.service;

import com.cis.Pagination;
import com.cis.attendance.dto.AttendanceDTO;
import com.cis.attendance.repository.IF_AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService implements IF_AttendanceService {

    @Autowired
    private IF_AttendanceRepository attendanceRepository;

    @Override
    public void workStart(AttendanceDTO attendanceDTO) throws Exception {
        attendanceRepository.attendanceInsert(attendanceDTO);
    }

    @Override
    public List<AttendanceDTO> attendanceList(Object login_emp, Pagination pagination) throws Exception {
        return attendanceRepository.attendanceSelectAll(login_emp, pagination);
    }

    @Override
    public void attendanceMod(AttendanceDTO attendanceDTO) throws Exception {
        attendanceRepository.attendanceUpdate(attendanceDTO);;
    }

    @Override
    public int attendanceListCnt(Object login_emp) throws Exception {
        return attendanceRepository.attendanceSelectAllCnt(login_emp);
    }

    @Override
    public int attendanceWorkStartCheck(Object login_emp, String now_date) throws Exception {
        return attendanceRepository.attendanceWorkStartCheck(login_emp, now_date);
    }

    @Override
    public int attendanceWorkEndCheck(Object login_emp, String now_date) throws Exception {
        return attendanceRepository.attendanceWorkEndCheck(login_emp, now_date);
    }

}
