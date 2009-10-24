package com.lapiota.pooh.spy;

import java.util.Iterator;

import net.bodz.bas.types.util.Strings;
import net.bodz.bas.ui.UserInterface;
import net.bodz.swt.gui.DialogUI;
import net.bodz.swt.widgets.util.Controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class SelectionDetailDialog extends Dialog {

    protected Object    result;
    protected Shell     shell;

    private UserInterface UI;

    public SelectionDetailDialog(Shell parent, Iterator<?> it) {
        super(parent, SWT.NONE);
        UI = new DialogUI(parent);
        createContents(it);
    }

    public Object open() {
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        return result;
    }

    int TEXT_LEN = 50;
    int TEXT_MAX = 300;

    protected void createContents(Iterator<?> it) {
        shell = new Shell(getParent(), SWT.APPLICATION_MODAL | SWT.TITLE
                | SWT.MAX | SWT.BORDER | SWT.RESIZE | SWT.CLOSE);
        shell.setLayout(new FillLayout());
        shell.setSize(400, 300);
        shell.setText("Selection Details");

        ScrolledComposite scroll = new ScrolledComposite(shell, SWT.V_SCROLL
                | SWT.H_SCROLL);
        Composite parent = new Composite(scroll, SWT.NONE);
        scroll.setContent(parent);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        parent.setLayout(gridLayout);

        int index = 0;
        while (it.hasNext()) {
            final Object sel = it.next();
            // 1
            new Label(parent, SWT.NONE).setText(++index + ". ");

            if (sel == null) {
                // 2 - null
                Label nullLabel = new Label(parent, SWT.NONE);
                nullLabel.setText("(null)");
                GridData spanData = new GridData();
                spanData.horizontalSpan = 3;
                nullLabel.setLayoutData(spanData);
            } else {
                // 2 - non-nulll
                final Class<? extends Object> type = sel.getClass();
                Link typeLink = new Link(parent, SWT.NONE);
                typeLink.setText("<a>" + type.getCanonicalName() + "</a>");
                // typeLink.setForeground(TYPE_COLOR);
                typeLink.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        new TypeHierarchyDialog(shell, type).open();
                    }
                });

                // 3
                final String text = sel.toString();
                String textAbbr = Strings.ellipse(text, TEXT_LEN);
                if (text.length() < TEXT_MAX) {
                    Label textLabel = new Label(parent, SWT.NONE);
                    textLabel.setText(textAbbr);
                    textLabel.setToolTipText(text);
                } else {
                    Link textLink = new Link(parent, SWT.NONE);
                    textLink.setText("<a>" + textAbbr + "</a>");
                    textLink.setToolTipText(Strings.ellipse(text, TEXT_MAX));
                    textLink.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            MessageDialog.openInformation(shell,
                                    "ToString Text", text);
                        }
                    });
                }

                // 4
                Button button = new Button(parent, SWT.NONE);
                button.setText("...");
                button.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        UI.alert(type.getCanonicalName() + " Detail", sel);
                    }
                });
            }
        }

        Point size = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        parent.setSize(size);
        Controls.resizeToFit(shell, size, //
                new Point(200, 50), shell.getSize());
        Controls.center(shell);
    }

}
