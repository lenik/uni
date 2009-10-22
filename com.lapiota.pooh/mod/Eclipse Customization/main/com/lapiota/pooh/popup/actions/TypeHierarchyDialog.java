package com.lapiota.pooh.popup.actions;

import net.bodz.swt.controls.util.Controls;
import net.bodz.swt.util.SWTResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TypeHierarchyDialog extends Dialog {

    protected Object result;
    protected Shell  shell;

    Image            ICON_CLASS;
    Image            ICON_IFACE;

    public TypeHierarchyDialog(Shell parent, Class<?> root) {
        super(parent, SWT.NONE);
        createContents(root);
    }

    public Object open() {
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        return result;
    }

    protected void createContents(Class<?> root) {
        ICON_CLASS = SWTResources.getImageRes("/icons/obj16/class_obj.gif");
        ICON_IFACE = SWTResources.getImageRes("/icons/obj16/int_obj.gif");

        shell = new Shell(getParent(), SWT.APPLICATION_MODAL | SWT.TITLE
                | SWT.MAX | SWT.BORDER | SWT.RESIZE | SWT.CLOSE);
        shell.setLayout(new FillLayout());
        shell.setSize(400, 300);
        shell.setText("Type Hierarchy Of: " + root);

        ScrolledComposite scroll = new ScrolledComposite(shell, SWT.V_SCROLL
                | SWT.H_SCROLL);
        Composite parent = new Composite(scroll, SWT.NONE);
        scroll.setContent(parent);
        parent.setLayout(new FillLayout());

        buildTree(parent, root);

        Point size = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        parent.setSize(size);

        Controls.resizeToFit(shell, size, //
                new Point(200, 50), shell.getSize());
        Controls.center(shell);
    }

    Tree buildTree(Composite parent, Class<?> c) {
        Tree tree = new Tree(parent, SWT.NONE);
        while (c != null) {
            TreeItem citem = new TreeItem(tree, 0);
            citem.setText(c.getCanonicalName());
            citem.setImage(ICON_CLASS);
            buildIfaceTree(citem, c.getInterfaces());
            citem.setExpanded(true);
            c = c.getSuperclass();
        }
        return tree;
    }

    void buildIfaceTree(TreeItem parent, Class<?>[] ifaces) {
        for (Class<?> iface : ifaces) {
            TreeItem item = new TreeItem(parent, 0);
            item.setText(iface.getCanonicalName());
            item.setImage(ICON_IFACE);
            buildIfaceTree(item, iface.getInterfaces());
            item.setExpanded(true);
        }
    }

}
