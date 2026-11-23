package com.degreely.app.view;

import com.degreely.app.controller.ChatStorageService;
import com.degreely.app.controller.GeminiService;
import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.ChatMessage;
import com.degreely.app.model.ChatSession;
import com.degreely.app.model.University;
import com.degreely.app.model.UniTableData;
import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.degreely.app.view.components.chat.ChatSidebar;
import com.degreely.app.view.components.chat.CompareDialog;
import com.degreely.app.view.components.chat.ComposerPanel;
import com.degreely.app.view.components.chat.MessageBubble;
import com.degreely.app.view.components.chat.StarterPromptsPanel;
import com.google.gson.Gson;

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

/**
 * Modern AI Counselor Chat Canvas:
 * - Top header with conversation title, Compare button, and Export chat.
 * - Dynamic scrollable message column with full-height message presentation.
 * - Bottom composer panel.
 * - Connects to persistent SaaS AppSidebar.
 */
public class ChatPage extends JPanel {

    private final ChatStorageService storageService = ChatStorageService.getInstance();
    private final GeminiService geminiService = GeminiService.getInstance();
    private final UniversityService uniService = UniversityService.getInstance();

    private ChatSidebar sidebar;
    private ChatSession currentSession;

    private JLabel headerTitleLabel = new JLabel("New Consultation");
    private JPanel messagesColumn = new JPanel();
    private JScrollPane messagesScrollPane = new JScrollPane();
    private ComposerPanel composerPanel;
    private SwingWorker<ChatMessage, Void> chatWorker;

    public ChatPage() {
        initUI();
        startNewChat();
    }

    public void setSidebar(ChatSidebar sidebar) {
        this.sidebar = sidebar;
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Top Minimal Header Bar
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // 2. Messages Column: Dynamic height so complete tables and conversations are fully visible
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

        add(messagesScrollPane, BorderLayout.CENTER);

        // 3. Bottom Composer with Compare Action
        composerPanel = new ComposerPanel(this::handleSendMessage, this::handleStopGeneration, this::openCompareDialog);
        add(composerPanel, BorderLayout.SOUTH);
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

        JButton exportBtn = UI.outlineButton("Export");
        exportBtn.setIcon(Icons.download(14, Theme.TEXT_MUTED));
        exportBtn.setIconTextGap(6);
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
        renderMessages();
        if (sidebar != null) {
            sidebar.selectSession(null);
        }
    }

    public void loadSession(ChatSession session) {
        this.currentSession = session;
        headerTitleLabel.setText(session.getTitle());
        renderMessages();
        if (sidebar != null) {
            sidebar.selectSession(session.getId());
        }
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
        if (isFirst) {
            String autoTitle = generateSmartTitle(prompt);
            currentSession.setTitle(autoTitle);
            headerTitleLabel.setText(autoTitle);
            storageService.saveSession(currentSession);
            if (sidebar != null) sidebar.renderSessionList();
        }

        renderMessages();
        composerPanel.setGenerating(true);

        // Show subtle "Thinking..." placeholder
        JPanel thinkingPanel = createThinkingIndicator();
        messagesColumn.add(thinkingPanel);
        messagesColumn.revalidate();
        messagesColumn.repaint();
        scrollToBottom();

        // Check if Gemini API key is available
        if (geminiService.hasApiKey()) {
            chatWorker = new SwingWorker<>() {
                @Override
                protected ChatMessage doInBackground() throws Exception {
                    if (isFirst) {
                        String title = geminiService.generateChatTitle(prompt);
                        if (title != null && !title.isBlank() && !title.equals("New Consultation")) {
                            currentSession.setTitle(title);
                            SwingUtilities.invokeLater(() -> {
                                headerTitleLabel.setText(title);
                                if (sidebar != null) sidebar.renderSessionList();
                            });
                        }
                    }
                    return geminiService.sendMessage(currentSession, prompt, files);
                }

                @Override
                protected void done() {
                    composerPanel.setGenerating(false);
                    if (isCancelled()) {
                        renderMessages();
                        return;
                    }
                    try {
                        ChatMessage assistantReply = get();
                        currentSession.addMessage(assistantReply);
                        storageService.saveSession(currentSession);
                        if (sidebar != null) sidebar.renderSessionList();
                        renderMessages();
                    } catch (Exception ex) {
                        messagesColumn.remove(thinkingPanel);
                        // Provide a comprehensive fallback advisory rather than a bare error
                        ChatMessage fallback = buildFallbackCounselingResponse(prompt);
                        currentSession.addMessage(fallback);
                        storageService.saveSession(currentSession);
                        if (sidebar != null) sidebar.renderSessionList();
                        renderMessages();
                    }
                }
            };
            chatWorker.execute();
        } else {
            // Instant detailed counseling response when offline / no API key entered yet
            SwingUtilities.invokeLater(() -> {
                composerPanel.setGenerating(false);
                messagesColumn.remove(thinkingPanel);
                ChatMessage detailedReply = buildFallbackCounselingResponse(prompt);
                currentSession.addMessage(detailedReply);
                storageService.saveSession(currentSession);
                if (sidebar != null) sidebar.renderSessionList();
                renderMessages();
            });
        }
    }

