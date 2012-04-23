package net.bodz.lapiota;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import net.bodz.bas.c.java.util.HashTextMap;
import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.snm.abc.ModulesRoot;

public class Lapiota {

    public static File lapRoot;
    public static File lapEtc;
    public static File lapAbcd;
    public static File lapHome;
    public static File userHome;

    public static TextMap<File> lapModules;

    static {
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

        lapModules = new HashTextMap<File>();
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
                lampath = lampath.trim();
                File lam = Files.canoniOf(lampath);
                lapModules.put(lam.getName(), lam);
            }
        }
    }

    private static final ModulesRoot abcdRoot;
    static {
        abcdRoot = new ModulesRoot(lapAbcd, lapModules);
    }

    public static File findabc(String name, File root) {
        return abcdRoot.findabc(name, root);
    }

    public static File findabc(String name) {
        return abcdRoot.findabc(name);
    }

    public static File findexp(String exp, File parent) {
        return abcdRoot.findexp(exp, parent);
    }

    public static File findexp(String exp) {
        return abcdRoot.findexp(exp);
    }

}
