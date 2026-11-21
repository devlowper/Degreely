package com.degreely.app.view.components.chat;

import com.degreely.app.controller.GeminiService;
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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;

public class ApiKeyDialog extends JDialog {

    private final GeminiService geminiService = GeminiService.getInstance();
    private final JPasswordField keyField = new JPasswordField();
    private final JComboBox<String> modelCombo = new JComboBox<>(new String[]{
            "gemini-2.5-flash", "gemini-1.5-flash", "gemini-1.5-pro"
    });

    public ApiKeyDialog(Frame owner) {
        super(owner, "Gemini API Settings", true);
        initUI();
    }

    private void initUI() {
        setSize(480, 360);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(Theme.CARD_BG);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
        titleRow.setOpaque(false);

        JLabel iconLabel = new JLabel(com.degreely.app.util.Icons.settings(20, Theme.PRIMARY));
        JLabel title = UI.sectionLabel("Gemini API Configuration");
        title.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 18));

        titleRow.add(iconLabel);
        titleRow.add(title);
        content.add(titleRow);
        content.add(Box.createVerticalStrut(Theme.SPACING_4));

        JLabel desc = UI.secondaryLabel("Enter your Gemini API key to power Night Owl AI Counselor.");
        content.add(desc);
        content.add(Box.createVerticalStrut(Theme.SPACING_16));

        // API Key Field
        JLabel keyLabel = new JLabel("Gemini API Key");
        keyLabel.setFont(Theme.FONT_SMALL);
        keyLabel.setForeground(Theme.TEXT_SECONDARY);
        content.add(keyLabel);
        content.add(Box.createVerticalStrut(Theme.SPACING_4));

        keyField.setText(geminiService.getApiKey());
        keyField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "AIzaSy...");
        keyField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 6,10,6,10;");
        keyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        content.add(keyField);
        content.add(Box.createVerticalStrut(Theme.SPACING_4));

        JLabel hint = UI.mutedLabel("Get a free API key at: aistudio.google.com");
        content.add(hint);
        content.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Model Selector
        JLabel modelLabel = new JLabel("Default AI Model");
        modelLabel.setFont(Theme.FONT_SMALL);
        modelLabel.setForeground(Theme.TEXT_SECONDARY);
        content.add(modelLabel);
        content.add(Box.createVerticalStrut(Theme.SPACING_4));

        modelCombo.setSelectedItem(geminiService.getSelectedModel());
        modelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        content.add(modelCombo);
        content.add(Box.createVerticalStrut(Theme.SPACING_24));

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = UI.outlineButton("Cancel");
        cancelBtn.setFont(Theme.FONT_SMALL);
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = UI.primaryButton("Save Settings");
        saveBtn.setFont(Theme.FONT_SMALL);
        saveBtn.addActionListener(e -> {
            String key = new String(keyField.getPassword()).trim();
            geminiService.setApiKey(key);
            geminiService.setSelectedModel((String) modelCombo.getSelectedItem());
            JOptionPane.showMessageDialog(this, "API settings saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        content.add(btnRow);

        add(content, BorderLayout.CENTER);
    }
}
