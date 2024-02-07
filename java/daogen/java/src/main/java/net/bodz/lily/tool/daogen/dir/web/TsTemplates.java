package net.bodz.lily.tool.daogen.dir.web;

import java.util.StringTokenizer;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.util.Attrs;

public class TsTemplates {

    final JavaGenProject project;

    public TsTemplates(JavaGenProject project) {
        this.project = project;
    }

    public void declColumn(TypeScriptWriter out, IColumnMetadata column) {
        ColumnNaming cname = project.config.naming(column);
        String label = column.getLabel();
        String description = column.getDescription();
        if (label == null)
            label = description;
        if (label == null)
            label = labelFromProperty(cname.propertyName);

        IProperty property = column.getProperty();
        Class<?> type = property.getPropertyClass();
        String tsType = TsUtils.toTsType(type);

        Attrs a = new Attrs();
        a.put("data-type", tsType);
        a.put("data-field", cname.propertyName);

        out.print(a.toXml("th"));
        out.print(label);
        out.println("</th>");
    }

    public void declFKColumn(TypeScriptWriter out, CrossReference xref) {
        String propertyName = xref.getJavaName();
        String label = xref.getLabel();
        String description = xref.getDescription();
        if (label == null)
            label = description;
        if (label == null)
            label = labelFromProperty(propertyName);

        Attrs a = new Attrs();
        a.put("data-type", "string");
        a.put("data-format", "label");
        a.put("data-field", propertyName);

        out.print(a.toXml("th"));
        out.print(label);
        out.println("</th>");
    }

    static String labelFromProperty(String property) {
        // property.replaceAll("[A-Z]", )
        String s = StringId.SPACE.breakCamel(property);
        StringBuilder sb = new StringBuilder(s.length());
        StringTokenizer tokens = new StringTokenizer(s, " ");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (sb.length() != 0)
                sb.append(" ");
            sb.append(Strings.ucfirst(token));
        }
        return sb.toString();
    }

}
