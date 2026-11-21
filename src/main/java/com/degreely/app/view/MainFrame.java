package com.degreely.app.view;

import com.degreely.app.model.University;
import com.degreely.app.util.Theme;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);
    private Navbar navbar;
    private UniversityDetailsPage detailsPage;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Degreely");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        // Top Navigation Bar
        navbar = new Navbar(this::showPage);
        add(navbar, BorderLayout.NORTH);

        // Details Page
        detailsPage = new UniversityDetailsPage(this::navigateTo, this::openUniversityDetails);

        // Content Area with CardLayout
        contentArea.setBackground(Theme.BACKGROUND);
        contentArea.add(new HomePage(this::navigateTo), "Home");
        contentArea.add(new UniversitiesPage(this::openUniversityDetails), "Universities");
        contentArea.add(detailsPage, "UniversityDetails");
        contentArea.add(new ScholarshipsPage(), "Scholarships");
        contentArea.add(new ComparePage(this::navigateTo), "Compare");
        contentArea.add(new PredictorPage(), "Predictor");
        contentArea.add(new DeadlinesPage(), "Deadlines");

        add(contentArea, BorderLayout.CENTER);

        // Show default page
        showPage("Home");
    }

    public void openUniversityDetails(University university) {
        if (detailsPage != null && university != null) {
            detailsPage.setUniversity(university);
            cardLayout.show(contentArea, "UniversityDetails");
        }
    }

    public void navigateTo(String pageName) {
        if (navbar != null) {
            navbar.setActive(pageName);
        } else {
            showPage(pageName);
        }
    }

    public void showPage(String pageName) {
        cardLayout.show(contentArea, pageName);
    }
}
