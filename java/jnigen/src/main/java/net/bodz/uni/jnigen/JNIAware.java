package net.bodz.uni.jnigen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface JNIAware {

    default String jniType(Class<?> javaType) {
        String c = JNIConsts.jniTypeName.get(javaType);
        if (c != null)
            return c;

        if (javaType.isArray())
            return "jarray";

        if (Throwable.class.isAssignableFrom(javaType))
            return "jthrowable";

        return "jobject";
    }

    default String signature(Class<?> type) {
        String c = JNIConsts.typeSignature.get(type);
        if (c != null)
            return c;

        if (type.isArray())
            return "[" + signature(type.getComponentType());

        return "L" + type.getName().replace('.', '/') + ";";
    }

    default String signature(Method method) {
        Class<?>[] pv = method.getParameterTypes();
        StringBuilder sb = new StringBuilder(pv.length * 32);
        sb.append("(");
        for (Class<?> paramType : pv) {
            String item = signature(paramType);
            sb.append(item);
        }
        sb.append(")");
        Class<?> returnType = method.getReturnType();
        String item = signature(returnType);
        sb.append(item);
        return sb.toString();
    }

    default String signature(Constructor<?> ctor) {
        Class<?>[] pv = ctor.getParameterTypes();
        StringBuilder sb = new StringBuilder(pv.length * 32);
        sb.append("(");
        for (Class<?> paramType : pv) {
            String item = signature(paramType);
            sb.append(item);
        }
        sb.append(")V");
        return sb.toString();
    }

    default String _callType(Class<?> type) {
        String callname = JNIConsts.typeCallname.get(type);
        return callname != null ? callname : "Object";
    }

    default String getFieldFunc(Class<?> type) {
        String callname = _callType(type);
        return "Get" + callname + "Field";
    }

    default String setFieldFunc(Class<?> type) {
        String callname = _callType(type);
        return "Set" + callname + "Field";
    }

    default String callMethodFunc(Class<?> retType) {
        String callname = _callType(retType);
        return "Call" + callname + "Method";
    }

    default String callStaticMethodFunc(Class<?> retType) {
        String callname = _callType(retType);
        return "CallStatic" + callname + "Method";
    }

    default String autoCastFromObject(Class<?> type, String expr) {
        if (JNIConsts.autoCastTypes.contains(type) //
                || type.isArray()) {
            String target = jniType(type);
            String castExpr = String.format("(%s) (%s)", target, expr);
            return castExpr;
        }
        return expr;
    }

    default String propertyType(Class<?> type) {
        String callname = JNIConsts.typeCallname.get(type);
        if (callname != null)
            return callname + "Property";
        else
            return "ObjectProperty<" + jniType(type) + ">";
    }

}
