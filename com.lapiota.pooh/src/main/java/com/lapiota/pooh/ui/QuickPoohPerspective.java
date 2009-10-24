package com.lapiota.pooh.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class QuickPoohPerspective implements IPerspectiveFactory {

    @Override
    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();

        IFolderLayout trees = layout.createFolder("trees", IPageLayout.LEFT,
                0.25f, editorArea);
        trees.addView("org.eclipse.jdt.ui.PackageExplorer");
        // trees.addView(IPageLayout.ID_RES_NAV);
        trees.addView(IPageLayout.ID_OUTLINE);
        // trees.addView(IPageLayout.ID_PROP_SHEET);

        IFolderLayout outputs = layout.createFolder("outputs",
                IPageLayout.BOTTOM, 0.2f, editorArea);
        outputs.addView(IPageLayout.ID_PROBLEM_VIEW);
        outputs.addView("org.eclipse.ui.console.ConsoleView");
        outputs.addView(IPageLayout.ID_PROGRESS_VIEW);
    }

}
