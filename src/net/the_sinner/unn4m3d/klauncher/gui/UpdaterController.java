package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kotlin.Unit;
import net.the_sinner.unn4m3d.klauncher.Config;
import net.the_sinner.unn4m3d.klauncher.MainClassKt;
import net.the_sinner.unn4m3d.klauncher.api.API;
import net.the_sinner.unn4m3d.klauncher.api.FilesData;
import net.the_sinner.unn4m3d.klauncher.api.SessionData;
import net.the_sinner.unn4m3d.klauncher.components.*;


import static net.the_sinner.unn4m3d.klauncher.components.UtilsKt.*;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by unn4m3d on 15.12.16.
 */
public class UpdaterController {
    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label status;

    @FXML
    //private ListView<LogMessage> logView;
    private ListView<String> logView;

    //private ObservableList<LogMessage> list = FXCollections.observableArrayList();
    private ObservableList<String> list = FXCollections.observableArrayList();

    private SessionData session;
    private String server;
    private String version;
    private API api;
    private MainController parent;

    private final String FTD_PREFIX = "files_to_download: ";
    private  int count = 0;
    private int downloaded = 0;

    public void _initialize() {
        //logView.setCellFactory((ListView<LogMessage> lw) -> new LogMessageCell());
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                return UpdaterKt.launchUpdater(
                        api,
                        fileJoin(getAppData(), Config.APP_FOLDER + File.separator + server),
                        server,
                        fileJoin(getAppData(), Config.APP_FOLDER + File.separator + "assets"),
                        MainClassKt.getForceUpdate(),
                        (l, m, e) -> {
                            if(l == Level.OFF && m.startsWith(FTD_PREFIX)) {
                                count = Integer.parseInt(m.substring(FTD_PREFIX.length()));
                            } else if(l == Level.OFF && m.equals("inc_dl")) {
                                downloaded++;
                                if(count > 0) {
                                    double progress = (double)downloaded / count;
                                    Platform.runLater(() -> progressBar.setProgress(progress));

                                }
                            } else {

                                println(l, m, e);
                            }
                            return Unit.INSTANCE;
                        });
            }
        };
        task.setOnSucceeded((WorkerStateEvent e) -> {
            boolean value = task.getValue();
            if (value) {
                Task gtask = new Task() {
                    @Override
                    public Object call() {
                        Game game = new Game(new GameData(session.getUsername(), session.getSessionId(), session.getAccessToken()));
                        try {
                            game.launch(
                                    fileJoin(getAppData(), Config.APP_FOLDER + File.separator + server).getAbsolutePath(),
                                    new Settings(
                                            640, 480,
                                            Config.PROTECTION_KEY,
                                            "Minecraft unn4m3d",
                                            false,
                                            fileJoin(getAppData(), Config.APP_FOLDER).getAbsolutePath(),
                                            version
                                    ), (s) -> {
                                        println(Level.INFO, s);
                                        if (s.startsWith("!!! ")) {
                                            Platform.runLater(() ->
                                                    progressBar.getScene().getWindow().hide());
                                            //System.out.println(s);
                                            //System.exit(0);
                                        }
                                        return Unit.INSTANCE;
                                    });
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        return null;
                    }
                };

                new Thread(gtask).start();
            }
        });

        task.setOnFailed((e) -> {
            status.setText("ERROR! " + e.toString());
            println(Level.ALL, e.toString());
            Throwable thr = (Throwable) (e.getSource().exceptionProperty().get());
            thr.printStackTrace();
        });

        new Thread(task).start();

    }

    public void setData(SessionData sd, String serv, String v) {
        println(Level.INFO, "Server is " + serv + " [" + v + "]");
        session = sd;
        server = serv;
        version = v;

    }

    public void setApi(API data)
    {
        api = data;
    }

    private void println(LogMessage m)
    {
        Platform.runLater(() -> {
            System.out.println(m.toString());
            if(m.level != Level.OFF) {
                list.addAll(m.toString());
                logView.setItems(list);
                logView.scrollTo(list.size() - 1);
            }
            status.setText(m.message);
            if(m.inner != null)
                m.inner.printStackTrace();
        });
    }

    private void println(Level level, String message)
    {
        println(new LogMessage(level,message));
    }

    private void println(Level level, String message, Exception inner)
    {
        println(new LogMessage(level,message,inner));
    }



}
