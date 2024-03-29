package net.bodz.uni.shelj.c.gui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.swt.program.BasicGUI;

/**
 * Command line editor
 */
@ProgramName("cliedit")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class VisualExec
        extends BasicGUI {

    private Text text;
    private Text cmdlineText;

    @Override
    protected void createInitialView(Composite holder) {
        holder.setLayout(new FormLayout());

        final Button commandLineButton = new Button(holder, SWT.NONE);
        final FormData fd_commandLineLabel = new FormData();
        fd_commandLineLabel.bottom = new FormAttachment(0, 30);
        fd_commandLineLabel.right = new FormAttachment(0, 45);
        fd_commandLineLabel.top = new FormAttachment(0, 5);
        fd_commandLineLabel.left = new FormAttachment(0, 5);
        commandLineButton.setLayoutData(fd_commandLineLabel);
        commandLineButton.setText("&Run");

        cmdlineText = new Text(holder, SWT.WRAP | SWT.BORDER);
        cmdlineText.setText("cmdline");
        final FormData fd_cmdlineText = new FormData();
        fd_cmdlineText.bottom = new FormAttachment(0, 80);
        fd_cmdlineText.right = new FormAttachment(100, -5);
        fd_cmdlineText.top = new FormAttachment(commandLineButton, 0, SWT.TOP);
        fd_cmdlineText.left = new FormAttachment(commandLineButton, 5, SWT.RIGHT);
        cmdlineText.setLayoutData(fd_cmdlineText);

        final Composite composite = new Composite(holder, SWT.NONE);
        composite.setLayout(new FillLayout());
        final FormData fd_composite = new FormData();
        fd_composite.top = new FormAttachment(cmdlineText, 5, SWT.BOTTOM);
        fd_composite.bottom = new FormAttachment(100, -5);
        fd_composite.right = new FormAttachment(100, -5);
        fd_composite.left = new FormAttachment(commandLineButton, 0, SWT.LEFT);
        composite.setLayoutData(fd_composite);

        text = new Text(composite, SWT.BORDER);
    }

    void exec()
            throws IOException {
        String cmdline = text.getText();
        Runtime.getRuntime().exec(cmdline);
    }

    public static void main(String[] args)
            throws Throwable {
        // Classpath.dumpURLs(Stdio.cerr);
        new VisualExec().execute(args);
    }

}
