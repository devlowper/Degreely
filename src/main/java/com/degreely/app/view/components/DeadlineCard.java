package com.degreely.app.view.components;

import com.degreely.app.controller.DeadlineService;
import com.degreely.app.model.ApplicationDeadline;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DeadlineCard extends JPanel {

    private final ApplicationDeadline deadline;
    private final DeadlineService service = DeadlineService.getInstance();
    private boolean isHovered = false;

    public DeadlineCard(ApplicationDeadline deadline) {
        this.deadline = deadline;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(Theme.SPACING_16, Theme.SPACING_12));
        setBackground(Theme.CARD_BG);
        putClientProperty(FlatClientProperties.STYLE, "arc: " + Theme.RADIUS + "; background: #FFFFFF;");
        updateBorder();

        // Hover Effect: Orange border
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

        // Top Row: University Name & Status + Days Left Badges
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel uniLabel = new JLabel(deadline.getUniversity());
        uniLabel.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 17));
        uniLabel.setForeground(Theme.TEXT);

        JPanel badgesRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        badgesRow.setOpaque(false);

        // Days left badge
        badgesRow.add(createDaysLeftBadge(deadline.getDeadlineDate()));

        // Status badge
        badgesRow.add(createStatusBadge(deadline.getStatus()));

        headerPanel.add(uniLabel, BorderLayout.WEST);
        headerPanel.add(badgesRow, BorderLayout.EAST);

        // Program Name
        JLabel programLabel = new JLabel(deadline.getProgram());
        programLabel.setFont(Theme.FONT_BODY);
        programLabel.setForeground(Theme.TEXT_SECONDARY);

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.add(headerPanel);
        topSection.add(Box.createVerticalStrut(Theme.SPACING_4));
        topSection.add(programLabel);

        // Bottom Row: Formatted Date & Action Controls (Status Dropdown + Delete)
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        String dateFormatted = formatDeadlineDate(deadline.getDeadlineDate());
        JLabel dateLabel = new JLabel("Deadline: " + dateFormatted);
        dateLabel.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
        dateLabel.setForeground(Theme.PRIMARY);

        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        actionsRow.setOpaque(false);

        // Status Quick Selector
        JLabel statusPrompt = new JLabel("Status:");
        statusPrompt.setFont(Theme.FONT_SMALL);
        statusPrompt.setForeground(Theme.TEXT_MUTED);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "Applied", "Done"});
        statusCombo.setFont(Theme.FONT_SMALL);
        statusCombo.setSelectedItem(deadline.getStatus());
        statusCombo.addActionListener(e -> {
            String selected = (String) statusCombo.getSelectedItem();
            if (selected != null && !selected.equals(deadline.getStatus())) {
                service.updateStatus(deadline.getId(), selected);
            }
        });

        // Delete Button
        JButton deleteBtn = UI.outlineButton("Delete");
        deleteBtn.setFont(Theme.FONT_SMALL);
        deleteBtn.setMargin(new Insets(6, 12, 6, 12));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this deadline entry?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.deleteDeadline(deadline.getId());
            }
        });

        actionsRow.add(statusPrompt);
        actionsRow.add(statusCombo);
        actionsRow.add(deleteBtn);

        bottomRow.add(dateLabel, BorderLayout.WEST);
        bottomRow.add(actionsRow, BorderLayout.EAST);

        // Assembly
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(topSection);
        center.add(Box.createVerticalStrut(Theme.SPACING_12));
        center.add(bottomRow);

        add(center, BorderLayout.CENTER);
    }

    private void updateBorder() {
        Color borderColor = isHovered ? Theme.PRIMARY : Theme.BORDER;
        setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(1, 1, 1, 1), borderColor, 1, Theme.RADIUS),
                BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16)
        ));
    }

    private JPanel createDaysLeftBadge(String dateStr) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        badge.setOpaque(true);

        long days = computeDaysLeft(dateStr);
        String text;
        Color bg;
        Color fg;

        if (days < 0) {
            text = Math.abs(days) + "d overdue";
            bg = new Color(254, 226, 226); // Red-100
            fg = new Color(220, 38, 38);   // Red-600
        } else if (days == 0) {
            text = "Due Today!";
            bg = new Color(254, 226, 226);
            fg = new Color(220, 38, 38);
        } else if (days <= 7) {
            text = days + " days left";
            bg = new Color(254, 226, 226); // Red
            fg = new Color(220, 38, 38);
        } else if (days <= 30) {
            text = days + " days left";
            bg = new Color(254, 243, 199); // Amber
            fg = new Color(217, 119, 6);
        } else {
            text = days + " days left";
            bg = new Color(220, 252, 231); // Green
            fg = new Color(22, 101, 52);
        }

        badge.setBackground(bg);
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");

        JLabel l = new JLabel(text);
        l.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        l.setForeground(fg);
        badge.add(l);
        return badge;
    }

    private JPanel createStatusBadge(String status) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        badge.setOpaque(true);

        Color bg;
        Color fg;

        if ("Done".equalsIgnoreCase(status)) {
            bg = new Color(220, 252, 231); // Green
            fg = new Color(22, 101, 52);
        } else if ("Applied".equalsIgnoreCase(status)) {
            bg = Theme.LIGHT_BLUE_HIGHLIGHT; // Blue
            fg = Color.decode("#2563EB");
        } else {
            bg = new Color(254, 243, 199); // Amber
            fg = new Color(217, 119, 6);
        }

        badge.setBackground(bg);
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");

        JLabel l = new JLabel(status);
        l.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        l.setForeground(fg);
        badge.add(l);
        return badge;
    }

    private long computeDaysLeft(String dateStr) {
        try {
            LocalDate deadline = LocalDate.parse(dateStr);
            return ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        } catch (Exception e) {
            return 999;
        }
    }

    private String formatDeadlineDate(String dateStr) {
        try {
            LocalDate d = LocalDate.parse(dateStr);
            return d.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        } catch (Exception e) {
            return dateStr != null ? dateStr : "TBA";
        }
    }
}
