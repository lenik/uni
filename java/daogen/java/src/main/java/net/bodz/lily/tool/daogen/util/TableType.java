package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.codegen.IImportNaming;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.Split;
import net.bodz.lily.concrete.IdEntity;
import net.bodz.lily.concrete.StructRow;
import net.bodz.lily.meta.TypeParamType;
import net.bodz.lily.meta.TypeParameters;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.MiscTemplates;

public class TableType {

    public QualifiedName idType;

    public String className;
    public String simpleName;
    public String baseClassName;

    public String params = "";
    public String baseParams = "";
    public String typeAgain = null;

    public TableType(JavaGenProject project, IImportNaming naming, //
            ITableMetadata table, String className) {
        this(project, naming, table, className, null);
    }

    public TableType(JavaGenProject project, IImportNaming naming, //
            ITableMetadata table, String className, String baseClassName) {
        this.className = className;
        this.simpleName = Split.packageName(className).b;

        MiscTemplates templates = new MiscTemplates(project);
        idType = templates.getIdType(table);

        if (baseClassName == null)
            baseClassName = table.getBaseTypeName();

        if (baseClassName != null) {
            Class<?> baseClass;
            try {
                baseClass = CanonicalClass.forName(baseClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            baseClassName = naming.importName(baseClassName); // import&compress

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
                    case THIS_REC:
                        baseParamsBuf.append(", " + simpleName);
                        break;
                    default:
                        baseParamsBuf.append(", any");
                    }
                }
                String rec = recBuf.length() == 0 ? "" : recBuf.substring(2);
                params = paramsBuf.length() == 0 ? "" : ("<" + paramsBuf.substring(2) + ">");
                params = params.replace("%R", rec);
                baseParams = baseParamsBuf.length() == 0 ? "" : "<" + baseParamsBuf.substring(2) + ">";
                typeAgain = typeAgainParams.length() == 0 ? null : typeAgainParams.substring(2);
            }
        } else if (idType != null) {
            baseClassName = naming.importName(IdEntity.class);
            baseParams = "<" + naming.importName(idType) + ">";
        } else
            baseClassName = naming.importName(StructRow.class);

        if (baseClassName == null) {
            if (idType != null) {
                baseClassName = IdEntity.class.getName();
                baseParams = "<" + idType + ">";
            } else
                baseClassName = StructRow.class.getName();
        }
    }

}
