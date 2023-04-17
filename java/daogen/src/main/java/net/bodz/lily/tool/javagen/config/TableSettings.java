package net.bodz.lily.tool.javagen.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

public class TableSettings
        implements
            IRstForm,
            IJsonForm {

    private static final String K_JAVA_NAME = "javaName";
    private static final String K_JAVA_TYPE = "javaType";
    private static final String K_DESCRIPTION = "description";
    private static final String K_COLUMNS = "columns";
    private static final String K_COLUMN = "column";
    private static final String K_MIXINS = "mixins";
    private static final String K_MIXIN = "mixin";

    public String javaName;
    public String javaType;
    public String description;

    public final Map<String, ColumnSettings> columnMap = new HashMap<>();
    public final Map<String, MixinSettings> mixinMap = new LinkedHashMap<>();

    ColumnSettings resolveColumn(String name) {
        ColumnSettings column = columnMap.get(name);
        if (column == null)
            columnMap.put(name, column = new ColumnSettings());
        return column;
    }

    MixinSettings resolveMixin(String name) {
        MixinSettings mixin = mixinMap.get(name);
        if (mixin == null)
            mixinMap.put(name, mixin = new MixinSettings());
        return mixin;
    }

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        javaName = o.getString(K_JAVA_NAME);
        javaType = o.getString(K_JAVA_TYPE);
        description = o.getString(K_DESCRIPTION);

        JsonObject j_columns = o.getJsonObject(K_COLUMNS);
        if (j_columns != null) {
            for (String k : j_columns.keySet()) {
                JsonObject j_column = j_columns.getJsonObject(k);
                ColumnSettings column = resolveColumn(k);
                column.jsonIn(j_column, opts);
            }
        }

        JsonObject j_mixins = o.getJsonObject(K_MIXINS);
        if (j_mixins != null) {
            for (String k : j_mixins.keySet()) {
                JsonObject j_mixin = j_mixins.getJsonObject(k);
                MixinSettings mixin = resolveMixin(k);
                mixin.jsonIn(j_mixin, opts);
            }
        }
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        out.entryNotNull(K_JAVA_NAME, this.javaName);
        out.entryNotNull(K_JAVA_TYPE, this.javaType);
        out.entryNotNull(K_DESCRIPTION, this.description);

        if (columnMap != null) {
            out.key(K_COLUMNS);
            out.object();
            for (String k : columnMap.keySet()) {
                ColumnSettings column = columnMap.get(k);
                out.key(k);
                column.jsonOut(out, opts);
            }
            out.endObject();
        }

        if (mixinMap != null) {
            out.key(K_MIXINS);
            out.object();
            for (String k : mixinMap.keySet()) {
                MixinSettings mixin = mixinMap.get(k);
                out.key(k);
                mixin.jsonOut(out, opts);
            }
            out.endObject();
        }
    }

    @Override
    public IRstHandler getElementHandler() {
        return new StackRstHandler() {

            @Override
            public IRstHandler beginChild(String name, String[] args)
                    throws ParseException, ElementHandlerException {
                super.beginChild(name, args);
                switch (name) {
                case K_COLUMN:
                    if (args.length < 1)
                        throw new IllegalArgumentException("expect column name");
                    String columnName = args[0];
                    ColumnSettings column = resolveColumn(columnName);
                    return column.getElementHandler();

                case K_MIXIN:
                    if (args.length < 1)
                        throw new IllegalArgumentException("expect mixin name");
                    String mixinName = args[0];
                    MixinSettings mixin = resolveMixin(mixinName);
                    return mixin.getElementHandler();
                }
                return this;
            }

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

                case K_DESCRIPTION:
                    description = data;
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
        if (description != null)
            out.attribute(K_DESCRIPTION, this.description);

        if (columnMap != null) {
            for (String k : columnMap.keySet()) {
                ColumnSettings column = columnMap.get(k);
                out.beginElement(K_COLUMN, k);
                column.writeObject(out);
                out.endElement();
            }
        }

        if (mixinMap != null) {
            for (String k : mixinMap.keySet()) {
                MixinSettings mixin = mixinMap.get(k);
                out.beginElement(K_MIXIN, k);
                mixin.writeObject(out);
                out.endElement();
            }
        }
    }

}
