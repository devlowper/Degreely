package com.degreely.app.view;

import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.function.Consumer;

public class HomePage extends JPanel {

    private final Consumer<String> onNavigate;

    public HomePage() {
        this(null);
    }

    public HomePage(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(
                Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32));

        JPanel welcomeCard = UI.card();
        welcomeCard.setLayout(new BoxLayout(welcomeCard, BoxLayout.Y_AXIS));
        welcomeCard.add(UI.titleLabel("Find Your Dream Degree & Scholarships"));
        welcomeCard.add(Box.createVerticalStrut(Theme.SPACING_8));
        welcomeCard.add(UI.secondaryLabel("One-stop degree search platform for Bachelor, Master, and PhD programs worldwide."));
        welcomeCard.add(Box.createVerticalStrut(Theme.SPACING_16));

        JPanel btnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);

        JButton exploreBtn = UI.primaryButton("Explore Universities");
        exploreBtn.addActionListener(e -> {
            if (onNavigate != null) onNavigate.accept("Universities");
        });

        JButton browseScholarshipsBtn = UI.outlineButton("Browse Scholarships");
        browseScholarshipsBtn.addActionListener(e -> {
            if (onNavigate != null) onNavigate.accept("Scholarships");
        });

        btnRow.add(exploreBtn);
        btnRow.add(Box.createHorizontalStrut(Theme.SPACING_12));
        btnRow.add(browseScholarshipsBtn);
        welcomeCard.add(btnRow);

        content.add(welcomeCard);
        add(content, BorderLayout.NORTH);
    }
}
