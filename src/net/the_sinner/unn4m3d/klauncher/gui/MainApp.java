package net.the_sinner.unn4m3d.klauncher.gui;/**
 * Created by unn4m3d on 15.12.16.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
        Parent root = loader.load();
        MainController controller = loader.<MainController>getController();
        primaryStage.setTitle("KLauncher");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        controller.postInitialize(primaryStage);
        //primaryStage.show();
    }
}
