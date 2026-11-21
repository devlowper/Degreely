package com.degreely.app.view;

import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.Program;
import com.degreely.app.model.University;
import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.view.components.chat.CompareDialog;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Minimal, clean Home Page designed in the Claude aesthetic:
 * 1. Warm, breathable Hero section with centered title, subtitle, CTAs, and quiet metrics.
 * 2. Uncluttered University Discovery catalog with 1-by-1 cards stacked vertically, zero horizontal scrolling.
 */
public class HomePage extends JPanel {

    private final UniversityService uniService = UniversityService.getInstance();
    private final Consumer<String> onAskAI;
    private final Runnable onNavigateToAI;

    private JTextField searchField;
    private JComboBox<String> countryCombo;
    private JComboBox<String> levelCombo;
    private JComboBox<String> tuitionCombo;
    private JLabel resultsCountLabel;
    private JPanel universitiesGrid;
    private JScrollPane scrollPane;
    private JPanel catalogSection;

    public HomePage(Consumer<String> onAskAI, Runnable onNavigateToAI) {
        this.onAskAI = onAskAI;
        this.onNavigateToAI = onNavigateToAI;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        // Content Panel with clean side and bottom margins
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(36, 40, 60, 40));

        // 1. Minimal Hero Section
        JPanel heroSection = createHeroSection();
        contentPanel.add(heroSection);
        contentPanel.add(Box.createVerticalStrut(36));

        // 2. University Discovery Catalog Section
        catalogSection = createCatalogSection();
        contentPanel.add(catalogSection);