    private void handleStopGeneration() {
        if (chatWorker != null && !chatWorker.isDone()) {
            chatWorker.cancel(true);
            composerPanel.setGenerating(false);
            renderMessages();
        }
    }

    private void handleRegenerate(ChatMessage msg) {
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
        JLabel spark = new JLabel(Icons.spark(14, Theme.PRIMARY));
        JLabel text = UI.mutedLabel("Degreely is analyzing requirements & drafting guidance...");
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
        CompareDialog dialog = new CompareDialog(owner, this::compareUniversities);
        dialog.setVisible(true);
    }

    public void compareUniversities(List<University> selectedUnis, String program, String degree) {
        if (selectedUnis == null || selectedUnis.size() < 2) return;

        StringBuilder names = new StringBuilder();
        for (int i = 0; i < selectedUnis.size(); i++) {
            if (i > 0 && i == selectedUnis.size() - 1) names.append(" and ");
            else if (i > 0) names.append(", ");
            names.append(selectedUnis.get(i).getName());
        }

        String userPrompt = "Please compare " + names + " side-by-side for " + degree + " in " + program + " in a detailed comparison table.";

        // Pre-build structured UniTableData from verified institutional dataset
        UniTableData tableData = uniService.buildComparisonTable(selectedUnis, program, degree);
        String tableJson = new Gson().toJson(tableData);

        String compTitle = "Compare: " + selectedUnis.get(0).getName() + " vs " + selectedUnis.get(1).getName();
        if (currentSession == null || currentSession.getMessages().isEmpty()) {
            currentSession = new ChatSession(compTitle);
            headerTitleLabel.setText(compTitle);
        }

        // 1. Add user message
        ChatMessage userMsg = new ChatMessage("user", userPrompt);
        currentSession.addMessage(userMsg);

        // 2. Generate concise, high-impact comparative counselor summary
        String inDepthAnalysis = buildDetailedComparisonAnalysis(selectedUnis, program, degree);

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
                            "\n\nTABLE DIRECTIVE: Provide a concise 2-paragraph comparative summary highlighting key differences in cost, requirements, and visa pathways, then append the side-by-side comparison inside a ```uni-table fenced JSON block at the bottom.";
                    ChatMessage reply = geminiService.sendMessage(currentSession, promptWithDirective, null);
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
                        if (sidebar != null) sidebar.renderSessionList();
                        renderMessages();
                    } catch (Exception ex) {
                        messagesColumn.remove(thinkingPanel);
                        ChatMessage fallback = new ChatMessage("assistant", inDepthAnalysis);
                        fallback.setUniTableJson(tableJson);
                        currentSession.addMessage(fallback);
                        storageService.saveSession(currentSession);
                        if (sidebar != null) sidebar.renderSessionList();
                        renderMessages();
                    }
                }
            };
            chatWorker.execute();
        } else {
            // Immediate detailed local comparison analysis
            ChatMessage reply = new ChatMessage("assistant", inDepthAnalysis);
            reply.setUniTableJson(tableJson);
            currentSession.addMessage(reply);
            storageService.saveSession(currentSession);
            if (sidebar != null) sidebar.renderSessionList();
            renderMessages();
        }
    }

    /**
     * Generates a concise, high-impact comparative counselor summary.
     */
    private String buildDetailedComparisonAnalysis(List<University> unis, String program, String degree) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Comparative Assessment: ").append(degree).append(" in ").append(program).append("\n\n");

        sb.append("When evaluating **");
        for (int i = 0; i < unis.size(); i++) {
            if (i > 0 && i == unis.size() - 1) sb.append(" and ");
            else if (i > 0) sb.append(", ");
            sb.append(unis.get(i).getName());
        }
        sb.append("**, the primary trade-offs center on academic selectivity, annual tuition costs, and post-study employment visas:\n\n");

        for (University u : unis) {
            String tuition = u.getTuitionPerYear() == 0 ? "Tuition-Free ($0 / yr)" : "$" + String.format("%,.0f", u.getTuitionPerYear()) + " / yr";
            sb.append("- **").append(u.getName()).append("** (QS #").append(u.getRanking()).append(", ").append(u.getCountry()).append("): ")
              .append("Tuition **").append(tuition).append("**, Min GPA **").append(String.format("%.1f", u.getMinGpa())).append(" / 4.0**, IELTS **")
              .append(u.getMinIelts()).append("**. Post-study visa: *").append(getCountryWorkRights(u.getCountry())).append("*.\n");
        }
        sb.append("\n");

        sb.append("**Strategic Takeaway:** If aiming for minimal debt and European industry access, public institutions offer unbeatable ROI. If targeting US/UK top-tier career ecosystems and venture networks, private US/UK programs yield strong starting salaries despite higher tuition.\n\n");
        sb.append("Review the side-by-side metric comparison table below:");

        return sb.toString();
    }

    /**
     * Generates a concise, scannable counseling response for university queries.
     */
    private ChatMessage buildFallbackCounselingResponse(String prompt) {
        String lower = prompt.toLowerCase();

        // Check if any university from our dataset is mentioned
        for (University u : uniService.getAll()) {
            if (lower.contains(u.getName().toLowerCase()) || (u.getCity() != null && lower.contains(u.getCity().toLowerCase()))) {
                StringBuilder sb = new StringBuilder();
                sb.append("### ").append(u.getName()).append(" (").append(u.getCountry()).append(")\n\n");

                String tuitionStr = u.getTuitionPerYear() == 0 ? "Tuition-Free ($0 / year)" : "$" + String.format("%,.0f", u.getTuitionPerYear()) + " / year";
                sb.append("Here is the key admissions and financial breakdown for **").append(u.getName()).append("**:\n\n");
                sb.append("- **QS World Ranking:** #").append(u.getRanking()).append(" • **Location:** ").append(u.getCity()).append(", ").append(u.getCountry()).append("\n");
                sb.append("- **Academic Cutoffs:** Min GPA **").append(String.format("%.2f", u.getMinGpa())).append(" / 4.0**, IELTS **").append(u.getMinIelts()).append(" Band**, Acceptance Rate: **~").append(u.getAcceptanceRate()).append("%**\n");
                sb.append("- **Cost of Attendance:** Tuition **").append(tuitionStr).append("**, Living Expenses **~$1,100–$1,800 / month**\n");
                sb.append("- **Work Visa & Funding:** ").append(getCountryWorkRights(u.getCountry())).append(". Eligible for merit scholarships and graduate assistantships (RA/TA).\n\n");
                sb.append("**Counselor Verdict:** Focus your SOP on specific research alignment and secure 2 strong academic recommendations. Apply during priority Fall rounds for maximum scholarship consideration.\n\n");

                if (!geminiService.hasApiKey()) {
                    sb.append("*(Tip: Add your free Gemini API key in **Gemini Settings** to chat live with AI!)*");
                }

                return new ChatMessage("assistant", sb.toString());
            }
        }

        // Generic counseling brief
        StringBuilder sb = new StringBuilder();
        sb.append("### Study Abroad Admissions Guidance\n\n");
        sb.append("Planning your international degree requires balancing academic fit, budget, and post-graduation career goals:\n\n");
        sb.append("- **Targeting Universities:** Categorize choices into Dream (Reach), Target (Matching profile), and Safety institutions.\n");
        sb.append("- **Budget & Free Tuition:** Germany and several European nations offer world-class degrees with $0 tuition. US/UK universities offer merit scholarships and assistantships for competitive profiles.\n");
        sb.append("- **Work Visas:** USA offers 3-year STEM OPT; UK offers 2-year Graduate Route; Canada offers up to 3-year PGWP; Germany offers 18-month job search visa.\n\n");
        sb.append("**Next Steps:** Explore **Home / Universities** to filter 500+ institutions, or use **Compare Universities** above for side-by-side analysis.\n");

        return new ChatMessage("assistant", sb.toString());
    }

    private String getCountryWorkRights(String country) {
        if (country == null) return "Post-study work permit";
        String c = country.toLowerCase();
        if (c.contains("united states") || c.contains("us")) {
            return "12-month OPT + 24-month STEM extension (3 years total)";
        } else if (c.contains("united kingdom") || c.contains("uk")) {
            return "2-year Graduate Route Post-Study Visa";
        } else if (c.contains("canada")) {
            return "Up to 3-year Post-Graduation Work Permit (PGWP)";
        } else if (c.contains("germany")) {
            return "18-month Job Seeking Visa leading to EU Blue Card";
        } else if (c.contains("australia")) {
            return "2 to 4-year Temporary Graduate Visa (subclass 485)";
        } else if (c.contains("switzerland")) {
            return "6-month graduate job search residence permit";
        } else if (c.contains("singapore")) {
            return "1-year Long Term Visit Pass (LTVP) for graduates";
        }
        return "Post-study work rights and employment visa options";
    }

    private String generateSmartTitle(String prompt) {
        if (prompt == null || prompt.isBlank()) return "New Consultation";
        String clean = prompt.replaceAll("[#*`\n\r]", " ").trim();
        if (clean.toLowerCase().startsWith("please compare ")) {
            clean = clean.substring(15).trim();
        }
        if (clean.length() > 32) {
            int lastSpace = clean.substring(0, 32).lastIndexOf(' ');
            if (lastSpace > 12) {
                return clean.substring(0, lastSpace) + "...";
            }
            return clean.substring(0, 32) + "...";
        }
        return clean;
    }

    /**
     * Custom scrollable panel for the chat messages ensuring dynamic height expansion
     * and proper viewport width matching so tables and long messages are never cut off.
     */
    private static class ScrollableChatWrapper extends JPanel implements Scrollable {
        public ScrollableChatWrapper(Component content) {
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
