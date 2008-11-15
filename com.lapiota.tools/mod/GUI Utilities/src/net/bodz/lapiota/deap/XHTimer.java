package net.bodz.lapiota.deap;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.gui.GUIException;
import net.bodz.lapiota.wrappers.BasicGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.swtdesigner.SWTResourceManager;

@Doc("[DEAP] X/10-Hours Timer")
@RcsKeywords(id = "$Id$")
@Version( { 0, 0 })
public class XHTimer extends BasicGUI {

    static class Config {
        String taskPause = "/icons/full/clcl16/pause.gif";

    }

    @Override
    protected Shell createShell() throws GUIException, SWTException {
        Shell shell = new Shell();
        shell.setLayout(new GridLayout());

        final Label label = new Label(shell, SWT.NONE);
        label.setImage(SWTResourceManager.getImage(XHTimer.class,
                "/icons/full/clcl16/pause.gif"));

        return shell;
    }

    public static void main(String[] args) throws Throwable {
        new XHTimer().run(args);
    }

}
