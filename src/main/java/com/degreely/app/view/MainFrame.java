package com.degreely.app.view;

import com.degreely.app.util.Theme;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {

    private ChatPage chatPage;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Night Owl — Study Abroad AI Counselor");
        setSize(1240, 780);
        setMinimumSize(new Dimension(960, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.CARD_BG);
        setLayout(new BorderLayout());

        // Dedicated Night Owl AI Study Abroad Chatbot Workspace
        chatPage = new ChatPage();
        add(chatPage, BorderLayout.CENTER);
    }
}
