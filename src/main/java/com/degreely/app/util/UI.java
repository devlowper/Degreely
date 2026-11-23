package com.degreely.app.util;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

public final class UI {

    private UI() {}

    /**
     * Creates a filled primary action button with signature orange branding.
     */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(Theme.PRIMARY);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(Theme.BUTTON_PAD_Y, Theme.BUTTON_PAD_X, Theme.BUTTON_PAD_Y, Theme.BUTTON_PAD_X));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        return btn;
    }

    /**
     * Creates an outlined button with subtle border and warm hover state.
     */
    public static JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BUTTON);
        btn.setForeground(Theme.PRIMARY);
        btn.setBackground(Theme.CARD_BG);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(Theme.BUTTON_PAD_Y, Theme.BUTTON_PAD_X, Theme.BUTTON_PAD_Y, Theme.BUTTON_PAD_X));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btn.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, Theme.RADIUS));
        return btn;
    }

    /**
     * Creates an elevated card surface with 10px rounded corners and subtle border.
     */
    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, Theme.RADIUS),
                BorderFactory.createEmptyBorder(
                        Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16)
        ));
        return panel;
    }

    /**
     * Creates a compact styled pill badge with text.
     */
    public static JPanel badge(String text, Color bg, Color fg) {
        return badge(text, null, bg, fg);
    }

    /**
     * Creates a compact styled pill badge with an optional icon and text.
     */
    public static JPanel badge(String text, javax.swing.Icon icon, Color bg, Color fg) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        badge.setBackground(bg);
        badge.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), bg, 1, 12));
        JLabel label = new JLabel(text);
        if (icon != null) {
            label.setIcon(icon);
            label.setIconTextGap(5);
        }
        label.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        label.setForeground(fg);
        badge.add(label);
        return badge;
    }

    // Standard Typography Labels
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
