package net.bodz.lily.tool.daogen.dir.web;

import net.bodz.bas.c.object.Nullables;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.esm.EsmModules;
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
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.JavaGen__ts;
import net.bodz.lily.tool.daogen.OutFormat;
import net.bodz.lily.tool.daogen.util.Attrs;
import net.bodz.lily.tool.daogen.util.TypeAnalyzer;
import net.bodz.lily.tool.daogen.util.TypeExtendInfo;

public class FooType0__ts
        extends JavaGen__ts {

    static final Logger logger = LoggerFactory.getLogger(FooType0__ts.class);

    public FooType0__ts(JavaGenProject project) {
        super(project, project.Esm_Foo_stuff_Type);
    }

    @Override
    protected void buildTsBody(TypeScriptWriter out, ITableMetadata table) {
        TypeExtendInfo extend = new TypeAnalyzer(project, out)//
                .getExtendInfo(table, project.Esm_Foo_stuff_Type.qName);

        String description = table.getDescription();

        QualifiedName validatorsClass = project.Esm_Foo.qName.nameAdd("Validators");
        QualifiedName superType = extend.baseClassName.nameAdd("Type");

        out.println("// Type Info");
        out.println();

        boolean useNs = false;

        if (useNs) {
            out.printf("export namespace %s_NS {\n", //
                    extend.simpleName);
            out.enter();
            {
                staticFields1(out, table, OutFormat.TS_NAMESPACE);
                staticFields2(out, table, OutFormat.TS_NAMESPACE);

                out.println("}");
                out.leave();
            }
            out.println();
        }

        out.printf("export class %s extends %s {\n", //
                extend.simpleName, //
                out.importName(superType));
        out.println();
        out.enter();
        {
            staticFields1(out, table, OutFormat.TS_CLASS);

            IType type = table.getEntityType();
            String iconName = "fa-tag";
            String label = type.getLabel().toString();
            if (Nullables.isEmpty(description))
                description = type.getDescription().toString();

            out.println();
            out.printf("name = \"%s\"\n", table.getEntityTypeName());
            if (iconName != null)
                out.printf("icon = \"%s\"\n", iconName);
            if (label != null)
                out.printf("label = \"%s\"\n", label);
            if (description != null)
                out.printf("description = \"%s\"\n", description);

            staticFields2(out, table, OutFormat.TS_CLASS);

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

                    declProperty(out, column, validatorsClass);
                }

                for (CrossReference xref : table.getForeignKeys().values()) {
                    if (xref.isExcluded(table))
                        continue;

                    if (xref.isCompositeProperty())
                        continue;

                    out.println();
                    declForeignKeyProperty(out, xref, table, validatorsClass);

                    for (String fkColumnName : xref.getForeignKey().getColumnNames()) {
                        IColumnMetadata column = table.getColumn(fkColumnName);
                        declProperty(out, column, validatorsClass);
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
                        extend.simpleName);
                out.leave();
            }
            out.println("}");
            out.println();
            out.leave();
        }
        out.println("}");

        out.println();
        out.printf("export default %s;\n", extend.simpleName);

    }

    void staticFields1(TypeScriptWriter out, ITableMetadata table, OutFormat fmt) {
        String mod = fmt == OutFormat.TS_NAMESPACE ? "export const" : "static";

        TableOid oid = table.getId();
        if (oid.getCatalogName() != null)
            out.printf("%s CATALOG_NAME = %s;\n", //
                    mod, StringQuote.qqJavaString(oid.getCatalogName()));
        if (oid.getSchemaName() != null)
            out.printf("%s SCHEMA_NAME = %s;\n", //
                    mod, StringQuote.qqJavaString(oid.getSchemaName()));
        if (oid.getTableName() != null)
            out.printf("%s TABLE_NAME = %s;\n", //
                    mod, StringQuote.qqJavaString(oid.getTableName()));
    }

    void staticFields2(TypeScriptWriter out, ITableMetadata table, OutFormat fmt) {
        templates.FIELD_consts(out, table, null, fmt);
        templates.N_consts(out, table, null, fmt);
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

    void declProperty(TypeScriptWriter out, IColumnMetadata column, QualifiedName validatorsClass) {
        boolean primaryKey = column.isPrimaryKey();
        boolean notNull = ! column.isNullable(true);

        String javaType = project.config.javaType(column);
        String tsType = tsTypes.resolve(javaType, column.getName());

        String description = column.getDescription();

        ColumnNaming cname = project.naming(column);
        out.print(cname.propertyName);
        out.print(": ");
        if (primaryKey)
            out.print(out.name(EsmModules.dba.entity.primaryKey));
        else
            out.print(out.name(EsmModules.dba.entity.property));

        Attrs attrs = new Attrs(TsConfig.newLineProps);
        attrs.putQuoted("type", tsType);
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
        if (description != null && ! description.isEmpty())
            attrs.putQuoted("description", description);

        String validatorFn = String.format("%s.validate%s", //
                out.importDefaultAs(validatorsClass), //
                cname.ucfirstPropertyName);
        attrs.put("validator", validatorFn);

        out.print("(");
        attrs.toJson(out, true);
        out.println("),");
    }

    public void declForeignKeyProperty(TypeScriptWriter out, CrossReference xref, ITableMetadata table,
            QualifiedName validatorsClass) {
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
                inheritDocFrom = out.importName(parentTable.getJavaType());
        }

        boolean anyNotNull = false;
        for (IColumnMetadata c : columns)
            if (! c.isNullable(false)) {
                anyNotNull = true;
                break;
            }

        String property = xref.getJavaName();

        QualifiedName parentType = parentTable.getJavaType();
        if (parentType == null)
            throw new NullPointerException("parentType");
        String parentTsType = tsTypes.resolve(parentType, property);

        out.print(property);
        out.print(": ");
        out.print(out.name(EsmModules.dba.entity.property));

        Attrs attrs = new Attrs(TsConfig.newLineProps);
        attrs.putQuoted("type", parentTsType);
        if (anyNotNull)
            attrs.put("nullable", false);

        // attrs.put("icon", "fa-user");
        // attrs.put("label", "label");
        if (description != null && ! description.isEmpty()) {
            if (inheritDocFrom != null)
                ; // description = "(" + description + ")";
            attrs.putQuoted("description", description);
        }

        String validatorFn = String.format("%s.validate%s", //
                out.importDefaultAs(validatorsClass), //
                Strings.ucfirst(property));
        attrs.put("validator", validatorFn);

        out.print("(");
        attrs.toJson(out, true);
        out.println("),");
    }

}
