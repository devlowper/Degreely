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
 * Modern Home Page displaying:
 * 1. Contemporary Hero section with text on the left, high-res 3D illustration on the right, quick metrics, and CTAs.
 * 2. Full-height University Discovery catalog with seamless dynamic scrolling (no clipping or overlapping at bottom).
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

        // Content panel stacked vertically with generous bottom padding so the last cards never get cut off
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 60, 32));

        // 1. Hero Section: Left Text + Right Image
        JPanel heroSection = createHeroSection();
        contentPanel.add(heroSection);
        contentPanel.add(Box.createVerticalStrut(Theme.SPACING_32));

        // 2. University Discovery Catalog Section
        catalogSection = createCatalogSection();
        contentPanel.add(catalogSection);

        // BorderLayout.NORTH inside scroll container allows contentPanel to size its height dynamically
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
     * Creates contemporary Hero section with centered headline, subtitle, CTAs, and quick stats.
     */
    private JPanel createHeroSection() {
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setOpaque(false);
        hero.setBorder(BorderFactory.createEmptyBorder(40, 48, 36, 48));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. Centered Category Badge
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        badgePanel.setOpaque(false);
        badgePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel badge = new JLabel("🎓 Global Higher Education Portal • 2026 Admissions");
        badge.setFont(Theme.FONT_SMALL);
        badge.setForeground(Theme.PRIMARY);
        badge.setOpaque(true);
        badge.setBackground(Theme.PRIMARY_LIGHT);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        badgePanel.add(badge);
        hero.add(badgePanel);
        hero.add(Box.createVerticalStrut(14));

        // 2. Centered Main Headline
        JLabel headline = new JLabel("<html><div style='text-align: center;'>Find Your Dream University &amp; Program Worldwide</div></html>");
        headline.setFont(new Font(Theme.FONT_HERO.getFamily(), Font.BOLD, 28));
        headline.setForeground(Theme.TEXT);
        headline.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.add(headline);
        hero.add(Box.createVerticalStrut(12));

        // 3. Centered Subtitle
        JLabel subtitle = new JLabel("<html><div style='text-align: center; max-width: 740px;'>Compare QS rankings, verified tuition costs, IELTS cutoffs, and living expenses across top universities in the US, UK, Canada, Germany, and more. 24/7 AI-powered admission counseling.</div></html>");
        subtitle.setFont(Theme.FONT_BODY_LARGE);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.add(subtitle);
        hero.add(Box.createVerticalStrut(24));

        // 4. Centered Action Buttons Row
        JPanel ctaRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        ctaRow.setOpaque(false);
        ctaRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exploreBtn = new JButton("Explore 20 Universities ↓");
        exploreBtn.setFont(Theme.FONT_BUTTON);
        exploreBtn.setBackground(Theme.PRIMARY);
        exploreBtn.setForeground(Color.WHITE);
        exploreBtn.setFocusPainted(false);
        exploreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exploreBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        exploreBtn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        exploreBtn.addActionListener(e -> scrollToCatalog());

        JButton chatAiBtn = new JButton("Chat with Degreely AI ✦", Icons.spark(15, Theme.PRIMARY));
        chatAiBtn.setFont(Theme.FONT_BUTTON);
        chatAiBtn.setBackground(Theme.PRIMARY_LIGHT);
        chatAiBtn.setForeground(Theme.PRIMARY);
        chatAiBtn.setFocusPainted(false);
        chatAiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chatAiBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        chatAiBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(9, 20, 9, 20)
        ));
        chatAiBtn.addActionListener(e -> {
            if (onNavigateToAI != null) onNavigateToAI.run();
        });

        ctaRow.add(exploreBtn);
        ctaRow.add(chatAiBtn);
        hero.add(ctaRow);
        hero.add(Box.createVerticalStrut(26));

        // 5. Centered Stats Strip
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 36, 0));
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
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font(Theme.FONT_HERO.getFamily(), Font.BOLD, 18));
        valLbl.setForeground(Theme.PRIMARY);
        valLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MUTED);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(valLbl);
        p.add(Box.createVerticalStrut(2));
        p.add(lbl);
        return p;
    }

    /**
     * Creates University Discovery section with filters, search bar, and university cards.
     */
    private JPanel createCatalogSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Section Title & Subtitle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);

        JLabel secTitle = new JLabel("Explore Universities & Programs");
        secTitle.setFont(Theme.FONT_TITLE);
        secTitle.setForeground(Theme.TEXT);

        JLabel secSub = new JLabel("Filter by country, degree level, or tuition fees. Click 'Ask AI Counselor' for personalized guidance.");
        secSub.setFont(Theme.FONT_BODY);
        secSub.setForeground(Theme.TEXT_SECONDARY);

        titles.add(secTitle);
        titles.add(Box.createVerticalStrut(4));
        titles.add(secSub);
        headerPanel.add(titles, BorderLayout.WEST);

        resultsCountLabel = new JLabel("Showing 20 of 20 Universities");
        resultsCountLabel.setFont(Theme.FONT_SMALL);
        resultsCountLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(resultsCountLabel, BorderLayout.EAST);

        section.add(headerPanel);
        section.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Filter Bar (White card with search and dropdowns)
        JPanel filterBar = createFilterBar();
        section.add(filterBar);
        section.add(Box.createVerticalStrut(Theme.SPACING_24));

        // University Cards Stack (1 by 1 downwards, no side scroll)
        universitiesGrid = new JPanel(new GridLayout(0, 1, 0, 16));
        universitiesGrid.setOpaque(false);
        universitiesGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(universitiesGrid);

        return section;
    }

    private JPanel createFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10)) {
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
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Search Input
        searchField = new JTextField(18);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search universities, cities, majors...");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, Icons.search(14, Theme.TEXT_MUTED));
        searchField.setFont(Theme.FONT_BODY);
        searchField.setPreferredSize(new Dimension(240, 36));
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
        styleComboBox(tuitionCombo, 150);
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
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
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
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
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
        combo.setPreferredSize(new Dimension(width, 36));
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
     * Creates a card for a university displaying ranking, location, tuition, cutoffs, programs, and CTA.
     */
    private JPanel createUniversityCard(University u) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Top Row: Rank pill + Country
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel rankBadge = new JLabel("#" + u.getRanking() + " QS World Rank");
        rankBadge.setFont(Theme.FONT_SMALL);
        rankBadge.setForeground(Theme.AMBER_TEXT);
        rankBadge.setOpaque(true);
        rankBadge.setBackground(Theme.AMBER_BG);
        rankBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        topRow.add(rankBadge, BorderLayout.WEST);

        JLabel countryBadge = new JLabel(u.getCountry());
        countryBadge.setFont(Theme.FONT_SMALL);
        countryBadge.setForeground(Theme.TEXT_SECONDARY);
        topRow.add(countryBadge, BorderLayout.EAST);

        // Center Area: Name, City, Metrics, Programs
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel nameLabel = new JLabel("<html><b>" + u.getName() + "</b></html>");
        nameLabel.setFont(new Font(Theme.FONT_SECTION.getFamily(), Font.BOLD, 15));
        nameLabel.setForeground(Theme.TEXT);

        JLabel locationLabel = new JLabel(u.getCity() + ", " + u.getCountry());
        locationLabel.setFont(Theme.FONT_SMALL);
        locationLabel.setForeground(Theme.TEXT_MUTED);

        center.add(topRow);
        center.add(Box.createVerticalStrut(8));
        center.add(nameLabel);
        center.add(Box.createVerticalStrut(2));
        center.add(locationLabel);
        center.add(Box.createVerticalStrut(12));

        // Metrics Row (4 metrics side-by-side in 1 by 1 view)
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        metricsPanel.setOpaque(false);

        String tuitionStr = u.getTuitionPerYear() == 0 ? "Tuition-Free ($0)" : "$" + NumberFormat.getNumberInstance(Locale.US).format((int) u.getTuitionPerYear()) + " / yr";
        metricsPanel.add(createMetricPill("Tuition", tuitionStr, u.getTuitionPerYear() == 0 ? Theme.GREEN_TEXT : Theme.TEXT));
        metricsPanel.add(createMetricPill("Min GPA", u.getMinGpa() + " / 4.0", Theme.TEXT));
        metricsPanel.add(createMetricPill("Min IELTS", u.getMinIelts() + " Band", Theme.TEXT));
        metricsPanel.add(createMetricPill("Acceptance", u.getAcceptanceRate() + "%", Theme.TEXT));

        center.add(metricsPanel);
        center.add(Box.createVerticalStrut(10));

        card.add(center, BorderLayout.CENTER);

        // Bottom Row: Program Tags on Left, Ask AI Button on Right
        JPanel bottomRow = new JPanel(new BorderLayout(16, 0));
        bottomRow.setOpaque(false);

        // Programs Preview Pills
        JPanel progPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        progPanel.setOpaque(false);
        int maxPrograms = Math.min(4, u.getPrograms().size());
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
        if (u.getPrograms().size() > 4) {
            JLabel moreTag = new JLabel("+" + (u.getPrograms().size() - 4) + " more");
            moreTag.setFont(Theme.FONT_SMALL);
            moreTag.setForeground(Theme.TEXT_MUTED);
            progPanel.add(moreTag);
        }
        bottomRow.add(progPanel, BorderLayout.CENTER);

        JButton askAiBtn = new JButton("Ask AI Counselor ✦", Icons.spark(14, Theme.PRIMARY));
        askAiBtn.setFont(Theme.FONT_BUTTON);
        askAiBtn.setForeground(Theme.PRIMARY);
        askAiBtn.setBackground(Theme.PRIMARY_LIGHT);
        askAiBtn.setFocusPainted(false);
        askAiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        askAiBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        askAiBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        askAiBtn.addActionListener(e -> {
            String prompt = "Tell me all about admission requirements, scholarships, deadlines, and cost of attendance for " + u.getName() + " in " + u.getCountry() + ".";
            if (onAskAI != null) {
                onAskAI.accept(prompt);
            }
        });
        bottomRow.add(askAiBtn, BorderLayout.EAST);

        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createMetricPill(String title, String val, Color valColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(true);
        p.setBackground(Theme.BACKGROUND);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel t = new JLabel(title);
        t.setFont(Theme.FONT_SMALL);
        t.setForeground(Theme.TEXT_MUTED);

        JLabel v = new JLabel(val);
        v.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.BOLD, 12));
        v.setForeground(valColor);

        p.add(t, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }

    private void scrollToCatalog() {
        if (catalogSection != null && scrollPane != null) {
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(catalogSection.getY() - 20);
            });
        }
    }
}
