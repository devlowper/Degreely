package com.degreely.app.view.components.chat;

import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class StarterPromptsPanel extends JPanel {

    private final Consumer<String> onSelectPrompt;
    private final Runnable onComparePrompt;

    public StarterPromptsPanel(Consumer<String> onSelectPrompt) {
        this(onSelectPrompt, null);
    }

    public StarterPromptsPanel(Consumer<String> onSelectPrompt, Runnable onComparePrompt) {
        this.onSelectPrompt = onSelectPrompt;
        this.onComparePrompt = onComparePrompt;
        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_24,
                Theme.SPACING_32));

        // 1. Centered Spark Icon
        JPanel sparkIcon = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 9)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        sparkIcon.setPreferredSize(new Dimension(42, 42));
        sparkIcon.setMaximumSize(new Dimension(42, 42));
        sparkIcon.setOpaque(false);
        sparkIcon.setAlignmentX(CENTER_ALIGNMENT);
        sparkIcon.add(new JLabel(Icons.spark(20, Color.WHITE)));

        add(Box.createVerticalGlue());
        add(sparkIcon);
        add(Box.createVerticalStrut(Theme.SPACING_12));

        // 2. Greeting Title in Serif font
        JLabel greeting = new JLabel("Welcome to Degreely");
        greeting.setFont(new Font(Font.SERIF, Font.PLAIN, 32));
        greeting.setForeground(Theme.TEXT);
        greeting.setAlignmentX(CENTER_ALIGNMENT);
        add(greeting);
        add(Box.createVerticalStrut(Theme.SPACING_8));

        JLabel subtitle = UI.secondaryLabel(
                "Your dedicated AI study-abroad counselor for universities, costs, requirements, and scholarships.");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        add(subtitle);
        add(Box.createVerticalStrut(Theme.SPACING_32));

        // 3. Four Starter Cards Grid (2x2)
        JPanel grid = new JPanel(new GridLayout(2, 2, Theme.SPACING_12, Theme.SPACING_12));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(760, 180));

        grid.add(createCard(
                Icons.scale(18, Theme.PRIMARY),
                "Compare 2 or 3 Universities in Table",
                "Side-by-side comparison of tuition, living cost, GPA & deadlines",
                () -> {
                    if (onComparePrompt != null) {
                        onComparePrompt.run();
                    } else if (onSelectPrompt != null) {
                        onSelectPrompt.accept("Compare Technical University of Munich, University of Toronto, and ETH Zurich for Master in Computer Science in a detailed comparison table.");
                    }
                }
        ));
        grid.add(createCard(
                Icons.coins(18, Theme.PRIMARY),
                "Cheapest countries for study abroad",
                "Explore zero-tuition and low-cost public institutions",
                () -> { if (onSelectPrompt != null) onSelectPrompt.accept("Which countries offer free or cheapest tuition for international Master's programs?"); }
        ));
        grid.add(createCard(
                Icons.documentCheck(18, Theme.PRIMARY),
                "IELTS and GPA requirements for Canada",
                "Check admissions cutoffs and visa score guidelines",
                () -> { if (onSelectPrompt != null) onSelectPrompt.accept("What are the minimum GPA and IELTS requirements for Master's programs in Canada?"); }
        ));
        grid.add(createCard(
                Icons.pen(18, Theme.PRIMARY),
                "Review my SOP & Academic Profile",
                "Attach draft SOP or CV for constructive admissions feedback",
                () -> { if (onSelectPrompt != null) onSelectPrompt.accept("Please review my academic profile and Statement of Purpose for admission eligibility."); }
        ));

        grid.setAlignmentX(CENTER_ALIGNMENT);
        add(grid);
        add(Box.createVerticalGlue());
    }

    private JPanel createCard(javax.swing.Icon icon, String title, String desc, Runnable onClick) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.CARD_BG);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFFFFF;");
        card.setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, 12),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel ic = new JLabel(icon);
        JLabel t = new JLabel(title);
        t.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
        t.setForeground(Theme.TEXT);
        headerRow.add(ic);
        headerRow.add(t);

        JLabel d = new JLabel(desc);
        d.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 11));
        d.setForeground(Theme.TEXT_MUTED);
        d.setAlignmentX(LEFT_ALIGNMENT);
        d.setBorder(BorderFactory.createEmptyBorder(0, 26, 0, 0));

        card.add(headerRow);
        card.add(Box.createVerticalStrut(4));
        card.add(d);

        // Hover Effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.PRIMARY, 1, 12),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, 12),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });

        return card;
    }
}
