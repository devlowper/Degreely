package com.degreely.app.view;

import com.degreely.app.util.Theme;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {

    private Navbar navbar;
    private JPanel cardContainer;
    private CardLayout cardLayout;

    private HomePage homePage;
    private ChatPage chatPage;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Degreely — Study Abroad AI Counselor");
        setSize(1280, 820);
        setMinimumSize(new Dimension(1020, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.CARD_BG);
        setLayout(new BorderLayout());

        // 1. Top Navbar
        navbar = new Navbar(this::navigateToTab);
        add(navbar, BorderLayout.NORTH);

        // 2. Card Container for Home and Degreely AI
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        // Degreely AI Chat Page
        chatPage = new ChatPage();

        // Home Page with Hero section and University Catalog
        homePage = new HomePage(
                this::handleAskAIFromHome,
                () -> navigateToTab(Navbar.TAB_AI)
        );

        cardContainer.add(homePage, Navbar.TAB_HOME);
        cardContainer.add(chatPage, Navbar.TAB_AI);

        add(cardContainer, BorderLayout.CENTER);

        // Start on HOME tab
        cardLayout.show(cardContainer, Navbar.TAB_HOME);
    }

    public void navigateToTab(String tabKey) {
        if (Navbar.TAB_HOME.equals(tabKey)) {
            cardLayout.show(cardContainer, Navbar.TAB_HOME);
            navbar.selectTabWithoutNotification(Navbar.TAB_HOME);
        } else if (Navbar.TAB_AI.equals(tabKey)) {
            cardLayout.show(cardContainer, Navbar.TAB_AI);
            navbar.selectTabWithoutNotification(Navbar.TAB_AI);
        }
    }

    private void handleAskAIFromHome(String prompt) {
        // Switch view to AI Chat tab
        navigateToTab(Navbar.TAB_AI);
        // Dispatch prompt to counselor
        chatPage.askQuestion(prompt);
    }
}
