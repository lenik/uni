package net.bodz.uni.site.util;

import java.util.LinkedHashMap;
import java.util.Map;

import net.bodz.bas.c.string.StringBreaks;

public class DebControlParser {

    private DebControl debControl;
    private Map<String, String> map;

    private String key = null;
    private String value = null;

    public DebControl parse(String s) {
        debControl = new DebControl();
        map = createMap();

        for (String line : new StringBreaks(s)) {
            if (line.trim().isEmpty())
                end();
            else
                parseLine(line);
        }
        end();

        return debControl;
    }

    private void parseLine(String line) {
        assert !line.isEmpty();
        boolean indented = Character.isWhitespace(line.charAt(0));
        if (indented) {
            line = line.trim();
            if (value == null)
                value = line;
            else
                value = value + " " + line;
            return;
        }

        if (key != null) {
            map.put(key, value);
            key = null;
            value = null;
        }

        int colon = line.indexOf(':');
        if (colon == -1) {
            // invalid...
            key = line;
        } else {
            key = line.substring(0, colon).trim();
            value = line.substring(colon + 1).trim();
        }
    }

    private void end() {
        if (key != null)
            map.put(key, value);
        key = null;
        value = null;

        if (!map.isEmpty()) {
            String sourceName = map.get("Source");
            String packageName = map.get("Package");
            if (sourceName != null) {
                debControl.setSource(map);
            } else {
                debControl.addPackage(packageName, map);
            }
        }

        map = createMap();
    }

    protected Map<String, String> createMap() {
        return new LinkedHashMap<>();
    }

}
