package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.codegen.IJavaImporter;
import net.bodz.bas.meta.decl.NotNull;

public class JavaConstructContext
        implements IJavaConstructContext {

    IJavaImporter importer;
    boolean inArray;

    public JavaConstructContext(@NotNull IJavaImporter importer) {
        this.importer = importer;
    }

    @NotNull
    @Override
    public IJavaImporter getImporter() {
        return importer;
    }

    @Override
    public boolean inArray() {
        return inArray;
    }

    public void setInArray(boolean inArray) {
        this.inArray = inArray;
    }

}
