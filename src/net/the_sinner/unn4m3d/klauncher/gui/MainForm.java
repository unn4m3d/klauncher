package net.the_sinner.unn4m3d.klauncher.gui;

import javax.swing.*;

/**
 * Created by unn4m3d on 13.12.16.
 */
public class MainForm {
    private JTextField loginField;
    private JComboBox serverBox;
    private JButton loginButton;
    private JButton settingsButton;
    private JProgressBar loadBar;
    private JPasswordField passwordField;
    private JPanel statusPanel;
    private JLabel serverLabel;
    private JLabel statusLabel;
    private JPanel mainPanel;

    public MainForm()
    {
        JFrame frame = new JFrame("KLauncher by unn4m3d");

        frame.setContentPane(this.mainPanel);
    }
}
