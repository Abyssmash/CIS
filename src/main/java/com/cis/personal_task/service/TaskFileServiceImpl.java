package com.cis.personal_task.service;

import com.cis.personal_task.exception.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class TaskFileServiceImpl implements TaskFileService {

    private final String fileUploadDirectory = "/path/to/upload";  // 파일 저장 경로

    @Override
    public String saveFile(MultipartFile file) throws FileUploadException {
        // 파일 저장 로직을 구현 (예: 파일 이름 변경 및 저장)
        // 파일 저장 실패 시 FileUploadException을 발생시킬 수 있음
        try {
            String savedFileName = file.getOriginalFilename(); // 파일 이름을 받아옴
            file.transferTo(new File(fileUploadDirectory + "/" + savedFileName));  // 파일 저장
            return savedFileName;
        } catch (Exception e) {
            throw new FileUploadException("파일 저장 오류: " + e.getMessage());
        }
    }
}
