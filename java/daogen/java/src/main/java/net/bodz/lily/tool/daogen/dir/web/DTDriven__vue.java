package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.c.string.StringId;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.code.util.Attrs;
import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.entity.esm.DTColumn;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.DaoGen__vue;
import net.bodz.lily.tool.daogen.util.DTColumnConfig;

public abstract class DTDriven__vue
        extends DaoGen__vue {

    Map<String, String> typeMap = new LinkedHashMap<>();

    public DTDriven__vue(DaoGenProject project, ClassPathInfo name) {
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
        else if (name.endsWith("_TYPE"))
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
            boolean composite = column.isCompositeProperty();
            if (composite) {
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
        String description = column.getDescription();

        if (label == null)
            label = labelFromProperty(cname.propertyName);

        // boolean debug = "user".equals(column.getTable().getName());

        IProperty property = column.getProperty();
        if (property == null)
            throw new IllegalUsageException("no property for column " + column.getName());

        DTColumn _aColumn = property.getAnnotation(DTColumn.class);
        DTColumnConfig dtColumn = new DTColumnConfig().parse(_aColumn);

        String typeKey;
        if (dtColumn.dataType != null)
            typeKey = dtColumn.dataType;
        else {
            Type type = property.getPropertyGenericType();
            if (type instanceof TypeVariable<?>)
                type = property.getPropertyClass();

            String typeInfo = typeInfoResolver() //
                    .property(property.getName()) //
                    .resolveGeneric(type);

            typeKey = addTypeInfo(typeInfo);
        }

        Attrs a = new Attrs();
        Set<String> classList = dtColumn.classList();
        if (! classList.isEmpty())
            a.put("class", StringArray.join(" ", classList));

        a.put("data-type", typeKey);
        a.put("data-field", cname.propertyName);

        if (dtColumn.dataFormat != null)
            a.put("data-format", dtColumn.dataFormat);

        if (dtColumn.dataRender != null)
            a.put("data-render", dtColumn.dataRender);

        if (description != null)
            if (! Nullables.equals(description, label))
                a.put("title", description);

        out.print(a.toHtml("th"));
        out.print(label);
        out.println("</th>");
    }

    public void declFKColumn(TypeScriptWriter out, CrossReference xref) {
        String propertyName = xref.getPropertyName();
        String label = xref.getLabel();
        String description = xref.getDescription();
        if (label == null)
            label = labelFromProperty(propertyName);

        IProperty property = xref.getProperty();
        if (property == null)
            throw new IllegalUsageException(//
                    String.format("foreign key (%s) on table %s: property %s isn't defined.", //
                            xref.getConstraintName(), //
                            xref.getParentTable().getId(), //
                            xref.getPropertyName()));

        // boolean debug = "user".equals(xref.getForeignTable().getName());

        DTColumn _aColumn = property.getAnnotation(DTColumn.class);
        DTColumnConfig dtColumn = new DTColumnConfig().parse(_aColumn);

        String typeKey;
        if (dtColumn.dataType != null)
            typeKey = dtColumn.dataType;
        else {
            QualifiedName type = xref.getParentTable().getJavaType();
            String typeInfo = typeInfoResolver()//
                    .property(propertyName)//
                    .resolve(type);
            typeKey = addTypeInfo(typeInfo);
        }

        Attrs a = new Attrs();

        Set<String> classList = dtColumn.classList();
        if (! classList.isEmpty())
            a.put("class", StringArray.join(" ", classList));

        a.put("data-type", typeKey);
        a.put("data-format", "label");
        a.put("data-field", propertyName);

        if (dtColumn.dataFormat != null)
            a.put("data-format", dtColumn.dataFormat);

        if (dtColumn.dataRender != null)
            a.put("data-render", dtColumn.dataRender);

        if (description != null)
            if (! Nullables.equals(description, label))
                a.put("title", description);

        out.print(a.toHtml("th"));
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
