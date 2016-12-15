package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.the_sinner.unn4m3d.klauncher.Config;
import net.the_sinner.unn4m3d.klauncher.api.API;
import net.the_sinner.unn4m3d.klauncher.api.APIException;
import net.the_sinner.unn4m3d.klauncher.api.SessionData;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by unn4m3d on 15.12.16.
 */
public class MainController {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> serverBox;

    @FXML
    private Button loginButton;

    @FXML
    private  Button settingsButton;

    public void onLogin(MouseEvent evt)
    {
        API api = new API(Config.API_URL);
        System.out.println("onLogin");
        try {
            SessionData data = api.auth(loginField.getText(),passwordField.getText(),Config.VERSION);
            Parent root = FXMLLoader.load(getClass().getResource("UpdaterForm.fxml"));
            Stage stage = new Stage();
            stage.setTitle("KLauncher");
            stage.setScene(new Scene(root));
            stage.show();
            ((Node)evt.getSource()).getScene().getWindow().hide();
        } catch (APIException e) {
            statusLabel.setText("[" + e.getErrorType().toUpperCase() + "] " + e.getError());
            JOptionPane.showMessageDialog(null,e.getError(),e.getErrorType().toUpperCase(),0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
