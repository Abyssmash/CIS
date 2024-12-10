package com.cis.personal_task.service;

import com.cis.personal_task.dto.TaskFileDTO;
import com.cis.personal_task.exception.FileUploadException;
import com.cis.personal_task.repository.TaskFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskFileServiceImpl implements TaskFileService {

    private final TaskFileRepository taskFileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public TaskFileServiceImpl(TaskFileRepository taskFileRepository) {
        this.taskFileRepository = taskFileRepository;
    }

    // 파일 저장
    @Override
    public void saveTaskFile(int task_num, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    // 파일 크기 확인 (10MB 이하)
                    if (file.getSize() > 10 * 1024 * 1024) {
                        throw new FileUploadException("파일 크기가 너무 큽니다. 10MB 이하의 파일만 업로드 가능합니다.");
                    }

                    // 암호화된 파일 이름 생성 (확장자 포함)
                    String fileExtension = getFileExtension(file.getOriginalFilename());
                    String encryptedFileName = UUID.randomUUID().toString() + fileExtension;
                    Path targetPath = Paths.get(uploadDir, encryptedFileName);

                    // 파일 저장
                    Files.copy(file.getInputStream(), targetPath);

                    // 파일 정보 DB 저장
                    TaskFileDTO taskFileDTO = TaskFileDTO.builder()
                            .task_num(task_num)
                            .file_name(encryptedFileName)  // 암호화된 파일 이름
                            .file_originname(file.getOriginalFilename())  // 원본 파일 이름
                            .upload_at(LocalDateTime.now())  // 현재 시간 설정
                            .build();

                    // DB에 파일 정보 저장
                    taskFileRepository.saveTaskFile(taskFileDTO);

                } catch (IOException e) {
                    throw new FileUploadException("파일 저장에 실패했습니다.", e);
                }
            }
        }
    }

    // 파일 조회
    @Override
    public TaskFileDTO findTaskFile(int task_num, String file_name) {
        return taskFileRepository.findByTaskNumAndFileName(task_num, file_name);
    }

    // 업무 파일 목록 조회
    @Override
    public List<TaskFileDTO> findTaskFilesByTaskId(int task_num) {
        return taskFileRepository.findByTaskNum(task_num);
    }

    // 파일 다운로드
    @Override
    public File downloadFile(int task_num, String file_name) throws IOException {
        TaskFileDTO taskFileDTO = taskFileRepository.findTaskFile(task_num, file_name);

        if (taskFileDTO == null) {
            throw new IOException("파일을 찾을 수 없습니다.");
        }

        Path filePath = Paths.get(uploadDir, taskFileDTO.getFile_name());

        // 파일이 존재하는지 확인
        File file = filePath.toFile();
        if (!file.exists()) {
            throw new IOException("파일이 서버에 존재하지 않습니다.");
        }

        return file;
    }

    // 파일 확장자 추출
    private String getFileExtension(String file_name) {
        int lastDotIndex = file_name.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : file_name.substring(lastDotIndex);
    }
}
