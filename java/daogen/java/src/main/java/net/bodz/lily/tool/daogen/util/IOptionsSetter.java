package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.meta.decl.NotNull;
import net.bodz.bas.rtx.IMutableOptions;
import net.bodz.bas.t.catalog.IColumnMetadata;

public interface IOptionsSetter {

    void setOptions(@NotNull IMutableOptions options, IColumnMetadata column);

}
