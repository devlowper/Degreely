package com.degreely.app.model;

import java.util.ArrayList;
import java.util.List;

public class University {
    private String id;
    private String name;
    private String country;
    private String city;
    private int ranking;
    private double tuitionPerYear;
    private double acceptanceRate; // e.g. 4.0 for 4%
    private double minGpa;
    private double minIelts;
    private List<Program> programs = new ArrayList<>();

    public University() {}

    public University(String id, String name, String country, String city, int ranking,
                      double tuitionPerYear, double acceptanceRate, double minGpa,
                      double minIelts, List<Program> programs) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.city = city;
        this.ranking = ranking;
        this.tuitionPerYear = tuitionPerYear;
        this.acceptanceRate = acceptanceRate;
        this.minGpa = minGpa;
        this.minIelts = minIelts;
        this.programs = programs != null ? programs : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public double getTuitionPerYear() {
        return tuitionPerYear;
    }

    public void setTuitionPerYear(double tuitionPerYear) {
        this.tuitionPerYear = tuitionPerYear;
    }

    public double getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(double acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public double getMinGpa() {
        return minGpa;
    }

    public void setMinGpa(double minGpa) {
        this.minGpa = minGpa;
    }

    public double getMinIelts() {
        return minIelts;
    }

    public void setMinIelts(double minIelts) {
        this.minIelts = minIelts;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs != null ? programs : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "#" + ranking + " " + name + " (" + city + ", " + country + ")";
    }
}
