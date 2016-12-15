package net.the_sinner.unn4m3d.klauncher.gui;

import net.the_sinner.unn4m3d.klauncher.Config;
import net.the_sinner.unn4m3d.klauncher.api.API;
import net.the_sinner.unn4m3d.klauncher.api.APIException;
import net.the_sinner.unn4m3d.klauncher.api.SessionData;
import net.the_sinner.unn4m3d.klauncher.api.ShortServerData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import  java.util.List;

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
    private ShortServerData current;

    public MainForm(API api)
    {
        JFrame frame = new JFrame("KLauncher by unn4m3d");
        Color color = statusLabel.getForeground();
        frame.setContentPane(this.mainPanel);


        List<ShortServerData> servers;
        try {
            servers = api.servers();
        } catch (APIException e) {
            servers = new ArrayList<>();
            JOptionPane.showMessageDialog(frame,null,e.getError(),0);
            statusLabel.setText("[" + e.getErrorType().toUpperCase() + "] : " + e.getError());
            statusLabel.setForeground(Color.RED);
        }

        final List<ShortServerData> _serv = servers;

        for(ShortServerData serv : servers){
            serverBox.addItem(serv.getName());
            serverBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    current = _serv.get(itemEvent.getStateChange());
                }
            });
        }

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try{
                    statusLabel.setForeground(color);
                    statusLabel.setText("Loading...");
                    SessionData data = api.auth(loginField.getText(),new String(passwordField.getPassword()), Config.VERSION);
                    statusLabel.setText("Done");
                    // TODO : Call updater form
                } catch(APIException ex) {
                    JOptionPane.showMessageDialog(frame,null,ex.getError(),0);
                    statusLabel.setText("[" + ex.getErrorType().toUpperCase() + "] : " + ex.getError());
                    statusLabel.setForeground(Color.RED);
                }
            }
        });
    }
}
