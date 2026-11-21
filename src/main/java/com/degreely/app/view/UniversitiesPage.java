package com.degreely.app.view;

import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.University;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.degreely.app.view.components.UniversityCard;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class UniversitiesPage extends JPanel {

    private final UniversityService service = UniversityService.getInstance();
    private final Consumer<University> onOpenDetails;

    // Filters
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> countryCombo = new JComboBox<>();
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{"All Levels", "Bachelor", "Master", "PhD"});
    private final JSlider tuitionSlider = new JSlider(0, 70000, 70000);
    private final JLabel tuitionValueLabel = new JLabel("$70,000 / yr");
    private final JComboBox<String> ieltsCombo = new JComboBox<>(new String[]{
            "Any IELTS", "6.5 or lower", "7.0 or lower", "7.5 or lower"
    });

    // Content container
    private final JPanel cardsContainer = new JPanel();
    private final JLabel resultsCountLabel = new JLabel();
    private SwingWorker<List<University>, Void> filterWorker;

    public UniversitiesPage() {
        this(null);
    }

    public UniversitiesPage(Consumer<University> onOpenDetails) {
        this.onOpenDetails = onOpenDetails;
        initUI();
        applyFilters();
    }

    private void initUI() {
        setLayout(new BorderLayout(Theme.SPACING_24, 0));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32));

        // 1. Left Filter Sidebar
        JPanel sidebar = createFilterSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. Right Content Area (Search bar + Scrollable cards)
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createFilterSidebar() {
        JPanel card = UI.card();
        card.setPreferredSize(new Dimension(290, 600));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Sidebar Title
        JLabel title = UI.sectionLabel("Filters");
        title.setFont(new Font(Theme.FONT_SECTION.getName(), Font.BOLD, 18));
        card.add(title);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Country Filter
        JLabel countryLabel = new JLabel("Country");
        countryLabel.setFont(Theme.FONT_SMALL);
        countryLabel.setForeground(Theme.TEXT_SECONDARY);
        card.add(countryLabel);
        card.add(Box.createVerticalStrut(Theme.SPACING_4));

        for (String c : service.getUniqueCountries()) {
            countryCombo.addItem(c);
        }
        countryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        countryCombo.addActionListener(e -> applyFilters());
        card.add(countryCombo);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Degree Level Filter
        JLabel levelLabel = new JLabel("Degree Level");
        levelLabel.setFont(Theme.FONT_SMALL);
        levelLabel.setForeground(Theme.TEXT_SECONDARY);
        card.add(levelLabel);
        card.add(Box.createVerticalStrut(Theme.SPACING_4));

        levelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        levelCombo.addActionListener(e -> applyFilters());
        card.add(levelCombo);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Max Tuition Slider
        JPanel tuitionLabelRow = new JPanel(new BorderLayout());
        tuitionLabelRow.setOpaque(false);
        JLabel tuitionLabel = new JLabel("Max Tuition / Year");
        tuitionLabel.setFont(Theme.FONT_SMALL);
        tuitionLabel.setForeground(Theme.TEXT_SECONDARY);
        tuitionValueLabel.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 12));
        tuitionValueLabel.setForeground(Theme.PRIMARY);
        tuitionLabelRow.add(tuitionLabel, BorderLayout.WEST);
        tuitionLabelRow.add(tuitionValueLabel, BorderLayout.EAST);

        card.add(tuitionLabelRow);
        card.add(Box.createVerticalStrut(Theme.SPACING_4));

        tuitionSlider.setOpaque(false);
        tuitionSlider.setMajorTickSpacing(20000);
        tuitionSlider.setMinorTickSpacing(5000);
        tuitionSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tuitionSlider.addChangeListener(e -> {
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            nf.setMaximumFractionDigits(0);
            tuitionValueLabel.setText(nf.format(tuitionSlider.getValue()) + " / yr");
            if (!tuitionSlider.getValueIsAdjusting()) {
                applyFilters();
            }
        });
        card.add(tuitionSlider);
        card.add(Box.createVerticalStrut(Theme.SPACING_16));

        // Min IELTS Score Filter
        JLabel ieltsLabel = new JLabel("Min IELTS Requirement");
        ieltsLabel.setFont(Theme.FONT_SMALL);
        ieltsLabel.setForeground(Theme.TEXT_SECONDARY);
        card.add(ieltsLabel);
        card.add(Box.createVerticalStrut(Theme.SPACING_4));

        ieltsCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ieltsCombo.addActionListener(e -> applyFilters());
        card.add(ieltsCombo);
        card.add(Box.createVerticalStrut(Theme.SPACING_24));

        // Action Buttons: Apply & Reset
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.SPACING_8, 0));
        btnRow.setOpaque(false);

        JButton applyBtn = UI.primaryButton("Apply");
        applyBtn.setFont(Theme.FONT_SMALL);
        applyBtn.addActionListener(e -> applyFilters());

        JButton resetBtn = UI.outlineButton("Reset");
        resetBtn.setFont(Theme.FONT_SMALL);
        resetBtn.addActionListener(e -> resetFilters());

        btnRow.add(applyBtn);
        btnRow.add(resetBtn);
        card.add(btnRow);

        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACING_16));
        panel.setOpaque(false);

        // Top bar with search input and results counter
        JPanel topBar = new JPanel(new BorderLayout(Theme.SPACING_16, 0));
        topBar.setOpaque(false);

        // Search text field
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search universities, degree programs, cities...");
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 8,12,8,12;");
        searchField.setFont(Theme.FONT_BODY);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        resultsCountLabel.setFont(Theme.FONT_BODY);
        resultsCountLabel.setForeground(Theme.TEXT_SECONDARY);

        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(resultsCountLabel, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Cards vertical container
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Executes filtering asynchronously via SwingWorker to keep UI smooth and responsive.
     */
    private void applyFilters() {
        if (filterWorker != null && !filterWorker.isDone()) {
            filterWorker.cancel(true);
        }

        final String keyword = searchField.getText();
        final String country = (String) countryCombo.getSelectedItem();
        final String level = (String) levelCombo.getSelectedItem();
        final double maxTuition = tuitionSlider.getValue();

        double minIeltsValue = -1;
        String ieltsSelection = (String) ieltsCombo.getSelectedItem();
        if (ieltsSelection != null) {
            if (ieltsSelection.startsWith("6.5")) minIeltsValue = 6.5;
            else if (ieltsSelection.startsWith("7.0")) minIeltsValue = 7.0;
            else if (ieltsSelection.startsWith("7.5")) minIeltsValue = 7.5;
        }
        final double minIelts = minIeltsValue;

        filterWorker = new SwingWorker<>() {
            @Override
            protected List<University> doInBackground() {
                return service.filter(keyword, country, level, maxTuition, minIelts);
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    List<University> filtered = get();
                    displayResults(filtered);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        filterWorker.execute();
    }

    private void displayResults(List<University> filtered) {
        cardsContainer.removeAll();

        if (filtered.isEmpty()) {
            cardsContainer.add(createEmptyState());
        } else {
            for (University u : filtered) {
                UniversityCard card = new UniversityCard(u, onOpenDetails);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
                cardsContainer.add(card);
                cardsContainer.add(Box.createVerticalStrut(Theme.SPACING_12));
            }
        }

        resultsCountLabel.setText(filtered.size() + " universities found");
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

        JLabel icon = new JLabel("🔍");
        icon.setFont(new Font(Theme.FONT_TITLE.getName(), Font.PLAIN, 32));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = UI.sectionLabel("No Universities Found");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = UI.secondaryLabel("No universities match your active filters or search query. Try broadening your criteria.");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton reset = UI.primaryButton("Reset Filters");
        reset.setAlignmentX(Component.CENTER_ALIGNMENT);
        reset.addActionListener(e -> resetFilters());

        empty.add(icon);
        empty.add(Box.createVerticalStrut(Theme.SPACING_8));
        empty.add(title);
        empty.add(Box.createVerticalStrut(Theme.SPACING_4));
        empty.add(sub);
        empty.add(Box.createVerticalStrut(Theme.SPACING_16));
        empty.add(reset);

        return empty;
    }

    private void resetFilters() {
        searchField.setText("");
        countryCombo.setSelectedIndex(0);
        levelCombo.setSelectedIndex(0);
        tuitionSlider.setValue(70000);
        ieltsCombo.setSelectedIndex(0);
        applyFilters();
    }
}
