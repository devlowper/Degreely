package com.degreely.app.controller;

import com.degreely.app.model.ChatMessage;
import com.degreely.app.model.ChatSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatStorageService {

    private static ChatStorageService instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File chatsFile;
    private final List<ChatSession> sessions = new ArrayList<>();

    private ChatStorageService() {
        String userHome = System.getProperty("user.home");
        File dir = new File(userHome, ".degreely");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.chatsFile = new File(dir, "chats.json");
        loadSessions();
    }

    public static synchronized ChatStorageService getInstance() {
        if (instance == null) {
            instance = new ChatStorageService();
        }
        return instance;
    }

    public synchronized List<ChatSession> getAllSessions() {
        List<ChatSession> copy = new ArrayList<>(sessions);
        // Sort: Pinned first, then by lastModified descending
        copy.sort((a, b) -> {
            if (a.isPinned() != b.isPinned()) {
                return a.isPinned() ? -1 : 1;
            }
            return Long.compare(b.getLastModified(), a.getLastModified());
        });
        return copy;
    }

    public synchronized ChatSession getSession(String id) {
        for (ChatSession s : sessions) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public synchronized void saveSession(ChatSession session) {
        boolean exists = false;
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getId().equals(session.getId())) {
                sessions.set(i, session);
                exists = true;
                break;
            }
        }
        if (!exists) {
            sessions.add(0, session);
        }
        persist();
    }

    public synchronized void deleteSession(String id) {
        sessions.removeIf(s -> s.getId().equals(id));
        persist();
    }

    public synchronized void togglePin(String id) {
        for (ChatSession s : sessions) {
            if (s.getId().equals(id)) {
                s.setPinned(!s.isPinned());
                break;
            }
        }
        persist();
    }

    public synchronized void renameSession(String id, String newTitle) {
        for (ChatSession s : sessions) {
            if (s.getId().equals(id)) {
                s.setTitle(newTitle);
                s.setLastModified(System.currentTimeMillis());
                break;
            }
        }
        persist();
    }

    public synchronized List<ChatSession> searchSessions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllSessions();
        }
        String q = query.toLowerCase().trim();
        List<ChatSession> matches = new ArrayList<>();
        for (ChatSession s : sessions) {
            if (s.getTitle().toLowerCase().contains(q)) {
                matches.add(s);
                continue;
            }
            for (ChatMessage m : s.getMessages()) {
                if (m.getContent() != null && m.getContent().toLowerCase().contains(q)) {
                    matches.add(s);
                    break;
                }
            }
        }
        return matches;
    }

    private void persist() {
        try (Writer writer = new FileWriter(chatsFile)) {
            gson.toJson(sessions, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSessions() {
        if (!chatsFile.exists()) return;
        try (Reader reader = new FileReader(chatsFile)) {
            Type listType = new TypeToken<List<ChatSession>>() {}.getType();
            List<ChatSession> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                sessions.clear();
                sessions.addAll(loaded);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
