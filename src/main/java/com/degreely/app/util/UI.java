package com.degreely.app.util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;

public final class UI {

    private UI() {}

    /**
     * Creates a filled primary action button with orange theme and hover state.
     */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BUTTON);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setBackground(Theme.PRIMARY);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(Theme.BUTTON_PAD_Y, Theme.BUTTON_PAD_X, Theme.BUTTON_PAD_Y, Theme.BUTTON_PAD_X));

        // FlatLaf client properties for modern styling
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: " + Theme.RADIUS + ";" +
                "background: #E37712;" +
                "hoverBackground: #D2500E;" +
                "foreground: #FFFFFF;" +
                "borderWidth: 0;" +
                "focusWidth: 0;" +
                "innerFocusWidth: 0;");
        return btn;
    }

    /**
     * Creates an outlined button with light hover highlight.
     */
    public static JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BUTTON);
        btn.setForeground(Theme.PRIMARY);
        btn.setBackground(Theme.CARD_BG);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(Theme.BUTTON_PAD_Y, Theme.BUTTON_PAD_X, Theme.BUTTON_PAD_Y, Theme.BUTTON_PAD_X));

        // FlatLaf client properties for outlined styling
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: " + Theme.RADIUS + ";" +
                "background: #FFFFFF;" +
                "hoverBackground: #EFF6FF;" +
                "foreground: #E37712;" +
                "borderColor: #E5E7EB;" +
                "hoverBorderColor: #E37712;" +
                "borderWidth: 1;" +
                "focusWidth: 0;" +
                "innerFocusWidth: 0;");
        return btn;
    }

    /**
     * Creates a card surface container with rounded corners, border, and white background.
     */
    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.CARD_BG);
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc: " + Theme.RADIUS + ";" +
                "background: #FFFFFF;");
        panel.setBorder(BorderFactory.createCompoundBorder(
                new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, Theme.RADIUS),
                BorderFactory.createEmptyBorder(
                        Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16)
        ));
        return panel;
    }

    // Typography labels
    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_TITLE);
        label.setForeground(Theme.TEXT);
        return label;
    }

    public static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_SECTION);
        label.setForeground(Theme.TEXT);
        return label;
    }

    public static JLabel bodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY);
        label.setForeground(Theme.TEXT);
        return label;
    }

    public static JLabel secondaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY);
        label.setForeground(Theme.TEXT_SECONDARY);
        return label;
    }

    public static JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_SMALL);
        label.setForeground(Theme.TEXT_MUTED);
        return label;
    }
}
