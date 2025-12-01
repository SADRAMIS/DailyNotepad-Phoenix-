package com.example.dailynotes.dto;

/**
 * DTO для ответов от AI сервиса
 */
public class AIResponse {
    private String content;
    private boolean success;
    private String error;

    public AIResponse() {
    }

    public AIResponse(String content) {
        this.content = content;
        this.success = true;
    }

    public AIResponse(String content, boolean success, String error) {
        this.content = content;
        this.success = success;
        this.error = error;
    }

    public static AIResponse error(String error) {
        return new AIResponse(null, false, error);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

