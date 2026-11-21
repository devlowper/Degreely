package com.degreely.app.controller;

import com.degreely.app.model.University;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompareManager {

    private static CompareManager instance;
    public static final int MAX_COMPARE = 3;

    private final List<University> compareList = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();

    private CompareManager() {
        // Pre-populate with first 2 universities for immediate comparison preview
        List<University> all = UniversityService.getInstance().getAll();
        if (all.size() >= 2) {
            compareList.add(all.get(0)); // MIT
            compareList.add(all.get(1)); // Cambridge
        }
    }

    public static synchronized CompareManager getInstance() {
        if (instance == null) {
            instance = new CompareManager();
        }
        return instance;
    }

    public synchronized boolean addUniversity(University uni) {
        if (uni == null) return false;
        if (compareList.size() >= MAX_COMPARE) return false;
        if (isCompared(uni)) return false;

        compareList.add(uni);
        notifyListeners();
        return true;
    }

    public synchronized void removeUniversity(University uni) {
        if (compareList.removeIf(u -> u.getId().equals(uni.getId()))) {
            notifyListeners();
        }
    }

    public synchronized void removeAt(int index) {
        if (index >= 0 && index < compareList.size()) {
            compareList.remove(index);
            notifyListeners();
        }
    }

    public synchronized boolean isCompared(University uni) {
        if (uni == null) return false;
        return compareList.stream().anyMatch(u -> u.getId().equals(uni.getId()));
    }

    public synchronized List<University> getComparedList() {
        return Collections.unmodifiableList(new ArrayList<>(compareList));
    }

    public synchronized int getCount() {
        return compareList.size();
    }

    public synchronized void clear() {
        compareList.clear();
        notifyListeners();
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
