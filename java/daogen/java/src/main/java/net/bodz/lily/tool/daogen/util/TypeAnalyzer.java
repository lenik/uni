package net.bodz.lily.tool.daogen.util;

import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.codegen.IJavaImporter;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.esm.ITsImporter;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.concrete.CoEntity;
import net.bodz.lily.concrete.StructRow;
import net.bodz.lily.meta.TypeParamType;
import net.bodz.lily.meta.TypeParameters;
import net.bodz.lily.tool.daogen.JavaGenProject;
import net.bodz.lily.tool.daogen.MiscTemplates;

public class TypeAnalyzer
        implements
            ITsImporterAware {

    final JavaGenProject project;
    final IJavaImporter javaImporter;
    final ITsImporter tsImporter;
    final boolean typeScript;

    public TypeAnalyzer(JavaGenProject project, IJavaImporter javaImporter) {
        this.project = project;
        this.javaImporter = javaImporter;
        this.tsImporter = null;
        this.typeScript = false;
    }

    public TypeAnalyzer(JavaGenProject project, ITsImporter tsImporter) {
        this.project = project;
        this.javaImporter = null;
        this.tsImporter = tsImporter;
        this.typeScript = true;
    }

    @Override
    public ITsImporter getTsImporter() {
        return tsImporter;
    }

    public final TypeExtendInfo getExtendInfo(ITableMetadata table, QualifiedName qName) {
        return getExtendInfo(table, qName, null);
    }

    public TypeExtendInfo getExtendInfo(ITableMetadata table, //
            QualifiedName type, //
            QualifiedName baseType) {

        TypeExtendInfo info = new TypeExtendInfo();
        info.type = type;

        try {
            info.javaClass = Class.forName(info.type.getFullName());
        } catch (ClassNotFoundException e1) {
        }

        MiscTemplates templates = new MiscTemplates(project);
        info.idType = templates.getIdType(table);

        if (baseType == null) {
            if (table.getBaseTypeName() != null)
                baseType = QualifiedName.parse(table.getBaseTypeName());
            else {
                Class<?> baseClass;
                if (info.idType != null) {
                    baseClass = CoEntity.class;
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

    void parseGenerics(TypeExtendInfo info, TypeParamType[] baseTypeVarTypes) {
        List<String> typeVars = new ArrayList<>();
        List<String> baseTypeArgs = new ArrayList<>();
        List<QualifiedName> baseTypeBounds = new ArrayList<>();
        List<String> recursiveArgs = new ArrayList<>();
        List<TypeParamType> typeVarTypes = new ArrayList<>();
        for (int i = 0; i < baseTypeVarTypes.length; i++) {
            TypeParamType baseParamType = baseTypeVarTypes[i];
            switch (baseParamType) {
            case ID_TYPE:
                if (typeScript) {
                    String tsIdType = typeResolver().property("<ID>")//
                            .importAsType().resolve(info.idType);
                    baseTypeArgs.add(tsIdType);
                } else {
                    baseTypeArgs.add(javaImporter.importName(info.idType));
                }
                baseTypeBounds.add(info.idType);
                break;

            case THIS_TYPE:
                baseTypeArgs.add(info.type.name);
                baseTypeBounds.add(info.type);
                break;

            case THIS_REC:
                if (typeScript)
                    typeVars.add("this_t");
                else
                    typeVars.add("this_t extends " + info.type.name + "<%R>");

                recursiveArgs.add("this_t");
                typeVarTypes.add(TypeParamType.THIS_TYPE);

                baseTypeArgs.add("this_t");
                baseTypeBounds.add(ThisType.QNAME);
                break;
            default:
                throw new UnexpectedException();
            }
        }

        String[] _typeVars = typeVars.toArray(new String[0]);
        String rec = StringArray.join(", ", recursiveArgs);
        for (int i = 0; i < _typeVars.length; i++)
            _typeVars[i] = _typeVars[i].replace("%R", rec);
        info.typeVars = _typeVars;
        info.typeVarTypes = typeVarTypes.toArray(new TypeParamType[0]);

        info.baseTypeArgs = baseTypeArgs.toArray(new String[0]);
        info.baseTypeBounds = baseTypeBounds.toArray(new QualifiedName[0]);
        info.baseTypeVarTypes = baseTypeVarTypes;
    }

    static String[] array(String... args) {
        return args;
    }

}
