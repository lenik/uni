package net.bodz.lily.tool.javagen.config;

import java.io.IOException;

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

public class ColumnSettings
        implements
            IRstForm,
            IJsonForm {

    private static final String K_JAVA_NAME = "javaName";
    private static final String K_JAVA_TYPE = "javaType";
    private static final String K_DESCRIPTION = "description";

    public String javaName;
    public String javaType;
    public String description;

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        javaName = o.getString(K_JAVA_NAME);
        javaType = o.getString(K_JAVA_TYPE);
        description = o.getString(K_DESCRIPTION);
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        out.entryNotNull(K_JAVA_NAME, this.javaName);
        out.entryNotNull(K_JAVA_TYPE, this.javaType);
        out.entryNotNull(K_DESCRIPTION, this.description);
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
    }

}
