package com.degreely.app.view;

import com.degreely.app.controller.PredictorService;
import com.degreely.app.controller.PredictorService.PredictionResult;
import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.University;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.List;

public class PredictorPage extends JPanel {

    private final PredictorService predictorService = PredictorService.getInstance();
    private final UniversityService universityService = UniversityService.getInstance();

    // Form Inputs
    private final JTextField gpaField = new JTextField("3.75");
    private final JComboBox<Double> ieltsCombo = new JComboBox<>(new Double[]{
            5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{"Master", "Bachelor", "PhD"});
    private final JComboBox<UniversityItem> universityCombo = new JComboBox<>();

    // Result Components
    private final JPanel resultCard = UI.card();
    private final JLabel percentLabel = new JLabel("--%");
    private final JLabel categoryBadge = new JLabel("Pending Calculation");
    private final ColoredProgressBar progressBar = new ColoredProgressBar();
    private final JLabel adviceLabel = new JLabel("Fill in your academic profile on the left and click 'Check Chance'.");
    private final JLabel uniMetaLabel = new JLabel("Select target university above");

    // Stat metric labels in result
    private final JLabel gpaComparisonLabel = new JLabel("-");
    private final JLabel ieltsComparisonLabel = new JLabel("-");
    private final JLabel acceptanceComparisonLabel = new JLabel("-");

    public PredictorPage() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, Theme.SPACING_16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_24, Theme.SPACING_32, Theme.SPACING_24, Theme.SPACING_32));

        // 1. Top Header Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(UI.sectionLabel("Admission Chance Predictor"));
        titlePanel.add(Box.createVerticalStrut(Theme.SPACING_4));
        titlePanel.add(UI.secondaryLabel("Estimate your admission probability based on your GPA, IELTS, and university selectivity."));
        add(titlePanel, BorderLayout.NORTH);

        // 2. Main Two-Column Layout (Form on Left, Result Card on Right)
        JPanel grid = new JPanel(new GridLayout(1, 2, Theme.SPACING_24, 0));
        grid.setOpaque(false);

        grid.add(createFormCard());
        grid.add(createResultCard());

        add(grid, BorderLayout.CENTER);

        // Run initial calculation with defaults
        calculate();
    }

    private JPanel createFormCard() {
        JPanel card = UI.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = UI.sectionLabel("Candidate Profile");
        title.setFont(new Font(Theme.FONT_SECTION.getName(), Font.BOLD, 17));
        card.add(title);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // 1. Target University
        card.add(createFieldLabel("Target University"));
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        populateUniversityCombo();
        universityCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(universityCombo);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // 2. Degree Level
        card.add(createFieldLabel("Degree Level"));
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        levelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(levelCombo);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // 3. GPA Input
        card.add(createFieldLabel("Cumulative GPA (Scale: 0.0 - 4.0)"));
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        gpaField.setFont(Theme.FONT_BODY);
        gpaField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. 3.80");
        gpaField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 6,10,6,10;");
        gpaField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(gpaField);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // 4. IELTS Score Input
        card.add(createFieldLabel("English Test Score (IELTS Band)"));
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        ieltsCombo.setSelectedItem(7.5);
        ieltsCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(ieltsCombo);
        card.add(Box.createVerticalStrut(Theme.SPACING_24));

        // Submit Button
        JButton checkBtn = UI.primaryButton("Check Chance");
        checkBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        checkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBtn.addActionListener(e -> calculate());
        card.add(checkBtn);

        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel createResultCard() {
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));

        JLabel title = UI.sectionLabel("Admission Forecast");
        title.setFont(new Font(Theme.FONT_SECTION.getName(), Font.BOLD, 17));
        resultCard.add(title);
        resultCard.add(Box.createVerticalStrut(Theme.SPACING_4));

        uniMetaLabel.setFont(Theme.FONT_SMALL);
        uniMetaLabel.setForeground(Theme.TEXT_MUTED);
        resultCard.add(uniMetaLabel);
        resultCard.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Big Percentage & Category Badge Row
        JPanel percentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_16, 0));
        percentRow.setOpaque(false);

        percentLabel.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 46));
        percentLabel.setForeground(PredictorService.COLOR_GREEN);

        categoryBadge.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
        categoryBadge.setForeground(PredictorService.COLOR_GREEN);
        categoryBadge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #DCFCE7;");
        categoryBadge.setOpaque(true);
        categoryBadge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        percentRow.add(percentLabel);
        percentRow.add(categoryBadge);
        resultCard.add(percentRow);
        resultCard.add(Box.createVerticalStrut(Theme.SPACING_12));

        // Colored Progress Bar
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        progressBar.setPreferredSize(new Dimension(400, 16));
        resultCard.add(progressBar);
        resultCard.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Metric Comparison Cards Grid (3 metrics)
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, Theme.SPACING_12, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        statsGrid.add(createMiniStat("Your GPA vs Req", gpaComparisonLabel));
        statsGrid.add(createMiniStat("IELTS vs Req", ieltsComparisonLabel));
        statsGrid.add(createMiniStat("Acceptance Rate", acceptanceComparisonLabel));

        resultCard.add(statsGrid);
        resultCard.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Advice Box Card
        JPanel adviceBox = new JPanel(new BorderLayout(Theme.SPACING_12, 0));
        adviceBox.setBackground(Theme.LIGHT_BLUE_HIGHLIGHT);
        adviceBox.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #EFF6FF;");
        adviceBox.setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(1, 1, 1, 1), Color.decode("#BFDBFE"), 1, 8),
                BorderFactory.createEmptyBorder(Theme.SPACING_12, Theme.SPACING_16, Theme.SPACING_12, Theme.SPACING_16)
        ));

        JLabel iconLabel = new JLabel("ADVICE");
        iconLabel.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 10));
        iconLabel.setForeground(Theme.PRIMARY);
        iconLabel.putClientProperty(FlatClientProperties.STYLE, "arc: 6; background: #FFF7ED;");
        iconLabel.setOpaque(true);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        adviceLabel.setFont(Theme.FONT_BODY);
        adviceLabel.setForeground(Theme.TEXT);

        adviceBox.add(iconLabel, BorderLayout.WEST);
        adviceBox.add(adviceLabel, BorderLayout.CENTER);

        resultCard.add(adviceBox);
        resultCard.add(Box.createVerticalGlue());

        return resultCard;
    }

    private JPanel createMiniStat(String label, JLabel valueLabel) {
        JPanel p = UI.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);

        valueLabel.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
        valueLabel.setForeground(Theme.TEXT);

        p.add(l);
        p.add(Box.createVerticalStrut(2));
        p.add(valueLabel);
        return p;
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);
        return l;
    }

    private void populateUniversityCombo() {
        universityCombo.removeAllItems();
        List<University> list = universityService.getAll();
        for (University u : list) {
            universityCombo.addItem(new UniversityItem(u));
        }
    }

    private void calculate() {
        // Validate GPA
        double gpa;
        try {
            gpa = Double.parseDouble(gpaField.getText().trim());
            if (gpa < 0.0 || gpa > 4.0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid GPA between 0.0 and 4.0", "Invalid GPA", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric GPA value (e.g. 3.75)", "Invalid GPA", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Selected values
        Double ielts = (Double) ieltsCombo.getSelectedItem();
        if (ielts == null) ielts = 6.5;

        String level = (String) levelCombo.getSelectedItem();
        UniversityItem selectedItem = (UniversityItem) universityCombo.getSelectedItem();
        if (selectedItem == null) return;
        University uni = selectedItem.uni;

        // Perform Calculation
        PredictionResult result = predictorService.calculateChance(gpa, ielts, level, uni);

        // Update UI
        int pct = result.getPercentage();
        percentLabel.setText(pct + "%");
        percentLabel.setForeground(result.getColor());

        categoryBadge.setText(result.getCategory());
        categoryBadge.setForeground(result.getColor());

        // Badge background matching color level
        if (pct > 70) {
            categoryBadge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #DCFCE7;");
        } else if (pct >= 40) {
            categoryBadge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFEDD5;");
        } else {
            categoryBadge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FEE2E2;");
        }

        progressBar.setProgress(pct, result.getColor());

        uniMetaLabel.setText(uni.getName() + " (#" + uni.getRanking() + " in " + uni.getCountry() + ")");

        // Metrics breakdown
        gpaComparisonLabel.setText(gpa + " / " + uni.getMinGpa());
        ieltsComparisonLabel.setText(ielts + " / " + uni.getMinIelts());
        acceptanceComparisonLabel.setText(uni.getAcceptanceRate() + "%");

        adviceLabel.setText("<html>" + result.getAdvice() + "</html>");

        resultCard.revalidate();
        resultCard.repaint();
    }

    private static class UniversityItem {
        final University uni;

        UniversityItem(University uni) {
            this.uni = uni;
        }

        @Override
        public String toString() {
            return "#" + uni.getRanking() + " " + uni.getName() + " (" + uni.getCountry() + ")";
        }
    }

    /**
     * Custom rounded progress bar supporting dynamic color based on admission score.
     */
    private static class ColoredProgressBar extends JPanel {
        private int progress = 0;
        private Color barColor = PredictorService.COLOR_GREEN;

        public ColoredProgressBar() {
            setOpaque(false);
        }

        public void setProgress(int progress, Color color) {
            this.progress = Math.max(0, Math.min(100, progress));
            this.barColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int arc = height;

            // Track background
            g2.setColor(new Color(243, 244, 246));
            g2.fillRoundRect(0, 0, width, height, arc, arc);

            // Colored Progress fill
            int fillWidth = (int) (width * (progress / 100.0));
            if (fillWidth > 0) {
                g2.setColor(barColor);
                g2.fillRoundRect(0, 0, fillWidth, height, arc, arc);
            }

            g2.dispose();
        }
    }
}
