package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.codegen.IImportNaming;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.concrete.CoEntity;
import net.bodz.lily.concrete.StructRow;
import net.bodz.lily.meta.TypeParamType;
import net.bodz.lily.meta.TypeParameters;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.MiscTemplates;
import net.bodz.lily.tool.daogen.dir.web.TsTypeResolver;

public class TypeAnalyzer {

    JavaGenProject project;
    IImportNaming naming;
    boolean typeScript;

    public TypeAnalyzer(JavaGenProject project, IImportNaming naming) {
        this(project, naming, false);
    }

    public TypeAnalyzer(JavaGenProject project, IImportNaming naming, boolean typeScript) {
        this.project = project;
        this.naming = naming;
        this.typeScript = typeScript;
    }

    public TypeExtendInfo getExtendInfo(ITableMetadata table, QualifiedName qName) {
        return getExtendInfo(table, qName, null);
    }

    public TypeExtendInfo getExtendInfo(ITableMetadata table, //
            QualifiedName qName, //
            QualifiedName baseClassName) {

        TsTypeResolver tsTypes = new TsTypeResolver(naming);

        TypeExtendInfo info = new TypeExtendInfo();
        info.className = qName.getFullName();
        info.simpleName = qName.name;

        try {
            info.clazz = Class.forName(info.className);
        } catch (ClassNotFoundException e1) {
        }

        MiscTemplates templates = new MiscTemplates(project);
        info.idType = templates.getIdType(table);

        if (baseClassName == null) {
            if (table.getBaseTypeName() != null)
                baseClassName = QualifiedName.parse(table.getBaseTypeName());
            else {
                if (info.idType != null) {
                    info.baseClass = CoEntity.class;
                    info.baseParams = "<" + naming.importName(info.idType) + ">";
                } else {
                    info.baseClass = StructRow.class;
                }
                baseClassName = QualifiedName.parse(info.baseClass.getCanonicalName());
            }
        }
        info.baseClassName = baseClassName;

        try {
            info.baseClass = Class.forName(baseClassName.getFullName());
            assert info.baseClass.getCanonicalName().equals(baseClassName.getFullName());
        } catch (ClassNotFoundException e) {
            // baseClass may be generated one.
        }

        if (info.baseClass != null) {
            TypeParameters aTypeParams = info.baseClass.getAnnotation(TypeParameters.class);
            if (aTypeParams != null) {
                StringBuilder paramsBuf = new StringBuilder();
                StringBuilder baseParamsBuf = new StringBuilder();
                StringBuilder recBuf = new StringBuilder();
                StringBuilder typeAgainParams = new StringBuilder();
                for (TypeParamType param : aTypeParams.value()) {
                    switch (param) {
                    case ID_TYPE:
                        String idType = typeScript ? tsTypes.resolve(info.idType) //
                                : info.idType.getFullName();
                        baseParamsBuf.append(", " + naming.importName(idType));
                        break;
                    case THIS_TYPE:
                        baseParamsBuf.append(", " + info.simpleName);
                        break;
                    case THIS_REC:
                        if (typeScript)
                            paramsBuf.append(", this_t");
                        else
                            paramsBuf.append(", this_t extends " + info.simpleName + "<%R>");

                        baseParamsBuf.append(", this_t");
                        recBuf.append(", this_t");
                        typeAgainParams.append(", " + //
                                naming.importName(TypeParamType.THIS_TYPE.getClass()) //
                                + "." + TypeParamType.THIS_TYPE.name());
                        break;
                    default:
                        baseParamsBuf.append(", any");
                    }
                }
                String rec = recBuf.length() == 0 ? "" : recBuf.substring(2);

                String params = paramsBuf.length() == 0 ? "" : ("<" + paramsBuf.substring(2) + ">");
                info.params = params.replace("%R", rec);

                info.baseParams = baseParamsBuf.length() == 0 ? "" : "<" + baseParamsBuf.substring(2) + ">";
                info.typeAgain = typeAgainParams.length() == 0 ? null : typeAgainParams.substring(2);
            }
        }
        return info;
    }

}
