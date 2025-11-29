package com.JPresent;

import com.JPresent.ui.NavigationWindow;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NavigationWindow navigationWindow = new NavigationWindow();
            navigationWindow.setVisible(true);
        });
    }
}
