package com.degreely.app.model;

import java.util.UUID;

public class ApplicationDeadline {
    private String id;
    private String university;
    private String program;
    private String deadlineDate; // Format: YYYY-MM-DD
    private String status;       // Pending, Applied, Done

    public ApplicationDeadline() {
        this.id = UUID.randomUUID().toString();
        this.status = "Pending";
    }

    public ApplicationDeadline(String university, String program, String deadlineDate, String status) {
        this.id = UUID.randomUUID().toString();
        this.university = university;
        this.program = program;
        this.deadlineDate = deadlineDate;
        this.status = (status != null && !status.isBlank()) ? status : "Pending";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return university + " - " + program + " (" + deadlineDate + ") [" + status + "]";
    }
}
