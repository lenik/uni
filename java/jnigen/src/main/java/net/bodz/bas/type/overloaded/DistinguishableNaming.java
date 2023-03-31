package net.bodz.bas.type.overloaded;

import java.lang.reflect.Constructor;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.StringTokenizer;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.IllegalUsageException;

public enum DistinguishableNaming {

    NO_PARAM,

    PARAM_COUNT,

    SIMPLE_PARAM_NAME,

    SIMPLE_PARAM_TYPE,

    QUALIFIED_PARAM_TYPE,

    ;

    static DistinguishableNaming[] defaultNamingCandidates = DistinguishableNaming.values();

    public String getName(Constructor<?> ctor) {
        return getSuffix(ctor.getParameters());
    }

    public String getName(Method method) {
        Parameter[] parameters;
        try {
            // MalformedParametersException
            parameters = method.getParameters();
        } catch (MalformedParametersException e) {
            String message = String.format("Error analyze method %s: %s", //
                    method, e.getMessage());
            throw new IllegalUsageException(message, e);
        }
        return method.getName() + getSuffix(parameters);
    }

    public String getSuffix(Parameter[] params) {
        StringBuilder sb;

        switch (this) {
        case NO_PARAM:
            return "";

        case PARAM_COUNT:
            if (params.length == 0)
                return "";
            else
                return "_" + params.length;

        case SIMPLE_PARAM_NAME:
            sb = new StringBuilder();
            for (Parameter p : params) {
                String paramName = p.getName();
                if (paramName == null)
                    paramName = "unknown";
                sb.append("_" + paramName);
            }
            return sb.toString();

        case SIMPLE_PARAM_TYPE:
            sb = new StringBuilder();
            for (Parameter p : params)
                sb.append("_" + toSimpleTypeName(p.getType()));
            return sb.toString();

        case QUALIFIED_PARAM_TYPE:
        default:
            sb = new StringBuilder();
            for (Parameter p : params) {
                String qCamelName = toQualifiedCamelName(p.getType());
                sb.append("_");
                sb.append(qCamelName);
            }
            return sb.toString();
        }
    }

    static String arrayName(String name) {
        return name + "Array";
    }

    static String toQualifiedCamelName(Class<?> type) {
        if (type.isArray()) {
            return arrayName(toQualifiedCamelName(type.getComponentType()));
        }
        String name = type.getName();
        StringTokenizer tokens = new StringTokenizer(name, ".");
        StringBuilder buf = new StringBuilder(name.length());
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            String word = Strings.ucfirst(token);
            buf.append(word);
        }
        return buf.toString();
    }

    static String toSimpleTypeName(Class<?> type) {
        if (type.isArray()) {
            return arrayName(toSimpleTypeName(type.getComponentType()));
        }
        String simpleName = type.getSimpleName();
        return simpleName;
    }

}
