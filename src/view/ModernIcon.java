package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class ModernIcon implements Icon {
    public enum IconType {
        ADD, EDIT, DELETE, EXPORT, HISTORY, RECYCLE_BIN, EXIT, USERS
    }

    private final IconType type;
    private final int size;
    private final Color color;

    public ModernIcon(IconType type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Center rendering
        g2.translate(x, y);

        switch (type) {
            case ADD:
                drawAdd(g2);
                break;
            case EDIT:
                drawEdit(g2);
                break;
            case DELETE:
                drawDelete(g2);
                break;
            case EXPORT:
                drawExport(g2);
                break;
            case HISTORY:
                drawHistory(g2);
                break;
            case RECYCLE_BIN:
                drawRecycleBin(g2);
                break;
            case EXIT:
                drawExit(g2);
                break;
            case USERS: // Placeholder if needed
                drawUsers(g2);
                break;
        }

        g2.dispose();
    }

    private void drawAdd(Graphics2D g2) {
        int pad = size / 4;
        g2.drawLine(size / 2, pad, size / 2, size - pad); // Vertical
        g2.drawLine(pad, size / 2, size - pad, size / 2); // Horizontal
    }

    private void drawEdit(Graphics2D g2) {
        // Simple pencil
        int pad = size / 5;
        // Body
        g2.drawLine(size - pad, pad, size / 2 + pad, size / 2 - pad);
        // Tip
        Path2D p = new Path2D.Float();
        p.moveTo(pad, size - pad);
        p.lineTo(pad + size / 4, size - pad);
        p.lineTo(size - pad, pad + size / 4);
        p.lineTo(size - pad - size / 4, pad);
        p.lineTo(pad, size - pad - size / 4);
        p.closePath();
        g2.draw(p);
    }

    private void drawDelete(Graphics2D g2) {
        // Red X
        int pad = size / 4;
        g2.drawLine(pad, pad, size - pad, size - pad);
        g2.drawLine(size - pad, pad, pad, size - pad);
        // Ensure bold stroke (handled by PaintIcon but can reinforce here if needed)
    }

    private void drawExport(Graphics2D g2) {
        // Spreadsheet Icon (Grid)
        int pad = size / 5;
        // Main box
        g2.drawRect(pad, pad, size - 2 * pad, size - 2 * pad);
        // Header line
        g2.drawLine(pad, pad + 6, size - pad, pad + 6);
        // Vertical separator (col)
        g2.drawLine(size / 2, pad + 6, size / 2, size - pad);
        // Horizontal separator (row)
        g2.drawLine(pad, size / 2 + 2, size - pad, size / 2 + 2);
    }

    private void drawHistory(Graphics2D g2) {
        // Clock
        int pad = size / 6;
        g2.drawOval(pad, pad, size - 2 * pad, size - 2 * pad);
        g2.drawLine(size / 2, size / 2, size / 2, pad + 2); // Hour
        g2.drawLine(size / 2, size / 2, size - pad - 2, size / 2); // Minute
    }

    private void drawRecycleBin(Graphics2D g2) {
        // Trash Can
        int pad = size / 5;
        int top = pad + 2;
        // Bin
        g2.drawLine(pad + 2, top, pad + 4, size - pad);
        g2.drawLine(size - pad - 4, size - pad, size - pad - 2, top);
        g2.drawLine(pad + 4, size - pad, size - pad - 4, size - pad); // Bottom
        // Lid
        g2.drawLine(pad, top, size - pad, top);
        // Handle (small arc or line on top)
        g2.drawArc(size / 2 - 2, top - 2, 4, 2, 0, 180);
    }

    private void drawExit(Graphics2D g2) {
        // Door or Arrow out
        int pad = size / 4;
        // Box
        g2.drawLine(pad, pad, pad, size - pad);
        g2.drawLine(pad, pad, size - pad, pad);
        g2.drawLine(pad, size - pad, size - pad, size - pad);
        // Arrow
        g2.drawLine(size / 2, size / 2, size - pad + 2, size / 2);
        g2.drawLine(size - pad - 2, size / 2 - 4, size - pad + 2, size / 2);
        g2.drawLine(size - pad - 2, size / 2 + 4, size - pad + 2, size / 2);
    }

    private void drawUsers(Graphics2D g2) {
        // Head
        int pad = size / 4;
        g2.drawOval(size / 2 - 3, pad, 6, 6);
        // Body
        g2.drawArc(pad, size / 2, size - 2 * pad, size / 2, 0, 180);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
