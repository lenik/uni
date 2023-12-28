package net.bodz.bas.stat.distrib.type;

import java.util.Map;

import net.bodz.bas.repr.form.SortOrder;

public class HtmlStats
        extends AbstractStructStats {

    private static final long serialVersionUID = 1L;

    public HtmlStats() {
        super();
    }

    public HtmlStats(SortOrder sortOrder) {
        super(sortOrder);
    }

    public HtmlStats(int lruSize) {
        super(lruSize);
    }

    public HtmlStats(int lruSize, int maxCountToDrop) {
        super(lruSize, maxCountToDrop);
    }

    public HtmlStats(Map<String, Integer> _orig) {
        super(_orig);
    }

    @Override
    protected void structAnalyze(String html) {
        // parser html -> dom.
    }

    @Override
    protected void merge(AbstractStructStats o) {
        super.merge(o);
        merge((HtmlStats) o);
    }

    protected void merge(HtmlStats o) {
        lenStats.merge(o.lenStats);
    }

}
