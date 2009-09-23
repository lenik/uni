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
import net.bodz.dist.pro.util.Registrant;
import net.bodz.lapiota.wrappers.BasicGUI;
import net.bodz.swt.adapters.TextAdapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@BootInfo(userlibs = "bodz_dist")
@DisplayName("ABTSACG")
@Doc("ABTS Activate Code Generator")
@ProgramName("Abtsacg")
@PreferredSize(width = 400, height = 200)
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
@WebSite("http://www.bodz.net/products/distins")
public class ActivateCodeGenerator extends BasicGUI {

    private Text  hostIdText;
    private Text  hashText;
    private Combo targetStringCombo;
    private Text  codeText;

    @Override
    protected void createInitialView(final Composite holder)
            throws UIException {

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        holder.setLayout(gridLayout);

        SelectionListener regenerate = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                holder.getDisplay().asyncExec(new Runnable() {
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

        final Label hostIdLabel = new Label(holder, SWT.NONE);
        hostIdLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false));
        hostIdLabel.setText(ProtectNLS
                .getString("ActivateCodeGenerator.hostId")); //$NON-NLS-1$

        hostIdText = new Text(holder, SWT.BORDER);
        hostIdText.setText("Host ID"); //$NON-NLS-1$
        final GridData gd_hostIdText = new GridData(SWT.FILL, SWT.CENTER, true,
                false, 2, 1);
        hostIdText.setLayoutData(gd_hostIdText);
        hostIdText.addSelectionListener(regenerate);
        TextAdapters.autoSelect(hostIdText);

        final Label hashLabel = new Label(holder, SWT.NONE);
        hashLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false));
        hashLabel.setText("Hash Variation: ");

        hashText = new Text(holder, SWT.BORDER);
        hashText.setText("0"); //$NON-NLS-1$
        final GridData hashTextData = new GridData(SWT.FILL, SWT.CENTER, true,
                false);
        hashText.setLayoutData(hashTextData);
        hashText.addSelectionListener(regenerate);
        TextAdapters.autoSelect(hashText);

        Button hashCalcButton = new Button(holder, SWT.NONE);
        hashCalcButton.setText("Calculator");
        hashCalcButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Registrant registrant = new Registrant();
                UI.alert("Edit", registrant);
                int hash = registrant.hashCode();
                hashText.setText(String.valueOf(hash));
            }
        });

        final Label targetStringLabel = new Label(holder, SWT.NONE);
        targetStringLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
                false, false));
        targetStringLabel.setText(ProtectNLS
                .getString("ActivateCodeGenerator.targetString")); //$NON-NLS-1$

        targetStringCombo = new Combo(holder, SWT.BORDER);
        targetStringCombo.setText("Target String"); //$NON-NLS-1$
        targetStringCombo.add("pass");
        final GridData gd_targetStringText = new GridData(SWT.FILL, SWT.CENTER,
                true, false, 2, 1);
        targetStringCombo.setLayoutData(gd_targetStringText);
        targetStringCombo.addSelectionListener(regenerate);

        final Label codeLabel = new Label(holder, SWT.NONE);
        codeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false));
        codeLabel.setText(ProtectNLS
                .getString("ActivateCodeGenerator.activationCode")); //$NON-NLS-1$

        codeText = new Text(holder, SWT.BORDER | SWT.READ_ONLY);
        codeText.setText("Code"); //$NON-NLS-1$
        final GridData gd_codeText = new GridData(SWT.FILL, SWT.CENTER, true,
                false, 2, 1);
        codeText.setLayoutData(gd_codeText);
        codeText.addSelectionListener(regenerate);
        TextAdapters.autoSelect(codeText);

        final Label separator = new Label(holder, SWT.HORIZONTAL
                | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, true,
                3, 1));

        final Composite buttons = new Composite(holder, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
                3, 1));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        gridLayout_1.verticalSpacing = 0;
        gridLayout_1.marginWidth = 0;
        gridLayout_1.marginHeight = 0;
        buttons.setLayout(gridLayout_1);

        final Button generateButton = new Button(buttons, SWT.NONE);
        generateButton.setText(ProtectNLS
                .getString("ActivateCodeGenerator.generate")); //$NON-NLS-1$
        generateButton.addSelectionListener(regenerate);

        final Button copyButton = new Button(buttons, SWT.NONE);
        copyButton.setText(ProtectNLS.getString("ActivateCodeGenerator.copy")); //$NON-NLS-1$
        copyButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String s = codeText.getText();
                Object[] data = { s };
                Transfer[] dataTypes = { TextTransfer.getInstance() };
                new Clipboard(holder.getDisplay()).setContents(data, dataTypes);
            }
        });
    }

    void generate() {
        try {
            String hostId = hostIdText.getText();
            String target = targetStringCombo.getText();
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
