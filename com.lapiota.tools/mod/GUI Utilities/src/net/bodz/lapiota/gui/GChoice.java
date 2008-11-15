package net.bodz.lapiota.gui;

import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.CharOut;
import net.bodz.lapiota.wrappers.BasicGUI;
import net.bodz.swt.gui.SWTInteraction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Shell;

@Doc("GUI Choice Utility")
@RcsKeywords(id = "$Id: VisualExec.java 29 2008-10-07 13:38:08Z lenik $")
@Version( { 0, 1 })
public class GChoice extends BasicGUI {

    @Option(alias = "t", vnam = "TITLE", doc = "window title")
    String       title   = "Choice";

    @Option(alias = "i", vnam = "INDEX", doc = "initial index of selection")
    int          initial = 0;

    @Option(alias = "e", vnam = "ENV-KEY", doc = "import list from environ vars")
    String       envKey;

    List<Object> list    = new ArrayList<Object>();

    @Option(alias = "T", doc = "set top-most (win32 only)")
    boolean      topMost;

    /**
     * Exit with 0 if user canceled, or the selection index number from 1.
     */
    @Override
    protected void doMain(String[] args) throws Throwable {
        SWTInteraction act = new SWTInteraction(SWT.SYSTEM_MODAL) {
            @Override
            protected void addEffects(Shell shell) {
                // Win32 Only
                if (topMost)
                    OS.SetWindowPos(shell.handle, OS.HWND_TOPMOST, 0, 0, 0, 0,
                            OS.SWP_NOMOVE | OS.SWP_NOSIZE);
            }
        };
        if (envKey != null) {
            String _count = System.getenv(envKey + "_count");
            if (_count == null) {
                int index = 1;
                while (true) {
                    String arg = System.getenv(envKey + "_" + index);
                    if (arg == null)
                        break;
                    list.add(arg);
                    index++;
                }
            } else {
                int count = Integer.parseInt(_count);
                for (int i = 1; i <= count; i++) {
                    String arg = System.getenv(envKey + "_" + i);
                    if (arg == null)
                        break;
                    list.add(arg);
                }
            }
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            list.add(arg);
        }
        int index = act.choice(title, list, initial);
        int retval = index == -1 ? 0 : (1 + index);
        System.exit(retval);
    }

    @Override
    protected void _help(CharOut out) throws CLIException {
        super._help(out);
        out.println();

        out.println("Import from environ vars: ");
        out.println("    KEY_count  = count of list entries");
        out.println("    KEY_<n>    = text of entry");
    }

    public static void main(String[] args) throws Throwable {
        new GChoice().run(args);
    }

}
