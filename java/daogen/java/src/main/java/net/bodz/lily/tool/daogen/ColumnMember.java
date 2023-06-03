package net.bodz.lily.tool.daogen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ColumnMember {

    public String declaringClassName;
    public Class<?> declaringClass;
    public Class<?> bestKnownClass;
    public Field field;
    public Method getter;
    public Method setter;

}
