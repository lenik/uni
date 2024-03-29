package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__vue;
import net.bodz.lily.tool.daogen.util.Attrs;

public abstract class DTDriven__vue
        extends JavaGen__vue {

    Map<String, String> typeMap = new LinkedHashMap<>();

    public DTDriven__vue(JavaGenProject project, ClassPathInfo name) {
        super(project, name);
    }

    @Override
    protected boolean templateFirst() {
        return true;
    }

    boolean conflictTypeInfo(String name, String typeInfoVal) {
        String existing = typeMap.get(name);
        if (existing == null)
            return false;
        else
            return ! typeInfoVal.equals(existing);
    }

    public String addTypeInfo(String typeInfoVal) {
        String name = typeInfoVal;
        if (name.endsWith(".TYPE"))
            name = name.substring(0, name.length() - 5);

        if (conflictTypeInfo(name, typeInfoVal)) {
            int suffix = 1, nTry = 10;
            while (suffix <= nTry) {
                String rename = name + suffix;
                if (conflictTypeInfo(rename, typeInfoVal)) {
                    suffix++;
                    continue;
                }
                name = rename;
            }
        }

        typeMap.put(name, typeInfoVal);
        return name;
    }

    void dumpTypeMap(TypeScriptWriter out) {
        out.println("const typeMap = {");
        out.enter();
        for (String key : typeMap.keySet()) {
            String typeInfoValue = typeMap.get(key);
            out.printf("%s: %s,\n", //
                    StringQuote.qqJavaString(key), //
                    typeInfoValue);
        }
        out.leave();
        out.println("};");
    }

    protected void buildColumns(TypeScriptWriter out, ITableMetadata table) {
        Set<String> handledXrefs = new HashSet<>();

        for (IColumnMetadata column : table.getColumns()) {
            if (column.isCompositeProperty()) {
                // checkCompositeProperty(table, column);
                continue;
            }

            if (column.isForeignKey()) {
                CrossReference xref = table.getForeignKeyFromColumn(column.getName());
                if (handledXrefs.add(xref.getConstraintName()))
                    declFKColumn(out, xref);
            } else {
                declColumn(out, column);
            }
        }
    }

    public void declColumn(TypeScriptWriter out, IColumnMetadata column) {

        ColumnNaming cname = project.config.naming(column);
        String label = column.getLabel();
        if (label == null)
            label = labelFromProperty(cname.propertyName);

        String description = column.getDescription();

        IProperty property = column.getProperty();
        Type type = property.getPropertyGenericType();

        String typeInfo = typeInfoResolver().property(property.getName())//
                .resolveGeneric(type);
        String typeInfoKey = addTypeInfo(typeInfo);

        Attrs a = new Attrs();
        a.put("data-type", typeInfoKey);
        a.put("data-field", cname.propertyName);
        if (description != null)
            a.put("title", description);

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

        QualifiedName type = xref.getParentTable().getJavaType();
        String typeInfo = typeInfoResolver().property(propertyName)//
                .resolve(type);
        String typeInfoKey = addTypeInfo(typeInfo);

        Attrs a = new Attrs();
        a.put("data-type", typeInfoKey);
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
