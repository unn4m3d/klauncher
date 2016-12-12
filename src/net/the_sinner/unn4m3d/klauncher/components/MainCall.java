package net.the_sinner.unn4m3d.klauncher.components;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by unn4m3d on 11.12.16.
 */
public class MainCall {

    public static void call(Method m, String[] array) throws InvocationTargetException, IllegalAccessException {
        m.invoke(null,array);
    }
}
