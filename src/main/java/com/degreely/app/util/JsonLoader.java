package com.degreely.app.util;

import com.degreely.app.model.Scholarship;
import com.degreely.app.model.University;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class JsonLoader {

    private static final Gson GSON = new Gson();

    private JsonLoader() {}

    /**
     * Loads list of Universities from resources/data/universities.json
     */
    public static List<University> loadUniversities() {
        return loadListFromResource("data/universities.json", new TypeToken<List<University>>() {}.getType());
    }

    /**
     * Loads list of Scholarships from resources/data/scholarships.json
     */
    public static List<Scholarship> loadScholarships() {
        return loadListFromResource("data/scholarships.json", new TypeToken<List<Scholarship>>() {}.getType());
    }

    private static <T> List<T> loadListFromResource(String resourcePath, Type type) {
        // Try ClassLoader first, fallback to class getResourceAsStream
        InputStream is = JsonLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null && !resourcePath.startsWith("/")) {
            is = JsonLoader.class.getResourceAsStream("/" + resourcePath);
        }

        if (is == null) {
            System.err.println("[JsonLoader] Resource not found: " + resourcePath);
            return new ArrayList<>();
        }

        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            List<T> list = GSON.fromJson(reader, type);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[JsonLoader] Error reading JSON from " + resourcePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
