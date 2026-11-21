package com.degreely.app.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

public final class Theme {

    private Theme() {}

    // Signature Brand Colors (Warm Academic Orange)
    public static final Color PRIMARY = Color.decode("#E37712");
    public static final Color PRIMARY_HOVER = Color.decode("#C2410C");
    public static final Color PRIMARY_LIGHT = Color.decode("#FFF7ED");
    public static final Color PRIMARY_BORDER = Color.decode("#FFEDD5");

    // Modern Neutral Ramp (Tailwind Slate)
    public static final Color TEXT = Color.decode("#0F172A");          // Slate 900
    public static final Color TEXT_SECONDARY = Color.decode("#475569");// Slate 600
    public static final Color TEXT_MUTED = Color.decode("#94A3B8");    // Slate 400

    // Canvas & Elevated Surfaces
    public static final Color BACKGROUND = Color.decode("#F8FAFC");    // Slate 50
    public static final Color CARD_BG = Color.decode("#FFFFFF");       // Pure White
    public static final Color CARD_HOVER = Color.decode("#FAFAFA");
    public static final Color BORDER = Color.decode("#E2E8F0");        // Slate 200

    // Semantic Color Badges & Alerts
    public static final Color LIGHT_BLUE_HIGHLIGHT = Color.decode("#EFF6FF");
    public static final Color BLUE_TEXT = Color.decode("#1D4ED8");

    public static final Color GREEN_BG = Color.decode("#DCFCE7");
    public static final Color GREEN_BORDER = Color.decode("#86EFAC");
    public static final Color GREEN_TEXT = Color.decode("#15803D");

    public static final Color RED_BG = Color.decode("#FEE2E2");
    public static final Color RED_BORDER = Color.decode("#FCA5A5");
    public static final Color RED_TEXT = Color.decode("#B91C1C");

    public static final Color AMBER_BG = Color.decode("#FEF3C7");
    public static final Color AMBER_TEXT = Color.decode("#B45309");

    // Geometric Sizing & Radius
    public static final int RADIUS = 10;
    public static final int BUTTON_PAD_Y = 10;
    public static final int BUTTON_PAD_X = 18;

    // Spacing Scale (4px grid)
    public static final int SPACING_4 = 4;
    public static final int SPACING_8 = 8;
    public static final int SPACING_12 = 12;
    public static final int SPACING_16 = 16;
    public static final int SPACING_24 = 24;
    public static final int SPACING_32 = 32;

    // Font Family resolution (Inter -> Segoe UI -> SansSerif)
    private static final String FONT_FAMILY = resolveFontFamily();

    private static String resolveFontFamily() {
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        boolean hasInter = Arrays.asList(availableFonts).contains("Inter");
        if (hasInter) {
            return "Inter";
        }
        boolean hasSegoe = Arrays.asList(availableFonts).contains("Segoe UI");
        if (hasSegoe) {
            return "Segoe UI";
        }
        return Font.SANS_SERIF;
    }

    // Typography Scale
    public static final Font FONT_HERO = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SECTION = new Font(FONT_FAMILY, Font.BOLD, 17);
    public static final Font FONT_BODY_LARGE = new Font(FONT_FAMILY, Font.PLAIN, 15);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);
}
