package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.code.util.BaseTypeExtendInfo;
import net.bodz.bas.t.tuple.QualifiedName;

public class TypeExtendInfo
        extends BaseTypeExtendInfo {

    public QualifiedName idType;
    public Class<?> javaIdClass;

    public void setIdType(QualifiedName idType) {
        this.idType = idType;
    }

    @Override
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

}
