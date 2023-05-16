package net.bodz.lily.tool.javagen.config;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import net.bodz.bas.err.FormatException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.api.ElementHandlerException;
import net.bodz.bas.fmt.json.IJsonForm;
import net.bodz.bas.fmt.json.IJsonOut;
import net.bodz.bas.fmt.json.JsonFormOptions;
import net.bodz.bas.fmt.rst.IRstForm;
import net.bodz.bas.fmt.rst.IRstHandler;
import net.bodz.bas.fmt.rst.IRstOutput;
import net.bodz.bas.fmt.rst.StackRstHandler;
import net.bodz.bas.json.JsonObject;

public class KeyColumnSettings
        implements
            IRstForm,
            IJsonForm {

    Map<String, KeyColumnFormat> formats = new TreeMap<>();

    public boolean isEmpty() {
        return formats.isEmpty();
    }

    public Map<String, KeyColumnFormat> getFormats() {
        return formats;
    }

    public KeyColumnNameInfo parseColumnByAnyFormat(String columnName) {
        for (KeyColumnFormat format : formats.values()) {
            KeyColumnNameInfo info = format.parse(columnName);
            if (info != null)
                return info;
        }
        return null;
    }

    private static final String K_FORMATS_json = "formats"; // json only
    private static final String K_FORMAT_rst = "format"; // rst only

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        JsonObject jFormats = o.getJsonObject(K_FORMATS_json);
        if (jFormats != null) {
            formats.clear();
            for (String name : jFormats.keySet()) {
                JsonObject jFormat = jFormats.getJsonObject(name);
                KeyColumnFormat format = new KeyColumnFormat();
                format.jsonIn(jFormat, opts);
                formats.put(name, format);
            }
        }
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        if (formats != null && !formats.isEmpty()) {
            out.key(K_FORMATS_json);
            out.map(formats);
        }
    }

    @Override
    public IRstHandler getElementHandler() {
        return new StackRstHandler() {

            @Override
            public IRstHandler beginChild(String name, String[] args)
                    throws ParseException, ElementHandlerException {
                switch (name) {
                case K_FORMAT_rst:
                    if (args.length == 0)
                        throw new ParseException("expect format name.");
                    String formatName = args[0];
                    KeyColumnFormat format = new KeyColumnFormat();
                    formats.put(formatName, format);
                    return format.getElementHandler();
                }
                return this;
            }

            @Override
            public boolean attribute(String name, String data)
                    throws ParseException, ElementHandlerException {
                return false;
            }

        };
    }

    @Override
    public void writeObject(IRstOutput out)
            throws IOException, FormatException {
        if (formats != null) {
            for (String name : formats.keySet()) {
                out.beginElement(K_FORMAT_rst, name);
                KeyColumnFormat format = formats.get(name);
                format.writeObject(out);
                out.endElement();
            }
        }
    }

}
