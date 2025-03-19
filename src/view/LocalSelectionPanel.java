package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LocalSelectionPanel extends JPanel {
    private MainApp mainApp;

    public LocalSelectionPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel label = new JLabel("Selecione o Local", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(20));

        JButton npdButton = new JButton("NPD");
        npdButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton infanButton = new JButton("INFAN");
        infanButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(npdButton);
        add(Box.createVerticalStrut(10));
        add(infanButton);

        npdButton.addActionListener(e -> mainApp.showInventoryPanel("NPD"));
        infanButton.addActionListener(e -> mainApp.showInventoryPanel("INFAN"));
    }
}
