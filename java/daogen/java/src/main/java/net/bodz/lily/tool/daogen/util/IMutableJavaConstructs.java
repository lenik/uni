package net.bodz.lily.tool.daogen.util;

public interface IMutableJavaConstructs
        extends IJavaConstructs {

    <T> void define(Class<T> type, JavaSimpleConstructor<T> ctor);

    <T> void define(int sqlType, JavaSimpleConstructor<T> ctor);

    <T> void define(String sqlTypeName, JavaSimpleConstructor<T> ctor);

    <T> void defineEx(Class<T> type, JavaConstructor<T> ctor);

    <T> void defineEx(int sqlType, JavaConstructor<T> ctor);

    <T> void defineEx(String sqlTypeName, JavaConstructor<T> ctor);

    <T> void defineImported(Class<T> type, JavaConstructor<T> ctor);

    <T> void defineImported(int sqlType, JavaConstructor<T> ctor);

    <T> void defineImported(String sqlTypeName, JavaConstructor<T> ctor);

}
