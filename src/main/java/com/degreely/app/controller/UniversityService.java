package com.degreely.app.controller;

import com.degreely.app.model.Program;
import com.degreely.app.model.Scholarship;
import com.degreely.app.model.University;
import com.degreely.app.util.JsonLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UniversityService {

    private static UniversityService instance;
    private final List<University> universities;
    private final List<Scholarship> scholarships;

    public UniversityService() {
        this.universities = JsonLoader.loadUniversities();
        this.scholarships = JsonLoader.loadScholarships();
    }

    public static synchronized UniversityService getInstance() {
        if (instance == null) {
            instance = new UniversityService();
        }
        return instance;
    }

    /**
     * Returns an unmodifiable list of all universities.
     */
    public List<University> getAll() {
        return Collections.unmodifiableList(universities);
    }

    /**
     * Returns an unmodifiable list of all scholarships.
     */
    public List<Scholarship> getAllScholarships() {
        return Collections.unmodifiableList(scholarships);
    }

    /**
     * Search and filter universities by keyword, country, program level, max tuition, and IELTS.
     */
    public List<University> filter(String keyword, String country, String level, double maxTuition, double minIelts) {
        final String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        final String targetCountry = (country != null) ? country.trim() : "";
        final String targetLevel = (level != null) ? level.trim() : "";

        return universities.stream().filter(uni -> {
            // Country filter
            if (!targetCountry.isEmpty() && !targetCountry.equalsIgnoreCase("All") && !targetCountry.equalsIgnoreCase("All Countries")) {
                if (!uni.getCountry().equalsIgnoreCase(targetCountry)) {
                    return false;
                }
            }

            // Degree level filter (Bachelor, Master, PhD)
            if (!targetLevel.isEmpty() && !targetLevel.equalsIgnoreCase("All") && !targetLevel.equalsIgnoreCase("All Levels")) {
                boolean hasLevel = uni.getPrograms().stream()
                        .anyMatch(p -> p.getLevel() != null && p.getLevel().equalsIgnoreCase(targetLevel));
                if (!hasLevel) {
                    return false;
                }
            }

            // Max Tuition filter
            if (maxTuition > 0 && uni.getTuitionPerYear() > maxTuition) {
                return false;
            }

            // IELTS requirement filter
            if (minIelts > 0 && uni.getMinIelts() > minIelts) {
                return false;
            }

            // Keyword filter
            if (!kw.isEmpty()) {
                boolean matchesName = uni.getName() != null && uni.getName().toLowerCase().contains(kw);
                boolean matchesCity = uni.getCity() != null && uni.getCity().toLowerCase().contains(kw);
                boolean matchesCountry = uni.getCountry() != null && uni.getCountry().toLowerCase().contains(kw);
                boolean matchesProgram = uni.getPrograms().stream()
                        .anyMatch(p -> p.getName() != null && p.getName().toLowerCase().contains(kw));

                if (!matchesName && !matchesCity && !matchesCountry && !matchesProgram) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Search and filter universities by keyword, country, and program degree level.
     * Pass null or "All" to ignore any criteria.
     */
    public List<University> search(String keyword, String country, String level) {
        return filter(keyword, country, level, -1, -1);
    }

    /**
     * Get similar universities based on same country and closest ranking.
     */
    public List<University> getSimilarUniversities(University current, int limit) {
        if (current == null) return Collections.emptyList();
        return universities.stream()
                .filter(u -> !u.getId().equals(current.getId()))
                .sorted((a, b) -> {
                    boolean aSameCountry = a.getCountry().equalsIgnoreCase(current.getCountry());
                    boolean bSameCountry = b.getCountry().equalsIgnoreCase(current.getCountry());
                    if (aSameCountry != bSameCountry) {
                        return aSameCountry ? -1 : 1;
                    }
                    return Integer.compare(Math.abs(a.getRanking() - current.getRanking()),
                            Math.abs(b.getRanking() - current.getRanking()));
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Filter scholarships by keyword, country, and level.
     */
    public List<Scholarship> filterScholarships(String keyword, String country, String level) {
        final String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        final String targetCountry = (country != null) ? country.trim() : "";
        final String targetLevel = (level != null) ? level.trim() : "";

        return scholarships.stream().filter(s -> {
            // Country filter
            if (!targetCountry.isEmpty() && !targetCountry.equalsIgnoreCase("All") && !targetCountry.equalsIgnoreCase("All Countries")) {
                if (!s.getCountry().equalsIgnoreCase(targetCountry)) {
                    return false;
                }
            }

            // Level filter
            if (!targetLevel.isEmpty() && !targetLevel.equalsIgnoreCase("All") && !targetLevel.equalsIgnoreCase("All Levels")) {
                if (!s.getLevel().equalsIgnoreCase(targetLevel) && !s.getLevel().equalsIgnoreCase("All")) {
                    return false;
                }
            }

            // Keyword filter
            if (!kw.isEmpty()) {
                boolean matchesName = s.getName() != null && s.getName().toLowerCase().contains(kw);
                boolean matchesUni = s.getUniversity() != null && s.getUniversity().toLowerCase().contains(kw);
                boolean matchesCountry = s.getCountry() != null && s.getCountry().toLowerCase().contains(kw);
                boolean matchesAmount = s.getAmount() != null && s.getAmount().toLowerCase().contains(kw);

                if (!matchesName && !matchesUni && !matchesCountry && !matchesAmount) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Get unique country names for scholarship filter.
     */
    public List<String> getScholarshipCountries() {
        List<String> list = new ArrayList<>();
        list.add("All Countries");
        scholarships.stream()
                .map(Scholarship::getCountry)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .forEach(list::add);
        return list;
    }

    /**
     * Get unique country names for filter dropdowns.
     */
    public List<String> getUniqueCountries() {
        List<String> list = new ArrayList<>();
        list.add("All Countries");
        universities.stream()
                .map(University::getCountry)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .forEach(list::add);
        return list;
    }
}
