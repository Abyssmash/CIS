package com.cis.personal_task.dto;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class TaskFileDTO {
    private int task_num; // 작업 번호
    private String file_name; // 암호화된 파일 이름
    private String file_originname; // 원본 파일 이름
    private LocalDateTime upload_at; // 파일 업로드 시간

    @Builder
    public TaskFileDTO(int task_num, String file_name, String file_originname, LocalDateTime upload_at) {
        this.task_num = task_num;
        this.file_name = file_name;
        this.file_originname = file_originname;
        this.upload_at = upload_at;
    }
}
