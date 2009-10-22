package com.lapiota.pooh.popup.actions;

import org.eclipse.jface.action.IAction;

public class ResetClasspathsAction extends SimpleAction {

    @Override
    protected void run(IAction action, Object selectedObject) throws Exception {
        BashWithContextAction.cpCache.clear();
    }

}
