package com.degreely.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatSession {
    private String id;
    private String title;
    private boolean isPinned;
    private long createdAt;
    private long lastModified;
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatSession() {
        this.id = UUID.randomUUID().toString();
        this.title = "New Study Consultation";
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }

    public ChatSession(String title) {
        this();
        this.title = title;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    public void addMessage(ChatMessage msg) {
        messages.add(msg);
        this.lastModified = System.currentTimeMillis();
    }
}
