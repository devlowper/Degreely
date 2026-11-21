package com.degreely.app.view;

import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Navbar extends JPanel {

    private final List<NavItemButton> navButtons = new ArrayList<>();
    private final Consumer<String> onNavigate;
    private String activePage = "Home";
    private JButton themeToggleBtn;

    public Navbar(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.CARD_BG);
        setPreferredSize(new Dimension(1200, 64));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(0, Theme.SPACING_32, 0, Theme.SPACING_32)
        ));

        // Left: Logo
        JLabel logo = new JLabel("Degreely");
        logo.setFont(new java.awt.Font(Theme.FONT_TITLE.getName(), java.awt.Font.BOLD, 22));
        logo.setForeground(Theme.PRIMARY);
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActive("Home");
            }
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 18));
        leftPanel.setOpaque(false);
        leftPanel.add(logo);

        // Center: Navigation Menu Items
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.SPACING_8, 0));
        centerPanel.setOpaque(false);

        String[] pages = {"Home", "Universities", "Scholarships", "Compare", "Predictor", "Deadlines"};
        for (String page : pages) {
            NavItemButton btn = new NavItemButton(page);
            btn.setActive(page.equals("Home"));
            btn.addActionListener(e -> setActive(page));
            navButtons.add(btn);
            centerPanel.add(btn);
        }

        // Right: Theme Toggle (Dark/Light mode)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 14));
        rightPanel.setOpaque(false);

        themeToggleBtn = UI.outlineButton("🌙 Dark");
        themeToggleBtn.setFont(Theme.FONT_SMALL);
        themeToggleBtn.setMargin(new Insets(6, 12, 6, 12));
        themeToggleBtn.addActionListener(e -> toggleTheme());
        rightPanel.add(themeToggleBtn);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private void toggleTheme() {
        try {
            if (FlatLaf.isLafDark()) {
                FlatLightLaf.setup();
                themeToggleBtn.setText("🌙 Dark");
            } else {
                FlatDarkLaf.setup();
                themeToggleBtn.setText("☀️ Light");
            }
            FlatLaf.updateUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setActive(String pageName) {
        this.activePage = pageName;
        for (NavItemButton btn : navButtons) {
            btn.setActive(btn.getText().equals(pageName));
        }
        if (onNavigate != null) {
            onNavigate.accept(pageName);
        }
    }

    public String getActivePage() {
        return activePage;
    }

    /**
     * Custom navigation button with text hover and bottom active indicator line.
     */
    private static class NavItemButton extends JButton {
        private boolean isActive = false;
        private boolean isHovered = false;

        public NavItemButton(String text) {
            super(text);
            setFont(Theme.FONT_BUTTON);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(getPreferredSize().width + 24, 62));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                setForeground(Theme.PRIMARY);
            } else if (isHovered) {
                setForeground(Theme.TEXT);
            } else {
                setForeground(Theme.TEXT_SECONDARY);
            }

            super.paintComponent(g2);

            if (isActive) {
                g2.setColor(Theme.PRIMARY);
                int underlineHeight = 3;
                int y = getHeight() - underlineHeight;
                int margin = 8;
                int width = getWidth() - (margin * 2);
                g2.fillRoundRect(margin, y, width, underlineHeight, 3, 3);
            }

            g2.dispose();
        }
    }
}
