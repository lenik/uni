package net.bodz.lapiota.xmltools;

import java.io.File;

import net.bodz.bas.snm.EclipseProject;
import net.bodz.lapiota.xmltools.XMLFlat;

import org.junit.Test;

public class XMLFlatTest {

    @Test
    public void test1()
            throws Throwable {
        EclipseProject proj = EclipseProject.findFromCWD();
        File file = new File(proj.getBase(), ".classpath"); //$NON-NLS-1$
        XMLFlat flat = new XMLFlat();
        flat.run("-f", file.getPath(), "-s", "//classpathentry", "@path"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

}
