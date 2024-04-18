package net.bodz.uni.typedet.type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bodz.bas.c.java.time.DateTimes;
import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class LocalDateTimeType
        extends AbstractSamplesType {

    static final Map<String, DateTimeFormatter> formats = new LinkedHashMap<>();
    {
        formats.put("LOCAL_DATE_TIME", DateTimes.ISO_LOCAL_DATE_TIME);
        formats.put("LOCAL_DATE", DateTimes.ISO_LOCAL_DATE);
//        formats.put("D10T8", DateTimes.D10T8);
//        formats.put("YY_MM_DD", DateTimes.YY_MM_DD);
    }

    LocalDateTime min;
    LocalDateTime max;

    SmallDatasetStats<LocalDateTime> histo = new SmallDatasetStats<>();

    SmallDatasetStats<Integer> yearHisto = new SmallDatasetStats<>();
    SmallDatasetStats<Integer> monthHisto = new SmallDatasetStats<>();
    SmallDatasetStats<Integer> dayHisto = new SmallDatasetStats<>();

    @Override
    protected final void merge(AbstractSamplesType o) {
        super.merge(o);
        merge((LocalDateTimeType) o);
    }

    protected void merge(LocalDateTimeType o) {
        if (o.min.compareTo(min) < 0)
            min = o.min;
        if (max.compareTo(o.max) < 0)
            max = o.max;

        histo.merge(o.histo);
        yearHisto.merge(o.yearHisto);
        monthHisto.merge(o.monthHisto);
        dayHisto.merge(o.dayHisto);
    }

    @Override
    public void addSample(String sample) {
        LocalDateTime zonedDateTime = null;
        for (String fmtName : formats.keySet()) {
            DateTimeFormatter format = formats.get(fmtName);
            // ZoneId zone = format.getZone();
            try {
                zonedDateTime = LocalDateTime.parse(sample, format);
                break;
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        if (zonedDateTime == null) {
            errorCount++;
            return;
        }

        histo.add(zonedDateTime);
        yearHisto.add(zonedDateTime.getYear());
        monthHisto.add(zonedDateTime.getMonthValue());
        dayHisto.add(zonedDateTime.getDayOfMonth());
    }

}
