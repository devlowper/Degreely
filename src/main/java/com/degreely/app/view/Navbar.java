package com.degreely.app.view;

import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Modern top navigation bar with brand identity, Home & Degreely AI navigation,
 * and quick access to study abroad counseling.
 */
public class Navbar extends JPanel {

    public static final String TAB_HOME = "HOME";
    public static final String TAB_AI = "AI";

    private final Consumer<String> onNavigate;
    private final JButton homeButton;
    private final JButton aiButton;
    private String currentTab = TAB_HOME;

    public Navbar(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(6, 24, 6, 24)
        ));
        setPreferredSize(new Dimension(1000, 56));

        // 1. Left: Brand Logo & Title
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        brandPanel.setOpaque(false);
        brandPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        brandPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveTab(TAB_HOME);
            }
        });

        JLabel logoIcon = new JLabel(Icons.spark(20, Theme.PRIMARY));
        JLabel brandName = new JLabel("Degreely");
        brandName.setFont(new Font(Theme.FONT_HERO.getFamily(), Font.BOLD, 19));
        brandName.setForeground(Theme.TEXT);

        JLabel badge = new JLabel("Global Study Abroad");
        badge.setFont(Theme.FONT_SMALL);
        badge.setForeground(Theme.PRIMARY);
        badge.setOpaque(true);
        badge.setBackground(Theme.PRIMARY_LIGHT);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12");

        brandPanel.add(logoIcon);
        brandPanel.add(brandName);
        brandPanel.add(badge);
        add(brandPanel, BorderLayout.WEST);

        // 2. Center: Navigation Tabs (Home, Degreely AI)
        JPanel navTabsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        navTabsPanel.setOpaque(false);

        homeButton = createNavButton("Home", Icons.home(16, Theme.TEXT_SECONDARY), TAB_HOME);
        aiButton = createNavButton("Degreely AI", Icons.spark(16, Theme.PRIMARY), TAB_AI);

        navTabsPanel.add(homeButton);
        navTabsPanel.add(aiButton);
        add(navTabsPanel, BorderLayout.CENTER);

        // 3. Right: Live Stats & Action CTA
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        rightPanel.setOpaque(false);

        JLabel liveCountLabel = new JLabel("● 20 Universities Live");
        liveCountLabel.setFont(Theme.FONT_SMALL);
        liveCountLabel.setForeground(Theme.GREEN_TEXT);

        JButton askAiBtn = new JButton("Ask AI Counselor ✦");
        askAiBtn.setFont(Theme.FONT_BUTTON);
        askAiBtn.setForeground(Color.WHITE);
        askAiBtn.setBackground(Theme.PRIMARY);
        askAiBtn.setFocusPainted(false);
        askAiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        askAiBtn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        askAiBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        askAiBtn.addActionListener(e -> setActiveTab(TAB_AI));

        rightPanel.add(liveCountLabel);
        rightPanel.add(askAiBtn);
        add(rightPanel, BorderLayout.EAST);

        updateTabStyles();
    }

    private JButton createNavButton(String text, javax.swing.Icon icon, String tabKey) {
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIconTextGap(8);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btn.addActionListener(e -> setActiveTab(tabKey));
        return btn;
    }

    public void setActiveTab(String tabKey) {
        if (!tabKey.equals(this.currentTab)) {
            this.currentTab = tabKey;
            updateTabStyles();
            if (onNavigate != null) {
                onNavigate.accept(tabKey);
            }
        }
    }

    public void selectTabWithoutNotification(String tabKey) {
        this.currentTab = tabKey;
        updateTabStyles();
    }

    private void updateTabStyles() {
        if (TAB_HOME.equals(currentTab)) {
            styleActive(homeButton, Icons.home(16, Theme.PRIMARY));
            styleInactive(aiButton, Icons.spark(16, Theme.TEXT_SECONDARY));
        } else {
            styleInactive(homeButton, Icons.home(16, Theme.TEXT_SECONDARY));
            styleActive(aiButton, Icons.spark(16, Theme.PRIMARY));
        }
        revalidate();
        repaint();
    }

    private void styleActive(JButton btn, javax.swing.Icon icon) {
        btn.setIcon(icon);
        btn.setBackground(Theme.PRIMARY_LIGHT);
        btn.setForeground(Theme.PRIMARY);
        btn.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.BOLD, 14));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 13, 5, 13)
        ));
    }

    private void styleInactive(JButton btn, javax.swing.Icon icon) {
        btn.setIcon(icon);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Theme.TEXT_SECONDARY);
        btn.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }
}
