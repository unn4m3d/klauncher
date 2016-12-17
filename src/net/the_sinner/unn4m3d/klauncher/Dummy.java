package net.the_sinner.unn4m3d.klauncher;

//import com.sun.javaws.Main;

import java.net.URISyntaxException;

/**
 * Created by unn4m3d on 17.12.16.
 */
public class Dummy {

    public static String getPath() throws URISyntaxException {
        return Dummy.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    }

    public static String getCname()
    {
        return MainClassKt.class.getCanonicalName();
    }
}
