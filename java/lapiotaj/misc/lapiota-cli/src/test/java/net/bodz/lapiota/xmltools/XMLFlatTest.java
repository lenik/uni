package net.bodz.lapiota.xmltools;

import java.io.File;

import org.junit.Test;

import net.bodz.bas.c.org.eclipse.EclipseProject;

public class XMLFlatTest {

    @Test
    public void test1()
            throws Throwable {
        EclipseProject proj = EclipseProject.findBaseDirFromWorkdir();
        File file = new File(proj.getBaseDir(), ".classpath");
        XMLFlat flat = new XMLFlat();
        flat.execute("-f", file.getPath(), "-s", "//classpathentry", "@path");
    }

}
