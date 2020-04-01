package net.the_sinner.unn4m3d.klauncher.components;

import net.the_sinner.unn4m3d.klauncher.Config;
import net.the_sinner.unn4m3d.klauncher.PreloaderKt;

public class ShutdownHook {
    public static boolean crashed = false;
    public static Thread getShutdownHook() {
        return new Thread() {
            @Override
            public void run() {
                if(crashed) {
                    String[] args = new String[2];
                    args[0] = "--crashed";
                    args[1] = PreloaderKt.getLog().getAbsolutePath();
                    PreloaderKt.old_main(args);
                } else if(Config.reopenAfterExit){
                    PreloaderKt.old_main(new String[]{});
                }
            }
        };
    }
}
