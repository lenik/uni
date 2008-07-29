package net.bodz.pooh.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;

public class RunCommandDialog extends Dialog {

    private Button browseButton;
    private IHandlerService handlerService;

    private Button cancelButton;
    private Button okButton;
    private Text commandIdText;
    protected Object result;
    protected Shell  shell;

    /**
     * Create the dialog
     * @param parent
     * @param style
     */
    public RunCommandDialog(Shell parent, int style,IHandlerService handlerService) {
        super(parent, style);
        this.handlerService=handlerService;
    }

    /**
     * Create the dialog
     * @param parent
     */
    public RunCommandDialog(Shell parent,IHandlerService handlerService) {
        this(parent, SWT.NONE,handlerService);
    }

    /**
     * Open the dialog
     * @return the result
     */
    public Object open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        return result;
    }

    /**
     * Create contents of the dialog
     */
    protected void createContents() {
        shell = new Shell(getParent(), SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.CLOSE);
        shell.setLayout(new FormLayout());
        shell.setSize(398, 110);
        shell.setText("Run Command...");

        final Label commandIdLabel = new Label(shell, SWT.NONE);
        final FormData fd_commandIdLabel = new FormData();
        fd_commandIdLabel.top = new FormAttachment(0, 16);
        fd_commandIdLabel.left = new FormAttachment(0, 15);
        commandIdLabel.setLayoutData(fd_commandIdLabel);
        commandIdLabel.setText("Command &Id: ");

        commandIdText = new Text(shell, SWT.BORDER);
        final FormData fd_commandIdText = new FormData();
        fd_commandIdText.right = new FormAttachment(100, -14);
        fd_commandIdText.left = new FormAttachment(commandIdLabel, 5, SWT.RIGHT);
        fd_commandIdText.top = new FormAttachment(commandIdLabel, 0, SWT.TOP);
        commandIdText.setLayoutData(fd_commandIdText);

        okButton = new Button(shell, SWT.NONE);
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                runCommand();
            }
        });
        final FormData fd_okButton = new FormData();
        okButton.setLayoutData(fd_okButton);
        okButton.setText("&Run");

        cancelButton = new Button(shell, SWT.NONE);
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                shell.close();
            }
        });
        fd_okButton.bottom = new FormAttachment(cancelButton, 0, SWT.BOTTOM);
        fd_okButton.right = new FormAttachment(cancelButton, -5, SWT.LEFT);
        final FormData fd_cancelButton = new FormData();
        fd_cancelButton.bottom = new FormAttachment(100, -15);
        fd_cancelButton.right = new FormAttachment(commandIdText, 0, SWT.RIGHT);
        cancelButton.setLayoutData(fd_cancelButton);
        cancelButton.setText("&Cancel");

        browseButton = new Button(shell, SWT.NONE);
        final FormData fd_browseButton = new FormData();
        fd_browseButton.bottom = new FormAttachment(okButton, 0, SWT.BOTTOM);
        fd_browseButton.right = new FormAttachment(okButton, -5, SWT.LEFT);
        browseButton.setLayoutData(fd_browseButton);
        browseButton.setText("&Browse");
        shell.setTabList(new Control[] {commandIdText, okButton, cancelButton, browseButton});
        //
    }

    private void runCommand() {
        String commandId = commandIdText.getText();
        try {
            handlerService.executeCommand(commandId, null);
        } catch (Exception e) {
        }
    }

}
