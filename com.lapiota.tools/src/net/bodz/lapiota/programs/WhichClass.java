package net.bodz.lapiota.programs;

import java.net.URL;

import net.sf.freejava.util.Classes;

public class WhichClass {

    public static void main(String[] args) {
        ClassLoader loader = Classes.getCallerClassLoader();
        for (String name : args) {
            String binpath = name.replace('.', '/') + ".class";
            URL url = loader.getResource(binpath);
            if (url == null)
                url = loader.getResource(name);
            if (url == null) {
                System.out.println("No-Class: " + name);
                continue;
            }

            String file = url.toString();
            if ("jar".equals(url.getProtocol())) {
                int pos = file.lastIndexOf("!");
                assert pos > 0;
                file = file.substring(4, pos);
            }
            file = file.replaceFirst("^file:/", "");
            System.out.println("Class: " + name + "\n\t" + file);
        }
    }

}
