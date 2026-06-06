package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class ThemeManager {

    public enum Theme {
        DARK, LIGHT, AVIATION
    }

    private static Theme currentTheme = Theme.DARK;

    // ─── Color Palettes ───────────────────────────────────────────────────────

    public static Color getBg()          { return switch(currentTheme) {
        case DARK     -> new Color(18, 18, 28);
        case LIGHT    -> new Color(245, 247, 250);
        case AVIATION -> new Color(10, 25, 55);
    };}
    public static Color getPanelBg()     { return switch(currentTheme) {
        case DARK     -> new Color(28, 28, 45);
        case LIGHT    -> new Color(255, 255, 255);
        case AVIATION -> new Color(15, 35, 70);
    };}
    public static Color getAccent()      { return switch(currentTheme) {
        case DARK     -> new Color(99, 102, 241);
        case LIGHT    -> new Color(79, 70, 229);
        case AVIATION -> new Color(0, 150, 255);
    };}
    public static Color getText()        { return switch(currentTheme) {
        case DARK     -> new Color(236, 237, 255);
        case LIGHT    -> new Color(30, 30, 50);
        case AVIATION -> new Color(220, 235, 255);
    };}
    public static Color getSubText()     { return switch(currentTheme) {
        case DARK     -> new Color(140, 140, 180);
        case LIGHT    -> new Color(100, 100, 130);
        case AVIATION -> new Color(150, 180, 220);
    };}
    public static Color getTableHeader() { return switch(currentTheme) {
        case DARK     -> new Color(40, 40, 65);
        case LIGHT    -> new Color(230, 232, 245);
        case AVIATION -> new Color(20, 50, 100);
    };}
    public static Color getTableRow1()   { return switch(currentTheme) {
        case DARK     -> new Color(32, 32, 50);
        case LIGHT    -> new Color(255, 255, 255);
        case AVIATION -> new Color(18, 42, 85);
    };}
    public static Color getTableRow2()   { return switch(currentTheme) {
        case DARK     -> new Color(26, 26, 42);
        case LIGHT    -> new Color(248, 249, 255);
        case AVIATION -> new Color(14, 35, 72);
    };}
    public static Color getBorder()      { return switch(currentTheme) {
        case DARK     -> new Color(55, 55, 80);
        case LIGHT    -> new Color(210, 213, 230);
        case AVIATION -> new Color(30, 70, 130);
    };}

    // Status colors (same across themes, just brightness adjusted)
    public static Color getScheduled()   { return new Color(34, 197, 94, 60); }
    public static Color getInAir()       { return new Color(99, 102, 241, 60); }
    public static Color getLanded()      { return new Color(148, 163, 184, 60); }
    public static Color getDelayed()     { return new Color(251, 191, 36, 60); }
    public static Color getCancelled()   { return new Color(239, 68, 68, 60); }

    public static Theme getTheme()       { return currentTheme; }
    public static void setTheme(Theme t) { currentTheme = t; }

    public static Font getFont(int size, int style) {
        return new Font("Segoe UI", style, size);
    }

    public static Border titledBorder(String title) {
        javax.swing.border.TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(getBorder(), 1), title);
        tb.setTitleColor(getSubText());
        tb.setTitleFont(getFont(11, Font.BOLD));
        return tb;
    }
}
