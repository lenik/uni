package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.t.tuple.QualifiedName;

public class TypeExtendInfo {

    public QualifiedName idType;

    public Class<?> clazz;
    public QualifiedName className;
    public String simpleName;

    public Class<?> baseClass;
    public QualifiedName baseClassName;

    public String params = "";
    public String baseParams = "";
    public String typeAgain = null;

}
