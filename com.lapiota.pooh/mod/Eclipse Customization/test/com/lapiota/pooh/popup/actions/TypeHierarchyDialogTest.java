package com.lapiota.pooh.popup.actions;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.lapiota.pooh.popup.actions.TypeHierarchyDialog;

public class TypeHierarchyDialogTest {

    public static void main(String[] args) {
        TypeHierarchyDialog dialog = new TypeHierarchyDialog(new Shell(),
                Button.class);
        dialog.open();
    }

}
