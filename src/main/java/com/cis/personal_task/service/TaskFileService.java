package com.cis.personal_task.service;

import com.cis.personal_task.dto.TaskFileDTO;
import com.cis.personal_task.exception.FileUploadException;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TaskFileService {


    String saveFile(MultipartFile file) throws FileUploadException;
}
