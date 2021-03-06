package net.the_sinner.unn4m3d.klauncher.gui;

import java.util.logging.Level;

/**
 * Created by unn4m3d on 15.12.16.
 */
public class LogMessage {
    public Level level;
    public String message;
    public Exception inner = null;

    public LogMessage(Level l, String message)
    {
        level = l;
        this.message = message;
    }

    public LogMessage(Level l, String message, Exception inner)
    {
        this(l,message);
        this.inner = inner;
    }

    @Override
    public String toString()
    {
        return "[" + level.toString() + "] " + message;
    }
}
