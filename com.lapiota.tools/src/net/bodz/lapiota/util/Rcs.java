package net.bodz.lapiota.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.freejava.err.IdentifiedException;
import net.sf.freejava.util.AutoType;

@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
public class Rcs {

    private static DateFormat dateFormat;
    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:sss");
    }

    public static Map<String, Object> parseId(String id) {
        id = id.substring(0, id.length() - 1);
        String[] parts = id.split("\\s+");
        Map<String, Object> map = new HashMap<String, Object>();
        switch (parts.length) {
        case 7: // state
            map.put("state", parts[6]);
        case 6: // lenik
            map.put("author", parts[5]);
        case 5: // 10:53:242
            map.put("_time", parts[4]);
            String datetime = parts[3] + " " + parts[4];
            try {
                map.put("date", dateFormat.parse(datetime));
            } catch (ParseException e) {
                throw new IdentifiedException(e.getMessage(), e);
            }
        case 4: // 2008-01-15
            map.put("_date", parts[3]);
        case 3: // 784
            map.put("rev", AutoType.toInt(parts[2]));
        case 2: // Rcs.java
            map.put("name", parts[1]);
        case 1: // $Id:
        }
        return map;
    }

    public static Map<String, Object> parseId(RcsKeywords keywords) {
        return parseId(keywords.id());
    }

    public static Map<String, Object> parseId(Class<?> clazz) {
        return parseId(clazz.getAnnotation(RcsKeywords.class));
    }

}
