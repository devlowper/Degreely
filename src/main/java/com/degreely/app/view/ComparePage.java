package com.degreely.app.view;

import com.degreely.app.controller.CompareManager;
import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.University;
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
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ComparePage extends JPanel {

    private final CompareManager compareManager = CompareManager.getInstance();
    private final Consumer<String> onNavigate;
    private final JPanel tableContainer = new JPanel();
    private final JComboBox<UniversityItem> addUniCombo = new JComboBox<>();

    public ComparePage() {
        this(null);
    }

    public ComparePage(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        initUI();
        compareManager.addChangeListener(this::renderComparisonTable);
        renderComparisonTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, Theme.SPACING_16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACING_24, Theme.SPACING_32, Theme.SPACING_24, Theme.SPACING_32));

        // 1. Top Bar: Header, slot count & Quick Add dropdown
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // 2. Center: Table-style Side by side comparison container
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout(Theme.SPACING_16, 0));
        bar.setOpaque(false);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(UI.sectionLabel("Compare Universities"));
        left.add(Box.createVerticalStrut(Theme.SPACING_4));
        left.add(UI.secondaryLabel("Side-by-side comparison (Maximum 3 universities). Best values are highlighted in green."));
        bar.add(left, BorderLayout.WEST);

        // Right side: Quick Add dropdown + Clear all
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 0));
        right.setOpaque(false);

        addUniCombo.setPreferredSize(new Dimension(240, 36));
        populateCombo();

        JButton addBtn = UI.primaryButton("+ Add");
        addBtn.setFont(Theme.FONT_SMALL);
        addBtn.setMargin(new Insets(8, 14, 8, 14));
        addBtn.addActionListener(e -> {
            UniversityItem item = (UniversityItem) addUniCombo.getSelectedItem();
            if (item != null) {
                if (compareManager.getCount() >= CompareManager.MAX_COMPARE) {
                    JOptionPane.showMessageDialog(this,
                            "Maximum 3 universities can be compared at a time. Please remove one to add another.",
                            "Compare Limit",
                            JOptionPane.WARNING_MESSAGE);
                } else if (compareManager.isCompared(item.uni)) {
                    JOptionPane.showMessageDialog(this,
                            item.uni.getName() + " is already in the comparison list.",
                            "Already Added",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    compareManager.addUniversity(item.uni);
                }
            }
        });

        JButton clearBtn = UI.outlineButton("Clear All");
        clearBtn.setFont(Theme.FONT_SMALL);
        clearBtn.setMargin(new Insets(8, 14, 8, 14));
        clearBtn.addActionListener(e -> compareManager.clear());

        right.add(addUniCombo);
        right.add(addBtn);
        right.add(clearBtn);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void populateCombo() {
        addUniCombo.removeAllItems();
        List<University> all = UniversityService.getInstance().getAll();
        for (University u : all) {
            addUniCombo.addItem(new UniversityItem(u));
        }
    }

    private void renderComparisonTable() {
        tableContainer.removeAll();
        List<University> list = compareManager.getComparedList();

        if (list.isEmpty()) {
            tableContainer.add(createEmptyState(), BorderLayout.CENTER);
        } else {
            tableContainer.add(createTableCard(list), BorderLayout.NORTH);
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private JPanel createEmptyState() {
        JPanel emptyCard = UI.card();
        emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
        emptyCard.setBorder(BorderFactory.createCompoundBorder(
                emptyCard.getBorder(),
                BorderFactory.createEmptyBorder(Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32)
        ));

        JLabel badge = new JLabel("COMPARE");
        badge.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        badge.setForeground(Theme.PRIMARY);
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFF7ED;");
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = UI.sectionLabel("No Universities to Compare Yet");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = UI.secondaryLabel("Select up to 3 universities to compare tuition, world ranking, acceptance rates, and requirements.");
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton browseBtn = UI.primaryButton("Browse Universities");
        browseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        browseBtn.addActionListener(e -> {
            if (onNavigate != null) {
                onNavigate.accept("Universities");
            }
        });

        emptyCard.add(badge);
        emptyCard.add(Box.createVerticalStrut(Theme.SPACING_12));
        emptyCard.add(title);
        emptyCard.add(Box.createVerticalStrut(Theme.SPACING_8));
        emptyCard.add(desc);
        emptyCard.add(Box.createVerticalStrut(Theme.SPACING_24));
        emptyCard.add(browseBtn);
        emptyCard.add(Box.createVerticalStrut(Theme.SPACING_16));

        return emptyCard;
    }

    private JPanel createTableCard(List<University> unis) {
        int colCount = unis.size();
        JPanel card = UI.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Find Best Values for Highlighting (when comparing 2 or 3)
        int bestRank = unis.stream().mapToInt(University::getRanking).min().orElse(-1);
        double bestTuition = unis.stream().mapToDouble(University::getTuitionPerYear).min().orElse(-1);
        double bestAcceptance = unis.stream().mapToDouble(University::getAcceptanceRate).max().orElse(-1);
        double bestGpa = unis.stream().mapToDouble(University::getMinGpa).min().orElse(-1);
        double bestIelts = unis.stream().mapToDouble(University::getMinIelts).min().orElse(-1);

        // Header Row: University Name, Location & Remove Button
        JPanel headerRow = new JPanel(new GridLayout(1, colCount + 1, Theme.SPACING_12, 0));
        headerRow.setOpaque(false);
        headerRow.add(createMetricLabelCell("Metric / University"));

        for (int i = 0; i < colCount; i++) {
            University u = unis.get(i);
            final int index = i;

            JPanel colHeader = new JPanel(new BorderLayout(Theme.SPACING_8, Theme.SPACING_8));
            colHeader.setOpaque(false);
            colHeader.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMARY),
                    BorderFactory.createEmptyBorder(0, 0, Theme.SPACING_8, 0)
            ));

            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setOpaque(false);

            JLabel name = new JLabel(u.getName());
            name.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 15));
            name.setForeground(Theme.TEXT);

            JLabel loc = new JLabel(u.getCity() + ", " + u.getCountry());
            loc.setFont(Theme.FONT_SMALL);
            loc.setForeground(Theme.TEXT_MUTED);

            titlePanel.add(name);
            titlePanel.add(Box.createVerticalStrut(Theme.SPACING_4));
            titlePanel.add(loc);

            JButton removeBtn = UI.outlineButton("Remove");
            removeBtn.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 11));
            removeBtn.setMargin(new Insets(4, 10, 4, 10));
            removeBtn.addActionListener(e -> compareManager.removeAt(index));

            colHeader.add(titlePanel, BorderLayout.CENTER);
            colHeader.add(removeBtn, BorderLayout.EAST);

            headerRow.add(colHeader);
        }

        card.add(headerRow);
        card.add(Box.createVerticalStrut(Theme.SPACING_8));

        NumberFormat currFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currFormat.setMaximumFractionDigits(0);

        // 1. World Ranking
        card.add(createRow(unis, "QS World Rank",
                u -> "#" + u.getRanking(),
                u -> colCount > 1 && u.getRanking() == bestRank));

        // 2. Tuition / Year
        card.add(createRow(unis, "Tuition / Year",
                u -> currFormat.format(u.getTuitionPerYear()),
                u -> colCount > 1 && u.getTuitionPerYear() == bestTuition));

        // 3. Acceptance Rate
        card.add(createRow(unis, "Acceptance Rate",
                u -> u.getAcceptanceRate() + "%",
                u -> colCount > 1 && u.getAcceptanceRate() == bestAcceptance));

        // 4. Minimum GPA
        card.add(createRow(unis, "Min GPA Requirement",
                u -> u.getMinGpa() + " / 4.0",
                u -> colCount > 1 && u.getMinGpa() == bestGpa));

        // 5. Minimum IELTS
        card.add(createRow(unis, "Min IELTS Score",
                u -> String.valueOf(u.getMinIelts()),
                u -> colCount > 1 && u.getMinIelts() == bestIelts));

        // 6. Country
        card.add(createRow(unis, "Country",
                University::getCountry,
                u -> false));

        // 7. Degree Programs Available
        card.add(createRow(unis, "Programs Available",
                u -> u.getPrograms().size() + " Degree Programs",
                u -> false));

        return card;
    }

    private JPanel createRow(List<University> unis, String label,
                             java.util.function.Function<University, String> valueExtractor,
                             java.util.function.Predicate<University> isBest) {
        int colCount = unis.size();
        JPanel row = new JPanel(new GridLayout(1, colCount + 1, Theme.SPACING_12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(Theme.SPACING_8, 0, Theme.SPACING_8, 0)
        ));

        // Label cell
        row.add(createMetricLabelCell(label));

        // University value cells
        for (University u : unis) {
            boolean best = isBest.test(u);
            String val = valueExtractor.apply(u);
            row.add(createValueCell(val, best));
        }

        return row;
    }

    private JPanel createMetricLabelCell(String text) {
        JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 6));
        cell.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
        l.setForeground(Theme.TEXT_SECONDARY);
        cell.add(l);
        return cell;
    }

    private JPanel createValueCell(String text, boolean isBest) {
        JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 6));

        if (isBest) {
            cell.setBackground(new Color(220, 252, 231)); // Soft pastel green (#DCFCE7)
            cell.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #DCFCE7;");
            cell.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), new Color(134, 239, 172), 1, 8));

            JLabel l = new JLabel(text + "  ★ Best");
            l.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
            l.setForeground(new Color(22, 101, 52)); // Dark green (#166534)
            cell.add(l);
        } else {
            cell.setOpaque(false);
            JLabel l = new JLabel(text);
            l.setFont(Theme.FONT_BODY);
            l.setForeground(Theme.TEXT);
            cell.add(l);
        }

        return cell;
    }

    private static class UniversityItem {
        final University uni;

        UniversityItem(University uni) {
            this.uni = uni;
        }

        @Override
        public String toString() {
            return "#" + uni.getRanking() + " " + uni.getName();
        }
    }
}
