package net.bodz.lily.tool.daogen.dir.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.bodz.bas.c.type.TypeParam;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.ITsImporter;
import net.bodz.bas.esm.skeljs.TsTypeInfos;
import net.bodz.bas.esm.skeljs.TsTypes;
import net.bodz.bas.t.predef.Predef;
import net.bodz.bas.t.predef.PredefMetadata;
import net.bodz.bas.t.tuple.QualifiedName;

public class TsTypeInfoResolver
        extends AbstractTsResolver<TsTypeInfoResolver> {

    public TsTypeInfoResolver(ITsImporter imports) {
        super(imports);
    }

    @Override
    public String resolveParameterizedType(ParameterizedType paramType) {
        Type rawType = paramType.getRawType();
        String rawTsType = resolveGeneric(rawType);

        Type[] typeArgs = paramType.getActualTypeArguments();
        Class<?>[] bounds = TypeParam.bounds(typeArgs);

        assert bounds.length > 0;
        StringBuilder sb = new StringBuilder();
        sb.append(rawTsType);
        sb.append('(');
        for (int i = 0; i < bounds.length; i++) {
            Class<?> bClass = bounds[i];
            String bTsType = resolveClass(bClass);
            if (i > 0)
                sb.append(", ");
            sb.append(bTsType);
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String resolveClass(Class<?> javaClass) {
        if (javaClass == null)
            throw new NullPointerException("javaClass");
        if (javaClass == Object.class)
            System.err.println();
        javaClass = TsConfig.getEquivType(javaClass);

        EsmName tsTypeInfo = TsTypeInfos.INSTANCE.forClass(javaClass);
        if (tsTypeInfo != null) {
            String alias = imports.importName(tsTypeInfo);
            if (javaClass.isArray()) {
                String itemType = resolveClass(javaClass.getComponentType());
                alias += "(" + itemType + ")";
            }
            return alias;
        }

        if (Predef.class.isAssignableFrom(javaClass)) {
            PredefMetadata<?, ?> metadata = PredefMetadata._forClass(javaClass);
            Class<?> keyType = metadata.getKeyType();
            return resolveClass(keyType);
        }

        EsmName tsType = TsTypes.INSTANCE.forClass(javaClass);
        if (tsType != null)
            return imports.importName(tsType) + ".TYPE";

        QualifiedName qName = QualifiedName.of(javaClass);
        return resolveQName(qName);
    }

    @Override
    public String resolveNotFoundClass(String className) {
        QualifiedName qName = QualifiedName.parse(className);
        return resolveQName(qName);
    }

    String resolveQName(QualifiedName qName) {
        if (qName == null)
            throw new NullPointerException("qName");
        if (qName.equals(thisType))
            return "this";
        return imports.importDefault(qName) + ".TYPE";
    }

}
