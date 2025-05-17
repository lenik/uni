package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.codegen.IJavaImporter;
import net.bodz.bas.meta.decl.NotNull;

public interface IJavaConstructContext {

    @NotNull
    IJavaImporter getImporter();

    default String name(Class<?> type) {
        return getImporter().importName(type);
    }

    boolean inArray();

}
