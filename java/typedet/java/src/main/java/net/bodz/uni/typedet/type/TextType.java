package net.bodz.uni.typedet.type;

import net.bodz.bas.stat.distrib.LRUHistoMap;
import net.bodz.bas.stat.distrib.SmallDatasetStats;
import net.bodz.bas.stat.distrib.type.CharStats;
import net.bodz.bas.stat.distrib.type.IntStats;

public class TextType
        extends AbstractSamplesType {

    SmallDatasetStats<String> histo;
    IntStats lenStats = new IntStats();
    CharStats startCharHisto = new CharStats();
    CharStats endCharHisto = new CharStats();

    public TextType(int histoSize) {
        histo = new LRUHistoMap<>(histoSize);
        // histo = new HistoMap<>(histoSize);
    }

    @Override
    protected final void merge(AbstractSamplesType o) {
        super.merge(o);
        merge((TextType) o);
    }

    protected void merge(TextType o) {
        histo.merge(o.histo);
        lenStats.merge(o.lenStats);
        startCharHisto.merge(o.startCharHisto);
        endCharHisto.merge(o.endCharHisto);
    }

    @Override
    public void addSample(String sample) {
        if (sample == null)
            throw new NullPointerException("sample");

        histo.add(sample);

        int len = sample.length();
        lenStats.add(len);

        if (!sample.isEmpty()) {
            char startChar = sample.charAt(0);
            char endChar = sample.charAt(len - 1);
            startCharHisto.add(startChar);
            endCharHisto.add(endChar);
        }
    }

}
