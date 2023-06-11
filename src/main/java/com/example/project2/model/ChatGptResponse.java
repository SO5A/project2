package com.example.project2.model;

import org.springframework.http.HttpStatus;

public class ChatGptResponse {
    private HttpStatus status;
    private String message;

    public ChatGptResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}