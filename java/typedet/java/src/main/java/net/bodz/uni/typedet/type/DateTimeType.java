package net.bodz.uni.typedet.type;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class DateTimeType
        extends AbstractSamplesType {

    static final Map<String, DateTimeFormatter> formats = new LinkedHashMap<>();
    {
        formats.put("fullDateTime", DateTimeFormat.fullDateTime());
        formats.put("longDateTime", DateTimeFormat.longDateTime());
        formats.put("mediumDateTime", DateTimeFormat.mediumDateTime());
        formats.put("fullDate", DateTimeFormat.fullDate());
        formats.put("longDate", DateTimeFormat.longDate());
        formats.put("mediumDate", DateTimeFormat.mediumDate());
        formats.put("fullTime", DateTimeFormat.fullTime());
        formats.put("longTime", DateTimeFormat.longTime());
        formats.put("mediumTime", DateTimeFormat.mediumTime());
    }

    DateTime min;
    DateTime max;

    SmallDatasetStats<DateTime> histo = new SmallDatasetStats<>();

    SmallDatasetStats<Integer> yearHisto = new SmallDatasetStats<>();
    SmallDatasetStats<Integer> monthHisto = new SmallDatasetStats<>();
    SmallDatasetStats<Integer> dayHisto = new SmallDatasetStats<>();
    SmallDatasetStats<Integer> zoneHisto = new SmallDatasetStats<>();

    @Override
    protected final void merge(AbstractSamplesType o) {
        super.merge(o);
        merge((DateTimeType) o);
    }

    protected void merge(DateTimeType o) {
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
        DateTime dateTime = null;
        for (String fmtName : formats.keySet()) {
            DateTimeFormatter format = formats.get(fmtName);
            // DateTimeZone zone = format.getZone();
            try {
                dateTime = format.parseDateTime(sample);
                break;
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        if (dateTime == null) {
            errorCount++;
            return;
        }

        histo.add(dateTime);
        yearHisto.add(dateTime.getYear());
        monthHisto.add(dateTime.getMonthOfYear());
        dayHisto.add(dateTime.getDayOfMonth());

        DateTimeZone zone = dateTime.getZone();
        int offset = zone.getOffset(0);
        zoneHisto.add(offset);
    }

}
