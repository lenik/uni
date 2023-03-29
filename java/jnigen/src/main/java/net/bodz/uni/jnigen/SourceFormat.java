package net.bodz.uni.jnigen;

import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.type.overloaded.TypeInfoOptions;

public class SourceFormat {

    public SortOrder memberOrder = SortOrder.KEEP;

    public TypeInfoOptions toTypeInfoOptions() {
        TypeInfoOptions options = new TypeInfoOptions();
        options.memberOrder = memberOrder;
        return options;
    }

}
