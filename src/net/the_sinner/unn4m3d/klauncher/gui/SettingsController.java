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
import net.the_sinner.unn4m3d.klauncher.components.LauncherConfig;
import net.the_sinner.unn4m3d.klauncher.components.UtilsKt;

import javax.swing.*;

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
    private boolean memUpdated = false;

    @FXML
    public void back(MouseEvent evt) {
        if (!rememberPassword.isSelected()) {
            MainClassKt.getConfig().set("password", "");
        }
        int value = (int)memSlider.getValue();
        ((Stage) parent.getScene().getWindow()).show();
        ((Stage) memSlider.getScene().getWindow()).close();

        if (memUpdated) {
            updateMemValue(value, true);
            JOptionPane.showMessageDialog(null, "Значение выделенной памяти было обновлено. Перезапустите лаунчер для применения значения");
        }
        MainClassKt.getConfig().save();
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
            memUpdated = true;
            updateMemValue((int)memSlider.getValue(), false);
        });
        memSlider.setMin(1024);
        int max = Integer.max((int)(UtilsKt.ramSize() / 1024 / 1024), 4096);
        memSlider.setMax(max);
        LauncherConfig cfg = MainClassKt.getConfig();
        int value = cfg.getOpt("memory", 1024);
        memSlider.setValue(value);

        rememberPassword.setSelected(MainClassKt.getConfig().<Boolean>getOpt("remember",false));
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
