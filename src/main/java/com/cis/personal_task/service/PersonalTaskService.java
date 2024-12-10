package com.cis.personal_task.service;

import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.dto.TaskFileDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonalTaskService {

      // 업무 전송
      void sendPersonalTask(PersonalTaskDTO taskDTO, HttpSession session) throws Exception;

      // 받은 업무 목록 조회
      List<PersonalTaskDTO> getReceivedTasks(String receive_id) throws Exception;

      // 상태에 따른 업무 목록 조회 (페이징 포함)
      List<PersonalTaskDTO> getTasksByStatus(String task_status, int page, int size);

      // 전체 업무 목록 조회 (페이징 포함)
      List<PersonalTaskDTO> getAllTasks(int page, int size);

//      // 업무 상태 변경 (진행 -> 완료)
//      void updateTaskStatus(int taskId);

      // 업무 파일 저장
      void saveTaskFiles(int taskNum, List<MultipartFile> files, HttpSession session) throws Exception;

      // 파일 다운로드
      TaskFileDTO findTaskFileByTaskIdAndFileName(int task_num, String file_name);

      // 업무 상세보기
      PersonalTaskDTO getTaskDetails(int task_num);

      // 업무 파일 가져오기
      List<TaskFileDTO> getTaskFiles(int task_num);

      // 받은 업무 목록을 페이징 처리하여 조회
      List<PersonalTaskDTO> getReceivedTasksWithPagination(String receive_id, int page, int size);

      // 업무 수행도 변경
      void updateTaskStatus(int task_num, String task_status);

}
