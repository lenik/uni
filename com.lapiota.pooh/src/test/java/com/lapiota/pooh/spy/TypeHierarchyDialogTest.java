package com.lapiota.pooh.spy;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.lapiota.pooh.spy.TypeHierarchyDialog;

public class TypeHierarchyDialogTest {

    public static void main(String[] args) {
        TypeHierarchyDialog dialog = new TypeHierarchyDialog(new Shell(),
                Button.class);
        dialog.open();
    }

}
