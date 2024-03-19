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
                .getExtendInfo(table, //
                        project.Esm_Foo_stuff_Type.qName);

        String description = table.getDescription();

        QualifiedName validatorsClass = project.Esm_Foo_stuff_Validators.qName;
        QualifiedName superType = extend.baseType.nameAdd(project.typeInfoSuffix);

        boolean useNs = false;

        if (useNs) {
            out.printf("export namespace %s_NS {\n", //
                    extend.type.name);
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
                extend.type.name, //
                out.importDefault(superType));
        out.println();
        out.enter();
        {
            // CATALOG, SCHEMA, TABLE
            staticFields1(out, table, OutFormat.TS_CLASS);

            staticFields2(out, table, OutFormat.TS_CLASS);

            IType type = table.getPotatoType();
            String iconName = "fa-tag";
            String label = type.getLabel().toString();
            if (Nullables.isEmpty(description))
                description = type.getDescription().toString();

            out.println();
            out.printf("readonly validators = new %s(this);\n", //
                    out.importDefault(validatorsClass));

            out.println();
            out.printf("constructor(%s) {\n", extend.getCtorParams(this));
            out.enter();
            {
                out.printf("super(%s);\n", extend.getSuperCtorArgs(this));
                if (extend.isSelfTypeNeeded())
                    out.println("this.selfType = this;");
                out.leave();
            }
            out.println("}");

            out.println();
            out.printf("get name() { return \"%s\"; }\n", table.getJavaType());
            if (iconName != null)
                out.printf("get icon() { return \"%s\"; }\n", iconName);
            if (label != null)
                out.printf("get label() { return \"%s\"; }\n", label);
            if (description != null)
                out.printf("get description() { return \"%s\"; }\n", description);

            out.println();
            out.println("override preamble() {");
            {
                out.enter();
                out.println("super.preamble();");
                out.println("this.declare({");
                {
                    out.enter();
                    declareProps(out, table);
                    out.leave();
                }
                out.println("});");
                out.leave();
            }
            out.println("}");

            if (extend.typeVarCount() == 0) {
                out.println();
                out.printf("static readonly INSTANCE = new %s();\n", //
                        extend.type.name);
            }

            out.println();
            out.leave();
        }
        out.println("}");

        out.println();
        out.printf("export default %s;\n", extend.type.name);
    }

    void declareProps(TypeScriptWriter out, ITableMetadata table) {
        for (IColumnMetadata column : table.getColumns()) {
            if (column.isExcluded())
                continue;

            if (column.isCompositeProperty()) {
                checkCompositeProperty(table, column);
                continue;
            }

            if (column.isForeignKey())
                continue;

            declProperty(out, column, true);
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
                declProperty(out, column, false);
            }
        }
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
        IProperty headProperty = table.getPotatoType().getProperty(head);
        if (headProperty == null)
            logger.warnf("context property (%s.%s) of the composite property(%s) isn't defined.", table.getJavaType(),
                    head, cname.propertyName);
    }

    void declProperty(TypeScriptWriter out, IColumnMetadata column, //
            boolean validator) {
        boolean primaryKey = column.isPrimaryKey();
        boolean notNull = ! column.isNullable(true);

        String javaType = project.config.javaType(column);
        // String tsType = typeResolver().property(column.getName()).resolve(javaType);
        String tsTypeInfo = typeInfoResolver().property(column.getName()).resolve(javaType);

        String label = column.getLabel();
        String description = column.getDescription();

        ColumnNaming cname = project.naming(column);
        out.print(cname.propertyName);
        out.print(": ");
        if (primaryKey)
            out.print(out.name(EsmModules.dba.entity.primaryKey));
        else
            out.print(out.name(EsmModules.dba.entity.property));

        Attrs attrs = new Attrs(TsCodeStyle.newLineProps);
        attrs.put("type", tsTypeInfo);
        if (notNull)
            attrs.put("nullable", false);

        int precision = column.getPrecision();
        if (precision > 0 && precision < Integer.MAX_VALUE)
            attrs.put("precision", precision);

        int scale = column.getScale();
        if (scale > 0 && scale < Integer.MAX_VALUE)
            attrs.put("scale", scale);

        // attrs.put("icon", "fa-user");

        if (! Nullables.isEmpty(label))
            attrs.putQuoted("label", label);

        if (! Nullables.isEmpty(description))
            attrs.putQuoted("description", description);

        if (validator) {
            String validatorFn = String.format("this.validators.validate%s", //
                    cname.ucfirstPropertyName);
            attrs.put("validator", validatorFn);
        }

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

        String label = xref.getLabel();

        String description = xref.getDescription();
        String inheritDocFrom = null;

        if (description == null) {
            description = parentTable.getDescription();
            if (description != null)
                inheritDocFrom = out.importDefaultType(parentTable.getJavaType());
        }

        boolean anyNotNull = false;
        for (IColumnMetadata c : columns)
            if (! c.isNullable(false)) {
                anyNotNull = true;
                break;
            }

        String property = xref.getPropertyName();

        QualifiedName parentType = parentTable.getJavaType();
        if (parentType == null)
            throw new NullPointerException("parentType");

        String parentTsTypeInfo = typeInfoResolver()//
                .property(property)//
                .resolve(parentType);

        if (table.getJavaType().equals(parentType))
            parentTsTypeInfo = "this";

        out.print(property);
        out.print(": ");
        out.print(out.name(EsmModules.dba.entity.property));

        Attrs attrs = new Attrs(TsCodeStyle.newLineProps);
        attrs.put("type", parentTsTypeInfo);
        if (anyNotNull)
            attrs.put("nullable", false);

        // attrs.put("icon", "fa-user");

        if (! Nullables.isEmpty(label))
            attrs.putQuoted("label", label);

        if (! Nullables.isEmpty(description)) {
            if (inheritDocFrom != null) {
                // description = "(" + description + ")";
                attrs.put("inheritsDoc", true);
            }
            attrs.putQuoted("description", description);
        }

        String validatorFn = String.format("this.validators.validate%s", //
                Strings.ucfirst(property));
        attrs.put("validator", validatorFn);

        out.print("(");
        attrs.toJson(out, true);
        out.println("),");
    }

}
