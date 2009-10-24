package com.lapiota.pooh.shortcuts.terms;

import java.io.File;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;

import com.lapiota.pooh.base.SimpleAction;
import com.lapiota.pooh.util.SysShells;

public class SystemTerminalAction extends SimpleAction {

    @Override
    protected void run(IAction action, Object selectedObject) throws Exception {
        if (selectedObject instanceof IResource) {
            openResource((IResource) selectedObject);
        } else if (selectedObject instanceof IAdaptable) {
            IAdaptable a = (IAdaptable) selectedObject;
            Object o = a.getAdapter(IResource.class);
            if (o != null) {
                IResource res = (IResource) o;
                openResource(res);
            }
        }
    }

    void openResource(IResource res) {
        // res.getFullPath();
        IPath path = res.getLocation();
        if (path != null) {
            File dir = path.toFile();
            while (dir != null) {
                if (dir.isDirectory() && dir.exists()) {
                    open(dir);
                    return;
                }
                dir = dir.getParentFile();
            }
            assert false;
        }
    }

    void open(File dir) {
        SysShells.open(shell, dir);
    }

}
