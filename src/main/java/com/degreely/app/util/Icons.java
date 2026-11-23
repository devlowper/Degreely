package com.degreely.app.util;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * Modern Lucide-spec 24x24 vector icon library for Degreely.
 * Clean, proportional, pixel-perfect rendering across any DPI and display scale.
 */
public final class Icons {

    private Icons() {}

    /**
     * User profile silhouette icon (Lucide user).
     */
    public static Icon user(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.draw(new Ellipse2D.Double(8, 3, 8, 8));
            Path2D.Double body = new Path2D.Double();
            body.moveTo(4, 21);
            body.lineTo(4, 19);
            body.curveTo(4, 16.8, 5.8, 15, 8, 15);
            body.lineTo(16, 15);
            body.curveTo(18.2, 15, 20, 16.8, 20, 19);
            body.lineTo(20, 21);
            g2.draw(body);
        });
    }

    /**
     * Settings 8-cog gear icon (Lucide settings).
     */
    public static Icon settings(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.draw(new Ellipse2D.Double(9, 9, 6, 6));

            Path2D.Double cog = new Path2D.Double();
            for (int i = 0; i < 8; i++) {
                double baseAngle = i * (Math.PI / 4.0);
                double a0 = baseAngle - 0.16;
                double a1 = baseAngle - 0.08;
                double a2 = baseAngle + 0.08;
                double a3 = baseAngle + 0.16;
                double rIn = 7.8;
                double rOut = 10.5;

                double x0 = 12 + Math.cos(a0) * rIn;
                double y0 = 12 + Math.sin(a0) * rIn;
                double x1 = 12 + Math.cos(a1) * rOut;
                double y1 = 12 + Math.sin(a1) * rOut;
                double x2 = 12 + Math.cos(a2) * rOut;
                double y2 = 12 + Math.sin(a2) * rOut;
                double x3 = 12 + Math.cos(a3) * rIn;
                double y3 = 12 + Math.sin(a3) * rIn;

                if (i == 0) {
                    cog.moveTo(x0, y0);
                } else {
                    cog.lineTo(x0, y0);
                }
                cog.lineTo(x1, y1);
                cog.lineTo(x2, y2);
                cog.lineTo(x3, y3);
            }
            cog.closePath();
            g2.draw(cog);
        });
    }

    /**
     * Search magnifying glass icon (Lucide search).
     */
    public static Icon search(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.draw(new Ellipse2D.Double(3.5, 3.5, 14.5, 14.5));
            Path2D.Double p = new Path2D.Double();
            p.moveTo(16.5, 16.5);
            p.lineTo(21, 21);
            g2.draw(p);
        });
    }

    /**
     * Paperclip attachment icon (Lucide paperclip).
     */
    public static Icon paperclip(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(21.44, 11.05);
            p.lineTo(12.25, 20.24);
            p.curveTo(9.91, 22.58, 6.11, 22.58, 3.76, 20.24);
            p.curveTo(1.42, 17.89, 1.42, 14.09, 3.76, 11.75);
            p.lineTo(12.95, 2.56);
            p.curveTo(14.51, 1.0, 17.04, 1.0, 18.61, 2.56);
            p.curveTo(20.17, 4.12, 20.17, 6.66, 18.61, 8.22);
            p.lineTo(9.41, 17.41);
            p.curveTo(8.63, 18.19, 7.37, 18.19, 6.59, 17.41);
            p.curveTo(5.81, 16.63, 5.81, 15.37, 6.59, 14.59);
            p.lineTo(15.08, 6.1);
            g2.draw(p);
        });
    }

    /**
     * Sparkles AI icon (Lucide sparkles).
     */
    public static Icon spark(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            // Main 4-point star
            Path2D.Double star = new Path2D.Double();
            star.moveTo(12, 2);
            star.quadTo(12, 12, 22, 12);
            star.quadTo(12, 12, 12, 22);
            star.quadTo(12, 12, 2, 12);
            star.quadTo(12, 12, 12, 2);
            star.closePath();
            g2.fill(star);

            // Small satellite sparkle (top right)
            Path2D.Double s2 = new Path2D.Double();
            s2.moveTo(19, 2);
            s2.quadTo(19, 5, 22, 5);
            s2.quadTo(19, 5, 19, 8);
            s2.quadTo(19, 5, 16, 5);
            s2.quadTo(19, 5, 19, 2);
            s2.closePath();
            g2.fill(s2);
        });
    }

    /**
     * Speech chat bubble icon (Lucide message-square).
     */
    public static Icon chat(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(21, 15);
            p.curveTo(21, 16.1, 20.1, 17, 19, 17);
            p.lineTo(7, 17);
            p.lineTo(3, 21);
            p.lineTo(3, 5);
            p.curveTo(3, 3.9, 3.9, 3, 5, 3);
            p.lineTo(19, 3);
            p.curveTo(20.1, 3, 21, 3.9, 21, 5);
            p.closePath();
            g2.draw(p);
        });
    }

    /**
     * Pushpin icon (Lucide pin).
     */
    public static Icon pin(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(12, 17);
            p.lineTo(12, 22);
            p.moveTo(5, 17);
            p.lineTo(19, 17);
            p.moveTo(7, 17);
            p.lineTo(9, 11);
            p.lineTo(8, 4);
            p.lineTo(16, 4);
            p.lineTo(15, 11);
            p.lineTo(17, 17);
            g2.draw(p);
        });
    }

    /**
     * Copy dual-sheet icon (Lucide copy).
     */
    public static Icon copy(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double back = new Path2D.Double();
            back.moveTo(5, 15);
            back.lineTo(4, 15);
            back.curveTo(2.9, 15, 2, 14.1, 2, 13);
            back.lineTo(2, 4);
            back.curveTo(2, 2.9, 2.9, 2, 4, 2);
            back.lineTo(13, 2);
            back.curveTo(14.1, 2, 15, 2.9, 15, 4);
            back.lineTo(15, 5);
            g2.draw(back);

            Path2D.Double front = new Path2D.Double();
            front.moveTo(9, 9);
            front.lineTo(20, 9);
            front.curveTo(21.1, 9, 22, 9.9, 22, 11);
            front.lineTo(22, 20);
            front.curveTo(22, 21.1, 21.1, 22, 20, 22);
            front.lineTo(11, 22);
            front.curveTo(9.9, 22, 9, 21.1, 9, 20);
            front.closePath();
            g2.draw(front);
        });
    }

    /**
     * Circular refresh / regenerate icon (Lucide rotate-ccw).
     */
    public static Icon refresh(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(3, 2);
            p.lineTo(3, 8);
            p.lineTo(9, 8);
            g2.draw(p);

            Arc2D.Double arc = new Arc2D.Double(3.5, 3.5, 17, 17, 150, -290, Arc2D.OPEN);
            g2.draw(arc);
        });
    }

    /**
     * Download / Export icon (Lucide download).
     */
    public static Icon download(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double tray = new Path2D.Double();
            tray.moveTo(21, 15);
            tray.lineTo(21, 19);
            tray.curveTo(21, 20.1, 20.1, 21, 19, 21);
            tray.lineTo(5, 21);
            tray.curveTo(3.9, 21, 3, 20.1, 3, 19);
            tray.lineTo(3, 15);
            g2.draw(tray);

            Path2D.Double arrow = new Path2D.Double();
            arrow.moveTo(7, 10);
            arrow.lineTo(12, 15);
            arrow.lineTo(17, 10);
            arrow.moveTo(12, 15);
            arrow.lineTo(12, 3);
            g2.draw(arrow);
        });
    }

    /**
     * Home house icon (Lucide home).
     */
    public static Icon home(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double roof = new Path2D.Double();
            roof.moveTo(3, 9);
            roof.lineTo(12, 2.5);
            roof.lineTo(21, 9);
            roof.lineTo(21, 20.5);
            roof.curveTo(21, 21.3, 20.3, 22, 19.5, 22);
            roof.lineTo(4.5, 22);
            roof.curveTo(3.7, 22, 3, 21.3, 3, 20.5);
            roof.closePath();
            g2.draw(roof);

            Path2D.Double door = new Path2D.Double();
            door.moveTo(9, 22);
            door.lineTo(9, 12);
            door.lineTo(15, 12);
            door.lineTo(15, 22);
            g2.draw(door);
        });
    }

    /**
     * University graduation cap icon (Lucide graduation-cap).
     */
    public static Icon university(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double cap = new Path2D.Double();
            cap.moveTo(22, 10);
            cap.lineTo(12, 5);
            cap.lineTo(2, 10);
            cap.lineTo(12, 15);
            cap.closePath();
            g2.draw(cap);

            Path2D.Double band = new Path2D.Double();
            band.moveTo(6, 12);
            band.lineTo(6, 17);
            band.curveTo(6, 19.8, 18, 19.8, 18, 17);
            band.lineTo(18, 12);
            g2.draw(band);

            Path2D.Double tassel = new Path2D.Double();
            tassel.moveTo(22, 10);
            tassel.lineTo(22, 16);
            g2.draw(tassel);
        });
    }

    /**
     * Coins / Affordable tuition icon (Lucide coins).
     */
    public static Icon coins(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.draw(new Ellipse2D.Double(4, 7, 12, 12));
            Arc2D.Double back = new Arc2D.Double(8, 3, 12, 12, 280, 160, Arc2D.OPEN);
            g2.draw(back);
        });
    }

    /**
     * Document checklist icon (Lucide file-check-2).
     */
    public static Icon documentCheck(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double doc = new Path2D.Double();
            doc.moveTo(4, 21);
            doc.lineTo(4, 5);
            doc.curveTo(4, 3.9, 4.9, 3, 6, 3);
            doc.lineTo(14, 3);
            doc.lineTo(20, 9);
            doc.lineTo(20, 21);
            doc.curveTo(20, 22.1, 19.1, 23, 18, 23);
            doc.lineTo(6, 23);
            doc.curveTo(4.9, 23, 4, 22.1, 4, 21);
            g2.draw(doc);

            Path2D.Double fold = new Path2D.Double();
            fold.moveTo(14, 3);
            fold.lineTo(14, 9);
            fold.lineTo(20, 9);
            g2.draw(fold);

            Path2D.Double chk = new Path2D.Double();
            chk.moveTo(8.5, 15.5);
            chk.lineTo(11, 18);
            chk.lineTo(15.5, 13);
            g2.draw(chk);
        });
    }

    /**
     * Pencil / Edit icon (Lucide pencil).
     */
    public static Icon pen(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(17, 3);
            p.curveTo(17.8, 2.2, 19.2, 2.2, 20, 3);
            p.curveTo(20.8, 3.8, 20.8, 5.2, 20, 6);
            p.lineTo(7, 19);
            p.lineTo(3, 21);
            p.lineTo(5, 17);
            p.closePath();
            g2.draw(p);
        });
    }

    /**
     * Trash can icon (Lucide trash-2).
     */
    public static Icon trash(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(3, 6);
            p.lineTo(21, 6);
            p.moveTo(19, 6);
            p.lineTo(18.2, 19.2);
            p.curveTo(18.1, 20.2, 17.3, 21, 16.3, 21);
            p.lineTo(7.7, 21);
            p.curveTo(6.7, 21, 5.9, 20.2, 5.8, 19.2);
            p.lineTo(5, 6);
            p.moveTo(8, 6);
            p.lineTo(8, 4);
            p.curveTo(8, 3.4, 8.4, 3, 9, 3);
            p.lineTo(15, 3);
            p.curveTo(15.6, 3, 16, 3.4, 16, 4);
            p.lineTo(16, 6);
            p.moveTo(10, 11);
            p.lineTo(10, 17);
            p.moveTo(14, 11);
            p.lineTo(14, 17);
            g2.draw(p);
        });
    }

    /**
     * Balance scale icon for university comparisons (Lucide scale).
     */
    public static Icon scale(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            // Center pillar & base
            Path2D.Double center = new Path2D.Double();
            center.moveTo(12, 3);
            center.lineTo(12, 21);
            center.moveTo(7, 21);
            center.lineTo(17, 21);
            center.moveTo(4, 7);
            center.lineTo(20, 7);
            g2.draw(center);

            // Left pan strings & dish
            Path2D.Double left = new Path2D.Double();
            left.moveTo(4, 7);
            left.lineTo(1.5, 13);
            left.moveTo(4, 7);
            left.lineTo(6.5, 13);
            left.moveTo(1, 13);
            left.curveTo(1, 16.5, 7, 16.5, 7, 13);
            left.closePath();
            g2.draw(left);

            // Right pan strings & dish
            Path2D.Double right = new Path2D.Double();
            right.moveTo(20, 7);
            right.lineTo(17.5, 13);
            right.moveTo(20, 7);
            right.lineTo(22.5, 13);
            right.moveTo(17, 13);
            right.curveTo(17, 16.5, 23, 16.5, 23, 13);
            right.closePath();
            g2.draw(right);
        });
    }

    /**
     * Plus icon (Lucide plus).
     */
    public static Icon plus(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(12, 5);
            p.lineTo(12, 19);
            p.moveTo(5, 12);
            p.lineTo(19, 12);
            g2.draw(p);
        });
    }

    /**
     * Plus in filled circle (Claude-style New Chat button icon).
     */
    public static Icon plusCircle(int size, Color bg, Color fg) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(bg);
            g2.fill(new Ellipse2D.Double(1.5, 1.5, 21, 21));
            g2.setColor(fg);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D.Double p = new Path2D.Double();
            p.moveTo(12, 7);
            p.lineTo(12, 17);
            p.moveTo(7, 12);
            p.lineTo(17, 12);
            g2.draw(p);
        });
    }

    /**
     * Upward Send Arrow (Lucide arrow-up / Claude send icon).
     */
    public static Icon send(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(12, 19);
            p.lineTo(12, 5);
            p.moveTo(5, 12);
            p.lineTo(12, 5);
            p.lineTo(19, 12);
            g2.draw(p);
        });
    }

    /**
     * Filled square stop generation icon (Lucide square).
     */
    public static Icon stop(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.fillRoundRect(6, 6, 12, 12, 3, 3);
        });
    }

    /**
     * Downward arrow icon (Lucide arrow-down).
     */
    public static Icon arrowDown(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(12, 5);
            p.lineTo(12, 19);
            p.moveTo(19, 12);
            p.lineTo(12, 19);
            p.lineTo(5, 12);
            g2.draw(p);
        });
    }

    /**
     * Three horizontal action dots (Lucide more-horizontal).
     */
    public static Icon moreHorizontal(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.fill(new Ellipse2D.Double(3.5, 10.5, 3.0, 3.0));
            g2.fill(new Ellipse2D.Double(10.5, 10.5, 3.0, 3.0));
            g2.fill(new Ellipse2D.Double(17.5, 10.5, 3.0, 3.0));
        });
    }

    /**
     * Success checkmark (Lucide check).
     */
    public static Icon check(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(20, 6);
            p.lineTo(9, 17);
            p.lineTo(4, 12);
            g2.draw(p);
        });
    }

    /**
     * Globe icon (Lucide globe).
     */
    public static Icon globe(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.draw(new Ellipse2D.Double(2, 2, 20, 20));
            Path2D.Double lat = new Path2D.Double();
            lat.moveTo(2, 12);
            lat.lineTo(22, 12);
            g2.draw(lat);
            g2.draw(new Ellipse2D.Double(6.5, 2, 11, 20));
        });
    }

    /**
     * Map Pin icon (Lucide map-pin).
     */
    public static Icon mapPin(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Double p = new Path2D.Double();
            p.moveTo(20, 10);
            p.curveTo(20, 16, 12, 22, 12, 22);
            p.curveTo(12, 22, 4, 16, 4, 10);
            p.curveTo(4, 5.6, 7.6, 2, 12, 2);
            p.curveTo(16.4, 2, 20, 5.6, 20, 10);
            p.closePath();
            g2.draw(p);
            g2.draw(new Ellipse2D.Double(9, 7, 6, 6));
        });
    }

    /**
     * Internal functional interface to draw vector graphics on a 24x24 normalized viewport.
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
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // Scale normalized 24x24 coordinate system into target width/height
            double scale = Math.min(width, height) / 24.0;
            double offsetX = (width - 24.0 * scale) / 2.0;
            double offsetY = (height - 24.0 * scale) / 2.0;
            g2.translate(offsetX, offsetY);
            g2.scale(scale, scale);

            // Default stroke: 2px rounded stroke on standard 24x24 viewport
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawer.draw(g2, 24, 24);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return width; }

        @Override
        public int getIconHeight() { return height; }
    }
}
