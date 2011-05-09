package com.lapiota.pooh.spy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.Perspective;
import org.eclipse.ui.internal.WorkbenchPage;

@SuppressWarnings("restriction")
public class DumpPerspectiveAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;
    ISelection               selection;

    @Override
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    static String TITLE = "Dump Perspective";

    @Override
    public void run(IAction action) {
        IWorkbenchPage activePage = window.getActivePage();
        if (activePage instanceof WorkbenchPage) {
            Perspective persp = ((WorkbenchPage) activePage)
                    .getActivePerspective();
            System.out.println("Not implemented...: " + persp);
        }
    }

    @Override
    public void dispose() {
    }

}
