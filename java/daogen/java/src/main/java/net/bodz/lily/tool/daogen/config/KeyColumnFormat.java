package net.bodz.lily.tool.daogen.config;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class KeyColumnFormat
        implements
            IRstForm,
            IJsonForm,
            INameDecorator {

    Pattern pattern;
    String aliasFormat;
    String componentFormat;

    public Pattern getPattern() {
        return pattern;
    }

    public String getAliasFormat() {
        return aliasFormat;
    }

    public String getComponentFormat() {
        return componentFormat;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public void setAliasFormat(String aliasFormat) {
        this.aliasFormat = aliasFormat;
    }

    public void setComponentFormat(String componentFormat) {
        this.componentFormat = componentFormat;
    }

    /**
     * @return <code>null</code> if not matched.
     */
    public KeyColumnNameInfo parse(String columnName) {
        if (columnName == null)
            throw new NullPointerException("columnName");
        Matcher m = pattern.matcher(columnName);
        if (m.find()) {
            String alias = m.replaceAll(aliasFormat);
            String component = m.replaceAll(componentFormat);
            return new KeyColumnNameInfo(alias, component);
        }
        return null;
    }

    @Override
    public boolean isDecorated(String s) {
        return false;
    }

    @Override
    public String decorate(String s) {
        return null;
    }

    @Override
    public String undecorate(String s) {
        return null;
    }

    private static final String K_PATTERN = "pattern";
    private static final String K_ALIAS_FORMAT = "alias";
    private static final String K_COMPONENT_FORMAT = "component";

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        String regex = o.getString(K_PATTERN);
        this.pattern = Pattern.compile(regex);
        this.aliasFormat = o.getString(K_ALIAS_FORMAT);
        this.componentFormat = o.getString(K_COMPONENT_FORMAT);
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        if (pattern == null)
            throw new NullPointerException("pattern");
        out.entryNotNull(K_PATTERN, this.pattern.pattern());
        out.entryNotNull(K_ALIAS_FORMAT, this.aliasFormat);
        out.entryNotNull(K_COMPONENT_FORMAT, this.componentFormat);
    }

    @Override
    public IRstHandler getElementHandler() {
        return new StackRstHandler() {
            @Override
            public boolean attribute(String name, String data)
                    throws ParseException, ElementHandlerException {
                switch (name) {
                case K_PATTERN:
                    pattern = Pattern.compile(data);
                    return true;

                case K_ALIAS_FORMAT:
                    aliasFormat = data;
                    return true;

                case K_COMPONENT_FORMAT:
                    componentFormat = data;
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
        if (pattern != null)
            out.attribute(K_PATTERN, this.pattern.pattern());
        if (aliasFormat != null)
            out.attribute(K_ALIAS_FORMAT, this.aliasFormat);
        if (componentFormat != null)
            out.attribute(K_COMPONENT_FORMAT, this.componentFormat);
    }

}
