package net.bodz.uni.catme.filter;

import java.util.List;

import net.bodz.uni.catme.IFrame;
import net.bodz.uni.catme.ITextFilter;
import net.bodz.uni.catme.ITextFilterClass;

public class StringFilterClass
        implements
            ITextFilterClass {

    final StringFnEnum fn;

    public StringFilterClass(StringFnEnum fn) {
        if (fn == null)
            throw new NullPointerException("fn");
        this.fn = fn;
    }

    @Override
    public boolean isScript() {
        return false;
    }

    @Override
    public ITextFilter createFilter(IFrame frame, List<String> args) {
        return new StringFilter(this, frame, args);
    }

    public static final StringFilterClass TOUPPER = new StringFilterClass(StringFnEnum.TO_UPPER);
    public static final StringFilterClass TOLOWER = new StringFilterClass(StringFnEnum.TO_LOWER);
    public static final StringFilterClass NOP = new StringFilterClass(StringFnEnum.NOP);
    public static final StringFilterClass INDENT = new StringFilterClass(StringFnEnum.INDENT);
    public static final StringFilterClass TABULAR = new StringFilterClass(StringFnEnum.TABULAR);

}
