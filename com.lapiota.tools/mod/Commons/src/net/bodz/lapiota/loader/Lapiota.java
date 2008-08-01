package net.bodz.lapiota.loader;

import static net.bodz.bas.cli.util.CLIFunctions.global;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.UnexpectedException;
import net.bodz.bas.lang.util.Classpath;
import net.bodz.bas.sys.SystemInfo;

public class Lapiota {

    public static File              lapRoot;
    public static File              lapEtc;
    public static File              lapAbcd;
    public static File              lapHome;
    public static File              userHome;

    public static Map<String, File> lapModules;

    // trigger classloader.
    public static void load() {
    }

    static {
        reconfig();
        try {
            global.register("findcp", //
                    global.wrap(Lapiota.class, "findcp", String.class));
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
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
    }

    private static class MaxFixes implements FilenameFilter {
        final String pattern;
        String       prefix;
        String       suffix;

        public MaxFixes(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean accept(File dir, String name) {
            if (name.startsWith(pattern)) {
                if (suffix == null)
                    suffix = name;
                else if (name.compareTo(suffix) > 0)
                    suffix = name;
            } else if (pattern.startsWith(name)) {
                if (prefix == null)
                    prefix = name;
                else if (name.length() > prefix.length())
                    prefix = name;
            }
            return false;
        }
    }

    public static File findabc(String name, File root) {
        File file = new File(root, name);
        if (file.exists())
            return file;

        // 1, find name* in root and return
        // 2, find nam in root and recurse
        MaxFixes max = new MaxFixes(name);
        root.list(max);
        if (max.suffix != null)
            return new File(root, max.suffix);
        if (max.prefix != null) {
            root = new File(root, max.prefix);
            if (root.isDirectory())
                return findabc(name, root);
        }
        return null;
    }

    public static File findabc(String name) {
        return findabc(name, lapAbcd);
    }

    /**
     * @example /lapiota/abc.d/eclipse&#42;/plugins/org.eclipse.core&#42;<br>
     *          &#64;root/abc.d/eclipse&#42;/plugins/org.eclipse.core&#42;<br>
     *          eclipse&#42;/plugins/org.eclipse.core&#42;<br>
     *          $ECLIPSE_HOME/plugins/org.eclipse.core&#42;<br>
     */
    public static File find(String exp) {
        return find(exp, null);
    }

    public static File find(String exp, File parent) {
        // termination
        if (exp == null)
            return parent;

        boolean first = parent == null;
        if (parent == null) {
            if (exp.startsWith("/"))
                return find(exp.substring(1), Files.canoniOf("/"));
            if (SystemInfo.isWin32() && exp.length() > 2
                    && exp.charAt(1) == ':') {
                File driveRoot = Files.canoniOf(exp.substring(0, 2));
                return find(exp.substring(2), driveRoot);
            }
            parent = lapAbcd;
        }
        String component = null;
        int slash = exp.indexOf('/');
        if (slash == -1) {
            component = exp;
            exp = null;
        } else {
            component = exp.substring(0, slash);
            exp = exp.substring(slash + 1);
        }
        // @lam
        if (first && component.startsWith("@")) {
            String mod = component.substring(1);
            parent = lapModules.get(mod);
            if (parent == null || !parent.isDirectory())
                return null;
            return find(exp, parent);
        }
        // fuzzy*
        if (component.endsWith("*")) {
            String prefix = component.substring(0, component.length() - 1);
            File expanded = findabc(prefix, parent);
            if (expanded == null || !expanded.exists())
                return null;
            return find(exp, expanded);
        }
        // plain
        parent = new File(parent, component);
        if (!parent.exists())
            throw null;
        return find(exp, parent);
    }

    public static Object findcp(String exp) throws IOException {
        File file = find(exp, null);
        if (file == null) // _log...
            return null;
        Classpath.addURL(file.toURI().toURL());
        return file;
    }

}
