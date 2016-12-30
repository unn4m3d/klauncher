package net.the_sinner.unn4m3d.klauncher.gui;

import com.sun.webkit.WebPage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.the_sinner.unn4m3d.klauncher.Config;
import net.the_sinner.unn4m3d.klauncher.MainClassKt;
import net.the_sinner.unn4m3d.klauncher.api.*;
import net.the_sinner.unn4m3d.klauncher.components.Crypt;
import net.the_sinner.unn4m3d.klauncher.components.UtilsKt;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
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

    @FXML
    private WebView newsView;

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
            System.out.println("Got " + servers.size() + " servers");
            serverBox.setItems(FXCollections.observableList(javaMap(servers,(ShortServerData s) -> s.getName())));
            System.out.println("Set servers");
            serverBox.setValue(servers.get(0).getName());
            newsView.getEngine().documentProperty().addListener((nv, o, n) -> {
                try {
                    Field f = newsView.getEngine().getClass().getDeclaredField("page");
                    f.setAccessible(true);
                    WebPage page = (WebPage)f.get(newsView.getEngine());
                    page.setBackgroundColor(new Color(0,0,0,0).getRGB());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    String text = api.news();
                    System.out.println("News loaded");
                    //System.out.println(text);
                    Platform.runLater(() -> newsView.getEngine().loadContent(text));
                    return null;
                }
            };

            new Thread(task).start();
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
            if(e.getErrorType().equals("launcher"))
            {
                int reply = JOptionPane.showConfirmDialog(
                        null,
                        "Вы используете устаревшую версию лаунчера. Для продолжения использования необходимо скачать новую версию. Начать загрузку?",
                        "Лаунчер устарел",
                        JOptionPane.YES_NO_OPTION
                );

                if(reply == JOptionPane.OK_OPTION)
                {
                    FXMLLoader ldr = new FXMLLoader(getClass().getResource("LauncherForm.fxml"));
                    try {
                        Parent root = ldr.load();
                        Stage s = new Stage();
                        s.setTitle("Launcher update");
                        s.setScene(new Scene(root));
                        s.show();
                        ((Node)evt.getSource()).getScene().getWindow().hide();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
                else
                {
                    System.exit(0);
                }
            }
            else
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
