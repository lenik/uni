package net.bodz.uni.typedet.type;

import net.bodz.bas.stat.distrib.LRUHistoMap;
import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class IntegerType
        extends AbstractSamplesType {

    int minLen;
    int maxLen;
    long min;
    long max;

    SmallDatasetStats<Long> histo;
    SmallDatasetStats<Integer> lenHisto = new SmallDatasetStats<>();

    public IntegerType(int histoSize) {
        histo = new LRUHistoMap<>(histoSize);
        // histo = new HistoMap<>(histoSize);
    }

    @Override
    protected final void merge(AbstractSamplesType o) {
        super.merge(o);
        merge((IntegerType) o);
    }

    protected void merge(IntegerType o) {
        if (o.minLen < minLen)
            minLen = o.minLen;
        if (maxLen < o.maxLen)
            maxLen = o.maxLen;

        if (o.min < min)
            min = o.min;
        if (max < o.max)
            max = o.max;

        histo.merge(o.histo);
        lenHisto.merge(o.lenHisto);
    }

    @Override
    public void addSample(String sample) {
        if (sample == null)
            throw new NullPointerException("sample");

        long num = Long.parseLong(sample);
        histo.add(num);

        if (num < min)
            min = num;
        if (num > max)
            max = num;

        int len = sample.length();
        if (len < minLen)
            minLen = len;
        if (len > maxLen)
            maxLen = len;

        lenHisto.add(len);
    }

}