        // Wrapper ensures content panel expands naturally in height and prevents horizontal scrolling
        JPanel scrollContentWrapper = new JPanel(new BorderLayout());
        scrollContentWrapper.setOpaque(false);
        scrollContentWrapper.add(contentPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(scrollContentWrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // Initial Data Load
        refreshUniversityList();
    }

    /**
     * Minimal, clean Hero section with centered typography and quiet metrics.
     */
    private JPanel createHeroSection() {
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setOpaque(false);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Headline
        JLabel headline = new JLabel("Find Your Dream University");
        headline.setFont(new Font(Theme.FONT_HERO.getFamily(), Font.BOLD, 30));
        headline.setForeground(Theme.TEXT);
        headline.setHorizontalAlignment(SwingConstants.CENTER);
        headline.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.add(headline);
        hero.add(Box.createVerticalStrut(10));

        // Subtitle
        JLabel subtitle = new JLabel("<html><div style='text-align: center; max-width: 680px;'>"
                + "Verified admissions data, tuition costs, and IELTS cutoffs for top global universities. 24/7 AI-powered counseling."
                + "</div></html>");
        subtitle.setFont(Theme.FONT_BODY_LARGE);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.add(subtitle);
        hero.add(Box.createVerticalStrut(22));

        // Action Buttons Row
        JPanel ctaRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        ctaRow.setOpaque(false);
        ctaRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exploreBtn = new JButton("Explore Universities ↓");
        exploreBtn.setFont(Theme.FONT_BUTTON);
        exploreBtn.setBackground(Theme.PRIMARY);
        exploreBtn.setForeground(Color.WHITE);
        exploreBtn.setFocusPainted(false);
        exploreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exploreBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        exploreBtn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        exploreBtn.addActionListener(e -> scrollToCatalog());

        JButton chatAiBtn = new JButton("Chat with Degreely AI ✦", Icons.spark(14, Theme.PRIMARY));
        chatAiBtn.setFont(Theme.FONT_BUTTON);
        chatAiBtn.setBackground(Color.WHITE);
        chatAiBtn.setForeground(Theme.PRIMARY);
        chatAiBtn.setFocusPainted(false);
        chatAiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chatAiBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        chatAiBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        chatAiBtn.addActionListener(e -> {
            if (onNavigateToAI != null) onNavigateToAI.run();
        });

        ctaRow.add(exploreBtn);
        ctaRow.add(chatAiBtn);
        hero.add(ctaRow);
        hero.add(Box.createVerticalStrut(20));

        // Minimal Stats Strip (Clean dot-separated metrics)
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsRow.add(createStatItem("20", "Top Universities"));
        statsRow.add(createStatItem("8", "Destinations"));
        statsRow.add(createStatItem("80+", "Degree Programs"));
        statsRow.add(createStatItem("$0/yr", "Tuition in Germany"));
        hero.add(statsRow);

        return hero;
    }

    private JPanel createStatItem(String value, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        p.setOpaque(false);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.BOLD, 14));
        valLbl.setForeground(Theme.PRIMARY);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MUTED);

        p.add(valLbl);
        p.add(lbl);
        return p;
    }

    /**
     * University Discovery section with unified header, filter bar, and 1-by-1 cards.
     */
    private JPanel createCatalogSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header: Title on Left, Count on Right
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel secTitle = new JLabel("Explore Universities & Programs");
        secTitle.setFont(new Font(Theme.FONT_TITLE.getFamily(), Font.BOLD, 20));
        secTitle.setForeground(Theme.TEXT);

        JLabel secSub = new JLabel("Filter by country, level, or tuition fees. Click 'Ask AI Counselor' for deep insights.");
        secSub.setFont(Theme.FONT_BODY);
        secSub.setForeground(Theme.TEXT_MUTED);

        titleBlock.add(secTitle);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(secSub);
        headerPanel.add(titleBlock, BorderLayout.WEST);

        resultsCountLabel = new JLabel("Showing 20 Universities");
        resultsCountLabel.setFont(Theme.FONT_SMALL);
        resultsCountLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(resultsCountLabel, BorderLayout.EAST);

        section.add(headerPanel);
        section.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Filter Bar (Clean single-row card)
        JPanel filterBar = createFilterBar();
        section.add(filterBar);
        section.add(Box.createVerticalStrut(Theme.SPACING_16));

        // University Cards Stack (1 by 1 downwards)
        universitiesGrid = new JPanel(new GridLayout(0, 1, 0, 14));
        universitiesGrid.setOpaque(false);
        universitiesGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(universitiesGrid);

        return section;
    }

    private JPanel createFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Search Input
        searchField = new JTextField(18);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search universities, cities, majors...");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, Icons.search(14, Theme.TEXT_MUTED));
        searchField.setFont(Theme.FONT_BODY);
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshUniversityList(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshUniversityList(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshUniversityList(); }
        });
        bar.add(searchField);

        // Country Filter
        countryCombo = new JComboBox<>(new String[]{
                "All Countries", "United States", "United Kingdom", "Canada",
                "Germany", "Australia", "Switzerland", "Singapore", "Japan"
        });
        styleComboBox(countryCombo, 130);
        countryCombo.addActionListener(e -> refreshUniversityList());
        bar.add(countryCombo);

        // Degree Level Filter
        levelCombo = new JComboBox<>(new String[]{
                "All Levels", "Bachelor", "Master", "PhD"
        });
        styleComboBox(levelCombo, 110);
        levelCombo.addActionListener(e -> refreshUniversityList());
        bar.add(levelCombo);

        // Tuition Filter
        tuitionCombo = new JComboBox<>(new String[]{
                "Any Tuition", "Tuition-Free (< $2k)", "Under $35,000/yr", "Under $60,000/yr"
        });
        styleComboBox(tuitionCombo, 145);
        tuitionCombo.addActionListener(e -> refreshUniversityList());
        bar.add(tuitionCombo);

        // Reset Filter Button
        JButton resetBtn = new JButton("Reset");
        resetBtn.setFont(Theme.FONT_SMALL);
        resetBtn.setForeground(Theme.TEXT_SECONDARY);
        resetBtn.setBackground(Color.WHITE);
        resetBtn.setFocusPainted(false);
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            countryCombo.setSelectedIndex(0);
            levelCombo.setSelectedIndex(0);
            tuitionCombo.setSelectedIndex(0);
            refreshUniversityList();
        });
        bar.add(resetBtn);

        // Compare Universities Button
        JButton compareBtn = new JButton("Compare", Icons.scale(14, Theme.PRIMARY));
        compareBtn.setFont(Theme.FONT_BUTTON);
        compareBtn.setForeground(Theme.PRIMARY);
        compareBtn.setBackground(Theme.PRIMARY_LIGHT);
        compareBtn.setFocusPainted(false);
        compareBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        compareBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        compareBtn.addActionListener(e -> {
            Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
            CompareDialog dialog = new CompareDialog(frame, (selectedUnis, program, degree) -> {
                StringBuilder names = new StringBuilder();
                for (int i = 0; i < selectedUnis.size(); i++) {
                    if (i == selectedUnis.size() - 1 && i > 0) names.append(" and ");
                    else if (i > 0) names.append(", ");
                    names.append(selectedUnis.get(i).getName());
                }
                String prompt = "Please compare " + names + " side-by-side for " + degree + " in " + program + " in a detailed comparison table.";
                if (onAskAI != null) onAskAI.accept(prompt);
            });
            dialog.setVisible(true);
        });
        bar.add(compareBtn);

        return bar;
    }

    private void styleComboBox(JComboBox<String> combo, int width) {
        combo.setFont(Theme.FONT_BODY);
        combo.setPreferredSize(new Dimension(width, 34));
        combo.setBackground(Color.WHITE);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void refreshUniversityList() {
        String keyword = searchField != null ? searchField.getText().trim() : "";
        String country = countryCombo != null ? (String) countryCombo.getSelectedItem() : "All Countries";
        String level = levelCombo != null ? (String) levelCombo.getSelectedItem() : "All Levels";

        double maxTuition = 0;
        if (tuitionCombo != null) {
            String selectedTuition = (String) tuitionCombo.getSelectedItem();
            if ("Tuition-Free (< $2k)".equals(selectedTuition)) {
                maxTuition = 2000;
            } else if ("Under $35,000/yr".equals(selectedTuition)) {
                maxTuition = 35000;
            } else if ("Under $60,000/yr".equals(selectedTuition)) {
                maxTuition = 60000;
            }
        }

        List<University> list = uniService.filter(keyword, country, level, maxTuition, 0);

        universitiesGrid.removeAll();
        for (University u : list) {
            universitiesGrid.add(createUniversityCard(u));
        }

        if (resultsCountLabel != null) {
            resultsCountLabel.setText("Showing " + list.size() + " of " + uniService.getAll().size() + " Universities");
        }

        universitiesGrid.revalidate();
        universitiesGrid.repaint();
        if (catalogSection != null) {
            catalogSection.revalidate();
            catalogSection.repaint();
        }
        revalidate();
        repaint();
    }

    /**
     * Minimal, clean university card structured with:
     * - Top: Rank badge + Name + Location on Left, Tuition & AI CTA on Right.
     * - Bottom: 4 Key Metrics + Program tags.
     */
    private JPanel createUniversityCard(University u) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // 1. Top Section: Name and Info on Left, Tuition & CTA on Right
        JPanel topSection = new JPanel(new BorderLayout(16, 0));
        topSection.setOpaque(false);

        // Left Block: Rank Pill, University Name, City
        JPanel leftBlock = new JPanel();
        leftBlock.setLayout(new BoxLayout(leftBlock, BoxLayout.Y_AXIS));
        leftBlock.setOpaque(false);

        JPanel rankRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rankRow.setOpaque(false);
        rankRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rankBadge = new JLabel("#" + u.getRanking() + " QS World Rank");
        rankBadge.setFont(Theme.FONT_SMALL);
        rankBadge.setForeground(Theme.AMBER_TEXT);
        rankBadge.setOpaque(true);
        rankBadge.setBackground(Theme.AMBER_BG);
        rankBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        JLabel countryLabel = new JLabel(u.getCountry());
        countryLabel.setFont(Theme.FONT_SMALL);
        countryLabel.setForeground(Theme.TEXT_MUTED);

        rankRow.add(rankBadge);
        rankRow.add(countryLabel);
        leftBlock.add(rankRow);
        leftBlock.add(Box.createVerticalStrut(4));

        JLabel nameLabel = new JLabel(u.getName());
        nameLabel.setFont(new Font(Theme.FONT_HERO.getFamily(), Font.BOLD, 16));
        nameLabel.setForeground(Theme.TEXT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftBlock.add(nameLabel);
        leftBlock.add(Box.createVerticalStrut(2));

        JLabel locationLabel = new JLabel(u.getCity() + ", " + u.getCountry());
        locationLabel.setFont(Theme.FONT_SMALL);
        locationLabel.setForeground(Theme.TEXT_MUTED);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftBlock.add(locationLabel);

        topSection.add(leftBlock, BorderLayout.CENTER);

        // Right Block: Annual Tuition + Ask AI Counselor Button
        JPanel rightBlock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        rightBlock.setOpaque(false);

        String tuitionStr = u.getTuitionPerYear() == 0 ? "Tuition-Free ($0)" : "$" + NumberFormat.getNumberInstance(Locale.US).format((int) u.getTuitionPerYear()) + " / yr";
        JLabel tuitionLabel = new JLabel(tuitionStr);
        tuitionLabel.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.BOLD, 14));
        tuitionLabel.setForeground(u.getTuitionPerYear() == 0 ? Theme.GREEN_TEXT : Theme.TEXT);

        JButton askAiBtn = new JButton("Ask AI Counselor ✦", Icons.spark(13, Theme.PRIMARY));
        askAiBtn.setFont(Theme.FONT_BUTTON);
        askAiBtn.setForeground(Theme.PRIMARY);
        askAiBtn.setBackground(Theme.PRIMARY_LIGHT);
        askAiBtn.setFocusPainted(false);
        askAiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        askAiBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        askAiBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        askAiBtn.addActionListener(e -> {
            String prompt = "Tell me all about admission requirements, scholarships, deadlines, and cost of attendance for " + u.getName() + " in " + u.getCountry() + ".";
            if (onAskAI != null) {
                onAskAI.accept(prompt);
            }
        });

        rightBlock.add(tuitionLabel);
        rightBlock.add(askAiBtn);
        topSection.add(rightBlock, BorderLayout.EAST);

        card.add(topSection, BorderLayout.NORTH);

        // 2. Bottom Section: Metrics Strip & Program Tags
        JPanel bottomSection = new JPanel(new BorderLayout(16, 0));
        bottomSection.setOpaque(false);
        bottomSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 0, 0, 0)
        ));

        // Key Metrics Row
        JPanel metricsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        metricsRow.setOpaque(false);
        metricsRow.add(createMetricTag("Min GPA", u.getMinGpa() + " / 4.0"));
        metricsRow.add(createMetricTag("Min IELTS", u.getMinIelts() + " Band"));
        metricsRow.add(createMetricTag("Acceptance", u.getAcceptanceRate() + "%"));

        // Programs Preview Tags
        JPanel progPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        progPanel.setOpaque(false);
        int maxPrograms = Math.min(3, u.getPrograms().size());
        for (int i = 0; i < maxPrograms; i++) {
            Program p = u.getPrograms().get(i);
            JLabel progTag = new JLabel(p.getName() + " (" + p.getLevel() + ")");
            progTag.setFont(Theme.FONT_SMALL);
            progTag.setForeground(Theme.TEXT_SECONDARY);
            progTag.setOpaque(true);
            progTag.setBackground(Theme.BACKGROUND);
            progTag.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            progPanel.add(progTag);
        }
        if (u.getPrograms().size() > 3) {
            JLabel moreTag = new JLabel("+" + (u.getPrograms().size() - 3) + " more");
            moreTag.setFont(Theme.FONT_SMALL);
            moreTag.setForeground(Theme.TEXT_MUTED);
            progPanel.add(moreTag);
        }

        bottomSection.add(metricsRow, BorderLayout.WEST);
        bottomSection.add(progPanel, BorderLayout.EAST);

        card.add(bottomSection, BorderLayout.CENTER);

        return card;
    }

    private JPanel createMetricTag(String label, String value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);

        JLabel l = new JLabel(label + ":");
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.BOLD, 12));
        v.setForeground(Theme.TEXT);

        p.add(l);
        p.add(v);
        return p;
    }

    private void scrollToCatalog() {
        if (catalogSection != null && scrollPane != null) {
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(catalogSection.getY() - 16);
            });
        }
    }
}
