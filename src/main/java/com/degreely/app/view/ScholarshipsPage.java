package com.degreely.app.view;

import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.Scholarship;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.degreely.app.view.components.ScholarshipCard;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class ScholarshipsPage extends JPanel {

    private final UniversityService service = UniversityService.getInstance();

    // Filters
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> countryCombo = new JComboBox<>();
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{"All Levels", "Bachelor", "Master", "PhD"});
    private final JLabel resultsCountLabel = new JLabel();

    // Cards list container
    private final JPanel cardsContainer = new JPanel();
    private SwingWorker<List<Scholarship>, Void> filterWorker;

    public ScholarshipsPage() {
        initUI();
        applyFilters();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, Theme.SPACING_16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32));

        // 1. Top Section: Header Title & Filter Bar
        JPanel topSection = createTopSection();
        add(topSection, BorderLayout.NORTH);

        // 2. Main Scrollable Scholarships List
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopSection() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        // Header Title Row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel pageTitle = UI.sectionLabel("Scholarships & Financial Aid");
        resultsCountLabel.setFont(Theme.FONT_BODY);
        resultsCountLabel.setForeground(Theme.TEXT_SECONDARY);

        titleRow.add(pageTitle, BorderLayout.WEST);
        titleRow.add(resultsCountLabel, BorderLayout.EAST);
        container.add(titleRow);
        container.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Filter Bar Card
        JPanel filterCard = UI.card();
        filterCard.setLayout(new BorderLayout(Theme.SPACING_16, 0));
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                filterCard.getBorder(),
                BorderFactory.createEmptyBorder(Theme.SPACING_8, Theme.SPACING_12, Theme.SPACING_8, Theme.SPACING_12)
        ));

        // Search Input Field
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search scholarships by name, university, or funding amount...");
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 6,10,6,10;");
        searchField.setFont(Theme.FONT_BODY);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        // Dropdown Filters Row
        JPanel dropdownsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_12, 0));
        dropdownsRow.setOpaque(false);

        // Country Dropdown
        countryCombo.setPreferredSize(new Dimension(170, 36));
        for (String c : service.getScholarshipCountries()) {
            countryCombo.addItem(c);
        }
        countryCombo.addActionListener(e -> applyFilters());

        // Level Dropdown
        levelCombo.setPreferredSize(new Dimension(140, 36));
        levelCombo.addActionListener(e -> applyFilters());

        dropdownsRow.add(new JLabel("Country:"));
        dropdownsRow.add(countryCombo);
        dropdownsRow.add(new JLabel("Degree:"));
        dropdownsRow.add(levelCombo);

        filterCard.add(searchField, BorderLayout.CENTER);
        filterCard.add(dropdownsRow, BorderLayout.EAST);

        container.add(filterCard);
        return container;
    }

    /**
     * Executes filtering asynchronously via SwingWorker to avoid freezing the UI thread.
     */
    private void applyFilters() {
        if (filterWorker != null && !filterWorker.isDone()) {
            filterWorker.cancel(true);
        }

        final String keyword = searchField.getText();
        final String country = (String) countryCombo.getSelectedItem();
        final String level = (String) levelCombo.getSelectedItem();

        filterWorker = new SwingWorker<>() {
            @Override
            protected List<Scholarship> doInBackground() {
                return service.filterScholarships(keyword, country, level);
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    List<Scholarship> filtered = get();
                    displayResults(filtered);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        filterWorker.execute();
    }

    private void displayResults(List<Scholarship> filtered) {
        cardsContainer.removeAll();

        if (filtered.isEmpty()) {
            cardsContainer.add(createEmptyState());
        } else {
            for (Scholarship s : filtered) {
                ScholarshipCard card = new ScholarshipCard(s);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
                cardsContainer.add(card);
                cardsContainer.add(Box.createVerticalStrut(Theme.SPACING_12));
            }
        }

        resultsCountLabel.setText(filtered.size() + " scholarships available");
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createEmptyState() {
        JPanel empty = UI.card();
        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
        empty.setBorder(BorderFactory.createCompoundBorder(
                empty.getBorder(),
                BorderFactory.createEmptyBorder(Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32)
        ));

        JLabel badge = new JLabel("SCHOLARSHIPS");
        badge.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        badge.setForeground(Theme.PRIMARY);
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFF7ED;");
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = UI.sectionLabel("No Scholarships Found");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = UI.secondaryLabel("No scholarships match the selected country or level. Try clearing filters.");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton reset = UI.primaryButton("Reset Filters");
        reset.setAlignmentX(Component.CENTER_ALIGNMENT);
        reset.addActionListener(e -> {
            searchField.setText("");
            countryCombo.setSelectedIndex(0);
            levelCombo.setSelectedIndex(0);
            applyFilters();
        });

        empty.add(badge);
        empty.add(Box.createVerticalStrut(Theme.SPACING_12));
        empty.add(title);
        empty.add(Box.createVerticalStrut(Theme.SPACING_4));
        empty.add(sub);
        empty.add(Box.createVerticalStrut(Theme.SPACING_16));
        empty.add(reset);

        return empty;
    }
}
