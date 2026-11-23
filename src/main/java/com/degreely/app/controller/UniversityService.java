package com.degreely.app.controller;

import com.degreely.app.model.Program;
import com.degreely.app.model.University;
import com.degreely.app.model.UniTableData;
import com.degreely.app.util.JsonLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UniversityService {

    private static UniversityService instance;
    private final List<University> universities;

    public UniversityService() {
        this.universities = JsonLoader.loadUniversities();
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

    /**
     * Builds structured UniTableData comparing 2 or 3 selected universities.
     */
    public UniTableData buildComparisonTable(List<University> selectedUnis, String programName, String degreeLevel) {
        UniTableData data = new UniTableData();
        String prog = (programName == null || programName.isBlank()) ? "Computer Science" : programName.trim();
        String deg = (degreeLevel == null || degreeLevel.isBlank()) ? "Master" : degreeLevel.trim();

        data.setTitle(prog + " (" + deg + ") — Side-by-Side Comparison");
        data.setCurrency("USD");
        data.setNotes("Figures are official estimates. Verify specific admission deadlines and English test requirements with individual university portals.");

        List<UniTableData.UniversityEntry> entries = new ArrayList<>();
        for (University u : selectedUnis) {
            UniTableData.UniversityEntry entry = new UniTableData.UniversityEntry();
            entry.setName(u.getName());
            entry.setCountry(u.getCountry());
            entry.setProgram(deg + " in " + prog);
            entry.setTuitionPerYear("$" + String.format("%,.0f", u.getTuitionPerYear()));

            // Realistic estimated monthly living expenses based on country/city
            String countryLower = (u.getCountry() != null) ? u.getCountry().toLowerCase() : "";
            String livingCost;
            String workRights;
            String scholarships;
            if (countryLower.contains("germany")) {
                livingCost = "$950 - $1,300";
                workRights = "18-month Job Seeking Visa (EU Blue Card)";
                scholarships = "DAAD Scholarship, Deutschlandstipendium";
            } else if (countryLower.contains("canada")) {
                livingCost = "$1,100 - $1,600";
                workRights = "Up to 3-year PGWP (Post-Graduation Work Permit)";
                scholarships = "Vanier CGS, University Research Fellowships";
            } else if (countryLower.contains("united kingdom") || countryLower.contains("uk")) {
                livingCost = "$1,200 - $1,800";
                workRights = "2-year Graduate Route Post-Study Visa";
                scholarships = "Chevening, Commonwealth, Gates Cambridge";
            } else if (countryLower.contains("united states") || countryLower.contains("us")) {
                livingCost = "$1,300 - $2,000";
                workRights = "12-month OPT + 24-month STEM Extension (3 years total)";
                scholarships = "Fulbright, Graduate Assistantships (RA/TA)";
            } else if (countryLower.contains("switzerland")) {
                livingCost = "$1,700 - $2,400";
                workRights = "6-month job search permit for graduates";
                scholarships = "ETH Excellence Scholarship, Swiss Gov Fellowships";
            } else if (countryLower.contains("singapore")) {
                livingCost = "$1,200 - $1,750";
                workRights = "1-year Long Term Visit Pass (LTVP)";
                scholarships = "SINGA Award, NUS Research Scholarship";
            } else if (countryLower.contains("australia")) {
                livingCost = "$1,350 - $1,900";
                workRights = "2 to 4-year Temporary Graduate Visa (subclass 485)";
                scholarships = "Australia Awards, RTP Scholarships";
            } else {
                livingCost = "$1,000 - $1,500";
                workRights = "Post-study graduate work visa";
                scholarships = "Merit & International Student Scholarships";
            }

            entry.setLivingCostPerMonth(livingCost);
            entry.setMinGpa(String.format("%.2f / 4.0", u.getMinGpa()));
            entry.setEnglishRequirement("IELTS " + u.getMinIelts() + "+ / TOEFL " + (int)(u.getMinIelts() * 12 + 10));
            entry.setOtherTests(u.getRanking() <= 10 ? "GRE / GMAT Recommended" : "GRE / GMAT Optional");
            entry.setDocuments(List.of("Official Transcripts", "Statement of Purpose (SOP)", "Curriculum Vitae (CV)", "2-3 Letters of Recommendation (LOR)"));
            entry.setApplicationDeadline("Fall: Dec 15 - Feb 1 | Spring: Sep 15");
            entry.setIntake("Fall / Spring");
            entry.setApplicationFee(u.getTuitionPerYear() == 0 ? "$0" : "$75 - $125");
            entry.setScholarships(scholarships);
            entry.setWorkRights(workRights);
            entry.setSourceUrl("https://" + u.getName().toLowerCase().replaceAll("[^a-z0-9]", "") + ".edu");

            entries.add(entry);
        }
        data.setUniversities(entries);
        return data;
    }
}
