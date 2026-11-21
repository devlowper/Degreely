package com.degreely.app.view;

import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
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
 * Clean, minimal top navigation bar matching the Claude aesthetic.
 * Contains brand identity on the left and Home / Degreely AI navigation tabs.
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
        setPreferredSize(new Dimension(1000, 52));

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

        JLabel logoIcon = new JLabel(Icons.spark(18, Theme.PRIMARY));
        JLabel brandName = new JLabel("Degreely");
        brandName.setFont(new Font(Theme.FONT_HERO.getFamily(), Font.BOLD, 18));
        brandName.setForeground(Theme.TEXT);

        brandPanel.add(logoIcon);
        brandPanel.add(brandName);
        add(brandPanel, BorderLayout.WEST);

        // 2. Center: Navigation Tabs (Home, Degreely AI)
        JPanel navTabsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        navTabsPanel.setOpaque(false);

        homeButton = createNavButton("Home", Icons.home(15, Theme.TEXT_SECONDARY), TAB_HOME);
        aiButton = createNavButton("Degreely AI", Icons.spark(15, Theme.PRIMARY), TAB_AI);

        navTabsPanel.add(homeButton);
        navTabsPanel.add(aiButton);
        add(navTabsPanel, BorderLayout.CENTER);

        // 3. Right: Clean spacer to keep layout balanced
        JPanel rightSpacer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightSpacer.setOpaque(false);
        rightSpacer.setPreferredSize(new Dimension(120, 32));
        add(rightSpacer, BorderLayout.EAST);

        updateTabStyles();
    }

    private JButton createNavButton(String text, javax.swing.Icon icon, String tabKey) {
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIconTextGap(8);
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
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
            styleActive(homeButton, Icons.home(15, Theme.PRIMARY));
            styleInactive(aiButton, Icons.spark(15, Theme.TEXT_SECONDARY));
        } else {
            styleInactive(homeButton, Icons.home(15, Theme.TEXT_SECONDARY));
            styleActive(aiButton, Icons.spark(15, Theme.PRIMARY));
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
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private void styleInactive(JButton btn, javax.swing.Icon icon) {
        btn.setIcon(icon);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Theme.TEXT_SECONDARY);
        btn.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
    }
}
