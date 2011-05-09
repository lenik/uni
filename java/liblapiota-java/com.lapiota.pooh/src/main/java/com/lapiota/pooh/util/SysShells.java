package com.lapiota.pooh.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.sys.SystemInfo;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @test {@link SysShellsTest}
 */
public class SysShells {

    public static String SHELL;
    public static String pathSeparator;

    static {
        // remove this to force using $SHELL for // default.
        if (!SystemInfo.isWin32())
            SHELL = System.getenv("SHELL");

        if (SHELL == null)
            SHELL = System.getenv("COMSPEC");
        if (SHELL == null)
            if (SystemInfo.isWin32())
                SHELL = "cmd.exe";
            else
                SHELL = "bash";

        pathSeparator = System.getProperty("path.separator");
        if (pathSeparator == null)
            pathSeparator = ":";
    }

    static String[] convertEnvp(Map<String, String> envmap) {
        String[] envp = new String[envmap.size()];
        int i = 0;
        for (Entry<String, String> e : envmap.entrySet()) {
            envp[i++] = e.getKey() + "=" + e.getValue();
        }
        return envp;
    }

    public static void open(Shell shell, File startDir) {
        open(shell, startDir, null, null);
    }

    public static void open(Shell shell, File startDir,
            Map<String, String> env, String title) {
        String winTitle = "Pooh Term";
        if (title != null)
            winTitle += " " + title;
        else
            winTitle += ": " + startDir;
        String[] cmdarray;
        if (SystemInfo.isWin32())
            cmdarray = new String[] { "cmd", "/c", "start",
                    "\"" + winTitle + "\"", SHELL };
        else
            cmdarray = new String[] { SHELL };
        try {
            String[] envp = null;
            if (env != null)
                envp = convertEnvp(env);
            Runtime.getRuntime().exec(cmdarray, envp, startDir);
        } catch (IOException e) {
            String mesg = e.getMessage();
            MessageDialog.openError(shell, "Error open terminal", mesg);
        }
    }
}
