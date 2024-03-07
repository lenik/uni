package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import net.bodz.bas.c.type.ClassNames;
import net.bodz.bas.c.type.TypeParam;
import net.bodz.bas.esm.ITsImporter;
import net.bodz.bas.t.tuple.QualifiedName;

public abstract class AbstractTsResolver<This> {

    final ITsImporter imports;

    boolean importAsType;
    String propertyName;
    QualifiedName thisType;

    public AbstractTsResolver(ITsImporter imports) {
        if (imports == null)
            throw new NullPointerException("imports");
        this.imports = imports;
    }

    @SuppressWarnings("unchecked")
    This _this() {
        return (This) this;
    }

    public This importAsType() {
        this.importAsType = true;
        return _this();
    }

    public This property(String propertyName) {
        this.propertyName = propertyName;
        return _this();
    }

    public This thisType(QualifiedName thisType) {
        this.thisType = thisType;
        return _this();
    }

    public This thisType(String thisTypeName) {
        this.thisType = QualifiedName.parse(thisTypeName);
        return _this();
    }

    public String importName(QualifiedName qName) {
        if (importAsType)
            return imports.importDefaultType(qName);
        else
            return imports.importDefault(qName);
    }

    public String resolve(QualifiedName qName) {
        String className = qName.getFullName();
        return resolve(className);
    }

    public String resolve(String className) {
        try {
            Class<?> javaClass = ClassNames.resolve(className);
            return resolveClass(javaClass);
        } catch (ClassNotFoundException e) {
            return resolveNotFoundClass(className);
        }
    }

    public abstract String resolveClass(Class<?> javaClass);

    public abstract String resolveNotFoundClass(String className);

    public String resolveGeneric(Type genericType) {
        if (genericType instanceof Class<?>)
            return resolveClass((Class<?>) genericType);

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            return resolveParameterizedType(pt);
        }

        if (genericType instanceof TypeVariable<?>) {
            // GenericDeclaration gd;
            TypeVariable<?> tv = (TypeVariable<?>) genericType;
            return resolveTypeVariable(tv);
        }
        throw new UnsupportedOperationException("genericType: " + genericType + ": " + genericType.getClass());
    }

    public String resolveParameterizedType(ParameterizedType paramType) {
        Type rawType = paramType.getRawType();
        String rawTsType = resolveGeneric(rawType);

        Type[] typeArgs = paramType.getActualTypeArguments();
        Class<?>[] bounds = TypeParam.bounds(typeArgs);

        assert bounds.length > 0;
        StringBuilder sb = new StringBuilder();
        sb.append(rawTsType);
        sb.append('<');
        for (int i = 0; i < bounds.length; i++) {
            Class<?> bClass = bounds[i];
            String bTsType = resolveClass(bClass);
            if (i > 0)
                sb.append(", ");
            sb.append(bTsType);
        }
        sb.append('>');
        return sb.toString();
    }

    public String resolveTypeVariable(TypeVariable<?> typeVariable) {
        Type[] bounds = typeVariable.getBounds();
        Type bound1 = bounds[0];
        String tsBound1 = resolveGeneric(bound1);
        return tsBound1;
    }

}
