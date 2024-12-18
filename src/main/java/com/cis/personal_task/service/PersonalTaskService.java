package com.cis.personal_task.service;

import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.dto.TaskFileDTO;
import com.cis.personal_task.exception.FileUploadException;
import com.github.pagehelper.Page;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface PersonalTaskService {


      // 수행도별 업무 리스트 가져오기
      List<PersonalTaskDTO> getTasksByStatus(String task_status);

      // 메인 화면 가져오기
      List<PersonalTaskDTO> getMainTasks(String emp_id);

      // 업무 전송 (업무 데이터 삽입 및 파일 첨부)
      void sendTask(PersonalTaskDTO personalTaskDTO, List<MultipartFile> files) throws FileUploadException;

      // 받는 사람 ID 유효성 검사
      boolean checkReceiveId(String receiveId);

      // 업무 상세 조회
      PersonalTaskDTO getTaskDetail(int taskNum);

      // 업무 상태를 '완료'로 변경
      void updateTaskStatusToComplete(int taskNum);

}


