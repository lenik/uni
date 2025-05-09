package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.code.util.BaseTypeAnalyzer;
import net.bodz.bas.code.util.BaseTypeExtendInfo;
import net.bodz.bas.code.util.TypeArg;
import net.bodz.bas.codegen.IJavaImporter;
import net.bodz.bas.esm.ITsImporter;
import net.bodz.bas.meta.decl.TypeParamType;
import net.bodz.bas.meta.decl.TypeParameters;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.concrete.CoEntity;
import net.bodz.lily.concrete.IdEntity;
import net.bodz.lily.concrete.StructRow;
import net.bodz.lily.tool.daogen.DaoGenProject;
import net.bodz.lily.tool.daogen.MiscTemplates;

public class TypeAnalyzer
        extends BaseTypeAnalyzer {

    final DaoGenProject project;

    public TypeAnalyzer(DaoGenProject project, IJavaImporter javaImporter) {
        super(javaImporter);
        this.project = project;
    }

    public TypeAnalyzer(DaoGenProject project, ITsImporter tsImporter) {
        super(tsImporter);
        this.project = project;
    }

    @Override
    protected TypeExtendInfo newInfo() {
        return new TypeExtendInfo();
    }

    public final TypeExtendInfo getExtendInfo(ITableMetadata table, QualifiedName qName) {
        return getExtendInfo(table, qName, null);
    }

    public TypeExtendInfo getExtendInfo(ITableMetadata table, //
            QualifiedName type, //
            QualifiedName baseType) {

        TypeExtendInfo info = newInfo();
        info.type = type;

        try {
            info.javaClass = Class.forName(info.type.getFullName());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            //
        }

        MiscTemplates templates = new MiscTemplates(project);
        info.idType = templates.getIdType(table);
        IColumnMetadata[] idCols = table.getPrimaryKeyColumns();

        if (baseType == null) {
            if (table.getBaseTypeName() != null)
                baseType = QualifiedName.parse(table.getBaseTypeName());
            else {
                Class<?> baseClass;
                if (info.idType != null) {
                    baseClass = CoEntity.class;
                    if (idCols.length == 1 && idCols[0].getName().equals("id"))
                        baseClass = IdEntity.class;
                    if (typeScript) {
                        String tsIdType = typeResolver().property("<id>")//
                                .importAsType().resolve(info.idType);
                        info.baseTypeArgs = array(tsIdType);
                    } else {
                        info.baseTypeArgs = array(javaImporter.importName(info.idType));
                    }
                } else {
                    baseClass = StructRow.class;
                }
                baseType = QualifiedName.parse(baseClass.getCanonicalName());
            }
        }
        info.setBaseType(baseType);

        if (info.javaBaseClass != null) {
            TypeParameters aBaseTypeParams = info.javaBaseClass.getAnnotation(TypeParameters.class);
            if (aBaseTypeParams != null)
                parseGenerics(info, aBaseTypeParams.value());
        }
        return info;
    }

    @Override
    protected TypeArg getArg(BaseTypeExtendInfo info, TypeParamType baseParamType) {
        if (baseParamType == TypeParamType.ID_TYPE) {
            TypeExtendInfo info2 = (TypeExtendInfo) info;
            TypeArg arg = new TypeArg();
            if (typeScript) {
                String tsIdType = typeResolver().property("<ID>")//
                        .importAsType().resolve(info2.idType);
                arg.name = tsIdType;
            } else {
                arg.name = javaImporter.importName(info2.idType);
            }
            arg.bounds = info2.idType;
            return arg;
        }
        return super.getArg(info, baseParamType);
    }

}
