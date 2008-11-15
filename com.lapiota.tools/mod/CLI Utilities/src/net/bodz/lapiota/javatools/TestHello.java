package net.bodz.lapiota.javatools;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.lapiota.wrappers.BasicCLI;
import net.bodz.swt.gui.SWTConfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

@BootInfo(configs = SWTConfig.class)
@Doc("Hello World (Test Program)")
@ProgramName("jhello")
@RcsKeywords(id = "$Id: TestHello.java 0 2008-10-22 下午06:44:18 Shecti $")
@Version( { 0, 0 })
public class TestHello extends BasicCLI {

    @Override
    protected void doMain(String[] args) throws Throwable {
        System.out.println(SWT.class);
        System.out.println(Widget.class);
        System.out.println(Layout.class);
    }

    public static void main(String[] args) throws Throwable {
        new TestHello().run(args);
    }

}
