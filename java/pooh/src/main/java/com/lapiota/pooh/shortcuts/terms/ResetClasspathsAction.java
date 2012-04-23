package com.lapiota.pooh.shortcuts.terms;

import org.eclipse.jface.action.IAction;

import com.lapiota.pooh.base.SimpleAction;

public class ResetClasspathsAction extends SimpleAction {

    @Override
    protected void run(IAction action, Object selectedObject) throws Exception {
        BashWithContextAction.cpCache.clear();
    }

}
