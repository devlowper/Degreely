package com.degreely.app.view;

import com.degreely.app.controller.ChatStorageService;
import com.degreely.app.controller.GeminiService;
import com.degreely.app.model.ChatMessage;
import com.degreely.app.model.ChatSession;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.degreely.app.view.components.chat.ChatSidebar;
import com.degreely.app.view.components.chat.ComposerPanel;
import com.degreely.app.view.components.chat.MessageBubble;
import com.degreely.app.view.components.chat.StarterPromptsPanel;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ChatPage extends JPanel {

    private final ChatStorageService storageService = ChatStorageService.getInstance();
    private final GeminiService geminiService = GeminiService.getInstance();

    private ChatSidebar sidebar;
    private ChatSession currentSession;

    // Center Chat Components
    private final JLabel headerTitleLabel = new JLabel("New Consultation");
    private final JLabel modelBadgeLabel = new JLabel("gemini-2.5-flash");
    private final JPanel messagesColumn = new JPanel();
    private final JScrollPane messagesScrollPane = new JScrollPane();
    private ComposerPanel composerPanel;
    private SwingWorker<ChatMessage, Void> chatWorker;

    public ChatPage() {
        initUI();
        startNewChat();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Left Sidebar (Claude style, 264px wide)
        sidebar = new ChatSidebar(this::loadSession, this::startNewChat);
        add(sidebar, BorderLayout.WEST);

        // 2. Right Main Chat Area
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(Color.WHITE);

        // Top Minimal Header Bar
        JPanel topBar = createTopBar();
        mainArea.add(topBar, BorderLayout.NORTH);

        // Messages Center Column (max-width around 780px centered)
        messagesColumn.setLayout(new BoxLayout(messagesColumn, BoxLayout.Y_AXIS));
        messagesColumn.setOpaque(false);
        messagesColumn.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_32, Theme.SPACING_16, Theme.SPACING_32));

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setOpaque(false);
        messagesColumn.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        messagesColumn.setPreferredSize(new Dimension(800, 400));
        centerWrapper.add(messagesColumn);

        messagesScrollPane.setViewportView(centerWrapper);
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messagesScrollPane.getViewport().setBackground(Color.WHITE);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainArea.add(messagesScrollPane, BorderLayout.CENTER);

        // Bottom Composer
        composerPanel = new ComposerPanel(this::handleSendMessage, this::handleStopGeneration);
        mainArea.add(composerPanel, BorderLayout.SOUTH);

        add(mainArea, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(800, 50));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E6E6E6")),
                BorderFactory.createEmptyBorder(0, Theme.SPACING_24, 0, Theme.SPACING_24)
        ));

        // Left: Title + Model Badge
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 14));
        left.setOpaque(false);

        headerTitleLabel.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 14));
        headerTitleLabel.setForeground(Theme.TEXT);

        JPanel badge = UI.badge(geminiService.getSelectedModel(), Theme.PRIMARY_LIGHT, Theme.PRIMARY);
        badge.setToolTipText("AI Counselor model powering Night Owl");

        left.add(headerTitleLabel);
        left.add(badge);

        // Right Actions: Export chat as Markdown
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 12));
        right.setOpaque(false);

        JButton exportBtn = UI.outlineButton("Export Chat");
        exportBtn.setFont(Theme.FONT_SMALL);
        exportBtn.addActionListener(e -> exportCurrentChat());

        right.add(exportBtn);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    public void startNewChat() {
        this.currentSession = new ChatSession("New Consultation");
        headerTitleLabel.setText("New Consultation");
        sidebar.setSelectedSessionId(currentSession.getId());
        renderMessages();
    }

    public void loadSession(ChatSession session) {
        if (session == null) return;
        this.currentSession = session;
        headerTitleLabel.setText(session.getTitle());
        renderMessages();
    }

    private void renderMessages() {
        messagesColumn.removeAll();

        if (currentSession == null || currentSession.getMessages().isEmpty()) {
            // Render Starter Prompts empty state
            StarterPromptsPanel starter = new StarterPromptsPanel(prompt -> {
                composerPanel.setPromptText(prompt);
            });
            messagesColumn.add(starter);
        } else {
            for (ChatMessage msg : currentSession.getMessages()) {
                MessageBubble bubble = new MessageBubble(msg, this::handleRegenerate);
                messagesColumn.add(bubble);
                messagesColumn.add(Box.createVerticalStrut(Theme.SPACING_12));
            }
        }

        messagesColumn.revalidate();
        messagesColumn.repaint();
        scrollToBottom();
    }

    private void handleSendMessage(String prompt, List<File> files) {
        if (currentSession == null) {
            currentSession = new ChatSession("New Consultation");
        }

        // Add user message
        ChatMessage userMsg = new ChatMessage("user", prompt);
        if (files != null && !files.isEmpty()) {
            List<String> names = new ArrayList<>();
            for (File f : files) names.add(f.getName());
            userMsg.setFileNames(names);
        }
        currentSession.addMessage(userMsg);

        // Auto-generate title if this is the first user message
        boolean isFirst = (currentSession.getMessages().size() == 1);

        renderMessages();
        composerPanel.setGenerating(true);

        // Show subtle "Thinking..." placeholder
        JPanel thinkingPanel = createThinkingIndicator();
        messagesColumn.add(thinkingPanel);
        messagesColumn.revalidate();
        messagesColumn.repaint();
        scrollToBottom();

        // Run Gemini API call in SwingWorker
        chatWorker = new SwingWorker<>() {
            @Override
            protected ChatMessage doInBackground() throws Exception {
                if (isFirst) {
                    String title = geminiService.generateChatTitle(prompt);
                    currentSession.setTitle(title);
                    SwingUtilities.invokeLater(() -> {
                        headerTitleLabel.setText(title);
                        sidebar.renderSessionList();
                    });
                }
                return geminiService.sendMessage(currentSession, prompt, files);
            }

            @Override
            protected void done() {
                composerPanel.setGenerating(false);
                if (isCancelled()) return;
                try {
                    ChatMessage assistantReply = get();
                    currentSession.addMessage(assistantReply);
                    storageService.saveSession(currentSession);
                    sidebar.renderSessionList();
                    renderMessages();
                } catch (Exception ex) {
                    messagesColumn.remove(thinkingPanel);
                    ChatMessage errorMsg = new ChatMessage("assistant", "⚠️ **Error:** " + ex.getMessage());
                    currentSession.addMessage(errorMsg);
                    renderMessages();
                }
            }
        };
        chatWorker.execute();
    }

    private void handleStopGeneration() {
        if (chatWorker != null && !chatWorker.isDone()) {
            chatWorker.cancel(true);
            composerPanel.setGenerating(false);
            renderMessages();
        }
    }

    private void handleRegenerate(ChatMessage msg) {
        // Find previous user prompt and re-send
        int idx = currentSession.getMessages().indexOf(msg);
        if (idx > 0) {
            ChatMessage prevUserMsg = currentSession.getMessages().get(idx - 1);
            if ("user".equals(prevUserMsg.getRole())) {
                currentSession.getMessages().remove(msg);
                handleSendMessage(prevUserMsg.getContent(), null);
            }
        }
    }

    private JPanel createThinkingIndicator() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 6));
        p.setOpaque(false);
        JLabel spark = new JLabel("✦");
        spark.setForeground(Theme.PRIMARY);
        JLabel text = UI.mutedLabel("Night Owl is thinking...");
        text.setFont(new Font(Theme.FONT_BODY.getName(), Font.ITALIC, 13));
        p.add(spark);
        p.add(text);
        return p;
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar v = messagesScrollPane.getVerticalScrollBar();
            v.setValue(v.getMaximum());
        });
    }

    private void exportCurrentChat() {
        if (currentSession == null || currentSession.getMessages().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No messages to export.", "Empty Chat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(currentSession.getTitle().replaceAll("[^a-zA-Z0-9_-]", "_") + ".md"));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (FileWriter w = new FileWriter(f)) {
                w.write("# " + currentSession.getTitle() + "\n\n");
                w.write("*Exported from Night Owl Study Abroad AI Counselor*\n\n---\n\n");
                for (ChatMessage m : currentSession.getMessages()) {
                    w.write("### " + ("user".equals(m.getRole()) ? "Student" : "Night Owl AI") + "\n\n");
                    w.write(m.getContent() + "\n\n");
                }
                JOptionPane.showMessageDialog(this, "Exported successfully to " + f.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
