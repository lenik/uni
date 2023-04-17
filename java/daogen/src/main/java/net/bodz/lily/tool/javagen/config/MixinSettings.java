package net.bodz.lily.tool.javagen.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.string.StringArray;
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
import net.bodz.bas.json.JsonArray;
import net.bodz.bas.json.JsonObject;

public class MixinSettings
        implements
            IRstForm,
            IJsonForm {

    private static final String K_JAVA_NAME = "javaName";
    private static final String K_JAVA_TYPE = "javaType";
    private static final String K_COLUMNS = "columns";

    public String javaName;
    public String javaType;
    public List<String> columns = new ArrayList<>();

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        javaName = o.getString(K_JAVA_NAME);
        javaType = o.getString(K_JAVA_TYPE);

        JsonArray j_columns = o.getJsonArray(K_COLUMNS);
        if (j_columns != null) {
            columns.clear();
            int n = j_columns.length();
            for (int i = 0; i < n; i++) {
                String columnName = j_columns.getString(i);
                columns.add(columnName);
            }
        }
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        out.entryNotNull(K_JAVA_NAME, this.javaName);
        out.entryNotNull(K_JAVA_TYPE, this.javaType);
        out.entry(K_COLUMNS, this.columns);
    }

    @Override
    public IRstHandler getElementHandler() {
        return new StackRstHandler() {
            @Override
            public boolean attribute(String name, String data)
                    throws ParseException, ElementHandlerException {
                switch (name) {
                case K_JAVA_NAME:
                    javaName = data;
                    return true;

                case K_JAVA_TYPE:
                    javaType = data;
                    return true;

                case K_COLUMNS:
                    for (String c : data.split(",")) {
                        c = c.trim();
                        if (!c.isEmpty())
                            columns.add(c);
                    }
                    return true;

                default:
                    return false;
                }
            }
        };
    }

    @Override
    public void writeObject(IRstOutput out)
            throws IOException, FormatException {
        if (javaName != null)
            out.attribute(K_JAVA_NAME, this.javaName);
        if (javaType != null)
            out.attribute(K_JAVA_TYPE, this.javaType);
        if (columns != null) {
            String s = StringArray.join(", ", columns);
            out.attribute(K_COLUMNS, s);
        }
    }

}
