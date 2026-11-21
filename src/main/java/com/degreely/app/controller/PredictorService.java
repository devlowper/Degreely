package com.degreely.app.controller;

import com.degreely.app.model.University;

import java.awt.Color;

public class PredictorService {

    private static PredictorService instance;

    // Color definitions based on score range
    public static final Color COLOR_RED = Color.decode("#DC2626");     // < 40% (Reach / Low)
    public static final Color COLOR_ORANGE = Color.decode("#E37712");  // 40% - 70% (Competitive / Target)
    public static final Color COLOR_GREEN = Color.decode("#16A34A");   // > 70% (Strong Match / Safe)

    public static class PredictionResult {
        private final int percentage;
        private final Color color;
        private final String category;
        private final String advice;
        private final boolean gpaEligible;
        private final boolean ieltsEligible;

        public PredictionResult(int percentage, Color color, String category, String advice,
                                boolean gpaEligible, boolean ieltsEligible) {
            this.percentage = percentage;
            this.color = color;
            this.category = category;
            this.advice = advice;
            this.gpaEligible = gpaEligible;
            this.ieltsEligible = ieltsEligible;
        }

        public int getPercentage() { return percentage; }
        public Color getColor() { return color; }
        public String getCategory() { return category; }
        public String getAdvice() { return advice; }
        public boolean isGpaEligible() { return gpaEligible; }
        public boolean isIeltsEligible() { return ieltsEligible; }
    }

    private PredictorService() {}

    public static synchronized PredictorService getInstance() {
        if (instance == null) {
            instance = new PredictorService();
        }
        return instance;
    }

    /**
     * Calculates estimated admission chance based on GPA, IELTS, and University competitiveness.
     *
     * FORMULA BREAKDOWN:
     * 1. Baseline Score (30 - 75 pts):
     *    Determined by the university's acceptance rate (e.g. MIT 4% -> ~32 base, Melbourne 70% -> ~72 base).
     *
     * 2. GPA Difference Factor (-30 to +20 pts):
     *    Compares candidate's GPA against minimum requirement:
     *    - If userGpa >= minGpa: + (userGpa - minGpa) * 35 bonus
     *    - If userGpa < minGpa:  - (minGpa - userGpa) * 60 penalty
     *
     * 3. IELTS Difference Factor (-25 to +15 pts):
     *    Compares candidate's IELTS against language cutoff:
     *    - If userIelts >= minIelts: + (userIelts - minIelts) * 10 bonus
     *    - If userIelts < minIelts:  - (minIelts - userIelts) * 30 penalty
     *
     * 4. Degree Level Calibration:
     *    PhD programs have higher research weight (-5 pts base), Bachelor has higher standard test weight.
     *
     * 5. Clamping:
     *    Scores are clamped between 5% and 95% (admissions are never 0% or 100% guaranteed).
     */
    public PredictionResult calculateChance(double userGpa, double userIelts, String level, University uni) {
        if (uni == null) {
            return new PredictionResult(0, COLOR_RED, "Unknown", "Please select a target university.", false, false);
        }

        double minGpa = uni.getMinGpa();
        double minIelts = uni.getMinIelts();
        double acceptanceRate = uni.getAcceptanceRate();

        // 1. Base Score from Acceptance Rate
        double score;
        if (acceptanceRate < 10.0) {
            score = 30.0 + (acceptanceRate * 0.8); // 30 - 38 base
        } else if (acceptanceRate <= 25.0) {
            score = 42.0 + ((acceptanceRate - 10.0) * 0.7); // 42 - 52 base
        } else if (acceptanceRate <= 50.0) {
            score = 54.0 + ((acceptanceRate - 25.0) * 0.5); // 54 - 66 base
        } else {
            score = 68.0 + ((acceptanceRate - 50.0) * 0.35); // 68 - 75 base
        }

        // 2. GPA Factor
        boolean gpaEligible = userGpa >= minGpa;
        if (gpaEligible) {
            double gpaBonus = (userGpa - minGpa) * 35.0;
            score += Math.min(20.0, gpaBonus);
        } else {
            double gpaPenalty = (minGpa - userGpa) * 60.0;
            score -= Math.min(35.0, gpaPenalty);
        }

        // 3. IELTS Factor
        boolean ieltsEligible = userIelts >= minIelts;
        if (ieltsEligible) {
            double ieltsBonus = (userIelts - minIelts) * 10.0;
            score += Math.min(15.0, ieltsBonus);
        } else {
            double ieltsPenalty = (minIelts - userIelts) * 30.0;
            score -= Math.min(30.0, ieltsPenalty);
        }

        // 4. Degree Level Fine-Tuning
        if ("PhD".equalsIgnoreCase(level)) {
            score -= 4.0; // Higher selectivity for research/funding
        }

        // 5. Bounds Clamping (5% to 95%)
        int finalPercentage = (int) Math.round(Math.max(5.0, Math.min(95.0, score)));

        // Color & Category Classification
        Color resultColor;
        String category;
        if (finalPercentage > 70) {
            resultColor = COLOR_GREEN;
            category = "Strong Match / High Chance";
        } else if (finalPercentage >= 40) {
            resultColor = COLOR_ORANGE;
            category = "Competitive / Target Match";
        } else {
            resultColor = COLOR_RED;
            category = "Reach / Challenging Match";
        }

        // Actionable Tailored Advice Generation
        String advice = generateAdvice(userGpa, minGpa, userIelts, minIelts, acceptanceRate, finalPercentage);

        return new PredictionResult(finalPercentage, resultColor, category, advice, gpaEligible, ieltsEligible);
    }

    private String generateAdvice(double gpa, double minGpa, double ielts, double minIelts,
                                  double acceptanceRate, int finalPercentage) {
        if (ielts < minIelts) {
            return "Your IELTS score is below the minimum requirement (" + minIelts + "). Retaking the test to meet the cutoff is essential before applying.";
        }
        if (gpa < minGpa) {
            return "Your GPA is below the recommended average (" + minGpa + "). Compensate by highlighting research papers, relevant internships, and exceptional recommendation letters.";
        }
        if (acceptanceRate < 10.0 && finalPercentage < 50) {
            return "Due to extremely high institutional selectivity (" + acceptanceRate + "% acceptance), focus heavily on an impactful Statement of Purpose and strong leadership profile.";
        }
        if (finalPercentage > 70) {
            return "Your profile comfortably exceeds all standard academic minimums. You stand as a very competitive candidate for this program!";
        }
        return "You have a balanced and viable competitive profile. Ensure all application materials, essays, and references are polished to maximize your chance.";
    }
}
