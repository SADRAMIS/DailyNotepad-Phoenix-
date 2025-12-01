package com.example.dailynotes.dto;

/**
 * DTO для запросов к AI сервису
 */
public class AIRequest {
    private String prompt;
    private String context;
    private Integer maxTokens;

    public AIRequest() {
    }

    public AIRequest(String prompt) {
        this.prompt = prompt;
    }

    public AIRequest(String prompt, String context) {
        this.prompt = prompt;
        this.context = context;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}

