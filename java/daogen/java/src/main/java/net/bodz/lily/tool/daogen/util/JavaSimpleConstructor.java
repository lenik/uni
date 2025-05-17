package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.meta.decl.NotNull;

public interface JavaSimpleConstructor<T>
        extends JavaConstructor<T> {

    String construct(@NotNull T value);

    @Override
    default String construct(@NotNull T value, @NotNull IJavaConstructContext context) {
        return construct(value);
    }

}
