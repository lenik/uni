package net.bodz.lapiota.devhelpers;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.a.Option;
import net.bodz.lapiota.wrappers.BasicGUI;
import net.bodz.swt.gui.util.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.POINT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

@Doc("Win32 Windows Editor")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: CommandLineEditor.java 24 2008-08-05 14:25:23Z lenik $")
@ProgramName("winedit")
public class WindowsHack extends BasicGUI {

    @Option(alias = "i", vnam = "ms", doc = "capture interval, default 1000")
    int           interval = 100;

    @Option(alias = "f", doc = "follow the mouse cursor")
    boolean       follow;

    private Label windowLabel;
    private Label controlLabel;
    private Label cursorLabel;

    private Timer updaterTimer;

    @Override
    protected void _start() {
        if (interval > 0) {
            updaterTimer = new Timer(interval, shell.getDisplay()) {
                @Override
                public void run() {
                    updater();
                }
            };
        }
    }

    synchronized void updater() {
        POINT cursor = new POINT();
        OS.GetCursorPos(cursor);
        cursorLabel.setText(cursor.x + ", " + cursor.y);
        int hWnd = OS.WindowFromPoint(cursor);
        String text = "NULL";
        if (hWnd != 0) {
            char buf[] = new char[200];
            int cc = OS.GetWindowTextW(hWnd, buf, buf.length);
            text = new String(buf, 0, cc);
            windowLabel.setText("HWND[" + hWnd + "] - " + text);
        }
        // boolean scroll = (OS.GetKeyState(OS.VK_SCROLL) & 0x1) != 0;
        boolean ctrlDown = (OS.GetKeyState(OS.VK_CONTROL) & 0x8000) != 0;
        if (follow ^ ctrlDown) {
            shell.setLocation(10 + cursor.x, 10 + cursor.y);
        }
    }

    @Override
    protected Composite createInitialView(Composite parent) {
        final Composite view = new Composite(parent, SWT.BORDER);
        view.setLayout(new FormLayout());

        cursorLabel = new Label(view, SWT.NONE);
        final FormData fd_cursorLabel = new FormData();
        fd_cursorLabel.right = new FormAttachment(100, -17);
        fd_cursorLabel.left = new FormAttachment(0, 13);
        fd_cursorLabel.top = new FormAttachment(0, 10);
        cursorLabel.setLayoutData(fd_cursorLabel);
        cursorLabel.setText("Cursor: ");

        windowLabel = new Label(view, SWT.NONE);
        final FormData fd_windowLabel = new FormData();
        fd_windowLabel.right = new FormAttachment(cursorLabel, 0, SWT.RIGHT);
        fd_windowLabel.bottom = new FormAttachment(0, 45);
        fd_windowLabel.top = new FormAttachment(cursorLabel, 5, SWT.BOTTOM);
        fd_windowLabel.left = new FormAttachment(cursorLabel, 0, SWT.LEFT);
        windowLabel.setLayoutData(fd_windowLabel);
        windowLabel.setText("Window: ");

        controlLabel = new Label(view, SWT.NONE);
        final FormData fd_controlLabel = new FormData();
        fd_controlLabel.bottom = new FormAttachment(0, 65);
        fd_controlLabel.right = new FormAttachment(windowLabel, 0, SWT.RIGHT);
        fd_controlLabel.top = new FormAttachment(windowLabel, 5, SWT.BOTTOM);
        fd_controlLabel.left = new FormAttachment(windowLabel, 0, SWT.LEFT);
        controlLabel.setLayoutData(fd_controlLabel);
        controlLabel.setText("Control: ");

        view.setSize(200, 200);
        return view;
    }

    public static void main(String[] args) throws Throwable {
        new WindowsHack().run(args);
    }

}
