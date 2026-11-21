package com.degreely.app.view;

import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;

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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Navbar extends JPanel {

    private final List<NavItemButton> navButtons = new ArrayList<>();
    private final Consumer<String> onNavigate;
    private String activePage = "Universities";

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
                BorderFactory.createEmptyBorder(0, Theme.SPACING_24, 0, Theme.SPACING_24)
        ));

        // Left: Stylized Logo & Brand Mark
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 16));
        brandPanel.setOpaque(false);
        brandPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brandPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActive("Universities");
            }
        });

        // Painted Logo Icon (28x28 rounded orange square with 'D')
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Theme.FONT_BUTTON.getName(), Font.BOLD, 17));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("D")) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("D", x, y);

                g2.dispose();
            }
        };
        logoIcon.setPreferredSize(new Dimension(30, 30));
        logoIcon.setOpaque(false);

        JLabel logoText = new JLabel("Degreely");
        logoText.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 20));
        logoText.setForeground(Theme.TEXT);

        brandPanel.add(logoIcon);
        brandPanel.add(logoText);

        // Center: Navigation Menu Items
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.SPACING_8, 0));
        centerPanel.setOpaque(false);

        String[] pages = {"Universities", "Scholarships", "Degreely AI", "Compare", "Predictor", "Deadlines"};
        for (String page : pages) {
            NavItemButton btn = new NavItemButton(page);
            btn.setActive(page.equals("Universities"));
            btn.addActionListener(e -> setActive(page));
            navButtons.add(btn);
            centerPanel.add(btn);
        }

        // Right: Clean Live Status Pill
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 18));
        rightPanel.setOpaque(false);

        JPanel statusPill = UI.badge("● Live Directory", Theme.GREEN_BG, Theme.GREEN_TEXT);
        statusPill.setToolTipText("20 Top Universities • 80+ Programs Available");
        rightPanel.add(statusPill);

        add(brandPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setActive(String pageName) {
        if ("Home".equalsIgnoreCase(pageName)) {
            pageName = "Universities";
        }
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
     * Custom navigation button with quiet text and 3px bottom indicator.
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
            setPreferredSize(new Dimension(getPreferredSize().width + 20, 62));

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
                int margin = 10;
                int width = getWidth() - (margin * 2);
                g2.fillRoundRect(margin, y, width, underlineHeight, 3, 3);
            }

            g2.dispose();
        }
    }
}
