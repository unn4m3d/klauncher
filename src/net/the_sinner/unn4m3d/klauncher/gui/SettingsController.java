package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.the_sinner.unn4m3d.klauncher.MainClassKt;
import net.the_sinner.unn4m3d.klauncher.components.UtilsKt;

/**
 * Created by unn4m3d on 18.12.16.
 */
public class SettingsController {
    @FXML private Button backButton;
    @FXML private Slider memSlider;
    @FXML private Label memLabel;
    @FXML private CheckBox rememberPassword;
    @FXML private CheckBox forceUpdate;
    private Node parent;

    @FXML
    public void back(MouseEvent evt)
    {
        if(!rememberPassword.isSelected())
        {
            MainClassKt.getConfig().set("password","");
        }
        MainClassKt.getConfig().save();
        ((Stage)parent.getScene().getWindow()).show();
        ((Stage)memSlider.getScene().getWindow()).close();
    }

    public void setNode(Node n)
    {
        parent = n;
    }

    private void updateMemValue(int mb, boolean setcfg)
    {
        memLabel.setText("Выделено " + mb + " МБ памяти");
        if(setcfg)
        {
            MainClassKt.getConfig().set("memory",mb);
        }
    }


    @FXML
    public void initialize()
    {
        updateMemValue(MainClassKt.getConfig().getOpt("memory",1024),false);
        memSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number o, Number n) -> {
            updateMemValue(n.intValue(),true);
        });
        memSlider.setMin(512);
        memSlider.setMax(UtilsKt.ramSize() / 1024 / 1024);
        memSlider.setValue(MainClassKt.getConfig().getOpt("memory",1024));
    }


    @FXML
    private void changeRP(ActionEvent evt)
    {
        MainClassKt.getConfig().set("remember",rememberPassword.isSelected());

    }

    @FXML
    private void changeFU(ActionEvent evt)
    {
        MainClassKt.setForceUpdate(forceUpdate.isSelected());
    }
}
