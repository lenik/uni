package net.bodz.lily.tool.daogen.dir.web;

import org.apache.logging.log4j.util.Strings;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmSource;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.t.catalog.CrossReference;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableKey;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.util.Attrs;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class Foo_stuff_Type__ts
        extends JavaGen__ts {

    static final Logger logger = LoggerFactory.getLogger(Foo_stuff_Type__ts.class);

    public Foo_stuff_Type__ts(JavaGenProject project) {
        super(project, project.Esm_Foo_stuff_Type);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        EsmSource validators = EsmModules.local.source("./PersonValidators");
        out.im.add(validators.name("*", "validators"));

        TypeExtendInfo extend = new TypeExtendInfo(project, out, table, project.Esm_Foo_stuff_Type.qName);

        String description = table.getDescription();

        String typeClassName = extend.simpleName + "Type";
        String superTypeClassName = extend.baseClassName + "Type";

        out.println("// Type Info");
        out.println();
        out.printf("export class %s extends %s {\n", //
                typeClassName, //
                out.localName(superTypeClassName));
        out.println();
        out.enter();
        {
            TableOid oid = table.getId();
            if (oid.getCatalogName() != null)
                out.printf("static const CATALOG_NAME = %s;\n", //
                        StringQuote.qqJavaString(oid.getCatalogName()));
            if (oid.getSchemaName() != null)
                out.printf("static const SCHEMA_NAME = %s;\n", //
                        StringQuote.qqJavaString(oid.getSchemaName()));
            if (oid.getTableName() != null)
                out.printf("static const TABLE_NAME = %s;\n", //
                        StringQuote.qqJavaString(oid.getTableName()));

            IType type = table.getEntityType();
            String iconName = "fa-tag";
            String label = type.getLabel().toString();
            if (Strings.isEmpty(description))
                description = type.getDescription().toString();

            out.println();
            out.printf("name = \"%s\"\n", table.getEntityTypeName());
            if (iconName != null)
                out.printf("icon = \"%s\"\n", iconName);
            if (label != null)
                out.printf("label = \"%s\"\n", label);
            if (description != null)
                out.printf("description = \"%s\"\n", description);

            templates.FIELD_consts(out, table, null, true);
            templates.N_consts(out, table, null, true);

            out.println();
            out.printf("static declaredProperty: %s = {\n", //
                    out.im.name(EsmModules.dba.entity.EntityPropertyMap));
            out.enter();
            {
                for (IColumnMetadata column : table.getColumns()) {
                    if (column.isExcluded())
                        continue;

                    if (column.isCompositeProperty()) {
                        checkCompositeProperty(table, column);
                        continue;
                    }

                    if (column.isForeignKey())
                        continue;

                    declProperty(out, column);
                }

                for (CrossReference xref : table.getForeignKeys().values()) {
                    if (xref.isExcluded(table))
                        continue;

                    if (xref.isCompositeProperty())
                        continue;

                    out.println();
                    declForeignKeyProperty(out, xref, table);

                    for (String fkColumnName : xref.getForeignKey().getColumnNames()) {
                        IColumnMetadata column = table.getColumn(fkColumnName);
                        declProperty(out, column);
                    }
                }
                out.leave();
            }
            out.println("}");
            out.println();
            out.println("constructor() {");
            out.enter();
            {
                out.println("super();");
                out.printf("this.declare(%s.declaredProperty);\n", //
                        typeClassName);
                out.leave();
            }
            out.println("}");
            out.println();
            out.leave();
        }
        out.println("}");
    }

    void checkCompositeProperty(ITableMetadata table, IColumnMetadata column) {
        ColumnNaming cname = project.naming(column);

        // composite property, need to be declared in the user type.
        // check if exists:
        String head = Split.headDomain(cname.propertyName).a;
        IProperty headProperty = table.getEntityType().getProperty(head);
        if (headProperty == null)
            logger.warnf("context property (%s.%s) of the composite property(%s) isn't defined.",
                    table.getEntityTypeName(), head, cname.propertyName);
    }

    void declProperty(TypeScriptWriter out, IColumnMetadata column) {
        boolean primaryKey = column.isPrimaryKey();
        boolean notNull = !column.isNullable(true);

        String javaType = project.config.javaType(column);
        // String simpleType = Split.packageName(javaType).b;

        String description = column.getDescription();

        ColumnNaming cname = project.naming(column);
        out.print(cname.propertyName);
        out.print(": ");
        if (primaryKey)
            out.print(out.name(EsmModules.dba.entity.primaryKey));
        else
            out.print(out.name(EsmModules.dba.entity.property));

        Attrs attrs = new Attrs(TsConfig.newLineProps);
        attrs.putQuoted("type", javaType);
        if (notNull)
            attrs.put("nullable", false);

        int precision = column.getPrecision();
        if (precision > 0 && precision < Integer.MAX_VALUE)
            attrs.put("precision", precision);

        int scale = column.getScale();
        if (scale > 0 && scale < Integer.MAX_VALUE)
            attrs.put("scale", scale);

        // attrs.put("icon", "fa-user");
        // attrs.put("label", "label");
        if (description != null && !description.isEmpty())
            attrs.putQuoted("description", description);

        attrs.put("validator", "validators.validate_" + cname.propertyName);

        out.print("(");
        attrs.toJson(out, true);
        out.println("),");
    }

    public void declForeignKeyProperty(TypeScriptWriter out, CrossReference xref, ITableMetadata table) {
        TableKey foreignKey = xref.getForeignKey();
        IColumnMetadata[] columns = foreignKey.resolve(table);
        TableOid parentOid = xref.getParentKey().getId();
        ITableMetadata parentTable = project.catalog.getTable(parentOid);
        if (parentTable == null) {
            throw new UnexpectedException("parent is not defined: " + parentOid);
        }

        String description = xref.getDescription();
        String inheritDocFrom = null;
        if (description == null) {
            description = parentTable.getDescription();
            if (description != null)
                inheritDocFrom = out.localName(parentTable.getJavaQName());
        }

        boolean anyNotNull = false;
        for (IColumnMetadata c : columns)
            if (!c.isNullable(false)) {
                anyNotNull = true;
                break;
            }

        String parentType = parentTable.getJavaQName();
        if (parentType == null)
            throw new NullPointerException("parentType");

        String property = xref.getJavaName();

        out.print(property);
        out.print(": ");
        out.print(out.name(EsmModules.dba.entity.property));

        Attrs attrs = new Attrs(TsConfig.newLineProps);
        attrs.putQuoted("type", out.localName(parentType));
        if (anyNotNull)
            attrs.put("nullable", false);

        // attrs.put("icon", "fa-user");
        // attrs.put("label", "label");
        if (description != null && !description.isEmpty()) {
            if (inheritDocFrom != null)
                ; // description = "(" + description + ")";
            attrs.putQuoted("description", description);
        }

        attrs.put("validator", "validators.validate_" + property);

        out.print("(");
        attrs.toJson(out, true);
        out.println("),");
    }

}
