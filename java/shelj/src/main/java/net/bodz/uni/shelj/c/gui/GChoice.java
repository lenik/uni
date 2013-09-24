package net.bodz.uni.shelj.c.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.swt.c.dialog.SwtUserDialogs;
import net.bodz.swt.program.BasicGUI;

/**
 * GUI Choice Utility
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class GChoice
        extends BasicGUI {

    /**
     * Window title
     *
     * @option -t =TITLE
     */
    String title = "Choice";

    /**
     * initial index of selection
     *
     * @option -i =INDEX
     */
    int initial = 0;

    /**
     * Import list from environ vars
     *
     * @option -e =ENV-KEY
     */
    String envKey;

    List<Object> list = new ArrayList<Object>();

    /**
     * set top-most (win32 only)
     *
     * @option -T
     */
    boolean topMost;

    /**
     * Exit with 0 if user canceled, or the selection index number from 1.
     */
    @Override
    protected void mainImpl(String... args)
            throws Exception {
        int style = SWT.SYSTEM_MODAL;
        if (topMost)
            style |= SWT.ON_TOP;
        SwtUserDialogs dialogs = new SwtUserDialogs(style);
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
        int index = dialogs.choice(title, list, initial);
        int retval = index == -1 ? 0 : (1 + index);
        System.exit(retval);
    }

    @Override
    protected void showHelpPage(IPrintOut out) {
        super.showHelpPage(out);
        out.println();

        out.println("Import from environ vars: ");
        out.println("    KEY_count  = count of list entries");
        out.println("    KEY_<n>    = text of entry");
        out.flush();
    }

    public static void main(String[] args)
            throws Exception {
        new GChoice().execute(args);
    }

}
