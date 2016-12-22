package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.the_sinner.unn4m3d.klauncher.Config;
import net.the_sinner.unn4m3d.klauncher.MainClassKt;
import net.the_sinner.unn4m3d.klauncher.api.*;
import net.the_sinner.unn4m3d.klauncher.components.Crypt;
import net.the_sinner.unn4m3d.klauncher.components.UtilsKt;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import static net.the_sinner.unn4m3d.klauncher.components.JavaMapKt.javaMap;

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

    private List<ShortServerData> servers;
    private API api = APIKt.getApiInstance();

    @FXML
    public void initialize()
    {
        try {
            if (
                    MainClassKt.getConfig().getOpt("remember",false) &&
                    MainClassKt.getConfig().getOpt("password", "") != "") {
                try {
                    passwordField.setText(
                            Crypt.decrypt(
                                    MainClassKt.getConfig().getOpt("password",""),
                            UtilsKt.padRight(Config.PROTECTION_KEY,16,'-'))
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                loginField.setText(MainClassKt.getConfig().<String>getOpt("username",""));
            }

            servers = api.servers();
            serverBox.setItems(FXCollections.observableList(javaMap(servers,(ShortServerData s) -> s.getName())));
            serverBox.setValue(servers.get(0).getName());
            statusLabel.setText("Готово");
        } catch (APIException e) {
            e.printStackTrace();
            statusLabel.setText("[" + e.getErrorType().toUpperCase() + "] " + e.getError());
            JOptionPane.showMessageDialog(null,e.getError(),e.getErrorType().toUpperCase(),0);
        }
    }

    public void onLogin(MouseEvent evt)
    {
        //API api = new API(Config.API_URL);
        if(serverBox.getValue() == null)
        {
            statusLabel.setText("[LOGIN] Выберите сервер");
            return;
        }
        System.out.println("onLogin");
        try {
            SessionData data = api.auth(loginField.getText(),passwordField.getText(),Config.VERSION);

            if(MainClassKt.getConfig().<Boolean>getOpt("remember",false))
            {
                try {
                    MainClassKt.getConfig().set("password",
                            Crypt.encrypt(passwordField.getText(),
                                    UtilsKt.padRight(Config.PROTECTION_KEY,16,'*')));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MainClassKt.getConfig().set("username", loginField.getText());
                MainClassKt.getConfig().save();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdaterForm.fxml"));
            Parent root = loader.load();
            ShortServerData serv = servers.get(serverBox.getSelectionModel().getSelectedIndex());
            UpdaterController ctrl = loader.getController();
            ctrl.setData(data,serv.getShortName(),serv.getVersion());
            Stage stage = new Stage();
            stage.setTitle("KLauncher");
            stage.setScene(new Scene(root));
            stage.show();
            ctrl._initialize();
            ((Node)evt.getSource()).getScene().getWindow().hide();
        } catch (APIException e) {
            statusLabel.setText("[" + e.getErrorType().toUpperCase() + "] " + e.getError());
            JOptionPane.showMessageDialog(null,e.getError(),e.getErrorType().toUpperCase(),0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSettings(MouseEvent evt)
    {
        FXMLLoader ldr = new FXMLLoader(getClass().getResource("SettingsForm.fxml"));
        try {
            Parent root = ldr.load();
            SettingsController ctrl = ldr.getController();
            Node self = (Node)evt.getSource();
            ctrl.setNode(self);
            self.getScene().getWindow().hide();
            Stage st = new Stage();
            st.setTitle("Settings");
            st.setScene(new Scene(root));
            st.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
