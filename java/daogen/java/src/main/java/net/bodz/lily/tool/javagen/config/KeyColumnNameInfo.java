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

    String alias;
    String component;

    public KeyColumnNameInfo(String alias, String component) {
        this.alias = alias;
        this.component = component;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getAliasProperty() {
        return Phrase.foo_bar(alias).fooBar;
    }

    public String getComponentProperty() {
        return Phrase.foo_bar(component).fooBar;
    }

    @Override
    public String toString() {
        return String.format("alias %s, component %s", alias, component);
    }

    private static final String K_ALIAS = "alias";
    private static final String K_COMPONENT = "component";

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        this.alias = o.getString(K_ALIAS);
        this.component = o.getString(K_COMPONENT);
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        out.entryNotNull(K_ALIAS, this.alias);
        out.entryNotNull(K_COMPONENT, this.component);
    }

}
