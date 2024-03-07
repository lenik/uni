package net.bodz.lily.tool.daogen.dir.web;

import java.util.Map;
import java.util.Set;

import net.bodz.bas.c.type.TypeId;
import net.bodz.bas.c.type.TypeKind;
import net.bodz.bas.c.type.TypePoMap;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.ITsImporter;
import net.bodz.bas.esm.skeljs.TsTypes;

public class TsTypeResolver
        extends AbstractTsResolver<TsTypeResolver> {

    static TypePoMap<String> builtins = new TypePoMap<>();

    static void addAbstractType(Class<?> type, String builtinName) {
        builtins.put(type, builtinName);
    }

    static {
        addAbstractType(Set.class, "Set");
        addAbstractType(Map.class, "Map");
    }

    public TsTypeResolver(ITsImporter imports) {
        super(imports);
    }

    @Override
    public String resolveClass(Class<?> javaClass) {
        if (javaClass == null)
            throw new NullPointerException("null javaClass: " + propertyName);

        javaClass = TsConfig.getEquivType(javaClass);

        switch (TypeKind.getTypeId(javaClass)) {
        case TypeId._boolean:
        case TypeId.BOOLEAN:
            return "boolean";

        case TypeId.STRING:
            return "string";

        case TypeId.DATE:
            return "Date";
        }

        EsmName baseTypeName = TsTypes.INSTANCE.forClass(javaClass);
        if (baseTypeName != null)
            return imports.importName(baseTypeName);

        if (javaClass.isArray())
            return resolveClass(javaClass.getComponentType()) + "[]";

        String builtin = builtins.meet(javaClass);
        if (builtin != null)
            return builtin;

        if (importAsType)
            return imports.importDefaultType(javaClass);
        else
            return imports.importDefault(javaClass);
    }

    @Override
    public String resolveNotFoundClass(String className) {
        if (importAsType)
            return imports.importDefaultType(className);
        else
            return imports.importDefault(className);
    }

}
