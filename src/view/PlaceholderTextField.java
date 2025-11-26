package view;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
    }

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
            return;
        }

        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getDisabledTextColor());

        // Calculate vertical centering
        int height = getHeight();
        FontMetrics fm = g2.getFontMetrics();
        int textHeight = fm.getAscent();
        int y = (height + textHeight) / 2 - 2; // -2 for slight visual adjustment

        // Add some left padding (standard is usually around 2-4px + insets)
        int x = getInsets().left;

        g2.drawString(placeholder, x, y);
    }
}
