package com.degreely.app.view;

import com.degreely.app.controller.ChatStorageService;
import com.degreely.app.controller.GeminiService;
import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.ChatMessage;
import com.degreely.app.model.ChatSession;
import com.degreely.app.model.UniTableData;
import com.degreely.app.model.University;
import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.degreely.app.view.components.chat.ChatSidebar;
import com.degreely.app.view.components.chat.CompareDialog;
import com.degreely.app.view.components.chat.ComposerPanel;
import com.degreely.app.view.components.chat.MessageBubble;
import com.degreely.app.view.components.chat.StarterPromptsPanel;
import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
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

        // Messages Column: 100% dynamic height so complete tables and conversations are fully visible
        messagesColumn.setLayout(new BoxLayout(messagesColumn, BoxLayout.Y_AXIS));
        messagesColumn.setOpaque(false);
        messagesColumn.setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_16, Theme.SPACING_24, 60, Theme.SPACING_24));
        messagesColumn.setAlignmentX(Component.LEFT_ALIGNMENT);

        ScrollableChatWrapper scrollWrapper = new ScrollableChatWrapper(messagesColumn);
        messagesScrollPane.setViewportView(scrollWrapper);
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messagesScrollPane.getViewport().setBackground(Color.WHITE);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        messagesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainArea.add(messagesScrollPane, BorderLayout.CENTER);

        // Bottom Composer with Compare Action
        composerPanel = new ComposerPanel(this::handleSendMessage, this::handleStopGeneration, this::openCompareDialog);
        mainArea.add(composerPanel, BorderLayout.SOUTH);

        add(mainArea, BorderLayout.CENTER);
    }

    /**
     * Programmatically send an inquiry to the AI counselor (e.g. from Home page university cards).
     */
    public void askQuestion(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) return;
        handleSendMessage(prompt.trim(), null);
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(800, 50));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E6E6E6")),
                BorderFactory.createEmptyBorder(0, Theme.SPACING_24, 0, Theme.SPACING_24)
        ));

        // Left: Title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 14));
        left.setOpaque(false);

        headerTitleLabel.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 14));
        headerTitleLabel.setForeground(Theme.TEXT);

        left.add(headerTitleLabel);

        // Right Actions: Compare Universities & Export chat
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_8, 10));
        right.setOpaque(false);

        JButton compareBtn = UI.outlineButton("Compare Universities");
        compareBtn.setIcon(Icons.scale(14, Theme.PRIMARY));
        compareBtn.setIconTextGap(6);
        compareBtn.setFont(Theme.FONT_SMALL);
        compareBtn.setToolTipText("Compare 2 or 3 universities side-by-side in a table");
        compareBtn.addActionListener(e -> openCompareDialog());

        JButton exportBtn = UI.outlineButton("Export Chat");
        exportBtn.setFont(Theme.FONT_SMALL);
        exportBtn.addActionListener(e -> exportCurrentChat());

        right.add(compareBtn);
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
            // Render Starter Prompts empty state with Compare action
            StarterPromptsPanel starter = new StarterPromptsPanel(
                    prompt -> composerPanel.setPromptText(prompt),
                    this::openCompareDialog
            );
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
        JLabel text = UI.mutedLabel("Degreely is thinking...");
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
                w.write("*Exported from Degreely Study Abroad AI Counselor*\n\n---\n\n");
                for (ChatMessage m : currentSession.getMessages()) {
                    w.write("### " + ("user".equals(m.getRole()) ? "Student" : "Degreely AI") + "\n\n");
                    w.write(m.getContent() + "\n\n");
                }
                JOptionPane.showMessageDialog(this, "Exported successfully to " + f.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openCompareDialog() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CompareDialog dialog = new CompareDialog(owner, this::handleCompareUniversities);
        dialog.setVisible(true);
    }

    private void handleCompareUniversities(List<University> selectedUnis, String program, String degree) {
        if (selectedUnis == null || selectedUnis.size() < 2) return;

        StringBuilder names = new StringBuilder();
        for (int i = 0; i < selectedUnis.size(); i++) {
            if (i > 0 && i == selectedUnis.size() - 1) names.append(" and ");
            else if (i > 0) names.append(", ");
            names.append(selectedUnis.get(i).getName());
        }

        String userPrompt = "Please compare " + names + " side-by-side for " + degree + " in " + program + " in a detailed comparison table.";

        // Pre-build structured UniTableData from our comprehensive institutional dataset
        UniTableData tableData = UniversityService.getInstance().buildComparisonTable(selectedUnis, program, degree);
        String tableJson = new Gson().toJson(tableData);

        if (currentSession == null) {
            currentSession = new ChatSession("Compare: " + selectedUnis.get(0).getName() + " vs " + selectedUnis.get(1).getName());
        }

        // 1. Add user message
        ChatMessage userMsg = new ChatMessage("user", userPrompt);
        currentSession.addMessage(userMsg);

        // 2. Process AI counseling response
        if (geminiService.hasApiKey()) {
            renderMessages();
            composerPanel.setGenerating(true);

            JPanel thinkingPanel = createThinkingIndicator();
            messagesColumn.add(thinkingPanel);
            messagesColumn.revalidate();
            messagesColumn.repaint();
            scrollToBottom();

            chatWorker = new SwingWorker<>() {
                @Override
                protected ChatMessage doInBackground() throws Exception {
                    String promptWithDirective = userPrompt + 
                            "\n\nTABLE DIRECTIVE: Output the side-by-side comparison using the ```uni-table fenced JSON specification with all fields filled accurately.";
                    ChatMessage reply = geminiService.sendMessage(currentSession, promptWithDirective, null);
                    // Ensure the tableJson is set if Gemini didn't format the JSON block properly
                    if (reply.getUniTableJson() == null || reply.getUniTableJson().isBlank()) {
                        reply.setUniTableJson(tableJson);
                    }
                    return reply;
                }

                @Override
                protected void done() {
                    composerPanel.setGenerating(false);
                    if (isCancelled()) return;
                    try {
                        ChatMessage reply = get();
                        currentSession.addMessage(reply);
                        storageService.saveSession(currentSession);
                        sidebar.renderSessionList();
                        renderMessages();
                    } catch (Exception ex) {
                        messagesColumn.remove(thinkingPanel);
                        ChatMessage fallback = new ChatMessage("assistant",
                                "Here is the side-by-side comparison for **" + degree + " in " + program +
                                "** across **" + names + "**:\n\n" +
                                "Below is the interactive comparison table detailing tuition fees, living costs, minimum GPA cutoffs, and application deadlines:");
                        fallback.setUniTableJson(tableJson);
                        currentSession.addMessage(fallback);
                        storageService.saveSession(currentSession);
                        sidebar.renderSessionList();
                        renderMessages();
                    }
                }
            };
            chatWorker.execute();
        } else {
            // Immediate local comparison response (instant offline mode, no API key required!)
            ChatMessage reply = new ChatMessage("assistant",
                    "Here is the detailed side-by-side comparison for **" + degree + " in " + program +
                    "** across **" + names + "**.\n\n" +
                    "Compare annual tuition, monthly living expenses, minimum GPA, English language criteria, and post-study work options in the table below:");
            reply.setUniTableJson(tableJson);
            currentSession.addMessage(reply);
            storageService.saveSession(currentSession);
            sidebar.renderSessionList();
            renderMessages();
        }
    }

    /**
     * Custom scrollable panel for the chat messages ensuring dynamic height expansion
     * and proper viewport width matching so tables and long messages are never cut off.
     */
    private static class ScrollableChatWrapper extends JPanel implements Scrollable {
        public ScrollableChatWrapper(JComponent content) {
            super(new BorderLayout());
            setOpaque(false);
            add(content, BorderLayout.NORTH);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 80;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
