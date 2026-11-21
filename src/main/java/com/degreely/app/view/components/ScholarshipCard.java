package com.degreely.app.view.components;

import com.degreely.app.model.Scholarship;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ScholarshipCard extends JPanel {

    private final Scholarship scholarship;
    private boolean isHovered = false;

    public ScholarshipCard(Scholarship scholarship) {
        this.scholarship = scholarship;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(Theme.SPACING_16, Theme.SPACING_12));
        setBackground(Theme.CARD_BG);
        putClientProperty(FlatClientProperties.STYLE, "arc: " + Theme.RADIUS + "; background: #FFFFFF;");
        updateBorder();

        // Hover Effect: Orange border on mouse enter
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                updateBorder();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                updateBorder();
                repaint();
            }
        });

        // Top Row: Scholarship Name & Badges (Level + Closing Soon)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(scholarship.getName());
        nameLabel.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 17));
        nameLabel.setForeground(Theme.TEXT);

        JPanel badgesRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        badgesRow.setOpaque(false);

        // Degree Level Tag
        badgesRow.add(createLevelBadge(scholarship.getLevel()));

        // Check if deadline is within 30 days
        LocalDate now = LocalDate.now();
        LocalDate deadlineDate = parseDeadline(scholarship.getDeadline());
        if (deadlineDate != null) {
            long daysLeft = ChronoUnit.DAYS.between(now, deadlineDate);
            if (daysLeft >= 0 && daysLeft <= 30) {
                badgesRow.add(createClosingSoonBadge("Closing soon (" + daysLeft + "d left)"));
            }
        }

        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(badgesRow, BorderLayout.EAST);

        // Subtitle: University & Country
        JLabel uniLabel = new JLabel(scholarship.getUniversity() + "  •  " + scholarship.getCountry());
        uniLabel.setFont(Theme.FONT_BODY);
        uniLabel.setForeground(Theme.TEXT_MUTED);

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.add(headerPanel);
        topSection.add(Box.createVerticalStrut(Theme.SPACING_4));
        topSection.add(uniLabel);

        // Bottom Row: Amount (Orange Bold) & Deadline, plus "Apply Link" button
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        JPanel amountAndDeadlines = new JPanel();
        amountAndDeadlines.setLayout(new BoxLayout(amountAndDeadlines, BoxLayout.Y_AXIS));
        amountAndDeadlines.setOpaque(false);

        // Amount in Orange Bold
        JLabel amountLabel = new JLabel(scholarship.getAmount());
        amountLabel.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 15));
        amountLabel.setForeground(Theme.PRIMARY);

        // Deadline
        String formattedDeadline = formatDeadlineDisplay(scholarship.getDeadline());
        JLabel deadlineLabel = new JLabel("Application Deadline: " + formattedDeadline);
        deadlineLabel.setFont(Theme.FONT_SMALL);
        deadlineLabel.setForeground(Theme.TEXT_SECONDARY);

        amountAndDeadlines.add(amountLabel);
        amountAndDeadlines.add(Box.createVerticalStrut(Theme.SPACING_4));
        amountAndDeadlines.add(deadlineLabel);

        // Apply Link Button
        JButton applyBtn = UI.primaryButton("Apply Link ↗");
        applyBtn.setFont(Theme.FONT_SMALL);
        applyBtn.setMargin(new Insets(8, 16, 8, 16));
        applyBtn.addActionListener(e -> openApplyLink());

        bottomRow.add(amountAndDeadlines, BorderLayout.WEST);
        bottomRow.add(applyBtn, BorderLayout.EAST);

        // Center container
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);
        centerContent.add(topSection);
        centerContent.add(Box.createVerticalStrut(Theme.SPACING_12));
        centerContent.add(bottomRow);

        add(centerContent, BorderLayout.CENTER);
    }

    private void updateBorder() {
        Color borderColor = isHovered ? Theme.PRIMARY : Theme.BORDER;
        setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(1, 1, 1, 1), borderColor, 1, Theme.RADIUS),
                BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16)
        ));
    }

    private JPanel createClosingSoonBadge(String text) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        badge.setBackground(new Color(254, 226, 226)); // Soft light red
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FEE2E2;");
        JLabel label = new JLabel(text);
        label.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        label.setForeground(new Color(220, 38, 38)); // Vibrant red
        badge.add(label);
        return badge;
    }

    private JPanel createLevelBadge(String level) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        badge.setBackground(Theme.LIGHT_BLUE_HIGHLIGHT);
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #EFF6FF;");
        JLabel label = new JLabel(level);
        label.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        label.setForeground(Theme.PRIMARY);
        badge.add(label);
        return badge;
    }

    private LocalDate parseDeadline(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatDeadlineDisplay(String dateStr) {
        LocalDate date = parseDeadline(dateStr);
        if (date == null) return dateStr != null ? dateStr : "TBA";
        return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    private void openApplyLink() {
        String url = scholarship.getApplyUrl();
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                showLinkDialog(url);
            }
        } catch (Exception ex) {
            showLinkDialog(url);
        }
    }

    private void showLinkDialog(String url) {
        JOptionPane.showMessageDialog(this,
                "Official Application Portal:\n" + url,
                scholarship.getName(),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
