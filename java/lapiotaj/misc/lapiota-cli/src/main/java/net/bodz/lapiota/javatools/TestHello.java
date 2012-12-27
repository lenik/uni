package net.bodz.lapiota.javatools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.meta.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * Hello World (Test Program)
 */
@ProgramName("jhello")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class TestHello
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        System.out.println(SWT.class);
        System.out.println(Widget.class);
        System.out.println(Layout.class);
    }

    public static void main(String[] args)
            throws Exception {
        new TestHello().execute(args);
    }

}
