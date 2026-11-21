package com.degreely.app.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

public final class Theme {

    private Theme() {}

    // Brand Colors
    public static final Color PRIMARY = Color.decode("#E37712");
    public static final Color PRIMARY_HOVER = Color.decode("#D2500E");

    // Text Colors
    public static final Color TEXT = Color.decode("#2B3340");
    public static final Color TEXT_SECONDARY = Color.decode("#697386");
    public static final Color TEXT_MUTED = Color.decode("#8C93A1");

    // Surface & Background Colors
    public static final Color BACKGROUND = Color.decode("#FCFCFC");
    public static final Color CARD_BG = Color.decode("#FFFFFF");
    public static final Color BORDER = Color.decode("#E5E7EB");
    public static final Color LIGHT_BLUE_HIGHLIGHT = Color.decode("#EFF6FF");

    // Dimensions & Radius
    public static final int RADIUS = 8;
    public static final int BUTTON_PAD_Y = 12;
    public static final int BUTTON_PAD_X = 20;

    // Spacing Scale
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

    // Typography
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_SECTION = new Font(FONT_FAMILY, Font.BOLD, 20);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BODY_LARGE = new Font(FONT_FAMILY, Font.PLAIN, 16);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 14);
}
