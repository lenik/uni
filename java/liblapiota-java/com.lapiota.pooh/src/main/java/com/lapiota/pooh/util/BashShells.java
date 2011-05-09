package com.lapiota.pooh.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.sys.SystemInfo;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class BashShells {

    static String BASH;

    static {
        // find in PATH variable.
        BASH = "bash";
    }

    public static void open(Shell shell, File startDir) {
        open(shell, startDir, null);
    }

    public static void open(Shell shell, File startDir, Map<String, String> env) {
        open(shell, startDir, env, null, null);
    }

    public static void open(Shell shell, File startDir,
            Map<String, String> env, Map<String, String> aliases, String title) {
        String winTitle = "Pooh Term";
        if (title != null)
            winTitle += " " + title;
        String[] cmdarray;
        if (SystemInfo.isWin32())
            cmdarray = new String[] { "cmd", "/c", "start",
                    "\"" + winTitle + "\"", BASH };
        else
            cmdarray = new String[] { BASH };

        try {
            File rcfile = File.createTempFile("initrc", ".sh");
            rcfile.deleteOnExit();
            PrintStream rc = new PrintStream(rcfile);
            if (aliases != null) {
                // rc.println("echo Set up aliases...");
                for (Entry<String, String> e : aliases.entrySet()) {
                    String name = e.getKey();
                    String value = e.getValue();
                    value = value.replace("\\", "\\\\");
                    value = value.replace("\"", "\\\"");
                    rc.print("alias " + name + "=\"" + value + "\"\n");
                }
            }
            rc.print("echo \"Welcome Pooh Terminal!\"\n");
            rc.close();

            String[] v = new String[cmdarray.length + 2];
            int len = cmdarray.length;
            System.arraycopy(cmdarray, 0, v, 0, len);
            v[len] = "--init-file";
            v[len + 1] = rcfile.getAbsolutePath();
            cmdarray = v;
        } catch (IOException e) {
            System.err.println("failed to create initrc: " + e);
        }

        try {
            String[] envp = null;
            if (env != null)
                envp = SysShells.convertEnvp(env);
            Runtime.getRuntime().exec(cmdarray, envp, startDir);
        } catch (IOException e) {
            String mesg = e.getMessage();
            MessageDialog.openError(shell, "Error open terminal", mesg);
        }
    }

}
