package net.bodz.lapiota.xmltools;

import java.io.File;

import org.junit.Test;

import net.bodz.bas.snm.EclipseProject;

public class XMLFlatTest {

    @Test
    public void test1()
            throws Throwable {
        EclipseProject proj = EclipseProject.findFromCWD();
        File file = new File(proj.getBase(), ".classpath");
        XMLFlat flat = new XMLFlat();
        flat.execute("-f", file.getPath(), "-s", "//classpathentry", "@path");
    }

}
