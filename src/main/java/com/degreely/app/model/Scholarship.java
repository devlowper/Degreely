package com.degreely.app.model;

public class Scholarship {
    private String name;
    private String university;
    private String country;
    private String level; // Bachelor, Master, PhD, All
    private String amount;
    private String deadline;
    private String applyUrl;

    public Scholarship() {}

    public Scholarship(String name, String university, String country, String level, String amount, String deadline) {
        this(name, university, country, level, amount, deadline, "https://www.google.com/search?q=" + name.replace(" ", "+"));
    }

    public Scholarship(String name, String university, String country, String level, String amount, String deadline, String applyUrl) {
        this.name = name;
        this.university = university;
        this.country = country;
        this.level = level;
        this.amount = amount;
        this.deadline = deadline;
        this.applyUrl = applyUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getApplyUrl() {
        return (applyUrl != null && !applyUrl.isBlank()) ? applyUrl : "https://www.google.com/search?q=" + (name != null ? name.replace(" ", "+") : "scholarship");
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    @Override
    public String toString() {
        return name + " (" + university + ", " + country + ") - " + amount;
    }
}
