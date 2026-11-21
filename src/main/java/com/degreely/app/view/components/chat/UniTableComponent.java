package com.degreely.app.view.components.chat;

import com.degreely.app.model.UniTableData;
import com.degreely.app.model.UniTableData.UniversityEntry;
import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.google.gson.Gson;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.List;

public class UniTableComponent extends JPanel {

    private final UniTableData tableData;

    public UniTableComponent(String json) {
        UniTableData parsed;
        try {
            parsed = new Gson().fromJson(json, UniTableData.class);
        } catch (Exception e) {
            parsed = new UniTableData();
        }
        this.tableData = parsed;
        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.CARD_BG);
        putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFFFFF;");
        setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, 12),
                BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16)
        ));

        if (tableData.getUniversities().isEmpty()) {
            add(new JLabel("No comparison data available"));
            return;
        }

        List<UniversityEntry> unis = tableData.getUniversities();
        int colCount = unis.size();

        // 1. Top Bar: Title & Action Buttons
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(tableData.getTitle());
        titleLabel.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 16));
        titleLabel.setForeground(Theme.TEXT);

        JPanel currBadge = UI.badge(tableData.getCurrency(), Theme.LIGHT_BLUE_HIGHLIGHT, Theme.BLUE_TEXT);

        titlePanel.add(titleLabel);
        titlePanel.add(currBadge);

        // Actions: Copy & CSV Export
        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        actionBtns.setOpaque(false);

        JButton copyBtn = UI.outlineButton("Copy Table");
        copyBtn.setIcon(Icons.copy(13, Theme.PRIMARY));
        copyBtn.setIconTextGap(6);
        copyBtn.setFont(Theme.FONT_SMALL);
        copyBtn.setMargin(new Insets(4, 10, 4, 10));
        copyBtn.addActionListener(e -> copyToClipboard());

        JButton csvBtn = UI.outlineButton("Export CSV");
        csvBtn.setIcon(Icons.download(13, Theme.PRIMARY));
        csvBtn.setIconTextGap(6);
        csvBtn.setFont(Theme.FONT_SMALL);
        csvBtn.setMargin(new Insets(4, 10, 4, 10));
        csvBtn.addActionListener(e -> exportToCsv());

        actionBtns.add(copyBtn);
        actionBtns.add(csvBtn);

        topBar.add(titlePanel, BorderLayout.WEST);
        topBar.add(actionBtns, BorderLayout.EAST);
        add(topBar);
        add(Box.createVerticalStrut(Theme.SPACING_12));

        // 2. Table Container
        JPanel tableGrid = new JPanel();
        tableGrid.setLayout(new BoxLayout(tableGrid, BoxLayout.Y_AXIS));
        tableGrid.setOpaque(false);

        // Header Row: University Names
        JPanel headerRow = new JPanel(new GridLayout(1, colCount + 1, Theme.SPACING_12, 0));
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMARY),
                BorderFactory.createEmptyBorder(0, 0, Theme.SPACING_8, 0)
        ));

        headerRow.add(createHeaderLabelCell("Institution & Program"));
        for (UniversityEntry u : unis) {
            headerRow.add(createUniHeaderCell(u));
        }
        tableGrid.add(headerRow);
        tableGrid.add(Box.createVerticalStrut(Theme.SPACING_8));

        // Group 1: Admission Requirements
        tableGrid.add(createGroupHeader("ADMISSION REQUIREMENTS"));
        tableGrid.add(createAttributeRow("Min GPA", unis, UniversityEntry::getMinGpa));
        tableGrid.add(createAttributeRow("English (IELTS / TOEFL)", unis, UniversityEntry::getEnglishRequirement));
        tableGrid.add(createAttributeRow("Other Tests (GRE / GMAT)", unis, UniversityEntry::getOtherTests));
        tableGrid.add(createAttributeRow("Required Documents", unis, u -> String.join(", ", u.getDocuments())));

        // Group 2: Costs & Financials
        tableGrid.add(createGroupHeader("COST & FINANCIAL AID"));
        tableGrid.add(createAttributeRow("Tuition / Year", unis, UniversityEntry::getTuitionPerYear));
        tableGrid.add(createAttributeRow("Living Cost / Month", unis, UniversityEntry::getLivingCostPerMonth));
        tableGrid.add(createAttributeRow("Application Fee", unis, UniversityEntry::getApplicationFee));
        tableGrid.add(createAttributeRow("Scholarships", unis, UniversityEntry::getScholarships));

        // Group 3: Deadlines & Intake
        tableGrid.add(createGroupHeader("DEADLINES & INTAKE"));
        tableGrid.add(createAttributeRow("Application Deadline", unis, UniversityEntry::getApplicationDeadline));
        tableGrid.add(createAttributeRow("Available Intakes", unis, UniversityEntry::getIntake));

        // Group 4: Post-Study Opportunities
        tableGrid.add(createGroupHeader("POST-STUDY WORK"));
        tableGrid.add(createAttributeRow("Work Rights & Visa", unis, UniversityEntry::getWorkRights));

        // Wrap tableGrid in a smooth horizontal scroll pane for responsive behavior
        JScrollPane scrollPane = new JScrollPane(tableGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.CARD_BG);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        // 3. Footer: Disclaimer & Sources
        add(Box.createVerticalStrut(Theme.SPACING_12));
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel disclaimer = UI.mutedLabel("Figures are estimates. Always verify on the official university website before applying.");
        disclaimer.setFont(new Font(Theme.FONT_SMALL.getName(), Font.ITALIC, 11));

        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        linksPanel.setOpaque(false);
        for (UniversityEntry u : unis) {
            if (u.getSourceUrl() != null && !u.getSourceUrl().isBlank()) {
                JButton linkBtn = new JButton(u.getName() + " ↗");
                linkBtn.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 11));
                linkBtn.setForeground(Color.decode("#1757AC"));
                linkBtn.setBorderPainted(false);
                linkBtn.setContentAreaFilled(false);
                linkBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                linkBtn.addActionListener(e -> {
                    try {
                        Desktop.getDesktop().browse(new URI(u.getSourceUrl()));
                    } catch (Exception ignored) {}
                });
                linksPanel.add(linkBtn);
            }
        }

        footerPanel.add(disclaimer, BorderLayout.WEST);
        footerPanel.add(linksPanel, BorderLayout.EAST);
        add(footerPanel);
    }

    private JPanel createGroupHeader(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 4));
        p.setBackground(Theme.BACKGROUND);
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 4; background: #F8FAFC;");
        JLabel l = new JLabel(text);
        l.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 10));
        l.setForeground(Theme.TEXT_MUTED);
        p.add(l);
        return p;
    }

    private JPanel createUniHeaderCell(UniversityEntry u) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel name = new JLabel(u.getName());
        name.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 14));
        name.setForeground(Theme.TEXT);

        JLabel country = new JLabel(u.getCountry());
        country.setFont(Theme.FONT_SMALL);
        country.setForeground(Theme.TEXT_MUTED);

        JLabel prog = new JLabel(u.getProgram());
        prog.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        prog.setForeground(Theme.PRIMARY);

        p.add(name);
        p.add(Box.createVerticalStrut(2));
        p.add(country);
        p.add(Box.createVerticalStrut(2));
        p.add(prog);
        return p;
    }

    private JPanel createHeaderLabelCell(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 6));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
        l.setForeground(Theme.TEXT_SECONDARY);
        p.add(l);
        return p;
    }

    private JPanel createAttributeRow(String label, List<UniversityEntry> unis, java.util.function.Function<UniversityEntry, String> getter) {
        int colCount = unis.size();
        JPanel row = new JPanel(new GridLayout(1, colCount + 1, Theme.SPACING_12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(Theme.SPACING_8, 0, Theme.SPACING_8, 0)
        ));

        // Label cell
        JPanel lCell = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 4));
        lCell.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_SECONDARY);
        lCell.add(l);
        row.add(lCell);

        // Values
        for (UniversityEntry u : unis) {
            String val = getter.apply(u);
            if (val == null || val.isBlank()) val = "Check official site";

            JPanel vCell = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 4));
            vCell.setOpaque(false);

            JLabel v = new JLabel(val);
            v.setFont(Theme.FONT_BODY);
            v.setForeground(Theme.TEXT);
            vCell.add(v);
            row.add(vCell);
        }

        return row;
    }

    private void copyToClipboard() {
        StringBuilder sb = new StringBuilder();
        sb.append(tableData.getTitle()).append("\n\n");
        for (UniversityEntry u : tableData.getUniversities()) {
            sb.append("University: ").append(u.getName()).append(" (").append(u.getCountry()).append(")\n");
            sb.append("Program: ").append(u.getProgram()).append("\n");
            sb.append("Tuition: ").append(u.getTuitionPerYear()).append("\n");
            sb.append("Living Cost: ").append(u.getLivingCostPerMonth()).append("\n");
            sb.append("Requirements: GPA ").append(u.getMinGpa()).append(", English: ").append(u.getEnglishRequirement()).append("\n");
            sb.append("Deadline: ").append(u.getApplicationDeadline()).append(" (Intake: ").append(u.getIntake()).append(")\n");
            sb.append("Work Rights: ").append(u.getWorkRights()).append("\n\n");
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        JOptionPane.showMessageDialog(this, "Comparison table copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("university_comparison.csv"));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                List<UniversityEntry> list = tableData.getUniversities();
                writer.write("Attribute");
                for (UniversityEntry u : list) {
                    writer.write("," + escapeCsv(u.getName()));
                }
                writer.write("\n");

                writeCsvRow(writer, "Country", list, UniversityEntry::getCountry);
                writeCsvRow(writer, "Program", list, UniversityEntry::getProgram);
                writeCsvRow(writer, "Tuition/Year", list, UniversityEntry::getTuitionPerYear);
                writeCsvRow(writer, "Living Cost/Month", list, UniversityEntry::getLivingCostPerMonth);
                writeCsvRow(writer, "Min GPA", list, UniversityEntry::getMinGpa);
                writeCsvRow(writer, "English Requirement", list, UniversityEntry::getEnglishRequirement);
                writeCsvRow(writer, "Other Tests", list, UniversityEntry::getOtherTests);
                writeCsvRow(writer, "Deadline", list, UniversityEntry::getApplicationDeadline);
                writeCsvRow(writer, "Intake", list, UniversityEntry::getIntake);
                writeCsvRow(writer, "Work Rights", list, UniversityEntry::getWorkRights);

                JOptionPane.showMessageDialog(this, "Exported successfully to " + file.getName(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Export Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void writeCsvRow(FileWriter w, String label, List<UniversityEntry> list, java.util.function.Function<UniversityEntry, String> getter) throws Exception {
        w.write(escapeCsv(label));
        for (UniversityEntry u : list) {
            w.write("," + escapeCsv(getter.apply(u)));
        }
        w.write("\n");
    }

    private String escapeCsv(String str) {
        if (str == null) return "\"\"";
        return "\"" + str.replace("\"", "\"\"") + "\"";
    }
}
