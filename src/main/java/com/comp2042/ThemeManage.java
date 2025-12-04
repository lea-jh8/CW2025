package com.comp2042;

public class ThemeManage {
    private static String currentBackground = "/backgrounds/default.png";

    public static String getCurrentBackground() {
        return currentBackground;
    }

    public static void setBackground(String newBackground) {
        currentBackground = newBackground;
    }
}
