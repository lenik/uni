package net.bodz.lily.tool.daogen.util;

public interface IJavaConstructs {

    String construct(Object val, IJavaConstructContext context);

    String construct(int sqlType, Object val, IJavaConstructContext context);

    String construct(String sqlTypeName, Object val, IJavaConstructContext context);

    IJavaConstructs DEFAULT = DefaultJavaConstructs.INSTANCE;

}
