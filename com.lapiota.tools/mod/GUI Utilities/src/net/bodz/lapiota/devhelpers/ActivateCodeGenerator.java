package net.bodz.lapiota.devhelpers;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.DisplayName;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.a.WebSite;
import net.bodz.bas.ui.UIException;
import net.bodz.bas.ui.a.PreferredSize;
import net.bodz.dist.pro.nls.ProtectNLS;
import net.bodz.dist.pro.pm.ProtectException;
import net.bodz.dist.pro.util.ActivationByTargetString;
import net.bodz.lapiota.wrappers.BasicGUI;
import net.bodz.swt.adapters.TextAdapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@BootInfo(userlibs = "bodz_dist")
@DisplayName("ABTSACG")
@Doc("ABTS Activate Code Generator")
@ProgramName("Abtsacg")
@PreferredSize(width = 400, height = 200)
@RcsKeywords(id = "$Id: QuickTone.java 15 2009-04-23 11:09:10Z Shecti $")
@Version( { 0, 1 })
@WebSite("http://www.bodz.net/products/distins")
public class ActivateCodeGenerator extends BasicGUI {

    private Text codeText;
    private Text targetStringText;
    private Text hostIdText;

    @Override
    protected void createInitialView(final Composite parent) throws UIException, SWTException {
        Composite composite = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        composite.setLayout(gridLayout);

        SelectionListener rg = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                parent.getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        generate();
                    }
                });
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };

        final Label hostIdLabel = new Label(composite, SWT.NONE);
        hostIdLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        hostIdLabel.setText(ProtectNLS.getString("ActivateCodeGenerator.hostId")); //$NON-NLS-1$

        hostIdText = new Text(composite, SWT.BORDER);
        hostIdText.setText("Host ID"); //$NON-NLS-1$
        final GridData gd_hostIdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        hostIdText.setLayoutData(gd_hostIdText);
        hostIdText.addSelectionListener(rg);
        TextAdapters.autoSelect(hostIdText);

        final Label targetStringLabel_1 = new Label(composite, SWT.NONE);
        targetStringLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        targetStringLabel_1.setText(ProtectNLS.getString("ActivateCodeGenerator.targetString")); //$NON-NLS-1$

        targetStringText = new Text(composite, SWT.BORDER);
        targetStringText.setText("Target String"); //$NON-NLS-1$
        final GridData gd_targetStringText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        targetStringText.setLayoutData(gd_targetStringText);
        targetStringText.addSelectionListener(rg);
        TextAdapters.autoSelect(targetStringText);

        final Label activationCodeLabel = new Label(composite, SWT.NONE);
        activationCodeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        activationCodeLabel.setText(ProtectNLS.getString("ActivateCodeGenerator.activationCode")); //$NON-NLS-1$

        codeText = new Text(composite, SWT.BORDER);
        codeText.setText("Code"); //$NON-NLS-1$
        final GridData gd_codeText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        codeText.setLayoutData(gd_codeText);
        codeText.addSelectionListener(rg);
        TextAdapters.autoSelect(codeText);

        final Label label = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, true, 2, 1));
        label.setText("Label");

        final Composite buttons = new Composite(composite, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        gridLayout_1.verticalSpacing = 0;
        gridLayout_1.marginWidth = 0;
        gridLayout_1.marginHeight = 0;
        buttons.setLayout(gridLayout_1);

        final Button generateButton = new Button(buttons, SWT.NONE);
        generateButton.setText(ProtectNLS.getString("ActivateCodeGenerator.generate")); //$NON-NLS-1$
        generateButton.addSelectionListener(rg);

        final Button copyButton = new Button(buttons, SWT.NONE);
        copyButton.setText(ProtectNLS.getString("ActivateCodeGenerator.copy")); //$NON-NLS-1$
        copyButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String s = codeText.getText();
                Object[] data = { s };
                Transfer[] dataTypes = { TextTransfer.getInstance() };
                new Clipboard(parent.getDisplay()).setContents(data, dataTypes);
            }
        });
    }

    void generate() {
        try {
            String hostId = hostIdText.getText();
            String target = targetStringText.getText();
            ActivationByTargetString abt = new ActivationByTargetString(hostId);
            String code = abt.generateFor(target);
            codeText.setText(code);
        } catch (ProtectException e) {
            UI.alert(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Throwable {
        new ActivateCodeGenerator().run(args);
    }

}
