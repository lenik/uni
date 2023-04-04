package net.bodz.bas.type.overloaded;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import net.bodz.bas.repr.form.SortOrder;

public class TypeInfoOptions {

    public boolean declaredOnly;
    public boolean _protected;
    public boolean packagePrivate;
    public boolean _private;

    public SortOrder memberOrder = SortOrder.KEEP;
    public boolean includeNativeMethodsAlways = true;
    public boolean includeConsts;

    public boolean include(Class<?> clazz, Member member) {
        if (member.getDeclaringClass() != clazz)
            if (member.getDeclaringClass() == Object.class)
                return false;

        int modifiers = member.getModifiers();
        if (Modifier.isProtected(modifiers) && !_protected)
            return false;
        if (isPackagePrivate(modifiers) && !packagePrivate)
            return false;
        if (Modifier.isPrivate(modifiers) && !_private)
            return false;
        return true;
    }

    static int packagePrivateMask = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

    static boolean isPackagePrivate(int modifier) {
        return (modifier & packagePrivateMask) == 0;
    }

}
