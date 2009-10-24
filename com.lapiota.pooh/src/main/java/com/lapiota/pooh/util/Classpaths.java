package com.lapiota.pooh.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.io.Files;
import net.bodz.bas.types.TextMap;
import net.bodz.bas.types.TreeTextMap;

import org.eclipse.core.runtime.IPath;

public class Classpaths {

    private List<File>      paths;
    private TextMap<String> appAliases;

    public Classpaths() {
        paths = new ArrayList<File>();
        appAliases = new TreeTextMap<String>();
    }

    public void merge(Classpaths classpaths) {
        assert classpaths != null;
        for (File pathFile : classpaths.paths) {
            if (!paths.contains(pathFile))
                paths.add(pathFile);
        }
        appAliases.putAll(classpaths.appAliases);
    }

    public File addRelative(IPath base, IPath path) {
        if (base != null)
            path = base.append(path);
        return addAbsolute(path);
    }

    public File addAbsolute(IPath path) {
        File pathFile;
        try {
            pathFile = path.toFile().getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!paths.contains(pathFile))
            paths.add(pathFile);
        return pathFile;
    }

    public String buildCPString(String pathSeparator) {
        StringBuffer buf = null;
        for (File pathFile : paths) {
            if (buf == null)
                buf = new StringBuffer(paths.size() * 30);
            else
                buf.append(pathSeparator);
            buf.append(pathFile);
        }
        return buf.toString();
    }

    public void addMainClass(String className) {
        int dot = className.lastIndexOf('.');
        String simpleName = dot == -1 ? className : className
                .substring(dot + 1);
        appAliases.put(simpleName.toLowerCase(), "java " + className);
    }

    public void addSourceFolder(File folder) {
        addSourceFolder(folder, null);
    }

    void addSourceFolder(File folder, String pkg) {
        for (File file : folder.listFiles()) {
            String name = file.getName();
            boolean isJava = name.endsWith(".java");
            if (isJava)
                name = name.substring(0, name.length() - 5);
            String qname = name;
            if (pkg != null)
                qname = pkg + "." + qname;
            if (!isJava) {
                if (file.isDirectory())
                    addSourceFolder(file, qname);
                continue;
            }
            try {
                String source = Files.readAll(file);
                if (source.contains("public static void main(String[]")) {
                    addMainClass(qname);
                }
            } catch (IOException e) {
                // ignore bad files
                continue;
            }
        }
    }

    public TextMap<String> getMainClasses() {
        return appAliases;
    }

}
