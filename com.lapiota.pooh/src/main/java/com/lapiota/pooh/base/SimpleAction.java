package com.lapiota.pooh.base;

import java.util.Iterator;

import net.bodz.bas.ui.UserInterface;
import net.bodz.swt.gui.DialogUI;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public abstract class SimpleAction implements IObjectActionDelegate {

    protected Shell         shell;
    private ISelection      selection;
    protected UserInterface UI;

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
        UI = new DialogUI(shell);
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    @Override
    public void run(IAction action) {
        if (selection == null || selection.isEmpty()) {
            System.err.println("Nothing selected");
            return;
        }
        if (selection instanceof IStructuredSelection) {
            Iterator<?> iterator = ((IStructuredSelection) selection)
                    .iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                try {
                    run(action, obj);
                } catch (Exception e) {
                    // stack trace..., or by eclipse error reporting?
                    UI.alert("Error", e.getMessage());
                    break; // preference (for this.getClass())?
                }
            }
        } else {
            System.err.println("Unknown selection mode");
        }
    }

    protected abstract void run(IAction action, Object selectedObject)
            throws Exception;

}
