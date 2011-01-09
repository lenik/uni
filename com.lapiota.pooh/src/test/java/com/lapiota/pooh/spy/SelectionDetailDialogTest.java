package com.lapiota.pooh.spy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

public class SelectionDetailDialogTest {

    @Test
    public void test() throws Exception {
        List<Object> list = new ArrayList<Object>();
        list.add("One");
        list.add(2.002002002002);
        List<Object> threev = new ArrayList<Object>();
        threev.add(1L);
        threev.add(2);
        threev.add(3f);
        list.add(threev);
        Object[] fourv = { 1, 2, 3, "4" };
        list.add(fourv);
        list.add(null);
        list.add("Six");
        SelectionDetailDialog dialog = new SelectionDetailDialog(new Shell(), list.iterator());
        dialog.open();
    }

}
