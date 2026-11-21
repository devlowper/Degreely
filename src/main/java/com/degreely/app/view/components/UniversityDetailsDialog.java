package com.degreely.app.view.components;

import com.degreely.app.model.Program;
import com.degreely.app.model.University;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.text.NumberFormat;
import java.util.Locale;

public class UniversityDetailsDialog extends JDialog {

    public UniversityDetailsDialog(Window owner, University university) {
        super(owner, university.getName(), ModalityType.APPLICATION_MODAL);
        initUI(university);
    }

    private void initUI(University university) {
        setSize(650, 520);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24));

        // Header
        JLabel title = UI.sectionLabel(university.getName());
        JLabel subtitle = UI.secondaryLabel(university.getCity() + ", " + university.getCountry() +
                "  •  World Rank: #" + university.getRanking());

        content.add(title);
        content.add(Box.createVerticalStrut(Theme.SPACING_4));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Key stats card
        JPanel statsCard = UI.card();
        statsCard.setLayout(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_24, Theme.SPACING_8));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setMaximumFractionDigits(0);

        statsCard.add(createMetric("Tuition / Year", currencyFormat.format(university.getTuitionPerYear())));
        statsCard.add(createMetric("Acceptance Rate", university.getAcceptanceRate() + "%"));
        statsCard.add(createMetric("Min IELTS", String.valueOf(university.getMinIelts())));
        statsCard.add(createMetric("Min GPA", String.valueOf(university.getMinGpa())));

        content.add(statsCard);
        content.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Programs list section
        JLabel programsTitle = UI.sectionLabel("Available Degree Programs");
        programsTitle.setFont(new Font(Theme.FONT_SECTION.getName(), Font.BOLD, 16));
        content.add(programsTitle);
        content.add(Box.createVerticalStrut(Theme.SPACING_8));

        JPanel programsList = new JPanel();
        programsList.setLayout(new BoxLayout(programsList, BoxLayout.Y_AXIS));
        programsList.setOpaque(false);

        for (Program program : university.getPrograms()) {
            JPanel pCard = UI.card();
            pCard.setLayout(new BorderLayout());
            pCard.setBorder(BorderFactory.createCompoundBorder(
                    pCard.getBorder(),
                    BorderFactory.createEmptyBorder(Theme.SPACING_4, Theme.SPACING_8, Theme.SPACING_4, Theme.SPACING_8)
            ));

            JLabel pName = new JLabel(program.getName());
            pName.setFont(Theme.FONT_BODY);
            pName.setForeground(Theme.TEXT);

            JLabel pMeta = new JLabel(program.getLevel() + " • " + program.getDurationMonths() + " months");
            pMeta.setFont(Theme.FONT_SMALL);
            pMeta.setForeground(Theme.PRIMARY);

            pCard.add(pName, BorderLayout.WEST);
            pCard.add(pMeta, BorderLayout.EAST);

            programsList.add(pCard);
            programsList.add(Box.createVerticalStrut(Theme.SPACING_8));
        }

        JScrollPane scrollPane = new JScrollPane(programsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        content.add(scrollPane);

        // Bottom close button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_12, 0, 0, 0));

        JButton closeBtn = UI.primaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);

        content.add(footer);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createMetric(String label, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 15));
        v.setForeground(Theme.TEXT);

        p.add(l);
        p.add(Box.createVerticalStrut(2));
        p.add(v);
        return p;
    }
}
