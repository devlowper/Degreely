package com.degreely.app.model;

public class StudentProfile {
    private String currentDegree = "Bachelor";
    private double cgpa = 3.65;
    private String fieldOfStudy = "Computer Science & Engineering";
    private double ieltsScore = 7.0;
    private String greScore = "315";
    private String yearlyBudget = "$15,000 - $25,000";
    private String targetIntake = "Fall 2027";
    private String preferredCountries = "Germany, Canada, USA";
    private String workExperience = "1 year software development";

    public StudentProfile() {}

    public String getCurrentDegree() { return currentDegree; }
    public void setCurrentDegree(String currentDegree) { this.currentDegree = currentDegree; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public double getIeltsScore() { return ieltsScore; }
    public void setIeltsScore(double ieltsScore) { this.ieltsScore = ieltsScore; }

    public String getGreScore() { return greScore; }
    public void setGreScore(String greScore) { this.greScore = greScore; }

    public String getYearlyBudget() { return yearlyBudget; }
    public void setYearlyBudget(String yearlyBudget) { this.yearlyBudget = yearlyBudget; }

    public String getTargetIntake() { return targetIntake; }
    public void setTargetIntake(String targetIntake) { this.targetIntake = targetIntake; }

    public String getPreferredCountries() { return preferredCountries; }
    public void setPreferredCountries(String preferredCountries) { this.preferredCountries = preferredCountries; }

    public String getWorkExperience() { return workExperience; }
    public void setWorkExperience(String workExperience) { this.workExperience = workExperience; }

    public String toContextPrompt() {
        return "Student Profile Context:\n" +
                "- Current Degree: " + currentDegree + "\n" +
                "- Field of Study: " + fieldOfStudy + "\n" +
                "- CGPA: " + cgpa + " (Scale 4.0)\n" +
                "- English Proficiency (IELTS/TOEFL): " + ieltsScore + "\n" +
                "- GRE/GMAT: " + (greScore.isBlank() ? "None" : greScore) + "\n" +
                "- Work Experience: " + (workExperience.isBlank() ? "None" : workExperience) + "\n" +
                "- Target Countries: " + preferredCountries + "\n" +
                "- Target Intake: " + targetIntake + "\n" +
                "- Annual Tuition Budget: " + yearlyBudget + "\n";
    }
}
