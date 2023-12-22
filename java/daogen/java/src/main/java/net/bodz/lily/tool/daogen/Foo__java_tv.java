package net.bodz.lily.tool.daogen;

import javax.persistence.Table;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.lily.meta.TypeParamType;
import net.bodz.lily.meta.TypeParameters;
import net.bodz.lily.model.base.CoEntity;
import net.bodz.lily.model.base.StructRow;
import net.bodz.lily.tool.daogen.util.CanonicalClass;

public class Foo__java_tv
        extends JavaGen__java {

    public Foo__java_tv(JavaGenProject project) {
        super(project, project.Foo);
    }

    @Override
    protected void buildClassBody(JavaSourceWriter out, ITableMetadata table) {
        TableOid oid = table.getId();

        QualifiedName idType = templates.getIdType(table);

        String simpleName = project.Foo.name;
        String baseClassName = project._Foo_stuff.getFullName();

        String params = "";
        String baseParams = "";
        String typeAgain = null;

        if (baseClassName != null) {
            Class<?> baseClass = null;
            try {
                baseClass = CanonicalClass.forName(baseClassName);
                baseClassName = out.im.name(baseClass); // import&compress
            } catch (ClassNotFoundException e) {
                baseClassName = out.im.name(baseClassName); // _Foo_stuff
            }

            if (baseClass != null) {
                TypeParameters aTypeParams = baseClass.getAnnotation(TypeParameters.class);
                if (aTypeParams != null) {
                    StringBuilder paramsBuf = new StringBuilder();
                    StringBuilder baseParamsBuf = new StringBuilder();
                    StringBuilder recBuf = new StringBuilder();
                    StringBuilder typeAgainParams = new StringBuilder();
                    for (TypeParamType param : aTypeParams.value()) {
                        switch (param) {
                        case ID_TYPE:
                            baseParamsBuf.append(", " + idType.name);
                            break;
                        case THIS_TYPE:
                            baseParamsBuf.append(", " + simpleName);
                            break;
                        case THIS_REC:
                            paramsBuf.append(", this_t extends " + simpleName + "<%R>");
                            baseParamsBuf.append(", this_t");
                            recBuf.append(", this_t");
                            typeAgainParams.append(", " + out.im.simpleId(TypeParamType.THIS_TYPE));
                            break;
                        default:
                            baseParamsBuf.append(", ?");
                        }
                    }
                    String rec = recBuf.length() == 0 ? "" : recBuf.substring(2);
                    params = paramsBuf.length() == 0 ? "" : ("<" + paramsBuf.substring(2) + ">");
                    params = params.replace("%R", rec);
                    baseParams = baseParamsBuf.length() == 0 ? "" : "<" + baseParamsBuf.substring(2) + ">";
                    typeAgain = typeAgainParams.length() == 0 ? null : typeAgainParams.substring(2);
                }
            }
        } else if (idType != null) {
            baseClassName = out.im.name(CoEntity.class);
            baseParams = "<" + out.im.name(idType) + ">";
        } else
            baseClassName = out.im.name(StructRow.class);

        out.print("@" + out.im.name(Table.class) + "(");
        {
            String catalog_name = oid.getCatalogName();
            if (catalog_name != null)
                out.printf("catalog = %s.CATALOG_NAME, ", simpleName);
            String schema_name = oid.getSchemaName();
            if (schema_name != null)
                out.printf("schema = %s.SCHEMA_NAME, ", simpleName);
            out.printf("name = %s.TABLE_NAME", simpleName);
            out.println(")");
        }

        out.printf("public class %s%s\n", simpleName, params);
        out.printf("        extends %s%s {\n", baseClassName, baseParams);
        out.enter();
        {
            out.println();
            out.println("private static final long serialVersionUID = 1L;");

            out.leave();
        }
        out.println();
        out.println("}");
    }

}
