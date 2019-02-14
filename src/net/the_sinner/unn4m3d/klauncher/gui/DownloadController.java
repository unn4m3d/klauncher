package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import kotlin.Unit;
import net.the_sinner.unn4m3d.klauncher.Dummy;
import net.the_sinner.unn4m3d.klauncher.api.API;
import net.the_sinner.unn4m3d.klauncher.components.UpdaterKt;
import net.the_sinner.unn4m3d.klauncher.components.UtilsKt;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by unn4m3d on 26.12.16.
 */
public class DownloadController {
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    public void initialize()
    {
        statusLabel.setText("Загрузка...");
    }

    public void postInitialize(API apiInstance)
    {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                String path = apiInstance.launcher(UtilsKt.getPlatform());
                UpdaterKt.downloadFile(
                        new File(Dummy.getPath()).getParentFile(),
                        new File(path).getParent(),
                        new File(path).getName(), (d,t) -> {
                            Platform.runLater(() -> {
                                progressBar.setProgress(((double)d)/t);
                                statusLabel.setText(
                                        "Загрузка " + UpdaterKt.size(d) + "B / " +
                                                UpdaterKt.size(t) + "B"
                                );
                            });
                            return Unit.INSTANCE;
                        });


                List<String> params = new ArrayList<>();
                params.add("java");
                params.add("-jar");
                params.add(Dummy.getPath());
                ProcessBuilder pb = new ProcessBuilder(params);
                pb = pb.inheritIO();
                pb.start();
                System.exit(0);

                return true;
            }
        };

        task.setOnFailed((evt) -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Произошла ошибка. Перекачайте лаунчер с сайта ",
                    "Ошибка!",
                    JOptionPane.ERROR_MESSAGE
            );
        });

        new Thread(task).start();
    }
}
