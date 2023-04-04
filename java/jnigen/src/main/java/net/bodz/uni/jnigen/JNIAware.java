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

    default String jniEscape(String s) {
        int n = s.length();
        StringBuilder sb = new StringBuilder(n + 10);
        for (int i = 0; i < n; i++) {
            char ch = s.charAt(i);
            if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9') {
                sb.append(ch);
                continue;
            }
            switch (ch) {
            case '/':
            case '.':
                sb.append(".");
                break;
            case '_':
                sb.append("_1");
                break;
            case ';':
                sb.append("_2");
                break;
            case '[':
                sb.append("_3");
                break;
            default:
                sb.append("_0");
                sb.append(String.format("%04x", (int) ch));
            }
        }
        return sb.toString();
    }

    default String jniClassName(Class<?> clazz) {
        String qName = clazz.getName();
        String escName = jniEscape(qName);
        escName = escName.replace('.', '_');
        return escName;
    }

    default String jniFunctionName(Method method) {
        String cls = jniClassName(method.getDeclaringClass());
        String name = method.getName();
        String escName = jniEscape(name);
        return "Java_" + cls + "_" + escName;
    }

}
