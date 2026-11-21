package com.degreely.app.view.components.chat;

import com.degreely.app.model.ChatMessage;
import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;

public class MessageBubble extends JPanel {

    private final ChatMessage message;
    private final Consumer<ChatMessage> onRegenerate;

    public MessageBubble(ChatMessage message, Consumer<ChatMessage> onRegenerate) {
        this.message = message;
        this.onRegenerate = onRegenerate;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_8, 0, Theme.SPACING_8, 0));

        boolean isUser = "user".equalsIgnoreCase(message.getRole());

        if (isUser) {
            // User message: Right-aligned light bubble
            JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightContainer.setOpaque(false);

            JPanel bubble = new JPanel();
            bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
            bubble.setBackground(new Color(241, 245, 249)); // Slate-100 (#F1F5F9)
            bubble.putClientProperty(FlatClientProperties.STYLE, "arc: 18; background: #F1F5F9;");
            bubble.setBorder(BorderFactory.createCompoundBorder(
                    new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, 18),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));

            // Display file chips if files were attached
            if (message.getFileNames() != null && !message.getFileNames().isEmpty()) {
                JPanel filesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
                filesRow.setOpaque(false);
                for (String fname : message.getFileNames()) {
                    JPanel chip = UI.badge(fname, Icons.paperclip(12, Theme.BLUE_TEXT), Theme.LIGHT_BLUE_HIGHLIGHT, Theme.BLUE_TEXT);
                    filesRow.add(chip);
                }
                bubble.add(filesRow);
                bubble.add(Box.createVerticalStrut(6));
            }

            JEditorPane textPane = createTextPane(formatUserText(message.getContent()));
            textPane.setBackground(new Color(241, 245, 249));
            bubble.add(textPane);

            rightContainer.add(bubble);
            add(rightContainer, BorderLayout.CENTER);

        } else {
            // Assistant message: Left-aligned with warm spark avatar
            JPanel leftContainer = new JPanel(new BorderLayout(Theme.SPACING_12, 0));
            leftContainer.setOpaque(false);

            // Spark Icon Avatar
            JPanel avatar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                }
            };
            avatar.setPreferredSize(new Dimension(28, 28));
            avatar.setOpaque(false);
            avatar.add(new JLabel(Icons.spark(16, Color.WHITE)));

            JPanel avatarWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
            avatarWrap.setOpaque(false);
            avatarWrap.add(avatar);

            // Message Body
            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setOpaque(false);

            // Formatted markdown/HTML pane
            String cleanedContent = removeUniTableBlock(message.getContent());
            JEditorPane htmlPane = createTextPane(convertMarkdownToHtml(cleanedContent));
            body.add(htmlPane);

            // Embed Uni-Table if present
            if (message.getUniTableJson() != null && !message.getUniTableJson().isBlank()) {
                body.add(Box.createVerticalStrut(Theme.SPACING_12));
                UniTableComponent table = new UniTableComponent(message.getUniTableJson());
                body.add(table);
            }

            // Actions row (Copy, Regenerate)
            JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
            actionRow.setOpaque(false);
            actionRow.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

            JButton copyBtn = new JButton("Copy");
            copyBtn.setIcon(Icons.copy(13, Theme.TEXT_MUTED));
            copyBtn.setIconTextGap(5);
            copyBtn.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 11));
            copyBtn.setForeground(Theme.TEXT_MUTED);
            copyBtn.setBorderPainted(false);
            copyBtn.setContentAreaFilled(false);
            copyBtn.setFocusPainted(false);
            copyBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            copyBtn.addActionListener(e -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(message.getContent()), null);
                copyBtn.setText("Copied ✓");
                javax.swing.Timer t = new javax.swing.Timer(2000, evt -> copyBtn.setText("Copy"));
                t.setRepeats(false);
                t.start();
            });
            actionRow.add(copyBtn);

            if (onRegenerate != null) {
                JButton regenBtn = new JButton("Regenerate");
                regenBtn.setIcon(Icons.refresh(13, Theme.TEXT_MUTED));
                regenBtn.setIconTextGap(5);
                regenBtn.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 11));
                regenBtn.setForeground(Theme.TEXT_MUTED);
                regenBtn.setBorderPainted(false);
                regenBtn.setContentAreaFilled(false);
                regenBtn.setFocusPainted(false);
                regenBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                regenBtn.addActionListener(e -> onRegenerate.accept(message));
                actionRow.add(regenBtn);
            }

            body.add(actionRow);

            leftContainer.add(avatarWrap, BorderLayout.WEST);
            leftContainer.add(body, BorderLayout.CENTER);
            add(leftContainer, BorderLayout.CENTER);
        }
    }

    private JEditorPane createTextPane(String html) {
        JEditorPane pane = new JEditorPane("text/html", html);
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        pane.setFont(Theme.FONT_BODY);
        pane.setBorder(BorderFactory.createEmptyBorder());
        return pane;
    }

    private String formatUserText(String text) {
        return "<html><body style='font-family:" + Theme.FONT_BODY.getFamily() + "; font-size:13px; color:#0F172A; line-height:1.4;'>" +
                escapeHtml(text).replace("\n", "<br/>") + "</body></html>";
    }

    private String convertMarkdownToHtml(String md) {
        if (md == null) return "";
        // Convert basic markdown formatting to safe styled HTML
        String html = md
                .replaceAll("(?m)^### (.*)$", "<h3 style='margin:8px 0 4px 0; color:#0F172A; font-size:15px;'>$1</h3>")
                .replaceAll("(?m)^## (.*)$", "<h2 style='margin:12px 0 6px 0; color:#0F172A; font-size:16px;'>$1</h2>")
                .replaceAll("(?m)^# (.*)$", "<h1 style='margin:14px 0 8px 0; color:#0F172A; font-size:18px;'>$1</h1>")
                .replaceAll("\\*\\*(.*?)\\*\\*", "<b style='color:#0F172A;'>$1</b>")
                .replaceAll("\\*(.*?)\\*", "<i>$1</i>")
                .replaceAll("`([^`]+)`", "<code style='background:#F1F5F9; padding:2px 4px; color:#E37712; font-size:12px;'>$1</code>")
                .replaceAll("(?m)^[-*] (.*)$", "• $1<br/>")
                .replace("\n\n", "<br/><br/>")
                .replace("\n", "<br/>");

        return "<html><body style='font-family:" + Theme.FONT_BODY.getFamily() + "; font-size:13px; color:#1E293B; line-height:1.5;'>" +
                html + "</body></html>";
    }

    private String removeUniTableBlock(String content) {
        if (content == null) return "";
        return content.replaceAll("```(?:uni-table|json)?\\s*\\{[\\s\\S]*?\"universities\"[\\s\\S]*?\\}\\s*```", "").trim();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
