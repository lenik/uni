package net.bodz.lapiota.javatools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli._RunInfo;
import net.bodz.bas.lang.Control;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.a.ProgramName;
import net.bodz.lapiota.loader.Lapiota;

@Doc("Lapiota Java Program Launcher")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
@ProgramName("jlaunch")
public class ClassLauncher {

    static {
        Types.load(Lapiota.class);
    }

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
        // System.out.println("Class Loaded: " + mainClass);

        _RunInfo runInfo = _RunInfo.parse(mainClass);
        runInfo.loadBoot();
        runInfo.loadLibraries();
        runInfo.loadDelayed();

        Method mainf = mainClass.getMethod("main", String[].class);
        args = Arrays.copyOfRange(args, 2, args.length);
        try {
            Control.invoke(mainf, null, (Object) args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}
