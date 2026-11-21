package com.degreely.app.controller;

import com.degreely.app.model.StudentProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

public class StudentProfileService {

    private static StudentProfileService instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File profileFile;
    private StudentProfile cachedProfile;

    private StudentProfileService() {
        String userHome = System.getProperty("user.home");
        File dir = new File(userHome, ".degreely");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.profileFile = new File(dir, "profile.json");
        loadProfile();
    }

    public static synchronized StudentProfileService getInstance() {
        if (instance == null) {
            instance = new StudentProfileService();
        }
        return instance;
    }

    public synchronized StudentProfile getProfile() {
        if (cachedProfile == null) {
            cachedProfile = new StudentProfile();
        }
        return cachedProfile;
    }

    public synchronized void saveProfile(StudentProfile profile) {
        this.cachedProfile = profile;
        try (Writer writer = new FileWriter(profileFile)) {
            gson.toJson(profile, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProfile() {
        if (!profileFile.exists()) {
            cachedProfile = new StudentProfile();
            saveProfile(cachedProfile);
            return;
        }
        try (Reader reader = new FileReader(profileFile)) {
            cachedProfile = gson.fromJson(reader, StudentProfile.class);
            if (cachedProfile == null) {
                cachedProfile = new StudentProfile();
            }
        } catch (Exception e) {
            cachedProfile = new StudentProfile();
        }
    }
}
