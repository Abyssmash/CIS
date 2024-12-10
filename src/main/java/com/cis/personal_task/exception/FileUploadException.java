package com.cis.personal_task.exception;

public class FileUploadException extends RuntimeException {

    // 기본 생성자
    public FileUploadException(String message) {
        super(message);
    }

    // 예외와 원인을 함께 전달하는 생성자
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
