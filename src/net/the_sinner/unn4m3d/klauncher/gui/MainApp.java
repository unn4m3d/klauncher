package net.the_sinner.unn4m3d.klauncher.gui;/**
 * Created by unn4m3d on 15.12.16.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainApp extends Application {

    public static boolean crashed = false;
    public static String log;

    public static void main(String[] args) {
        int index = Arrays.asList(args).indexOf("--crashed ");
        if(index != -1)
        {
            crashed = true;
            log = args[index + 1];
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
        Parent root = loader.load();
        MainController controller = loader.<MainController>getController();
        primaryStage.setTitle("KLauncher");
        primaryStage.setScene(new Scene(root));
        //primaryStage.setResizable(false);
        controller.postInitialize(primaryStage);
        //primaryStage.show();
    }
}
