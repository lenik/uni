package net.bodz.lily.tool.daogen.util;

import java.io.File;
import java.io.IOException;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.json.JsonFn;
import net.bodz.bas.io.res.ResFn;
import net.bodz.bas.json.JsonObject;

public class NpmDir {

    static final String PACKAGE_JSON = "package.json";

    private File packageFile;
    private File baseDir;

    public NpmDir(File baseDir) {
        if (baseDir == null)
            throw new NullPointerException("baseDir");
        this.baseDir = baseDir;
        this.packageFile = new File(baseDir, PACKAGE_JSON);
    }

    public String getName() {
        String name = baseDir.getName();
        return name;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getPackageFile() {
        return packageFile;
    }

    public File find(String name) {
        return new File(baseDir, name);
    }

    public File getPreferredPackageDir(String packageName) {
        String dir = packageName.replace('.', '/');
        return new File(baseDir, "src/" + dir);
    }

    public JsonObject resolve()
            throws ParseException, IOException {
        if (! packageFile.exists())
            return null;
        String json = ResFn.file(packageFile).read().readString();
        return JsonFn.parseObject(json);
    }

    public static NpmDir closest(String child) {
        return closest(new File(child));
    }

    public static NpmDir closest(File child) {
        while (child != null) {
            if (isPackageDir(child))
                return new NpmDir(child);
            child = child.getParentFile();
        }
        return null;
    }

    public static boolean isPackageDir(File dir) {
        File packageJson = new File(dir, PACKAGE_JSON);
        if (packageJson.exists())
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseDir == null) ? 0 : baseDir.hashCode());
        result = prime * result + ((packageFile == null) ? 0 : packageFile.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NpmDir other = (NpmDir) obj;
        if (! Nullables.equals(baseDir, other.baseDir))
            return false;
        if (! Nullables.equals(packageFile, other.packageFile))
            return false;
        return true;
    }

}
