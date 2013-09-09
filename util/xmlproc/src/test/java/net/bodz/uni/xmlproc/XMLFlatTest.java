package net.bodz.uni.xmlproc;

import java.io.File;

import org.junit.Assert;

import net.bodz.bas.c.org.eclipse.JavaProject;
import net.bodz.bas.c.org.eclipse.JavaProjectBaseDir;
import net.bodz.uni.xml.XMLFlat;

public class XMLFlatTest
        extends Assert {

    public static void main(String[] args)
            throws Exception {
        File baseDir = JavaProjectBaseDir.fromWorkdir();
        JavaProject project = new JavaProject(baseDir);
        File file = new File(project.getBaseDir(), ".classpath");
        XMLFlat flat = new XMLFlat();
        flat.execute("-f", file.getPath(), "-s", "//classpathentry", "@path");
    }

}
