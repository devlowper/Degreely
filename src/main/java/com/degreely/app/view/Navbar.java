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
 * Clean, minimal top navigation bar matching the Claude aesthetic.
 * Left-aligned brand logo and navigation tabs (Home & Degreely AI).
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
        setPreferredSize(new Dimension(1000, 50));

        // Group Brand Logo + Navigation Tabs together on the Left
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 3));
        leftGroup.setOpaque(false);

        // Brand Logo & Name
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brand.setOpaque(false);
        brand.setCursor(new Cursor(Cursor.HAND_CURSOR));
        brand.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveTab(TAB_HOME);
            }
        });

        JLabel logoIcon = new JLabel(Icons.spark(18, Theme.PRIMARY));
        JLabel brandName = new JLabel("Degreely");
        brandName.setFont(new Font(Theme.FONT_HERO.getFamily(), Font.BOLD, 18));
        brandName.setForeground(Theme.TEXT);

        brand.add(logoIcon);
        brand.add(brandName);
        leftGroup.add(brand);

        leftGroup.add(Box.createHorizontalStrut(16));

        // Navigation Tabs (Home, Degreely AI)
        homeButton = createNavButton("Home", Icons.home(15, Theme.TEXT_SECONDARY), TAB_HOME);
        aiButton = createNavButton("Degreely AI", Icons.spark(15, Theme.PRIMARY), TAB_AI);

        leftGroup.add(homeButton);
        leftGroup.add(aiButton);

        add(leftGroup, BorderLayout.WEST);

        updateTabStyles();
    }

    private JButton createNavButton(String text, javax.swing.Icon icon, String tabKey) {
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font(Theme.FONT_BODY.getFamily(), Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIconTextGap(8);
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
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
