package net.bodz.lapiota.loader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.cli.util.PathFunctions;
import net.bodz.bas.io.Files;

public class Lapiota {

    public static File              lapRoot;
    public static File              lapEtc;
    public static File              lapAbcd;
    public static File              lapHome;
    public static File              userHome;

    public static Map<String, File> lapModules;

    static {
        reconfig();
    }

    public static void reconfig() {
        String s = System.getenv("LAPIOTA");
        if (s == null) {
            if (!(lapRoot = Files.canoniOf("/lapiota")).isDirectory())
                if (!(lapRoot = Files.canoniOf("C:/lapiota")).isDirectory())
                    throw new Error("Can't find lapiota");
        } else {
            lapRoot = Files.canoniOf(s);
        }
        lapEtc = new File(lapRoot, "etc");
        lapAbcd = new File(lapRoot, "abc.d");
        lapHome = new File(lapRoot, "home");

        if ((s = System.getenv("HOME")) == null)
            if ((s = System.getenv("USERPROFILE")) == null) {
                if ((s = System.getenv("USERNAME")) == null)
                    s = "noname";
                s = "/home/" + s;
            }
        userHome = Files.canoniOf(s);
        if (userHome.isFile())
            userHome = Files.canoniOf("/");
        else if (!userHome.exists())
            userHome.mkdirs();

        lapModules = new HashMap<String, File>();
        lapModules.put("root", lapRoot);
        File lams = new File(userHome, "etc/lams");
        if (lams.isFile()) {
            List<String> lamdef;
            try {
                lamdef = Files.readLines(lams);
            } catch (IOException e) {
                throw new Error("can't read " + lams);
            }
            for (String lampath : lamdef) {
                File lam = Files.canoniOf(lampath);
                lapModules.put(lam.getName(), lam);
            }
        }

        PathFunctions.defaultRoot = lapAbcd;
        PathFunctions.namedRoots.putAll(lapModules);
    }

}
