package com.lapiota.pooh.spy;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

public class TypeHierarchyDialogTest {

    @Test
    public void test() {
        TypeHierarchyDialog dialog = new TypeHierarchyDialog(new Shell(),
                Button.class);
        dialog.open();
    }

}
