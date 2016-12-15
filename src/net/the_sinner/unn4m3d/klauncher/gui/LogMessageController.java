package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import net.the_sinner.unn4m3d.klauncher.components.StackTraceConverterKt;

import java.util.List;

/**
 * Created by unn4m3d on 15.12.16.
 */
public class LogMessageController {
    @FXML
    private TitledPane mainPane;

    @FXML
    private ListView<String> exceptionView;

    private LogMessage message;

    public void setMessage(LogMessage m)
    {
        message = m;
        if(m.inner != null) {
            mainPane.setCollapsible(true);
            exceptionView.setItems(StackTraceConverterKt.convertStackTrace(m.inner));
        }
    }
}
