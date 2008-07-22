package net.bodz.lapiota.programs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.lapiota.annotations.ProgramName;

@Doc("Lapiota Java Program Launcher")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@ProgramName("jlaunch")
public class Launcher {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Throwable {
        if (args.length < 2)
            throw new Error("Launcher CLASS-LOADER MAIN-CLASS [ARGUMENTS]");
        String loaderClassName = args[0];
        String mainClassName = args[1];

        Class<ClassLoader> loaderClass = (Class<ClassLoader>) Class
                .forName(loaderClassName);
        ClassLoader loader = loaderClass.newInstance();

        Class<?> mainClass = loader.loadClass(mainClassName);
        Method mainf = mainClass.getMethod("main", String[].class);
        args = Arrays.copyOfRange(args, 2, args.length);
        try {
            mainf.invoke(null, (Object) args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}
