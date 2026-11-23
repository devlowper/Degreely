package com.degreely.app.view;

import com.degreely.app.model.ChatSession;
import com.degreely.app.model.University;
import com.degreely.app.util.Theme;
import com.degreely.app.view.components.chat.ChatSidebar;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * Main application window designed as a modern, unified SaaS platform:
 * - Persistent left sidebar with chat history and direct navigation.
 * - "Home / Universities" section located directly above the Settings & Profile section.
 * - Dynamic central canvas with smooth switching between University Catalog (Home) and AI Counselor Chat.
 * - Top navbar removed for a clean, distraction-free modern interface.
 */
public class MainFrame extends JFrame {

    public static final String VIEW_HOME = "HOME";
    public static final String VIEW_CHAT = "CHAT";

    private ChatSidebar sidebar;
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

        // 1. Unified SaaS Left Sidebar (Persistent across Home and AI Counselor)
        sidebar = new ChatSidebar(
                this::handleSelectSession,
                this::handleNewChat,
                this::handleNavigateHome
        );
        add(sidebar, BorderLayout.WEST);

        // 2. Central Canvas with CardLayout (Home vs Degreely AI Chat)
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        chatPage = new ChatPage();
        chatPage.setSidebar(sidebar);

        homePage = new HomePage(
                this::handleAskAIFromHome,
                this::handleNavigateToAI,
                this::handleCompareFromHome
        );

        cardContainer.add(homePage, VIEW_HOME);
        cardContainer.add(chatPage, VIEW_CHAT);

        add(cardContainer, BorderLayout.CENTER);

        // Start on Home view
        sidebar.setActiveView("HOME");
        cardLayout.show(cardContainer, VIEW_HOME);
    }

    private void handleNavigateHome() {
        sidebar.setActiveView("HOME");
        cardLayout.show(cardContainer, VIEW_HOME);
    }

    private void handleNavigateToAI() {
        sidebar.setActiveView("CHAT");
        cardLayout.show(cardContainer, VIEW_CHAT);
    }

    private void handleNewChat() {
        sidebar.setActiveView("CHAT");
        cardLayout.show(cardContainer, VIEW_CHAT);
        chatPage.startNewChat();
    }

    private void handleSelectSession(ChatSession session) {
        sidebar.setActiveView("CHAT");
        cardLayout.show(cardContainer, VIEW_CHAT);
        chatPage.loadSession(session);
    }

    private void handleAskAIFromHome(String prompt) {
        sidebar.setActiveView("CHAT");
        cardLayout.show(cardContainer, VIEW_CHAT);
        chatPage.askQuestion(prompt);
    }

    private void handleCompareFromHome(List<University> selectedUnis, String program, String degree) {
        sidebar.setActiveView("CHAT");
        cardLayout.show(cardContainer, VIEW_CHAT);
        chatPage.compareUniversities(selectedUnis, program, degree);
    }
}
