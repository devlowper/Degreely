package com.degreely.app.controller;

import com.degreely.app.model.ApplicationDeadline;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeadlineService {

    private static DeadlineService instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final List<ApplicationDeadline> deadlines = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private final File dataFile;

    private DeadlineService() {
        // Local JSON file stored in user.home/.degreely/deadlines.json
        String userHome = System.getProperty("user.home");
        File dir = new File(userHome, ".degreely");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.dataFile = new File(dir, "deadlines.json");
        loadDeadlines();
    }

    public static synchronized DeadlineService getInstance() {
        if (instance == null) {
            instance = new DeadlineService();
        }
        return instance;
    }

    private void loadDeadlines() {
        deadlines.clear();
        if (dataFile.exists() && dataFile.length() > 0) {
            try (Reader reader = new FileReader(dataFile, StandardCharsets.UTF_8)) {
                Type listType = new TypeToken<List<ApplicationDeadline>>() {}.getType();
                List<ApplicationDeadline> loaded = gson.fromJson(reader, listType);
                if (loaded != null) {
                    deadlines.addAll(loaded);
                }
            } catch (Exception e) {
                System.err.println("[DeadlineService] Error loading local deadlines: " + e.getMessage());
            }
        }

        // If newly created or empty, add realistic default deadlines
        if (deadlines.isEmpty()) {
            deadlines.add(new ApplicationDeadline("Massachusetts Institute of Technology (MIT)",
                    "M.S. in Artificial Intelligence", "2026-12-15", "Pending"));
            deadlines.add(new ApplicationDeadline("University of Cambridge",
                    "M.Phil. in Machine Learning and Speech", "2026-12-05", "Applied"));
            deadlines.add(new ApplicationDeadline("Technical University of Munich (TUM)",
                    "M.Sc. in Informatics & Data Analytics", "2027-01-15", "Pending"));
            saveDeadlines();
        }
    }

    private synchronized void saveDeadlines() {
        try (Writer writer = new FileWriter(dataFile, StandardCharsets.UTF_8)) {
            gson.toJson(deadlines, writer);
        } catch (Exception e) {
            System.err.println("[DeadlineService] Error saving local deadlines: " + e.getMessage());
        }
    }

    /**
     * Returns deadlines sorted by deadlineDate ascending (earliest upcoming deadlines first).
     */
    public synchronized List<ApplicationDeadline> getAllDeadlines() {
        List<ApplicationDeadline> sorted = new ArrayList<>(deadlines);
        sorted.sort(Comparator.comparing(d -> {
            try {
                return LocalDate.parse(d.getDeadlineDate());
            } catch (Exception e) {
                return LocalDate.MAX;
            }
        }));
        return Collections.unmodifiableList(sorted);
    }

    public synchronized void addDeadline(ApplicationDeadline deadline) {
        if (deadline == null) return;
        deadlines.add(deadline);
        saveDeadlines();
        notifyListeners();
    }

    public synchronized void deleteDeadline(String id) {
        if (id == null) return;
        boolean removed = deadlines.removeIf(d -> id.equals(d.getId()));
        if (removed) {
            saveDeadlines();
            notifyListeners();
        }
    }

    public synchronized void updateStatus(String id, String newStatus) {
        if (id == null || newStatus == null) return;
        for (ApplicationDeadline d : deadlines) {
            if (id.equals(d.getId())) {
                d.setStatus(newStatus);
                saveDeadlines();
                notifyListeners();
                break;
            }
        }
    }

    public synchronized void addChangeListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private void notifyListeners() {
        for (Runnable r : listeners) {
            try {
                r.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
