package net.bodz.bas.stat.distrib.type;

import java.util.function.Function;

import net.bodz.bas.repr.form.SortOrder;

public class StructConfig {

    public SortOrder defaultSortOrder = SortOrder.KEEP;
    public int defaultLRUSize;
    public int defaultMaxCountToDrop;

    public String levelSep = "_";
    public int maxLevel = 2;

    public boolean extractArray;
    public int arrayBase = 1;

    public Function<String, String> nameMapper = Function.identity();

    public static final StructConfig DEFAULT = new StructConfig();

}
