package com.degreely.app.view.components.chat;

import com.degreely.app.controller.StudentProfileService;
import com.degreely.app.model.StudentProfile;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

public class ProfileDialog extends JDialog {

    private final StudentProfileService profileService = StudentProfileService.getInstance();
    private final JTextField degreeField = new JTextField();
    private final JTextField fieldStudyField = new JTextField();
    private final JTextField cgpaField = new JTextField();
    private final JTextField ieltsField = new JTextField();
    private final JTextField greField = new JTextField();
    private final JTextField budgetField = new JTextField();
    private final JTextField countriesField = new JTextField();
    private final JTextField intakeField = new JTextField();
    private final JTextField workExpField = new JTextField();

    public ProfileDialog(Frame owner) {
        super(owner, "My Student Profile", true);
        initUI();
        loadProfileData();
    }

    private void initUI() {
        setSize(620, 640);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(Theme.CARD_BG);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
        titleRow.setOpaque(false);

        JLabel iconLabel = new JLabel(com.degreely.app.util.Icons.user(20, Theme.PRIMARY));
        JLabel title = UI.sectionLabel("Student Academic Profile");
        title.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 18));

        titleRow.add(iconLabel);
        titleRow.add(title);
        mainPanel.add(titleRow);
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_4));

        JLabel desc = UI.secondaryLabel("Degreely AI uses your profile silently to personalize admission recommendations.");
        mainPanel.add(desc);
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Form Fields (2-column layout)
        JPanel grid = new JPanel(new GridLayout(0, 2, Theme.SPACING_16, Theme.SPACING_12));
        grid.setOpaque(false);

        grid.add(createField("Current / Target Degree", degreeField, "e.g. Bachelor / Master"));
        grid.add(createField("Field of Study", fieldStudyField, "e.g. Computer Science"));
        grid.add(createField("Cumulative CGPA (Scale 4.0)", cgpaField, "e.g. 3.75"));
        grid.add(createField("IELTS / English Score", ieltsField, "e.g. 7.5"));
        grid.add(createField("GRE / GMAT (Optional)", greField, "e.g. 320"));
        grid.add(createField("Target Intake", intakeField, "e.g. Fall 2027"));
        grid.add(createField("Preferred Countries", countriesField, "e.g. Germany, Canada"));
        grid.add(createField("Annual Budget", budgetField, "e.g. $15,000 - $25,000"));

        mainPanel.add(grid);
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_12));

        mainPanel.add(createField("Work / Research Experience", workExpField, "e.g. 1 year software development, 1 publication"));
        mainPanel.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UI.outlineButton("Cancel");
        cancelBtn.setFont(Theme.FONT_SMALL);
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = UI.primaryButton("Save Profile");
        saveBtn.setFont(Theme.FONT_SMALL);
        saveBtn.addActionListener(e -> saveProfileData());

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        mainPanel.add(btnRow);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.CARD_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createField(String labelText, JTextField field, String placeholder) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel l = new JLabel(labelText);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);

        field.setFont(Theme.FONT_BODY);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 6,10,6,10;");
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        p.add(l);
        p.add(Box.createVerticalStrut(Theme.SPACING_4));
        p.add(field);
        return p;
    }

    private void loadProfileData() {
        StudentProfile p = profileService.getProfile();
        degreeField.setText(p.getCurrentDegree());
        fieldStudyField.setText(p.getFieldOfStudy());
        cgpaField.setText(String.valueOf(p.getCgpa()));
        ieltsField.setText(String.valueOf(p.getIeltsScore()));
        greField.setText(p.getGreScore());
        budgetField.setText(p.getYearlyBudget());
        countriesField.setText(p.getPreferredCountries());
        intakeField.setText(p.getTargetIntake());
        workExpField.setText(p.getWorkExperience());
    }

    private void saveProfileData() {
        StudentProfile p = profileService.getProfile();
        p.setCurrentDegree(degreeField.getText().trim());
        p.setFieldOfStudy(fieldStudyField.getText().trim());

        try {
            p.setCgpa(Double.parseDouble(cgpaField.getText().trim()));
        } catch (Exception ignored) {}

        try {
            p.setIeltsScore(Double.parseDouble(ieltsField.getText().trim()));
        } catch (Exception ignored) {}

        p.setGreScore(greField.getText().trim());
        p.setYearlyBudget(budgetField.getText().trim());
        p.setPreferredCountries(countriesField.getText().trim());
        p.setTargetIntake(intakeField.getText().trim());
        p.setWorkExperience(workExpField.getText().trim());

        profileService.saveProfile(p);
        JOptionPane.showMessageDialog(this, "Profile updated! AI Counselor will now tailor responses to your background.", "Profile Saved", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
