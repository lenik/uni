package net.bodz.uni.typedet.type;

import java.math.BigDecimal;

import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class DecimalType
        extends AbstractSamplesType {

    BigDecimal min;
    BigDecimal max;

    SmallDatasetStats<Integer> lenHisto = new SmallDatasetStats<>();
    SmallDatasetStats<Integer> intLenHisto = new SmallDatasetStats<>();
    SmallDatasetStats<Integer> decLenHisto = new SmallDatasetStats<>();

    public DecimalType() {
        this(DEFAULT_HISTO_MAXSIZE);
    }

    public DecimalType(int histoSize) {
        // histo = new LRUHistoMap<>(histoSize);
        // histo = new HistoMap<>(histoSize);
    }

    @Override
    protected final void merge(AbstractSamplesType o) {
        super.merge(o);
        merge((DecimalType) o);
    }

    protected void merge(DecimalType o) {
        if (min == null || o.min.compareTo(min) < 0)
            min = o.min;
        if (max == null || max.compareTo(o.max) < 0)
            max = o.max;

        // histo.merge(o.histo);
        lenHisto.merge(o.lenHisto);
    }

    @Override
    public void addSample(String sample) {
        if (sample == null)
            throw new NullPointerException("sample");

        BigDecimal num = new BigDecimal(sample);
        // histo.add(num);

        if (min == null || num.compareTo(min) < 0)
            min = num;
        if (max == null || num.compareTo(max) > 0)
            max = num;

        int len = sample.length();

        int dot = sample.indexOf('.');
        int intLen, decLen;
        if (dot == -1) {
            intLen = len;
            decLen = 0;
        } else {
            intLen = dot;
            decLen = len - dot - 1;
        }

        lenHisto.add(len);
        intLenHisto.add(intLen);
        decLenHisto.add(decLen);
    }

}
