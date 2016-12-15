package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import net.the_sinner.unn4m3d.klauncher.components.StackTraceConverterKt;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;


/**
 * Created by unn4m3d on 15.12.16.
 */
public class LogMessageController {
    @FXML
    private TitledPane mainPane;

    @FXML
    private ListView<String> exceptionView;

    private LogMessage message;

    private String colorToRGB(Color c)
    {
        return "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
    }

    public LogMessageController()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogMessageCell.fxml"));
        System.out.println("Loaded view of LMC");
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void setMessage(LogMessage m)
    {
        message = m;
        if(m.inner != null) {
            mainPane.setCollapsible(true);
            exceptionView.setItems(StackTraceConverterKt.convertStackTrace(m.inner));
        }
        Color color = Color.BLACK;
        switch(m.level.getName())
        {
            case "SEVERE":
            case "WARNING":
                color = Color.ORANGE;
                break;
            case "ALL":
                color = Color.RED;
                break;
        }
        mainPane.setStyle("-fx-inner-color : " + colorToRGB(color) + ";");
        mainPane.setText(m.message);
    }

    public TitledPane getView()
    {
        return mainPane;
    }
}
