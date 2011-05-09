package com.lapiota.pooh.spy;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ResourceProbeAction implements IObjectActionDelegate {

    private Shell      shell;
    private ISelection selection;

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    String TITLE = "Resource Probe";

    @Override
    public void run(IAction action) {
        if (selection.isEmpty()) {
            MessageDialog.openInformation(shell, TITLE, "Nothing selected");
            return;
        }
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structsel = (IStructuredSelection) selection;
            Iterator<?> it = structsel.iterator();
            SelectionDetailDialog dialog = new SelectionDetailDialog(shell, it);
            dialog.open();
        } else {
            String selectionType = selection.getClass().getName();
            MessageDialog.openInformation(shell, TITLE,
                    "Unknown selection type: " + selectionType);
        }
    }

}
