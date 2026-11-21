package com.degreely.app.view;

import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.Program;
import com.degreely.app.model.University;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class UniversityDetailsPage extends JPanel {

    private final Consumer<String> onNavigate;
    private final Consumer<University> onOpenUniversity;
    private University university;

    // Header Components
    private final JLabel titleLabel = new JLabel();
    private final JLabel locationLabel = new JLabel();
    private final JButton compareBtn = UI.primaryButton("Add to Compare");
    private final JButton saveBtn = UI.outlineButton("Save");

    // Tabbed pane and similar list
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JPanel similarContainer = new JPanel();

    public UniversityDetailsPage(Consumer<String> onNavigate, Consumer<University> onOpenUniversity) {
        this.onNavigate = onNavigate;
        this.onOpenUniversity = onOpenUniversity;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, Theme.SPACING_16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_24, Theme.SPACING_32, Theme.SPACING_24, Theme.SPACING_32));

        // 1. Top Bar: Back Button, Title, Location, and Action Buttons
        JPanel topHeader = createTopHeader();
        add(topHeader, BorderLayout.NORTH);

        // 2. Main Body: Tabs on Left/Center, Similar Universities on Right
        JPanel body = new JPanel(new BorderLayout(Theme.SPACING_24, 0));
        body.setOpaque(false);

        // Styled Underlined Tabs with FlatLaf styling
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_TYPE,
                FlatClientProperties.TABBED_PANE_TAB_TYPE_UNDERLINED);
        tabbedPane.putClientProperty(FlatClientProperties.STYLE,
                "underlineColor: #E37712; selectedForeground: #E37712;");
        tabbedPane.setFont(new Font(Theme.FONT_BUTTON.getName(), Font.BOLD, 14));
        tabbedPane.setBackground(Theme.BACKGROUND);

        body.add(tabbedPane, BorderLayout.CENTER);

        // Similar Universities Sidebar
        JPanel rightSidebar = createSimilarSidebar();
        body.add(rightSidebar, BorderLayout.EAST);

        add(body, BorderLayout.CENTER);
    }

    private JPanel createTopHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        // Back button row
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setOpaque(false);
        JButton backBtn = UI.outlineButton("← Back to Universities");
        backBtn.setFont(Theme.FONT_SMALL);
        backBtn.setMargin(new Insets(6, 12, 6, 12));
        backBtn.addActionListener(e -> {
            if (onNavigate != null) onNavigate.accept("Universities");
        });
        backRow.add(backBtn);
        headerPanel.add(backRow);
        headerPanel.add(Box.createVerticalStrut(Theme.SPACING_12));

        // Title and Action Buttons Row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JPanel titleInfo = new JPanel();
        titleInfo.setLayout(new BoxLayout(titleInfo, BoxLayout.Y_AXIS));
        titleInfo.setOpaque(false);

        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT);

        locationLabel.setFont(Theme.FONT_BODY);
        locationLabel.setForeground(Theme.TEXT_SECONDARY);

        titleInfo.add(titleLabel);
        titleInfo.add(Box.createVerticalStrut(Theme.SPACING_4));
        titleInfo.add(locationLabel);

        // Action Buttons Row
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        actionsRow.setOpaque(false);

        compareBtn.setFont(Theme.FONT_SMALL);
        compareBtn.setMargin(new Insets(8, 16, 8, 16));
        compareBtn.addActionListener(e -> {
            if (university == null) return;
            com.degreely.app.controller.CompareManager cm = com.degreely.app.controller.CompareManager.getInstance();
            if (cm.isCompared(university)) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        university.getName() + " is already in your compare list.",
                        "Compare", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else if (cm.getCount() >= com.degreely.app.controller.CompareManager.MAX_COMPARE) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Maximum 3 universities can be compared at a time. Please remove one from Compare page first.",
                        "Compare Limit", javax.swing.JOptionPane.WARNING_MESSAGE);
            } else {
                cm.addUniversity(university);
                compareBtn.setText("Added to Compare ✓");
                compareBtn.setEnabled(false);
            }
        });

        saveBtn.setFont(Theme.FONT_SMALL);
        saveBtn.setMargin(new Insets(8, 16, 8, 16));
        saveBtn.addActionListener(e -> {
            if ("Save".equals(saveBtn.getText())) {
                saveBtn.setText("Saved ✓");
            } else {
                saveBtn.setText("Save");
            }
        });

        actionsRow.add(compareBtn);
        actionsRow.add(saveBtn);

        titleRow.add(titleInfo, BorderLayout.WEST);
        titleRow.add(actionsRow, BorderLayout.EAST);

        headerPanel.add(titleRow);
        return headerPanel;
    }

    private JPanel createSimilarSidebar() {
        JPanel container = new JPanel(new BorderLayout(0, Theme.SPACING_8));
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(300, 500));

        JLabel title = UI.sectionLabel("Similar Universities");
        title.setFont(new Font(Theme.FONT_SECTION.getName(), Font.BOLD, 16));
        container.add(title, BorderLayout.NORTH);

        similarContainer.setLayout(new BoxLayout(similarContainer, BoxLayout.Y_AXIS));
        similarContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(similarContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    public void setUniversity(University uni) {
        this.university = uni;
        if (uni == null) return;

        titleLabel.setText(uni.getName());
        locationLabel.setText(uni.getCity() + ", " + uni.getCountry() + "  •  QS World Rank #" + uni.getRanking());
        compareBtn.setText("Add to Compare");
        compareBtn.setEnabled(true);
        saveBtn.setText("Save");

        // Rebuild Tabs
        tabbedPane.removeAll();
        tabbedPane.addTab("About", createAboutTab());
        tabbedPane.addTab("Costs", createCostsTab());
        tabbedPane.addTab("Admissions", createAdmissionsTab());
        tabbedPane.addTab("Pre-requisites", createPrerequisitesTab());

        // Rebuild Similar Universities List
        rebuildSimilarUniversities();

        revalidate();
        repaint();
    }

    private JScrollPane wrapInScroll(JPanel panel) {
        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Theme.BACKGROUND);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        return sp;
    }

    private JScrollPane createAboutTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_4, Theme.SPACING_16, Theme.SPACING_16));

        // Overview Card
        JPanel overviewCard = UI.card();
        overviewCard.setLayout(new BoxLayout(overviewCard, BoxLayout.Y_AXIS));
        overviewCard.add(UI.sectionLabel("Institution Overview"));
        overviewCard.add(Box.createVerticalStrut(Theme.SPACING_8));
        overviewCard.add(UI.bodyLabel(university.getName() + " is globally renowned for world-class research, state-of-the-art facilities, and diverse campus community located in " + university.getCity() + ", " + university.getCountry() + "."));
        overviewCard.add(Box.createVerticalStrut(Theme.SPACING_4));
        overviewCard.add(UI.secondaryLabel("Current QS World University Ranking: #" + university.getRanking() + " with an average student acceptance rate of " + university.getAcceptanceRate() + "%."));
        panel.add(overviewCard);
        panel.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Programs Offered Card
        JPanel programsCard = UI.card();
        programsCard.setLayout(new BoxLayout(programsCard, BoxLayout.Y_AXIS));
        programsCard.add(UI.sectionLabel("Degrees & Programs Offered"));
        programsCard.add(Box.createVerticalStrut(Theme.SPACING_12));

        for (Program p : university.getPrograms()) {
            JPanel item = new JPanel(new BorderLayout());
            item.setOpaque(false);
            item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                    BorderFactory.createEmptyBorder(Theme.SPACING_8, 0, Theme.SPACING_8, 0)
            ));

            JLabel pName = new JLabel(p.getName());
            pName.setFont(Theme.FONT_BODY);
            pName.setForeground(Theme.TEXT);

            JLabel pMeta = new JLabel(p.getLevel() + "  (" + p.getDurationMonths() + " months)");
            pMeta.setFont(Theme.FONT_SMALL);
            pMeta.setForeground(Theme.PRIMARY);

            item.add(pName, BorderLayout.WEST);
            item.add(pMeta, BorderLayout.EAST);
            programsCard.add(item);
        }

        panel.add(programsCard);
        return wrapInScroll(panel);
    }

    private JScrollPane createCostsTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_4, Theme.SPACING_16, Theme.SPACING_16));

        double tuition = university.getTuitionPerYear();
        double livingCost = estimateLivingCost(university.getCountry());
        double totalCost = tuition + livingCost;

        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        nf.setMaximumFractionDigits(0);

        // Cost Summary Cards Row
        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, Theme.SPACING_16, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        cardsGrid.add(createSummaryMetricCard("Tuition / Year", nf.format(tuition), "Academic fees & campus facilities"));
        cardsGrid.add(createSummaryMetricCard("Est. Living Cost", nf.format(livingCost), "Housing, food, & personal expenses"));
        cardsGrid.add(createSummaryMetricCard("Total Annual Estimate", nf.format(totalCost), "Complete expected annual budget"));

        panel.add(cardsGrid);
        panel.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Cost Breakdown Details Card
        JPanel breakdownCard = UI.card();
        breakdownCard.setLayout(new BoxLayout(breakdownCard, BoxLayout.Y_AXIS));
        breakdownCard.add(UI.sectionLabel("Estimated Cost Breakdown"));
        breakdownCard.add(Box.createVerticalStrut(Theme.SPACING_12));

        breakdownCard.add(createBreakdownRow("Undergraduate / Graduate Tuition", nf.format(tuition)));
        breakdownCard.add(createBreakdownRow("On/Off Campus Accommodation (12 mos)", nf.format(livingCost * 0.55)));
        breakdownCard.add(createBreakdownRow("Meals & Groceries", nf.format(livingCost * 0.25)));
        breakdownCard.add(createBreakdownRow("Health Insurance & Transit", nf.format(livingCost * 0.20)));
        breakdownCard.add(Box.createVerticalStrut(Theme.SPACING_8));
        breakdownCard.add(UI.mutedLabel("* Note: Actual costs may vary depending on student lifestyle and program duration."));

        panel.add(breakdownCard);
        return wrapInScroll(panel);
    }

    private JScrollPane createAdmissionsTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_4, Theme.SPACING_16, Theme.SPACING_16));

        JPanel admCard = UI.card();
        admCard.setLayout(new BoxLayout(admCard, BoxLayout.Y_AXIS));
        admCard.add(UI.sectionLabel("Admission Statistics & Intakes"));
        admCard.add(Box.createVerticalStrut(Theme.SPACING_12));

        admCard.add(createBreakdownRow("Acceptance Rate", university.getAcceptanceRate() + "%"));
        admCard.add(createBreakdownRow("Competition Level", university.getAcceptanceRate() < 10 ? "Highly Competitive" : "Moderate"));
        admCard.add(createBreakdownRow("Fall Intake Deadline", "December 15 - January 15"));
        admCard.add(createBreakdownRow("Spring Intake Deadline", "October 1"));
        admCard.add(createBreakdownRow("Application Fee", "$85 - $105 USD"));

        panel.add(admCard);
        return wrapInScroll(panel);
    }

    private JScrollPane createPrerequisitesTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_4, Theme.SPACING_16, Theme.SPACING_16));

        // Academic & Language Card
        JPanel reqCard = UI.card();
        reqCard.setLayout(new BoxLayout(reqCard, BoxLayout.Y_AXIS));
        reqCard.add(UI.sectionLabel("Minimum Requirements"));
        reqCard.add(Box.createVerticalStrut(Theme.SPACING_12));

        reqCard.add(createBreakdownRow("Minimum Academic GPA", university.getMinGpa() + " / 4.0 scale"));
        reqCard.add(createBreakdownRow("Minimum IELTS Score", university.getMinIelts() + " (no band less than 6.0)"));
        reqCard.add(createBreakdownRow("TOEFL iBT Equivalent", (int)(university.getMinIelts() * 14) + "+ overall"));
        reqCard.add(createBreakdownRow("GRE / GMAT Requirement", "Optional / Program-specific"));

        panel.add(reqCard);
        panel.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Required Documents List Card
        JPanel docsCard = UI.card();
        docsCard.setLayout(new BoxLayout(docsCard, BoxLayout.Y_AXIS));
        docsCard.add(UI.sectionLabel("Required Application Documents"));
        docsCard.add(Box.createVerticalStrut(Theme.SPACING_12));

        String[] docs = {
                "Official Academic Transcripts (Certified English translation)",
                "Statement of Purpose (SOP) / Personal Motivation Letter",
                "2-3 Letters of Recommendation (Academic or Professional)",
                "Updated Curriculum Vitae (CV) / Academic Resume",
                "Official English Proficiency Test Report (IELTS / TOEFL)",
                "Copy of Valid Passport & Financial Guarantee Document"
        };

        for (String doc : docs) {
            JPanel dRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 4));
            dRow.setOpaque(false);
            JLabel check = new JLabel("✓");
            check.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 14));
            check.setForeground(Theme.PRIMARY);
            JLabel dText = new JLabel(doc);
            dText.setFont(Theme.FONT_BODY);
            dText.setForeground(Theme.TEXT);
            dRow.add(check);
            dRow.add(dText);
            docsCard.add(dRow);
        }

        panel.add(docsCard);
        return wrapInScroll(panel);
    }

    private void rebuildSimilarUniversities() {
        similarContainer.removeAll();
        List<University> similarList = UniversityService.getInstance().getSimilarUniversities(university, 3);

        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        nf.setMaximumFractionDigits(0);

        for (University sim : similarList) {
            JPanel card = UI.card();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel sName = new JLabel(sim.getName());
            sName.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 13));
            sName.setForeground(Theme.TEXT);

            JLabel sMeta = new JLabel(sim.getCity() + ", " + sim.getCountry() + "  •  #" + sim.getRanking());
            sMeta.setFont(Theme.FONT_SMALL);
            sMeta.setForeground(Theme.TEXT_MUTED);

            JLabel sTuition = new JLabel(nf.format(sim.getTuitionPerYear()) + " / yr");
            sTuition.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 12));
            sTuition.setForeground(Theme.PRIMARY);

            card.add(sName);
            card.add(Box.createVerticalStrut(Theme.SPACING_4));
            card.add(sMeta);
            card.add(Box.createVerticalStrut(Theme.SPACING_4));
            card.add(sTuition);

            // Click on similar university to open its details
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onOpenUniversity != null) {
                        onOpenUniversity.accept(sim);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                            new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.PRIMARY, 1, Theme.RADIUS),
                            BorderFactory.createEmptyBorder(Theme.SPACING_12, Theme.SPACING_12, Theme.SPACING_12, Theme.SPACING_12)
                    ));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                            new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, Theme.RADIUS),
                            BorderFactory.createEmptyBorder(Theme.SPACING_12, Theme.SPACING_12, Theme.SPACING_12, Theme.SPACING_12)
                    ));
                }
            });

            similarContainer.add(card);
            similarContainer.add(Box.createVerticalStrut(Theme.SPACING_12));
        }

        similarContainer.revalidate();
        similarContainer.repaint();
    }

    private JPanel createSummaryMetricCard(String title, String value, String sub) {
        JPanel card = UI.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(Theme.FONT_SMALL);
        t.setForeground(Theme.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 18));
        v.setForeground(Theme.PRIMARY);

        JLabel s = new JLabel(sub);
        s.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 11));
        s.setForeground(Theme.TEXT_SECONDARY);

        card.add(t);
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        card.add(v);
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        card.add(s);
        return card;
    }

    private JPanel createBreakdownRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(Theme.SPACING_8, 0, Theme.SPACING_8, 0)
        ));

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT);

        JLabel v = new JLabel(value);
        v.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
        v.setForeground(Theme.TEXT);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private double estimateLivingCost(String country) {
        if (country == null) return 15000;
        String c = country.toLowerCase();
        if (c.contains("united states")) return 18000;
        if (c.contains("united kingdom")) return 16000;
        if (c.contains("switzerland")) return 22000;
        if (c.contains("germany")) return 12000;
        if (c.contains("singapore")) return 15000;
        if (c.contains("australia")) return 17000;
        if (c.contains("canada")) return 15000;
        return 14000;
    }
}
