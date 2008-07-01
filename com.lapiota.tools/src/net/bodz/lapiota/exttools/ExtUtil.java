package net.bodz.lapiota.exttools;

import java.lang.reflect.Method;

public class ExtUtil {

    public static void callMain(String className, String[] args)
            throws Throwable {
        Class<?> clazz = Class.forName(className);
        Method mainf = clazz.getMethod("main", String[].class);
        mainf.invoke(null, (Object) args);
    }

}
