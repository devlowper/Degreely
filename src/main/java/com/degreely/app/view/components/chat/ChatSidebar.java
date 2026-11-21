package com.degreely.app.view.components.chat;

import com.degreely.app.controller.ChatStorageService;
import com.degreely.app.controller.GeminiService;
import com.degreely.app.model.ChatSession;
import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatSidebar extends JPanel {

    private final ChatStorageService storageService = ChatStorageService.getInstance();
    private final Consumer<ChatSession> onSelectSession;
    private final Runnable onNewChat;

    private final JTextField searchField = new JTextField();
    private final JPanel chatListContainer = new JPanel();
    private String currentSelectedId = null;

    public ChatSidebar(Consumer<ChatSession> onSelectSession, Runnable onNewChat) {
        this.onSelectSession = onSelectSession;
        this.onNewChat = onNewChat;
        initUI();
        renderSessionList();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FAFAFA"));
        setPreferredSize(new Dimension(264, 600));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#E6E6E6")));

        // Top Section: App name + New Chat Button + Search
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        // Header: Brand & Serif App Name
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandRow.setOpaque(false);

        JLabel brandIcon = new JLabel(Icons.spark(18, Theme.PRIMARY));

        JLabel appName = new JLabel("Night Owl");
        appName.setFont(new Font(Font.SERIF, Font.BOLD, 19));
        appName.setForeground(Theme.TEXT);

        brandRow.add(brandIcon);
        brandRow.add(appName);
        topSection.add(brandRow);
        topSection.add(Box.createVerticalStrut(14));

        // "New chat" button (Claude-style with orange plus icon)
        JButton newChatBtn = createNewChatButton();
        topSection.add(newChatBtn);
        topSection.add(Box.createVerticalStrut(12));

        // Search chats input
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search chats... (Ctrl+K)");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, Icons.search(14, Theme.TEXT_MUTED));
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 4,8,4,8;");
        searchField.setFont(Theme.FONT_SMALL);
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { renderSessionList(); }
            @Override public void removeUpdate(DocumentEvent e) { renderSessionList(); }
            @Override public void changedUpdate(DocumentEvent e) { renderSessionList(); }
        });
        topSection.add(searchField);
        topSection.add(Box.createVerticalStrut(12));

        // Ctrl+K keyboard shortcut to focus search
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_DOWN_MASK), "focusSearch");
        getActionMap().put("focusSearch", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                searchField.requestFocusInWindow();
                searchField.selectAll();
            }
        });

        // "Recents" Section Label
        JLabel recentsLabel = new JLabel("Recents");
        recentsLabel.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        recentsLabel.setForeground(Theme.TEXT_MUTED);
        topSection.add(recentsLabel);

        add(topSection, BorderLayout.NORTH);

        // Center: Scrollable Chat List grouped by date
        chatListContainer.setLayout(new BoxLayout(chatListContainer, BoxLayout.Y_AXIS));
        chatListContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(chatListContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.decode("#FAFAFA"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom: Profile, Settings, and Free Plan
        JPanel bottomSection = createBottomSection();
        add(bottomSection, BorderLayout.SOUTH);
    }

    private JButton createNewChatButton() {
        JButton btn = new JButton("New chat") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw small orange plus circle on left
                g2.setColor(Theme.PRIMARY);
                g2.fillOval(12, 8, 18, 18);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                g2.drawString("+", 17, 21);
                g2.dispose();
            }
        };
        btn.setFont(new Font(Theme.FONT_BODY.getName(), Font.BOLD, 13));
        btn.setForeground(Theme.TEXT);
        btn.setBackground(Color.WHITE);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #FFFFFF;");
        btn.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.decode("#E2E8F0"), 1, 8));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(232, 36));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 36, 0, 0));
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.addActionListener(e -> {
            if (onNewChat != null) onNewChat.run();
        });
        return btn;
    }

    private JPanel createBottomSection() {
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#E6E6E6")),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // 1. My Profile Button
        JButton profileBtn = createSidebarActionBtn("My Student Profile", Icons.user(15, Theme.TEXT_SECONDARY), e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            new ProfileDialog(owner).setVisible(true);
        });

        // 2. API Key / Settings Button
        JButton settingsBtn = createSidebarActionBtn("Gemini Settings", Icons.settings(15, Theme.TEXT_SECONDARY), e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            new ApiKeyDialog(owner).setVisible(true);
        });

        // 3. Free plan indicator
        JPanel planRow = new JPanel(new BorderLayout());
        planRow.setOpaque(false);
        planRow.setBorder(BorderFactory.createEmptyBorder(6, 4, 2, 4));

        JLabel planLabel = new JLabel("Free plan");
        planLabel.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 12));
        planLabel.setForeground(Theme.TEXT_MUTED);

        JLabel upgradeLink = new JLabel("Upgrade");
        upgradeLink.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 12));
        upgradeLink.setForeground(Color.decode("#1757AC"));
        upgradeLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        planRow.add(planLabel, BorderLayout.WEST);
        planRow.add(upgradeLink, BorderLayout.EAST);

        bottom.add(profileBtn);
        bottom.add(Box.createVerticalStrut(4));
        bottom.add(settingsBtn);
        bottom.add(Box.createVerticalStrut(6));
        bottom.add(planRow);

        return bottom;
    }

    private JButton createSidebarActionBtn(String text, javax.swing.Icon icon, java.awt.event.ActionListener al) {
        JButton btn = new JButton(text);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
        }
        btn.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 12));
        btn.setForeground(Theme.TEXT_SECONDARY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btn.addActionListener(al);
        return btn;
    }

    public void renderSessionList() {
        chatListContainer.removeAll();
        String query = searchField.getText();
        List<ChatSession> list = storageService.searchSessions(query);

        if (list.isEmpty()) {
            JLabel empty = UI.mutedLabel("No chats yet");
            empty.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            chatListContainer.add(empty);
        } else {
            LocalDate today = LocalDate.now();
            List<ChatSession> todayList = new ArrayList<>();
            List<ChatSession> yesterdayList = new ArrayList<>();
            List<ChatSession> last7DaysList = new ArrayList<>();
            List<ChatSession> olderList = new ArrayList<>();

            for (ChatSession s : list) {
                LocalDate d = Instant.ofEpochMilli(s.getLastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
                long diff = ChronoUnit.DAYS.between(d, today);
                if (diff == 0) todayList.add(s);
                else if (diff == 1) yesterdayList.add(s);
                else if (diff <= 7) last7DaysList.add(s);
                else olderList.add(s);
            }

            addCategorySection("Today", todayList);
            addCategorySection("Yesterday", yesterdayList);
            addCategorySection("Previous 7 days", last7DaysList);
            addCategorySection("Older", olderList);
        }

        chatListContainer.revalidate();
        chatListContainer.repaint();
    }

    private void addCategorySection(String title, List<ChatSession> sessions) {
        if (sessions.isEmpty()) return;

        JLabel cat = new JLabel(title);
        cat.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 10));
        cat.setForeground(Theme.TEXT_MUTED);
        cat.setBorder(BorderFactory.createEmptyBorder(10, 16, 4, 16));
        chatListContainer.add(cat);

        for (ChatSession s : sessions) {
            chatListContainer.add(createChatItem(s));
        }
    }

    private JPanel createChatItem(ChatSession s) {
        JPanel item = new JPanel(new BorderLayout(4, 0));
        boolean isSelected = s.getId().equals(currentSelectedId);
        item.setBackground(isSelected ? Color.decode("#EBECEF") : Color.decode("#FAFAFA"));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        item.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 12));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel l = new JLabel(s.getTitle());
        if (s.isPinned()) {
            l.setIcon(Icons.pin(13, Theme.PRIMARY));
        } else {
            l.setIcon(Icons.chat(13, Theme.TEXT_MUTED));
        }
        l.setIconTextGap(8);
        l.setFont(new Font(Theme.FONT_BODY.getName(), isSelected ? Font.BOLD : Font.PLAIN, 12));
        l.setForeground(Theme.TEXT);

        item.add(l, BorderLayout.CENTER);

        // Click to open session
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e, s);
                } else {
                    currentSelectedId = s.getId();
                    renderSessionList();
                    if (onSelectSession != null) onSelectSession.accept(s);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!s.getId().equals(currentSelectedId)) {
                    item.setBackground(Color.decode("#F0F0F0"));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!s.getId().equals(currentSelectedId)) {
                    item.setBackground(Color.decode("#FAFAFA"));
                }
            }
        });

        return item;
    }

    private void showContextMenu(MouseEvent e, ChatSession s) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem pinItem = new JMenuItem(s.isPinned() ? "Unpin chat" : "Pin chat");
        pinItem.setIcon(Icons.pin(14, Theme.PRIMARY));
        pinItem.addActionListener(evt -> {
            storageService.togglePin(s.getId());
            renderSessionList();
        });

        JMenuItem renameItem = new JMenuItem("Rename chat");
        renameItem.setIcon(Icons.pen(14, Theme.TEXT_SECONDARY));
        renameItem.addActionListener(evt -> {
            String newTitle = JOptionPane.showInputDialog(this, "Enter new chat title:", s.getTitle());
            if (newTitle != null && !newTitle.isBlank()) {
                storageService.renameSession(s.getId(), newTitle.trim());
                renderSessionList();
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete chat");
        deleteItem.setIcon(Icons.trash(14, Color.decode("#DC2626")));
        deleteItem.addActionListener(evt -> {
            int ret = JOptionPane.showConfirmDialog(this, "Delete \"" + s.getTitle() + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) {
                storageService.deleteSession(s.getId());
                renderSessionList();
                if (s.getId().equals(currentSelectedId) && onNewChat != null) {
                    onNewChat.run();
                }
            }
        });

        menu.add(pinItem);
        menu.add(renameItem);
        menu.addSeparator();
        menu.add(deleteItem);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    public void setSelectedSessionId(String id) {
        this.currentSelectedId = id;
        renderSessionList();
    }
}
