package net.bodz.lily.tool.daogen.util;

import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.codegen.IJavaImporter;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.meta.TypeParamType;

public class TypeExtendInfo {

    public QualifiedName idType;
    public Class<?> javaIdClass;

    public QualifiedName type;
    public Class<?> javaClass;

    public QualifiedName baseType;
    public Class<?> javaBaseClass;

    public String[] typeVars;
    public TypeParamType[] typeVarTypes;

    public String[] baseTypeArgs;
    public QualifiedName[] baseTypeBounds;
    public TypeParamType[] baseTypeVarTypes;

    public void setIdType(QualifiedName idType) {
        this.idType = idType;
    }

    public void setBaseType(QualifiedName baseType) {
        this.baseType = baseType;

        String name = baseType.getFullName();
        try {
            javaBaseClass = Class.forName(name);
            assert javaBaseClass.getCanonicalName().equals(name);
        } catch (ClassNotFoundException e) {
            // baseClass may be generated one.
        }
    }

    public int typeVarCount() {
        return typeVars == null ? 0 : typeVars.length;
    }

    public int baseTypeArgCount() {
        return baseTypeArgs == null ? 0 : baseTypeArgs.length;
    }

    public String angledTypeVars() {
        if (typeVars == null || typeVars.length == 0)
            return "";
        else
            return "<" + StringArray.join(", ", typeVars) + ">";
    }

    public String angledBaseTypeArgs() {
        if (baseTypeArgs == null || baseTypeArgs.length == 0)
            return "";
        else
            return "<" + StringArray.join(", ", baseTypeArgs) + ">";
    }

    public String bracedTypeParamTypes(IJavaImporter importer) {
        if (typeVarTypes == null || typeVarTypes.length == 0)
            return null;
        StringBuilder sb = new StringBuilder(typeVarTypes.length * 30);
        sb.append("{");
        for (int i = 0; i < typeVarTypes.length; i++) {
            TypeParamType type = typeVarTypes[i];
            if (i > 0)
                sb.append(",");
            sb.append(' ');
            sb.append(importer.importName(TypeParamType.class));
            sb.append('.');
            sb.append(type.name());
        }
        sb.append(" }");
        return sb.toString();
    }

    public String getCtorParams(ITsImporterAware importer) {
        List<String> ctorParams = new ArrayList<>();
        if (this.typeVarTypes != null)
            for (TypeParamType varType : this.typeVarTypes) {
                switch (varType) {
                case THIS_REC:
                case THIS_TYPE:
                    // ctorParams.add("selfType: any");
                    break;
                default:
                }
            }
        return StringArray.join(", ", ctorParams);
    }

    public String getSuperCtorArgs(ITsImporterAware importer) {
        List<String> superArgs = new ArrayList<>();
        if (this.baseTypeVarTypes != null)
            for (int i = 0; i < this.baseTypeVarTypes.length; i++) {
                TypeParamType baseVarType = this.baseTypeVarTypes[i];
                switch (baseVarType) {
                case THIS_REC:
                    // superArgs.add("selfType");
                    break;

                case THIS_TYPE:
                    // see: isSelfTypeNeeded()
                    break;

                case ID_TYPE:
                    QualifiedName bound = this.baseTypeBounds[i];
                    String tsBound = importer.typeInfoResolver()//
                            .property("base<" + i + ">")//
                            .resolve(bound);
                    // superArgs.add(this.baseTypeArgs[i]);
                    superArgs.add(tsBound);
                    break;

                default:
                }
            }
        return StringArray.join(", ", superArgs);
    }

    public boolean isSelfTypeNeeded() {
        if (this.baseTypeVarTypes != null)
            for (TypeParamType baseVarType : baseTypeVarTypes)
                if (baseVarType == TypeParamType.THIS_TYPE)
                    return true;
        return false;
    }

}
