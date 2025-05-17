package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.meta.decl.NotNull;

public interface JavaConstructor<T> {

    String construct(T value, @NotNull IJavaConstructContext context);

}
