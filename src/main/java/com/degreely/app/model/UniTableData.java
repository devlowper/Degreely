package com.degreely.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class UniTableData {
    private String title = "University Comparison";
    private String currency = "USD";
    private List<UniversityEntry> universities = new ArrayList<>();
    private String notes = "";

    public UniTableData() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<UniversityEntry> getUniversities() { return universities; }
    public void setUniversities(List<UniversityEntry> universities) { this.universities = universities; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class UniversityEntry {
        private String name = "";
        private String country = "";
        private String program = "";

        @SerializedName("tuition_per_year")
        private String tuitionPerYear = "N/A";

        @SerializedName("living_cost_per_month")
        private String livingCostPerMonth = "N/A";

        @SerializedName("min_gpa")
        private String minGpa = "N/A";

        @SerializedName("english_requirement")
        private String englishRequirement = "N/A";

        @SerializedName("other_tests")
        private String otherTests = "None";

        private List<String> documents = new ArrayList<>();

        @SerializedName("application_deadline")
        private String applicationDeadline = "N/A";

        private String intake = "Fall";

        @SerializedName("application_fee")
        private String applicationFee = "N/A";

        private String scholarships = "Available";

        @SerializedName("work_rights")
        private String workRights = "Post-study work permit";

        @SerializedName("source_url")
        private String sourceUrl = "";

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getProgram() { return program; }
        public void setProgram(String program) { this.program = program; }

        public String getTuitionPerYear() { return tuitionPerYear; }
        public void setTuitionPerYear(String tuitionPerYear) { this.tuitionPerYear = tuitionPerYear; }

        public String getLivingCostPerMonth() { return livingCostPerMonth; }
        public void setLivingCostPerMonth(String livingCostPerMonth) { this.livingCostPerMonth = livingCostPerMonth; }

        public String getMinGpa() { return minGpa; }
        public void setMinGpa(String minGpa) { this.minGpa = minGpa; }

        public String getEnglishRequirement() { return englishRequirement; }
        public void setEnglishRequirement(String englishRequirement) { this.englishRequirement = englishRequirement; }

        public String getOtherTests() { return otherTests; }
        public void setOtherTests(String otherTests) { this.otherTests = otherTests; }

        public List<String> getDocuments() { return documents; }
        public void setDocuments(List<String> documents) { this.documents = documents; }

        public String getApplicationDeadline() { return applicationDeadline; }
        public void setApplicationDeadline(String applicationDeadline) { this.applicationDeadline = applicationDeadline; }

        public String getIntake() { return intake; }
        public void setIntake(String intake) { this.intake = intake; }

        public String getApplicationFee() { return applicationFee; }
        public void setApplicationFee(String applicationFee) { this.applicationFee = applicationFee; }

        public String getScholarships() { return scholarships; }
        public void setScholarships(String scholarships) { this.scholarships = scholarships; }

        public String getWorkRights() { return workRights; }
        public void setWorkRights(String workRights) { this.workRights = workRights; }

        public String getSourceUrl() { return sourceUrl; }
        public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    }
}
