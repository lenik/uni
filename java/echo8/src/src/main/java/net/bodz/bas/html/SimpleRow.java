package net.bodz.bas.html;

import java.io.Writer;

public class SimpleRow
        extends HtmlSugar {

    public SimpleRow() {
        super();
    }

    public SimpleRow(Writer writer) {
        super(writer);
    }

    public void put(String name, Object value) {
        String _value = value == null ? "" : value.toString();
        tr();
        th().classAttr("key").text(name).end();
        td().classAttr("value").text(_value).end();
        end();
    }

    public void put(String name, Object value, String comment) {
        String _value = value == null ? "" : value.toString();
        tr();
        th().classAttr("key").text(name).end();
        td().classAttr("value").text(_value).end();
        td().classAttr("comment").text(comment == null ? "" : comment).end();
        end();
    }

}
