package com.degreely.app.model;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    private String id;
    private String role; // "user" or "assistant"
    private String content;
    private long timestamp;
    private List<String> fileNames = new ArrayList<>();
    private String uniTableJson; // Extracted uni-table JSON if present

    public ChatMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    public ChatMessage(String role, String content) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public List<String> getFileNames() { return fileNames; }
    public void setFileNames(List<String> fileNames) { this.fileNames = fileNames; }

    public String getUniTableJson() { return uniTableJson; }
    public void setUniTableJson(String uniTableJson) { this.uniTableJson = uniTableJson; }
}
