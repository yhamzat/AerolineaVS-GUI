package com.aerolineavs.tarifas;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Punto de entrada de la aplicación de escritorio.
 */
public final class PricingGuiApp {

    private PricingGuiApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException
                    | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            }

            IPricingService pricingService = new PricingServiceImpl();
            new PricingFrame(pricingService).setVisible(true);
        });
    }
}