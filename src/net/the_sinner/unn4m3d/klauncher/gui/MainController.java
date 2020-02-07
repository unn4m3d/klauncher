package net.the_sinner.unn4m3d.klauncher.gui;

import com.sun.webkit.WebPage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.util.Callback;
import net.the_sinner.unn4m3d.klauncher.Config;
import net.the_sinner.unn4m3d.klauncher.MainClassKt;
import net.the_sinner.unn4m3d.klauncher.api.*;
import net.the_sinner.unn4m3d.klauncher.components.Crypt;
import net.the_sinner.unn4m3d.klauncher.components.UtilsKt;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import static net.the_sinner.unn4m3d.klauncher.components.JavaMapKt.javaMap;

/**
 * Created by unn4m3d on 15.12.16.
 */
public class MainController {
    @FXML
    private SplitPane pane;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<ShortServerData> serverBox;

    @FXML
    private Button loginButton;

    @FXML
    private  Button settingsButton;

    @FXML
    private WebView newsView;

    private List<ShortServerData> servers;
    private API api;

    @FXML
    public void initialize()
    {
        serverBox.setCellFactory(new Callback<ListView<ShortServerData>, ListCell<ShortServerData>>()
        {
            @Override
            public ListCell<ShortServerData> call(ListView<ShortServerData> shortServerDataListView)
            {
                return new ListCell<ShortServerData>(){

                    {
                        setContentDisplay(ContentDisplay.TEXT_ONLY);
                    }

                    @Override
                    protected void updateItem(ShortServerData data, boolean empty)
                    {
                        super.updateItem(data, empty);
                        if(empty || data == null)
                        {
                            setText("<empty>");
                        }
                        else
                        {
                            setText(data.getName());
                        }
                    }
                };
            }
        });
        pane.resize(pane.getPrefWidth(), pane.getPrefHeight());
    }

    public void postInitialize(Stage stage)
    {
        new Thread(()->{
            try {
                synchronized (this) {
                    this.api = new API(Config.API_URL);
                    this.api.initialize();
                }

                if (
                        MainClassKt.getConfig().getOpt("remember",false) &&
                                MainClassKt.getConfig().getOpt("password", "") != "") {

                        Platform.runLater(() -> {
                            try {
                                passwordField.setText(
                                    Crypt.xorStr(
                                            MainClassKt.getConfig().getOpt("password", ""),
                                            Config.PROTECTION_KEY )
                                );
                            } catch (Exception e) {
                                setError(e.getMessage(), "PWD");
                                e.printStackTrace();
                            }
                        });


                    Platform.runLater(() -> loginField.setText(MainClassKt.getConfig().getOpt("username","")));
                }
            } catch( APIException e) {
                setError(e.getError(), e.getErrorType());
                e.printStackTrace();
            } catch( Exception e) {
                setError("Cannot connect to API", "API");
                e.printStackTrace();
                return;
            } finally {
                Platform.runLater(() -> loginButton.setDisable(true));

                Platform.runLater(() -> stage.show());
            }

            setError("Loading", "...");

            synchronized (this) {
                try {
                    this.servers = api.servers();
                } catch ( APIException e) {
                    setError(e.getError(), e.getErrorType());
                    e.printStackTrace();
                }
            }

            Platform.runLater(() -> {
                    this.serverBox.setItems(
                        FXCollections.observableList(servers)
                    );
                    String serverName = MainClassKt
                            .getConfig()
                            .<String>getOpt("server", null);
                    Stream<ShortServerData> stream = servers.stream().filter(s -> s.getShortName().equals(serverName));

                    ShortServerData data = stream.findFirst().orElse(null);
                    if(serverName == null || data == null)
                        this.serverBox.getSelectionModel().select(0);
                    else
                        this.serverBox.getSelectionModel().select(servers.indexOf(data));
                }
            );

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

            Platform.runLater(() -> loginButton.setDisable(false));
            String text = api.news();
            Platform.runLater(() -> newsView.getEngine().loadContent(text));

            Platform.runLater(() -> statusLabel.setText("Готово!"));


        }).start();
    }

    protected void setError(String text, String type)
    {
        Platform.runLater(()-> statusLabel.setText("[" + type.toUpperCase() + "] " + text));
    }

    public void onLogin(MouseEvent evt)
    {
        onLoginImpl(evt);
    }

    public void onAction(ActionEvent evt)
    {
        onLoginImpl(evt);
    }

    public void onLoginImpl(Event evt)
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
                    MainClassKt.getConfig().set(
                            "password",
                            Crypt.xorStr(passwordField.getText(),Config.PROTECTION_KEY)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MainClassKt.getConfig().set("username", loginField.getText());
                MainClassKt.getConfig().set("server", serverBox.getValue().getShortName());
                MainClassKt.getConfig().save();
            }



            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdaterForm.fxml"));
            Parent root = loader.load();
            ShortServerData serv = serverBox.getSelectionModel().getSelectedItem();
            UpdaterController ctrl = loader.getController();
            ctrl.setData(data,serv.getShortName(),serv.getVersion());
            ctrl.setApi(api);
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
                        ((DownloadController)ldr.getController()).postInitialize(api);
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
