package com.degreely.app.view;

import com.degreely.app.controller.DeadlineService;
import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.ApplicationDeadline;
import com.degreely.app.model.University;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.degreely.app.view.components.DeadlineCard;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeadlinesPage extends JPanel {

    private final DeadlineService deadlineService = DeadlineService.getInstance();
    private final UniversityService universityService = UniversityService.getInstance();

    // Form inputs
    private final JComboBox<String> universityCombo = new JComboBox<>();
    private final JTextField programField = new JTextField();
    private final JTextField deadlineDateField = new JTextField(LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "Applied", "Done"});

    // List container
    private final JPanel cardsContainer = new JPanel();
    private final JLabel countLabel = new JLabel();

    public DeadlinesPage() {
        initUI();
        deadlineService.addChangeListener(this::renderList);
        renderList();
    }

    private void initUI() {
        setLayout(new BorderLayout(Theme.SPACING_24, 0));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_24, Theme.SPACING_32, Theme.SPACING_24, Theme.SPACING_32));

        // 1. Left Form Sidebar (Add Application Deadline)
        JPanel formCard = createFormSidebar();
        add(formCard, BorderLayout.WEST);

        // 2. Right Main List (Scrollable cards sorted by deadline ascending)
        JPanel mainListPanel = createMainList();
        add(mainListPanel, BorderLayout.CENTER);
    }

    private JPanel createFormSidebar() {
        JPanel card = UI.card();
        card.setPreferredSize(new Dimension(320, 600));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = UI.sectionLabel("Track Application");
        title.setFont(new Font(Theme.FONT_SECTION.getName(), Font.BOLD, 17));
        card.add(title);
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        card.add(UI.secondaryLabel("Add your target degree applications to keep track of upcoming deadlines."));
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // 1. Target University
        card.add(createFieldLabel("University Name"));
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        populateUniversityCombo();
        universityCombo.setEditable(true);
        universityCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(universityCombo);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // 2. Program Name
        card.add(createFieldLabel("Program / Degree Name"));
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        programField.setFont(Theme.FONT_BODY);
        programField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. M.S. in Data Science");
        programField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 6,10,6,10;");
        programField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(programField);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // 3. Deadline Date
        card.add(createFieldLabel("Deadline Date (YYYY-MM-DD)"));
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        deadlineDateField.setFont(Theme.FONT_BODY);
        deadlineDateField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-MM-DD");
        deadlineDateField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 6,10,6,10;");
        deadlineDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(deadlineDateField);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // 4. Initial Status
        card.add(createFieldLabel("Application Status"));
        card.add(Box.createVerticalStrut(Theme.SPACING_4));
        statusCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(statusCombo);
        card.add(Box.createVerticalStrut(Theme.SPACING_24));

        // Submit Button
        JButton addBtn = UI.primaryButton("+ Add Application");
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> addApplication());
        card.add(addBtn);

        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel createMainList() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACING_16));
        panel.setOpaque(false);

        // Header row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel title = UI.sectionLabel("Upcoming Deadlines");
        countLabel.setFont(Theme.FONT_BODY);
        countLabel.setForeground(Theme.TEXT_SECONDARY);

        topRow.add(title, BorderLayout.WEST);
        topRow.add(countLabel, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        // Scrollable cards list
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void renderList() {
        cardsContainer.removeAll();
        List<ApplicationDeadline> list = deadlineService.getAllDeadlines();

        if (list.isEmpty()) {
            JPanel empty = UI.card();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setBorder(BorderFactory.createCompoundBorder(
                    empty.getBorder(),
                    BorderFactory.createEmptyBorder(Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32)
            ));

            JLabel badge = new JLabel("APPLICATIONS");
            badge.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
            badge.setForeground(Theme.PRIMARY);
            badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFF7ED;");
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            badge.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel title = UI.sectionLabel("No Deadlines Tracked");
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel desc = UI.secondaryLabel("Use the form on the left to add your target university application deadlines.");
            desc.setAlignmentX(Component.CENTER_ALIGNMENT);

            empty.add(badge);
            empty.add(Box.createVerticalStrut(Theme.SPACING_12));
            empty.add(title);
            empty.add(Box.createVerticalStrut(Theme.SPACING_4));
            empty.add(desc);
            cardsContainer.add(empty);
        } else {
            for (ApplicationDeadline d : list) {
                DeadlineCard card = new DeadlineCard(d);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
                cardsContainer.add(card);
                cardsContainer.add(Box.createVerticalStrut(Theme.SPACING_12));
            }
        }

        countLabel.setText(list.size() + " applications tracked");
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private void addApplication() {
        String uni = (String) universityCombo.getSelectedItem();
        if (uni == null || uni.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter or select a University name.", "Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String prog = programField.getText().trim();
        if (prog.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Program name (e.g. M.S. in Computer Science).", "Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dateStr = deadlineDateField.getText().trim();
        try {
            LocalDate.parse(dateStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date in YYYY-MM-DD format (e.g. 2026-12-15).", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) statusCombo.getSelectedItem();

        ApplicationDeadline d = new ApplicationDeadline(uni.trim(), prog, dateStr, status);
        deadlineService.addDeadline(d);

        // Reset program field
        programField.setText("");
    }

    private void populateUniversityCombo() {
        universityCombo.removeAllItems();
        List<University> all = universityService.getAll();
        for (University u : all) {
            universityCombo.addItem(u.getName());
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);
        return l;
    }
}
