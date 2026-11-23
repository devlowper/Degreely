package com.degreely.app.view.components.chat;

import com.degreely.app.util.Icons;
import com.degreely.app.util.Theme;
import com.degreely.app.util.UI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ComposerPanel extends JPanel {

    private final JTextArea inputArea = new JTextArea();
    private final JPanel filesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
    private final List<File> attachedFiles = new ArrayList<>();
    private final JButton sendBtn;
    private final JButton stopBtn;
    private final BiConsumer<String, List<File>> onSendMessage;
    private final Runnable onStopGeneration;
    private final Runnable onCompareRequested;
    private boolean isGenerating = false;

    public ComposerPanel(BiConsumer<String, List<File>> onSendMessage, Runnable onStopGeneration) {
        this(onSendMessage, onStopGeneration, null);
    }

    public ComposerPanel(BiConsumer<String, List<File>> onSendMessage, Runnable onStopGeneration, Runnable onCompareRequested) {
        this.onSendMessage = onSendMessage;
        this.onStopGeneration = onStopGeneration;
        this.onCompareRequested = onCompareRequested;

        this.sendBtn = createSendButton();
        this.stopBtn = createStopButton();

        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(Theme.SPACING_12, Theme.SPACING_32, Theme.SPACING_16, Theme.SPACING_32));

        // 1. Files Preview Strip (shows above input if files are attached)
        filesPanel.setOpaque(false);
        filesPanel.setVisible(false);
        add(filesPanel);

        // 2. Main Rounded Box (Claude-style 24px radius)
        JPanel box = new JPanel(new BorderLayout(Theme.SPACING_8, 0));
        box.setBackground(Theme.CARD_BG);
        box.putClientProperty(FlatClientProperties.STYLE, "arc: 24; background: #FFFFFF;");
        box.setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(1, 1, 1, 1), Theme.BORDER, 1, 24),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        // Left: Paperclip Button for file upload + Compare Button
        JButton paperclipBtn = new JButton(Icons.paperclip(16, Theme.TEXT_MUTED));
        paperclipBtn.setBorderPainted(false);
        paperclipBtn.setContentAreaFilled(false);
        paperclipBtn.setFocusPainted(false);
        paperclipBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        paperclipBtn.setToolTipText("Attach SOP, CV, Transcripts, or brochures (PDF, Images, TXT, CSV)");
        paperclipBtn.addActionListener(e -> chooseFiles());

        JButton compareBtn = new JButton(Icons.scale(16, Theme.TEXT_MUTED));
        compareBtn.setBorderPainted(false);
        compareBtn.setContentAreaFilled(false);
        compareBtn.setFocusPainted(false);
        compareBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        compareBtn.setToolTipText("Compare 2 or 3 universities side-by-side in a table");
        compareBtn.addActionListener(e -> {
            if (onCompareRequested != null) onCompareRequested.run();
        });

        JPanel leftWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 4));
        leftWrap.setOpaque(false);
        leftWrap.add(paperclipBtn);
        leftWrap.add(compareBtn);

        // Center: Textarea with auto-growing height & placeholder
        inputArea.setFont(Theme.FONT_BODY);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setRows(1);
        inputArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        inputArea.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "How can I help you today? (e.g. Compare MSc in Germany vs Canada)");

        // Key Listener: Enter sends, Shift+Enter new line
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        inputArea.append("\n");
                    } else {
                        e.consume();
                        doSend();
                    }
                }
            }
        });

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createEmptyBorder());
        inputScroll.setOpaque(false);
        inputScroll.getViewport().setOpaque(false);

        // Right: Send Button or Stop Button
        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        rightWrap.setOpaque(false);
        rightWrap.add(sendBtn);
        rightWrap.add(stopBtn);
        stopBtn.setVisible(false);

        box.add(leftWrap, BorderLayout.WEST);
        box.add(inputScroll, BorderLayout.CENTER);
        box.add(rightWrap, BorderLayout.EAST);

        add(box);

        // Disclaimer at bottom
        JLabel botDisclaimer = UI.mutedLabel("Degreely AI can make mistakes. Verify critical deadlines and admission criteria with official universities.");
        botDisclaimer.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 11));
        botDisclaimer.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(6));
        add(botDisclaimer);
    }

    private JButton createSendButton() {
        JButton btn = new JButton(Icons.send(14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Send message (Enter)");
        btn.addActionListener(e -> doSend());
        return btn;
    }

    private JButton createStopButton() {
        JButton btn = UI.outlineButton("Stop");
        btn.setIcon(Icons.stop(11, Theme.PRIMARY));
        btn.setIconTextGap(6);
        btn.setFont(new Font(Theme.FONT_SMALL.getName(), Font.BOLD, 11));
        btn.setMargin(new Insets(4, 10, 4, 10));
        btn.addActionListener(e -> {
            if (onStopGeneration != null) onStopGeneration.run();
        });
        return btn;
    }

    private void chooseFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Documents & Images (*.pdf, *.png, *.jpg, *.txt, *.csv, *.docx)",
                "pdf", "png", "jpg", "jpeg", "txt", "csv", "docx"));

        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            for (File f : files) {
                if (attachedFiles.size() < 5 && !attachedFiles.contains(f)) {
                    attachedFiles.add(f);
                }
            }
            renderFilesStrip();
        }
    }

    private void renderFilesStrip() {
        filesPanel.removeAll();
        if (attachedFiles.isEmpty()) {
            filesPanel.setVisible(false);
        } else {
            filesPanel.setVisible(true);
            for (File f : attachedFiles) {
                JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
                chip.setBackground(Theme.LIGHT_BLUE_HIGHLIGHT);
                chip.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #EFF6FF;");
                chip.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.decode("#BFDBFE"), 1, 12));

                JLabel name = new JLabel(f.getName());
                name.setIcon(Icons.paperclip(12, Theme.BLUE_TEXT));
                name.setIconTextGap(4);
                name.setFont(new Font(Theme.FONT_SMALL.getName(), Font.PLAIN, 11));
                name.setForeground(Theme.BLUE_TEXT);

                JButton rm = new JButton("✕");
                rm.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                rm.setForeground(Theme.TEXT_MUTED);
                rm.setBorderPainted(false);
                rm.setContentAreaFilled(false);
                rm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                rm.addActionListener(e -> {
                    attachedFiles.remove(f);
                    renderFilesStrip();
                });

                chip.add(name);
                chip.add(rm);
                filesPanel.add(chip);
            }
        }
        revalidate();
        repaint();
    }

    private void doSend() {
        String text = inputArea.getText().trim();
        if (text.isEmpty() && attachedFiles.isEmpty()) return;

        if (onSendMessage != null) {
            List<File> filesCopy = new ArrayList<>(attachedFiles);
            inputArea.setText("");
            attachedFiles.clear();
            renderFilesStrip();
            onSendMessage.accept(text, filesCopy);
        }
    }

    public void setGenerating(boolean generating) {
        this.isGenerating = generating;
        sendBtn.setVisible(!generating);
        stopBtn.setVisible(generating);
        inputArea.setEnabled(!generating);
    }

    public void setPromptText(String text) {
        inputArea.setText(text);
        inputArea.requestFocusInWindow();
    }
}
