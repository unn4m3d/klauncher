package net.the_sinner.unn4m3d.klauncher.gui;

import javafx.scene.control.ListCell;
import javafx.scene.control.TitledPane;

/**
 * Created by unn4m3d on 15.12.16.
 */
public final class LogMessageCell extends ListCell<LogMessage> {
    private final LogMessageController controller = new LogMessageController();
    private final TitledPane pane = controller.getView();

    @Override
    protected void updateItem(LogMessage item,boolean empty) {
        if(empty)
            setGraphic(null);
        else
        {
            controller.setMessage(item);
            setGraphic(pane);
        }
    }

}
