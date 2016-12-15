package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import net.the_sinner.unn4m3d.klauncher.api.SessionData;

import java.util.LinkedHashMap;
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
    private ListView<LogMessage> logView;

    private ObservableList<LogMessage> list = FXCollections.observableArrayList();

    @FXML
    public void initialize()
    {
        logView.setCellFactory((ListView<LogMessage> lw) -> new LogMessageCell());
        println(Level.WARNING,"Hello!");
    }

    public void setData(SessionData sd, String serv)
    {
        // TODO
    }

    private void println(LogMessage m)
    {
        list.addAll(m);
        logView.setItems(list);
        logView.scrollTo(list.size()-1);
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
