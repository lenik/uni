package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.codegen.QualifiedName;

public class TypeExtendInfo {

    public QualifiedName idType;

    public Class<?> clazz;
    public String className;
    public String simpleName;

    public Class<?> baseClass;
    public QualifiedName baseClassName;

    public String params = "";
    public String baseParams = "";
    public String typeAgain = null;

}
