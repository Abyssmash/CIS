package com.cis.personal_task.repository;

import com.cis.personal_task.dto.TaskFileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TaskFileRepository {

    void insertTaskFile(String fileName, int taskId);

    void saveFileInfo(String fileName);

    void deleteFileInfo(String fileName);

}
