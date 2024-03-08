package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.c.string.StringArray;
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

    public String angledTypeVars() {
        if (typeVars.length == 0)
            return "";
        else
            return "<" + StringArray.join(", ", typeVars) + ">";
    }

    public String angledBaseTypeArgs() {
        if (baseTypeArgs.length == 0)
            return "";
        else
            return "<" + StringArray.join(", ", baseTypeArgs) + ">";
    }

    public String bracedTypeParamTypes() {
        if (typeVarTypes.length == 0)
            return null;
        else
            return "{ " + StringArray.join(", ", typeVarTypes) + " }";
    }

}
