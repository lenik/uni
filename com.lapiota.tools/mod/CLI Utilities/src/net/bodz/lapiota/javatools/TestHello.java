package net.bodz.lapiota.javatools;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.RunInfo;
import net.bodz.lapiota.wrappers.BasicCLI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

@Doc("Hello World (Test Program)")
@Version( { 0, 0 })
@RcsKeywords(id = "$Id: TestHello.java 0 2008-10-22 下午06:44:18 Shecti $")
@ProgramName("jhello")
@RunInfo(

load = { "findcp eclipse*/plugins/org.eclipse.swt.win32.win32.x86_*", })
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
