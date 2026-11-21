package com.degreely.app.model;

public class Program {
    private String name;
    private String level; // Bachelor, Master, PhD
    private int durationMonths;

    public Program() {}

    public Program(String name, String level, int durationMonths) {
        this.name = name;
        this.level = level;
        this.durationMonths = durationMonths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    @Override
    public String toString() {
        return name + " (" + level + ", " + durationMonths + " mos)";
    }
}
