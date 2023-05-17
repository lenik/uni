package net.bodz.lily.tool.javagen.config;

import java.io.IOException;

import net.bodz.bas.c.string.Phrase;
import net.bodz.bas.err.FormatException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.json.IJsonForm;
import net.bodz.bas.fmt.json.IJsonOut;
import net.bodz.bas.fmt.json.JsonFormOptions;
import net.bodz.bas.json.JsonObject;

public class KeyColumnNameInfo
        implements
            IJsonForm {

    String sqlAlias;
    String sqlField;

    public KeyColumnNameInfo(String sqlAlias, String sqlField) {
        this.sqlAlias = sqlAlias;
        this.sqlField = sqlField;
    }

    public String getSqlAlias() {
        return sqlAlias;
    }

    public void setSqlAlias(String sqlAlias) {
        this.sqlAlias = sqlAlias;
    }

    public String getSqlField() {
        return sqlField;
    }

    public void setSqlField(String sqlField) {
        this.sqlField = sqlField;
    }

    public String getAliasProperty() {
        return Phrase.foo_bar(sqlAlias).fooBar;
    }

    public String getFieldProperty() {
        return Phrase.foo_bar(sqlField).fooBar;
    }

    @Override
    public String toString() {
        return String.format("alias %s, field %s", sqlAlias, sqlField);
    }

    private static final String K_ALIAS = "alias";
    private static final String K_FIELD = "field";

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        this.sqlAlias = o.getString(K_ALIAS);
        this.sqlField = o.getString(K_FIELD);
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        out.entryNotNull(K_ALIAS, this.sqlAlias);
        out.entryNotNull(K_FIELD, this.sqlField);
    }

}
