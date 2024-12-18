package com.cis.personal_task.service;


import com.cis.member.repository.IF_MemberDao;
import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.exception.FileUploadException;
import com.cis.personal_task.repository.PersonalTaskRepository;
import com.cis.personal_task.repository.TaskFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class PersonalTaskServiceImpl implements PersonalTaskService {

    private final PersonalTaskRepository personalTaskRepository;
    private final TaskFileService taskFileService;

    // 생성자에서 TaskFileService 주입을 받지 않음
    public PersonalTaskServiceImpl(PersonalTaskRepository personalTaskRepository, TaskFileService taskFileService) {
        this.personalTaskRepository = personalTaskRepository;
        this.taskFileService = taskFileService;
    }

    @Override
    public void sendTask(PersonalTaskDTO personalTaskDTO, List<MultipartFile> files) {
        // 업무 삽입 처리
        personalTaskRepository.insertTask(personalTaskDTO);

        // 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            // 파일 처리 로직을 TaskFileService에 위임
            for (MultipartFile file : files) {
                try {
                    String savedFileName = taskFileService.saveFile(file); // TaskFileService 사용
                    // DB에 파일 관련 정보 저장
                    personalTaskRepository.insertTask(savedFileName, personalTaskDTO.getTask_num());
                } catch (FileUploadException e) {
                    System.err.println("파일 업로드 오류: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<PersonalTaskDTO> getTasksByStatus(String task_status) {
        if ("전체보기".equals(task_status)) {
            return personalTaskRepository.findAllTasks();
        } else {
            return personalTaskRepository.findTasksByStatus(task_status);
        }
    }

    @Override
    public List<PersonalTaskDTO> getMainTasks(String empId) {
        return personalTaskRepository.getMainTasks(empId);
    }

    @Override
    public boolean checkReceiveId(String receiveId) {
        Integer count = personalTaskRepository.existsReceiveId(receiveId);
        return count != null && count > 0;
    }

    @Override
    public PersonalTaskDTO getTaskDetail(int taskNum) {
        return personalTaskRepository.findTaskByTaskNum(taskNum);
    }

    @Override
    public void updateTaskStatusToComplete(int taskNum) {
        personalTaskRepository.updateTaskStatusToComplete(taskNum);
    }
}
