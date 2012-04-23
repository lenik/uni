package net.bodz.lapiota.javatools;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.info.Doc;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.swt.program.SWTConfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

@BootInfo(configs = SWTConfig.class)
@Doc("Hello World (Test Program)")
@ProgramName("jhello")
@RcsKeywords(id = "$Id$")
@Version({ 0, 0 })
public class TestHello
        extends BasicCLI {

    @Override
    protected void doMain(String[] args)
            throws Exception {
        System.out.println(SWT.class);
        System.out.println(Widget.class);
        System.out.println(Layout.class);
    }

    public static void main(String[] args)
            throws Exception {
        new TestHello().run(args);
    }

}
