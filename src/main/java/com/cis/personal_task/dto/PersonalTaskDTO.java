package com.cis.personal_task.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor//  // 기본 생성자 생성
public class PersonalTaskDTO {
    private int task_num;
    private String task_title;
    private String task_content;
    private String task_status;
    private LocalDateTime create_at;
    private String directive_id;
    private String receive_id;

    @Builder
    public PersonalTaskDTO(int task_num, String task_title, String task_content, String task_status,
                           LocalDateTime create_at, String directive_id, String receive_id) {
        this.task_num = task_num;
        this.task_title = task_title;
        this.task_content = task_content;
        this.task_status = task_status;
        this.create_at = create_at;
        this.directive_id = directive_id;
        this.receive_id = receive_id;
    }
}