package com.cis.personal_task.repository;

import com.cis.personal_task.dto.PersonalTaskDTO;
import com.cis.personal_task.dto.TaskFileDTO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
@Mapper
public interface PersonalTaskRepository {

    // 모든 업무 가져오기 (메인)
    List<PersonalTaskDTO> findAllTasks();

    // 상태별 업무 가져오기 (메인)
    List<PersonalTaskDTO> findTasksByStatus(@Param("task_status") String task_status);

    // 받은 사람 id로 개인 업무 조회 (로그인한 사람 = 받은 사람)
    List <PersonalTaskDTO> getMainTasks(String receive_id);

    // 업무 보내기
    void insertTask(PersonalTaskDTO personalTaskDTO);

    // 받는 사람 ID 유효성 검사
    Integer existsReceiveId(@Param("receiveId") String receiveId);

    // 업무 상세 정보 가져오기
    PersonalTaskDTO findTaskByTaskNum(int taskNum);

    void insertTask(String savedFileName, int taskNum);

    void updateTaskStatusToComplete(int taskNum);
}
