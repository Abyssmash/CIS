package com.cis.personal_task.service;

import com.cis.personal_task.dto.TaskFileDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TaskFileService {

    // 파일 저장
    void saveTaskFile(int task_num, List<MultipartFile> files) throws Exception;

    // 파일 조회 (업무 ID와 파일명으로 특정 파일 조회)
    TaskFileDTO findTaskFile(int task_num, String file_name);

    // 파일 다운로드
    File downloadFile(int taskNum, String fileName) throws IOException;

    // 업무 파일 목록 조회 (업무 ID로 해당 업무에 속한 모든 파일 목록 조회)
    List<TaskFileDTO> findTaskFilesByTaskId(int taskNum);
}
