package net.bodz.bas.stat.distrib.type;

import java.util.Map;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.json.JsonFn;
import net.bodz.bas.fmt.json.JsonVariant;
import net.bodz.bas.json.JsonObject;
import net.bodz.bas.repr.form.SortOrder;

public class JsonStats
        extends AbstractStructStats {

    private static final long serialVersionUID = 1L;

    CharStats firstCharStats = new CharStats();

    public JsonStats() {
        super();
    }

    public JsonStats(SortOrder sortOrder) {
        super(sortOrder);
    }

    public JsonStats(int lruSize) {
        super(lruSize);
    }

    public JsonStats(int lruSize, int maxCountToDrop) {
        super(lruSize, maxCountToDrop);
    }

    public JsonStats(Map<String, Integer> _orig) {
        super(_orig);
    }

    @Override
    protected void structAnalyze(String json) {
        lenStats.add(json.length());
        if (!json.isEmpty()) {
            char firstChar = json.charAt(0);
            firstCharStats.add(firstChar);
        }

        try {
            JsonVariant any = JsonFn.parseAny(json);
            switch (any.getType()) {
            case OBJECT:
                JsonObject jo = (JsonObject) any.getValue();
                for (String sk : jo.keySet()) {
                    keyStats.add(sk);
                }
                break;
            case SCALAR:
            case ARRAY:
            }
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    protected void merge(AbstractStructStats o) {
        super.merge(o);
        merge((JsonStats) o);
    }

    protected void merge(JsonStats o) {
        firstCharStats.merge(o.firstCharStats);
    }

}
