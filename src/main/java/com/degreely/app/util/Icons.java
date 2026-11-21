package com.degreely.app.util;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

public final class Icons {

    private Icons() {}

    /**
     * User profile silhouette icon.
     */
    public static Icon user(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Head circle
            int headR = (int) (w * 0.22);
            int headX = (w - headR * 2) / 2;
            int headY = (int) (h * 0.16);
            g2.drawOval(headX, headY, headR * 2, headR * 2);

            // Shoulders arc
            int sX = (int) (w * 0.2);
            int sY = (int) (h * 0.58);
            int sW = (int) (w * 0.6);
            int sH = (int) (h * 0.36);
            g2.drawArc(sX, sY, sW, sH, 0, 180);
        });
    }

    /**
     * Settings gear icon.
     */
    public static Icon settings(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = w / 2;
            int cy = h / 2;
            int rOuter = (int) (w * 0.36);
            int rInner = (int) (w * 0.20);

            // Center hole
            g2.drawOval(cx - rInner, cy - rInner, rInner * 2, rInner * 2);

            // Outer teeth (6 teeth)
            for (int i = 0; i < 6; i++) {
                double angle = i * (Math.PI / 3);
                int x1 = (int) (cx + Math.cos(angle) * (rOuter - 2));
                int y1 = (int) (cy + Math.sin(angle) * (rOuter - 2));
                int x2 = (int) (cx + Math.cos(angle) * (rOuter + 2));
                int y2 = (int) (cy + Math.sin(angle) * (rOuter + 2));
                g2.drawLine(x1, y1, x2, y2);
            }
            g2.drawOval(cx - rOuter + 2, cy - rOuter + 2, (rOuter - 2) * 2, (rOuter - 2) * 2);
        });
    }

    /**
     * Search magnifying glass icon.
     */
    public static Icon search(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int r = (int) (w * 0.30);
            int cx = (int) (w * 0.40);
            int cy = (int) (h * 0.40);

            // Lens circle
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);

            // Handle
            int x1 = (int) (cx + r * 0.7);
            int y1 = (int) (cy + r * 0.7);
            int x2 = (int) (w * 0.82);
            int y2 = (int) (h * 0.82);
            g2.drawLine(x1, y1, x2, y2);
        });
    }

    /**
     * Paperclip attachment icon.
     */
    public static Icon paperclip(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            GeneralPath p = new GeneralPath();
            // Tilted paperclip
            p.moveTo(w * 0.65, h * 0.35);
            p.lineTo(w * 0.38, h * 0.62);
            p.curveTo(w * 0.28, h * 0.72, w * 0.32, h * 0.82, w * 0.42, h * 0.82);
            p.curveTo(w * 0.48, h * 0.82, w * 0.54, h * 0.76, w * 0.72, h * 0.58);
            p.curveTo(w * 0.84, h * 0.46, w * 0.84, h * 0.30, w * 0.72, h * 0.18);
            p.curveTo(w * 0.60, h * 0.08, w * 0.44, h * 0.08, w * 0.30, h * 0.22);
            p.lineTo(w * 0.18, h * 0.34);
            g2.draw(p);
        });
    }

    /**
     * Sparkle 4-point star icon.
     */
    public static Icon spark(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            int cx = w / 2;
            int cy = h / 2;
            int r = (int) (w * 0.44);

            GeneralPath p = new GeneralPath();
            p.moveTo(cx, cy - r);
            p.quadTo(cx, cy, cx + r, cy);
            p.quadTo(cx, cy, cx, cy + r);
            p.quadTo(cx, cy, cx - r, cy);
            p.quadTo(cx, cy, cx, cy - r);
            p.closePath();
            g2.fill(p);
        });
    }

    /**
     * Speech chat bubble icon.
     */
    public static Icon chat(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int bx = (int) (w * 0.12);
            int by = (int) (h * 0.14);
            int bw = (int) (w * 0.76);
            int bh = (int) (h * 0.58);
            g2.drawRoundRect(bx, by, bw, bh, 6, 6);

            // Tail
            GeneralPath p = new GeneralPath();
            p.moveTo(w * 0.28, by + bh);
            p.lineTo(w * 0.22, h * 0.86);
            p.lineTo(w * 0.44, by + bh);
            g2.draw(p);
        });
    }

    /**
     * Pushpin icon.
     */
    public static Icon pin(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            GeneralPath p = new GeneralPath();
            p.moveTo(w * 0.35, h * 0.15);
            p.lineTo(w * 0.85, h * 0.65);
            p.lineTo(w * 0.70, h * 0.70);
            p.lineTo(w * 0.50, h * 0.50);
            p.lineTo(w * 0.25, h * 0.75);
            p.lineTo(w * 0.20, h * 0.90);
            p.lineTo(w * 0.35, h * 0.85);
            p.lineTo(w * 0.60, h * 0.60);
            p.closePath();
            g2.draw(p);
        });
    }

    /**
     * Copy dual-sheet icon.
     */
    public static Icon copy(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Back sheet
            g2.drawRoundRect((int)(w * 0.30), (int)(h * 0.12), (int)(w * 0.55), (int)(h * 0.60), 4, 4);

            // Front sheet
            g2.setColor(Color.WHITE);
            g2.fillRoundRect((int)(w * 0.15), (int)(h * 0.28), (int)(w * 0.55), (int)(h * 0.60), 4, 4);
            g2.setColor(color);
            g2.drawRoundRect((int)(w * 0.15), (int)(h * 0.28), (int)(w * 0.55), (int)(h * 0.60), 4, 4);
        });
    }

    /**
     * Circular arrow (refresh/regenerate) icon.
     */
    public static Icon refresh(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = w / 2;
            int cy = h / 2;
            int r = (int) (w * 0.34);

            // Circular arc
            g2.drawArc(cx - r, cy - r, r * 2, r * 2, 45, 270);

            // Arrow head
            int ax = (int) (cx + r * Math.cos(Math.toRadians(45)));
            int ay = (int) (cy - r * Math.sin(Math.toRadians(45)));
            g2.drawLine(ax, ay, ax + 3, ay + 4);
            g2.drawLine(ax, ay, ax - 4, ay + 2);
        });
    }

    /**
     * Download / Export CSV icon.
     */
    public static Icon download(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = w / 2;

            // Downward arrow
            g2.drawLine(cx, (int) (h * 0.18), cx, (int) (h * 0.62));
            g2.drawLine(cx, (int) (h * 0.62), (int) (cx - w * 0.18), (int) (h * 0.44));
            g2.drawLine(cx, (int) (h * 0.62), (int) (cx + w * 0.18), (int) (h * 0.44));

            // Tray
            g2.drawLine((int) (w * 0.20), (int) (h * 0.78), (int) (w * 0.80), (int) (h * 0.78));
        });
    }

    /**
     * University building icon.
     */
    public static Icon university(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Triangular Roof
            GeneralPath roof = new GeneralPath();
            roof.moveTo(w * 0.12, h * 0.34);
            roof.lineTo(w * 0.50, h * 0.14);
            roof.lineTo(w * 0.88, h * 0.34);
            roof.closePath();
            g2.draw(roof);

            // Pillars (4 pillars)
            g2.drawLine((int)(w * 0.24), (int)(h * 0.36), (int)(w * 0.24), (int)(h * 0.72));
            g2.drawLine((int)(w * 0.42), (int)(h * 0.36), (int)(w * 0.42), (int)(h * 0.72));
            g2.drawLine((int)(w * 0.58), (int)(h * 0.36), (int)(w * 0.58), (int)(h * 0.72));
            g2.drawLine((int)(w * 0.76), (int)(h * 0.36), (int)(w * 0.76), (int)(h * 0.72));

            // Base
            g2.drawLine((int)(w * 0.14), (int)(h * 0.74), (int)(w * 0.86), (int)(h * 0.74));
            g2.drawLine((int)(w * 0.08), (int)(h * 0.82), (int)(w * 0.92), (int)(h * 0.82));
        });
    }

    /**
     * Coins / Affordable tuition icon.
     */
    public static Icon coins(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int r = (int) (w * 0.28);
            // Front coin
            g2.drawOval((int)(w * 0.24), (int)(h * 0.24), r * 2, r * 2);
            // Back coin curve
            g2.drawArc((int)(w * 0.40), (int)(h * 0.12), r * 2, r * 2, 45, 180);
        });
    }

    /**
     * Document checklist icon.
     */
    public static Icon documentCheck(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Document outline
            g2.drawRoundRect((int)(w * 0.20), (int)(h * 0.12), (int)(w * 0.60), (int)(h * 0.76), 4, 4);

            // Lines
            g2.drawLine((int)(w * 0.32), (int)(h * 0.30), (int)(w * 0.68), (int)(h * 0.30));
            g2.drawLine((int)(w * 0.32), (int)(h * 0.46), (int)(w * 0.68), (int)(h * 0.46));
            g2.drawLine((int)(w * 0.32), (int)(h * 0.62), (int)(w * 0.52), (int)(h * 0.62));
        });
    }

    /**
     * Pen / Edit icon.
     */
    public static Icon pen(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            GeneralPath p = new GeneralPath();
            p.moveTo(w * 0.70, h * 0.15);
            p.lineTo(w * 0.85, h * 0.30);
            p.lineTo(w * 0.35, h * 0.80);
            p.lineTo(w * 0.18, h * 0.82);
            p.lineTo(w * 0.20, h * 0.65);
            p.closePath();
            g2.draw(p);
        });
    }

    /**
     * Trash can icon.
     */
    public static Icon trash(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Can outline
            g2.drawRoundRect((int)(w * 0.26), (int)(h * 0.30), (int)(w * 0.48), (int)(h * 0.56), 3, 3);
            // Lid
            g2.drawLine((int)(w * 0.18), (int)(h * 0.30), (int)(w * 0.82), (int)(h * 0.30));
            g2.drawArc((int)(w * 0.38), (int)(h * 0.18), (int)(w * 0.24), (int)(h * 0.20), 0, 180);
            // Ribs
            g2.drawLine((int)(w * 0.42), (int)(h * 0.42), (int)(w * 0.42), (int)(h * 0.72));
            g2.drawLine((int)(w * 0.58), (int)(h * 0.42), (int)(w * 0.58), (int)(h * 0.72));
        });
    }

    /**
     * Balance scale icon for university comparisons.
     */
    public static Icon scale(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = w / 2;
            // Central beam
            g2.drawLine(cx, (int)(h * 0.16), cx, (int)(h * 0.82));
            // Base
            g2.drawLine((int)(w * 0.28), (int)(h * 0.82), (int)(w * 0.72), (int)(h * 0.82));

            // Top crossbar
            g2.drawLine((int)(w * 0.16), (int)(h * 0.28), (int)(w * 0.84), (int)(h * 0.28));

            // Left pan strings & pan
            g2.drawLine((int)(w * 0.16), (int)(h * 0.28), (int)(w * 0.10), (int)(h * 0.56));
            g2.drawLine((int)(w * 0.16), (int)(h * 0.28), (int)(w * 0.24), (int)(h * 0.56));
            g2.drawArc((int)(w * 0.08), (int)(h * 0.50), (int)(w * 0.18), (int)(h * 0.16), 180, 180);

            // Right pan strings & pan
            g2.drawLine((int)(w * 0.84), (int)(h * 0.28), (int)(w * 0.76), (int)(h * 0.56));
            g2.drawLine((int)(w * 0.84), (int)(h * 0.28), (int)(w * 0.90), (int)(h * 0.56));
            g2.drawArc((int)(w * 0.74), (int)(h * 0.50), (int)(w * 0.18), (int)(h * 0.16), 180, 180);
        });
    }

    /**
     * Internal functional interface to draw vector graphics.
     */
    @FunctionalInterface
    public interface VectorDrawer {
        void draw(Graphics2D g2, int width, int height);
    }

    private static class VectorIcon implements Icon {
        private final int width;
        private final int height;
        private final VectorDrawer drawer;

        public VectorIcon(int width, int height, VectorDrawer drawer) {
            this.width = width;
            this.height = height;
            this.drawer = drawer;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            drawer.draw(g2, width, height);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return width; }

        @Override
        public int getIconHeight() { return height; }
    }
}
