package com.degreely.app.view.components.chat;

import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.University;
import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class CompareDialog extends JDialog {

    public interface OnCompareSubmit {
        void onSubmit(List<University> selectedUnis, String program, String degree);
    }

    private final UniversityService uniService = UniversityService.getInstance();
    private final JComboBox<String> uni1Combo = new JComboBox<>();
    private final JComboBox<String> uni2Combo = new JComboBox<>();
    private final JComboBox<String> uni3Combo = new JComboBox<>();
    private final JTextField programField = new JTextField("Computer Science");
    private final JComboBox<String> degreeCombo = new JComboBox<>(new String[]{
            "Master's (MSc / MEng)", "Bachelor's (BSc / BA)", "Doctorate (PhD)"
    });

    private final List<University> allUniversities;
    private final OnCompareSubmit onSubmit;

    public CompareDialog(Frame owner, OnCompareSubmit onSubmit) {
        super(owner, "Compare Universities in Table", true);
        this.onSubmit = onSubmit;
        this.allUniversities = uniService.getAll();
        initUI();
    }

    private void initUI() {
        setSize(580, 530);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(Theme.CARD_BG);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24));

        // Header Row: Scale Icon + Title
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
        titleRow.setOpaque(false);

        JLabel iconLabel = new JLabel(Icons.scale(22, Theme.PRIMARY));
        JLabel title = UI.sectionLabel("Compare Universities in Table");
        title.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 18));

        titleRow.add(iconLabel);
        titleRow.add(title);
        mainPanel.add(titleRow);
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_4));

        JLabel desc = UI.secondaryLabel("Select 2 or 3 universities to generate a side-by-side comparison table of costs, GPA cutoffs, and deadlines.");
        mainPanel.add(desc);
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Populate University Dropdowns
        uni3Combo.addItem("-- None (Compare 2 universities) --");
        for (University u : allUniversities) {
            String label = u.getName() + " (" + u.getCountry() + ")";
            uni1Combo.addItem(label);
            uni2Combo.addItem(label);
            uni3Combo.addItem(label);
        }

        // Set thoughtful default selections: e.g. MIT vs TUM
        if (allUniversities.size() > 0) uni1Combo.setSelectedIndex(0);
        if (allUniversities.size() > 6) {
            uni2Combo.setSelectedIndex(6); // Often Technical University of Munich or similar
        } else if (allUniversities.size() > 1) {
            uni2Combo.setSelectedIndex(1);
        }
        uni3Combo.setSelectedIndex(0);

        styleCombo(uni1Combo);
        styleCombo(uni2Combo);
        styleCombo(uni3Combo);
        styleCombo(degreeCombo);

        programField.setFont(Theme.FONT_BODY);
        programField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. Computer Science, Mechanical Eng, Data Science");
        programField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 6,10,6,10;");
        programField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Form Fields
        mainPanel.add(createFieldRow("1st University (Required):", uni1Combo));
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_12));

        mainPanel.add(createFieldRow("2nd University (Required):", uni2Combo));
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_12));

        mainPanel.add(createFieldRow("3rd University (Optional):", uni3Combo));
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_16));

        JPanel progDegreeRow = new JPanel(new GridLayout(1, 2, Theme.SPACING_12, 0));
        progDegreeRow.setOpaque(false);
        progDegreeRow.add(createFieldRow("Field of Study / Program:", programField));
        progDegreeRow.add(createFieldRow("Degree Level:", degreeCombo));
        mainPanel.add(progDegreeRow);

        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_24));

        // Action Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UI.outlineButton("Cancel");
        cancelBtn.setFont(Theme.FONT_SMALL);
        cancelBtn.addActionListener(e -> dispose());

        JButton compareBtn = UI.primaryButton("Compare in Table");
        compareBtn.setIcon(Icons.scale(14, java.awt.Color.WHITE));
        compareBtn.setIconTextGap(6);
        compareBtn.setFont(Theme.FONT_SMALL);
        compareBtn.addActionListener(e -> executeComparison());

        btnRow.add(cancelBtn);
        btnRow.add(compareBtn);
        mainPanel.add(btnRow);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(Theme.FONT_BODY);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        combo.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
    }

    private JPanel createFieldRow(String labelText, javax.swing.JComponent component) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel l = new JLabel(labelText);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);

        p.add(l);
        p.add(Box.createVerticalStrut(Theme.SPACING_4));
        p.add(component);
        return p;
    }

    private void executeComparison() {
        int idx1 = uni1Combo.getSelectedIndex();
        int idx2 = uni2Combo.getSelectedIndex();
        int idx3 = uni3Combo.getSelectedIndex() - 1; // offset by 1 because of None option

        if (idx1 == idx2) {
            JOptionPane.showMessageDialog(this,
                    "Please select two different universities for 1st and 2nd choices.",
                    "Duplicate University",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idx3 >= 0 && (idx3 == idx1 || idx3 == idx2)) {
            JOptionPane.showMessageDialog(this,
                    "3rd university must be different from 1st and 2nd universities.",
                    "Duplicate University",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<University> selected = new ArrayList<>();
        if (idx1 >= 0 && idx1 < allUniversities.size()) selected.add(allUniversities.get(idx1));
        if (idx2 >= 0 && idx2 < allUniversities.size()) selected.add(allUniversities.get(idx2));
        if (idx3 >= 0 && idx3 < allUniversities.size()) selected.add(allUniversities.get(idx3));

        String prog = programField.getText().trim();
        if (prog.isEmpty()) prog = "Computer Science";
        String degree = (String) degreeCombo.getSelectedItem();
        if (degree != null && degree.contains(" ")) {
            degree = degree.substring(0, degree.indexOf(' '));
        }

        dispose();
        if (onSubmit != null) {
            onSubmit.onSubmit(selected, prog, degree);
        }
    }
}
