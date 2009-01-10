package net.bodz.lapiota.xmltools;

import java.io.File;

import net.bodz.bas.snm.EclipseProject;

import org.junit.Test;

public class XMLFlatTest {

    @Test
    public void test1() throws Throwable {
        EclipseProject proj = EclipseProject.findFromCWD();
        File file = new File(proj.getBase(), ".classpath");
        XMLFlat flat = new XMLFlat();
        flat.run("-f", file.getPath(), "-s", "//classpathentry", "@path");
    }

}
