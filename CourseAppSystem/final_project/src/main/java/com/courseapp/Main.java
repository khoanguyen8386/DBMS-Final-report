package com.courseapp;

import com.courseapp.ui.LoginFrame;
import com.courseapp.ui.UITheme;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel for native OS styling
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default
        }

        UITheme.applyGlobalDefaults();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
