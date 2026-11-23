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
                        currentSession.setTitle(title);
                        SwingUtilities.invokeLater(() -> {
                            headerTitleLabel.setText(title);
                            if (sidebar != null) sidebar.renderSessionList();
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
        JLabel spark = new JLabel("✦");
        spark.setForeground(Theme.PRIMARY);
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

        // Pre-build structured UniTableData from verified institutional dataset
        UniTableData tableData = uniService.buildComparisonTable(selectedUnis, program, degree);
        String tableJson = new Gson().toJson(tableData);

        if (currentSession == null) {
            currentSession = new ChatSession("Compare: " + selectedUnis.get(0).getName() + " vs " + selectedUnis.get(1).getName());
        }

        // 1. Add user message
        ChatMessage userMsg = new ChatMessage("user", userPrompt);
        currentSession.addMessage(userMsg);

        // 2. Generate rich, ChatGPT-style in-depth comparison analysis
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
                            "\n\nTABLE DIRECTIVE: Provide a detailed, in-depth multi-section ChatGPT-style counseling evaluation covering academic profile, tuition, living costs, scholarships, and post-study work permits. Then append the side-by-side comparison inside a ```uni-table fenced JSON block at the bottom.";
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
     * Generates a comprehensive, ChatGPT-style multi-section comparative analysis.
     */
    private String buildDetailedComparisonAnalysis(List<University> unis, String program, String degree) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Comprehensive Institutional Analysis: ").append(degree).append(" in ").append(program).append("\n\n");
        sb.append("Choosing between top-tier global institutions requires balancing academic rigor, financial investment, career ROI, and immigration pathways. Here is your detailed counselor assessment:\n\n");

        sb.append("#### 1. 🏛️ Academic Prestige & Faculty Research\n");
        for (University u : unis) {
            sb.append("- **").append(u.getName()).append("** (QS World Rank #").append(u.getRanking()).append(", ").append(u.getCountry()).append("): ")
              .append("Celebrated for world-class research infrastructure and distinguished alumni networks. ")
              .append("The department offers high-impact electives, cutting-edge labs, and close corporate partnerships.\n");
        }
        sb.append("\n");

        sb.append("#### 2. 🎯 Admission Thresholds & Competitiveness\n");
        for (University u : unis) {
            sb.append("- **").append(u.getName()).append("**: Requires a minimum GPA of **").append(String.format("%.2f", u.getMinGpa())).append(" / 4.0** ")
              .append("and an English proficiency score of **IELTS ").append(u.getMinIelts()).append("+** (or equivalent TOEFL). ")
              .append("The acceptance rate stands around **").append(u.getAcceptanceRate()).append("%**, making holistic profile preparation (SOP, LORs, and relevant projects) essential.\n");
        }
        sb.append("\n");

        sb.append("#### 3. 💰 Financial Commitment & Cost of Living\n");
        for (University u : unis) {
            String tStr = u.getTuitionPerYear() == 0 ? "Tuition-Free ($0 / year for international students)" : "$" + String.format("%,.0f", u.getTuitionPerYear()) + " / year";
            sb.append("- **").append(u.getName()).append("**: Annual tuition is **").append(tStr).append("**. ")
              .append("Estimated living expenses in ").append(u.getCity()).append(", ").append(u.getCountry())
              .append(" typically range between **$1,100 to $1,800 / month** covering student housing, groceries, health insurance, and local transit.\n");
        }
        sb.append("\n");

        sb.append("#### 4. 💼 Post-Study Work Rights & Industry Prospects\n");
        for (University u : unis) {
            String workRights = getCountryWorkRights(u.getCountry());
            sb.append("- **").append(u.getName()).append(" (").append(u.getCountry()).append(")**: Graduates qualify for **")
              .append(workRights).append("**, providing a strong pathway to transition into multinational tech and finance sectors.\n");
        }
        sb.append("\n");

        sb.append("#### 5. 🚀 Strategic Counselor Recommendation\n");
        sb.append("- **For High ROI & Minimal Debt:** European institutions (such as Germany/Switzerland) offer world-class degrees with near-zero tuition fees, significantly minimizing financial risk.\n");
        sb.append("- **For Rapid Industry Scaling & High Starting Salaries:** US and UK universities lead in starting compensations ($110k+ in tech in the US) and deep venture capital ecosystems.\n");
        sb.append("- **Action Plan:** Prepare your Statement of Purpose (SOP) emphasizing technical leadership, finalize academic recommendation letters, and submit within the priority round.\n\n");
        sb.append("Examine the detailed interactive comparison table below for specific metric-by-metric figures:");

        return sb.toString();
    }

    /**
     * Generates a comprehensive, ChatGPT-style detailed advisory response for university queries.
     */
    private ChatMessage buildFallbackCounselingResponse(String prompt) {
        String lower = prompt.toLowerCase();

        // Check if any university from our dataset is mentioned
        for (University u : uniService.getAll()) {
            if (lower.contains(u.getName().toLowerCase()) || (u.getCity() != null && lower.contains(u.getCity().toLowerCase()))) {
                StringBuilder sb = new StringBuilder();
                sb.append("### Expert Counseling Brief: ").append(u.getName()).append(" (").append(u.getCountry()).append(")\n\n");
                sb.append("Here is a comprehensive breakdown of admissions criteria, academic expectations, expenses, and strategic advice for **").append(u.getName()).append("**:\n\n");

                sb.append("#### 1. 🏛️ Institution Profile & Standing\n");
                sb.append("**").append(u.getName()).append("** is globally ranked **#").append(u.getRanking()).append("** in the QS World University Rankings. ")
                  .append("Located in **").append(u.getCity()).append(", ").append(u.getCountry()).append("**, it is celebrated as an elite academic hub with extensive research funding, internationally renowned faculty, and exceptional employer reputation.\n\n");

                sb.append("#### 2. 🎯 Academic Cutoffs & Admission Requirements\n");
                sb.append("- **Minimum GPA Requirement:** ").append(String.format("%.2f", u.getMinGpa())).append(" / 4.0 (or equivalent First Class / Division).\n");
                sb.append("- **English Language Testing:** Minimum IELTS score of **").append(u.getMinIelts()).append(" Band** or TOEFL iBT **").append((int)(u.getMinIelts() * 12 + 10)).append("+**.\n");
                sb.append("- **Standardized Tests:** ").append(u.getRanking() <= 10 ? "GRE / GMAT scores are strongly recommended for STEM & Business programs." : "GRE / GMAT is optional or waived for most departments.").append("\n");
                sb.append("- **Acceptance Rate:** Approximately **").append(u.getAcceptanceRate()).append("%**, requiring a standout portfolio of academic coursework and research achievements.\n\n");

                sb.append("#### 3. 💰 Tuition Fees & Cost of Attendance\n");
                String tuitionStr = u.getTuitionPerYear() == 0 ? "Tuition-Free ($0 / year for international students)" : "$" + String.format("%,.0f", u.getTuitionPerYear()) + " / year";
                sb.append("- **Annual Tuition:** **").append(tuitionStr).append("**\n");
                sb.append("- **Living Expenses:** Estimated between **$1,100 to $1,800 per month** covering accommodation, meal plans, health insurance, and local transport.\n");
                sb.append("- **Financial Proof:** When applying for a student visa, you must demonstrate liquid funds covering 1 year of tuition plus living costs.\n\n");

                sb.append("#### 4. 🎓 Scholarships & Financial Aid\n");
                sb.append("- **Institutional Fellowships:** Merit-based awards, Graduate Teaching Assistantships (TA), and Research Assistantships (RA) providing partial to full tuition waivers plus monthly stipends.\n");
                sb.append("- **External Scholarships:** Eligible for prestigious international funding (e.g. Fulbright, DAAD, Commonwealth, Chevening depending on destination country).\n\n");

                sb.append("#### 5. 💼 Post-Study Work Visas & Career Pathways\n");
                sb.append("International graduates are eligible for **").append(getCountryWorkRights(u.getCountry())).append("**. ")
                  .append("Graduates enjoy excellent placement rates across leading technology companies, top-tier consulting firms, and premier research institutes.\n\n");

                sb.append("#### 6. 🚀 Strategic Application Roadmap\n");
                sb.append("1. **Statement of Purpose (SOP):** Craft a compelling 2-page essay highlighting your academic motivations, research questions, and long-term career vision.\n");
                sb.append("2. **Letters of Recommendation (LOR):** Secure 2 to 3 strong letters from professors who can vouch for your technical and intellectual acumen.\n");
                sb.append("3. **Intake Deadlines:** Target the primary Fall intake (Deadlines typically span December 1 to February 1).\n\n");

                if (!geminiService.hasApiKey()) {
                    sb.append("*(Tip: Connect your free Gemini API key in **Gemini Settings** in the left sidebar to unlock real-time web-connected AI counseling and custom essay reviews.)*");
                }

                return new ChatMessage("assistant", sb.toString());
            }
        }

        // Generic deep counseling overview
        StringBuilder sb = new StringBuilder();
        sb.append("### Study Abroad Guidance & Strategic Overview\n\n");
        sb.append("Thank you for your inquiry! Preparing an international higher education application requires a systematic, step-by-step approach. Here is what you should focus on:\n\n");

        sb.append("#### 1. 🎯 Profile Evaluation & University Matching\n");
        sb.append("- **Dream Tier (Reach):** Top 1-20 institutions globally (e.g., MIT, Oxford, Cambridge, ETH Zurich). These require a GPA >= 3.8 and IELTS >= 7.5.\n");
        sb.append("- **Target Tier:** Top 50-150 institutions where your GPA and test scores strongly match the median accepted class.\n");
        sb.append("- **Safety Tier:** Well-regarded universities with higher acceptance rates ensuring guaranteed admission.\n\n");

        sb.append("#### 2. 💰 Financial Planning & Low-Cost Destinations\n");
        sb.append("- **Tuition-Free Options:** Germany and public European universities offer world-class Master's degrees with zero tuition ($0), requiring only living expenses (~€934/month blocked account).\n");
        sb.append("- **High-Scholarship Markets:** US universities offer Graduate Assistantships (RA/TA) that frequently include 100% tuition coverage and living stipends for STEM applicants.\n\n");

        sb.append("#### 3. 💼 Work Rights & Immigration Benefits\n");
        sb.append("- **USA:** 3-Year STEM OPT extension for tech/science programs.\n");
        sb.append("- **UK:** 2-Year Graduate Route post-study work visa.\n");
        sb.append("- **Canada:** Up to 3-Year Post-Graduation Work Permit (PGWP).\n");
        sb.append("- **Germany:** 18-Month Job Seeking Visa leading to EU Blue Card.\n\n");

        sb.append("#### 4. 🚀 Recommended Action Steps\n");
        sb.append("1. Explore the **Home / Universities** section to view verified admission cutoffs and fees for 20+ top institutions.\n");
        sb.append("2. Use the **Compare Universities** tool to compare programs side-by-side in a detailed interactive table.\n");
        sb.append("3. Add your free Gemini API key in **Gemini Settings** (in the bottom left sidebar) to enable unrestricted live AI dialogue and personal SOP reviews.\n");

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
