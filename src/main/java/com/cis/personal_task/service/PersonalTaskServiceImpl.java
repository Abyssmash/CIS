package com.cis.personal_task.service;

import com.cis.member.dto.ManagerEmployeeDTO;
import com.cis.member.repository.IF_MemberDao;
import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.dto.TaskFileDTO;
import com.cis.personal_task.repository.PersonalTaskRepository;
import com.cis.personal_task.repository.TaskFileRepository;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PersonalTaskServiceImpl implements PersonalTaskService {

    private final PersonalTaskRepository personalTaskRepository;
    private final TaskFileRepository taskFileRepository;
    private final IF_MemberDao memberDao;

    @Autowired
    public PersonalTaskServiceImpl(
            PersonalTaskRepository personalTaskRepository,
            TaskFileRepository taskFileRepository,
            IF_MemberDao memberDao
    ) {
        this.personalTaskRepository = personalTaskRepository;
        this.taskFileRepository = taskFileRepository;
        this.memberDao = memberDao;
    }

    @Override
    public void sendPersonalTask(PersonalTaskDTO taskDTO) throws Exception {
        String receiveEmpId = taskDTO.getReceive_id();

        // ManagerEmployeeDTO에서 수신자 정보 조회
        ManagerEmployeeDTO receiveEmp = memberDao.findEmployeeById(receiveEmpId);

        if (receiveEmp == null) {
            throw new Exception("해당 ID에 해당하는 수신자가 없습니다.");
        }

        // 수신자 정보로 처리 후, 업무 전송
        String empDept = receiveEmp.getEmp_dept();
        String empRank = receiveEmp.getEmp_rank();
        String empName = receiveEmp.getEmp_name();

        // 업무 전송 처리 (부서, 직급, 이름은 세팅하지 않고 업무만 처리)
        // (예: 이메일 전송, 다른 업무 처리 등)
        // 여기서 부서, 직급, 이름은 직접 사용해서 처리할 수 있음.
        personalTaskRepository.insertTask(taskDTO);
    }


    // 받은 업무 목록 조회
    @Override
    public List<PersonalTaskDTO> getReceivedTasks(String receiveId, HttpSession session) throws Exception {
        if (receiveId == null || receiveId.isEmpty()) {
            throw new IllegalArgumentException("받는 사람 ID는 필수입니다.");
        }
        return personalTaskRepository.findReceivedTasks(receiveId);
    }

    // 업무 파일 저장
    @Override
    public void saveTaskFiles(int task_num, List<MultipartFile> files) throws Exception {
        if (files.size() > 3) {
            throw new IllegalArgumentException("최대 3개의 파일만 업로드 가능합니다.");
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = UUID.randomUUID().toString();
                Path targetPath = Paths.get("uploads/" + fileName);

                // 파일 저장
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // DB에 저장
                TaskFileDTO taskFileDTO = TaskFileDTO.builder()
                        .task_num(task_num)
                        .file_name(fileName)
                        .file_originname(file.getOriginalFilename())
                        .upload_at(LocalDateTime.now())
                        .build();
                taskFileRepository.saveTaskFile(taskFileDTO);
            }
        }
    }

    // 파일 다운로드
    @Override
    public TaskFileDTO findTaskFileByTaskIdAndFileName(int task_num, String file_name) {
        return taskFileRepository.findTaskFile(task_num, file_name);
    }

    // 업무 상세보기
    @Override
    public PersonalTaskDTO getTaskDetails(int task_num) {
        return personalTaskRepository.findTaskById(task_num);
    }

    // 업무 파일 가져오기
    @Override

    public List<TaskFileDTO> getTaskFiles(int task_num) {
        return taskFileRepository.findFilesByTaskId(task_num);
    }
    @Override
    public List<PersonalTaskDTO> getReceivedTasksWithPagination(String receive_id, int page, int size) {
        // PageHelper를 사용하여 페이지 처리
        PageHelper.startPage(page, size);  // page, size를 통해 페이징 처리
        return personalTaskRepository.findReceivedTasksByReceiveId(receive_id);
    }

    // 업무 상태 변경 (진행 -> 완료)
    @Override
    public void updateTaskStatus(int task_num, String task_status) {
        personalTaskRepository.updateTaskStatus(task_num, task_status);
    }

    @Override
    public List<PersonalTaskDTO> getReceivedTasks(String receiveId) {
        return List.of();
    }

    // 상태에 따른 업무 목록 조회 (페이징 포함)
    public List<PersonalTaskDTO> getTasksByStatus(String task_status, int page, int size) {
        int offset = (page - 1) * size;
        return personalTaskRepository.selectTasksByStatus(task_status, size, offset);
    }

    // 전체 업무 목록 조회 (페이징 포함)
    public List<PersonalTaskDTO> getAllTasks(int page, int size) {
        int offset = (page - 1) * size;
        return personalTaskRepository.selectAllTasks(size, offset);
    }

    // 업무 상태 변경 (진행 -> 완료)
    //public void updateTaskStatus(int task_num, String task_status) {
    //    personalTaskRepository.updateTaskStatus(task_num, task_status);
    //}

}
