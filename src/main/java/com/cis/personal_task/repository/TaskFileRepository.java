package com.cis.personal_task.repository;

import com.cis.personal_task.dto.TaskFileDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskFileRepository {
    // 파일 저장
    void saveTaskFile(TaskFileDTO taskFileDTO);

    // 파일 조회 (taskNum과 fileName으로 조회)
    TaskFileDTO findByTaskNumAndFileName(int task_num, String file_name);

    // 작업 ID로 파일 목록 조회
    List<TaskFileDTO> findByTaskNum(int task_num);

    // 업무 파일 조회 (taskNum과 fileName으로 단일 파일 조회)
    TaskFileDTO findTaskFile(int task_num, String file_name);

    // 작업 ID로 파일 목록 조회
    List<TaskFileDTO> findFilesByTaskId(int task_num);
}
