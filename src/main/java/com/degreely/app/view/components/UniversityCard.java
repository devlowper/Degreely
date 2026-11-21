package com.degreely.app.view.components;

import com.degreely.app.model.University;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class UniversityCard extends JPanel {

    private final University university;
    private final Consumer<University> onOpenDetails;
    private boolean isHovered = false;

    public UniversityCard(University university) {
        this(university, null);
    }

    public UniversityCard(University university, Consumer<University> onOpenDetails) {
        this.university = university;
        this.onOpenDetails = onOpenDetails;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(Theme.SPACING_16, Theme.SPACING_12));
        setBackground(Theme.CARD_BG);
        putClientProperty(FlatClientProperties.STYLE, "arc: " + Theme.RADIUS + "; background: #FFFFFF;");
        updateBorder();

        // Hover effect: border transitions to primary orange
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

        // Top Header Row: Name & Ranking Badge
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(university.getName());
        nameLabel.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 17));
        nameLabel.setForeground(Theme.TEXT);

        JPanel badge = createRankingBadge("QS #" + university.getRanking() + " World Rank");

        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(badge, BorderLayout.EAST);

        // Subtitle: Location (City, Country)
        JLabel locationLabel = new JLabel(university.getCity() + ", " + university.getCountry());
        locationLabel.setFont(Theme.FONT_BODY);
        locationLabel.setForeground(Theme.TEXT_SECONDARY);

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.add(headerPanel);
        topSection.add(Box.createVerticalStrut(Theme.SPACING_4));
        topSection.add(locationLabel);

        // Middle Section: Programs available pill tags
        JPanel programsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
        programsRow.setOpaque(false);
        programsRow.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_8, 0, Theme.SPACING_8, 0));

        boolean hasBachelor = university.getPrograms().stream().anyMatch(p -> "Bachelor".equalsIgnoreCase(p.getLevel()));
        boolean hasMaster = university.getPrograms().stream().anyMatch(p -> "Master".equalsIgnoreCase(p.getLevel()));
        boolean hasPhd = university.getPrograms().stream().anyMatch(p -> "PhD".equalsIgnoreCase(p.getLevel()));

        if (hasBachelor) programsRow.add(createProgramTag("Bachelor"));
        if (hasMaster) programsRow.add(createProgramTag("Master"));
        if (hasPhd) programsRow.add(createProgramTag("PhD"));

        // Bottom Row: Stats Cluster & "View Details →" Button
        JPanel bottomRow = new JPanel(new BorderLayout(Theme.SPACING_16, 0));
        bottomRow.setOpaque(false);

        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
        statsRow.setOpaque(false);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setMaximumFractionDigits(0);
        String tuitionStr = currencyFormat.format(university.getTuitionPerYear()) + "/yr";

        statsRow.add(createStatChip("Tuition", tuitionStr));
        statsRow.add(createStatChip("Acceptance", university.getAcceptanceRate() + "%"));
        statsRow.add(createStatChip("Min IELTS", String.valueOf(university.getMinIelts())));
        statsRow.add(createStatChip("Min GPA", String.valueOf(university.getMinGpa())));

        JButton detailsBtn = UI.outlineButton("View Details →");
        detailsBtn.setFont(Theme.FONT_SMALL);
        detailsBtn.setMargin(new Insets(6, 14, 6, 14));
        detailsBtn.addActionListener(e -> {
            if (onOpenDetails != null) {
                onOpenDetails.accept(university);
            }
        });

        bottomRow.add(statsRow, BorderLayout.WEST);
        bottomRow.add(detailsBtn, BorderLayout.EAST);

        // Assemble Card
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);
        centerContent.add(topSection);
        centerContent.add(programsRow);
        centerContent.add(Box.createVerticalStrut(Theme.SPACING_4));
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

    private JPanel createRankingBadge(String text) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        badge.setBackground(Theme.PRIMARY_LIGHT);
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFF7ED;");
        badge.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.PRIMARY_BORDER, 1, 12));

        JLabel label = new JLabel(text);
        label.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        label.setForeground(Theme.PRIMARY);
        badge.add(label);
        return badge;
    }

    private JPanel createProgramTag(String text) {
        JPanel tag = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        tag.setBackground(Theme.BACKGROUND);
        tag.putClientProperty(FlatClientProperties.STYLE, "arc: 6; background: #F8FAFC;");
        tag.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, 6));

        JLabel label = new JLabel(text);
        label.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        label.setForeground(Theme.TEXT_SECONDARY);
        tag.add(label);
        return tag;
    }

    private JPanel createStatChip(String label, String value) {
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBackground(Theme.BACKGROUND);
        chip.putClientProperty(FlatClientProperties.STYLE, "arc: 6; background: #F8FAFC;");
        chip.setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, 6),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 10));
        lbl.setForeground(Theme.TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 12));
        val.setForeground(Theme.TEXT);

        chip.add(lbl);
        chip.add(Box.createVerticalStrut(1));
        chip.add(val);
        return chip;
    }
}
