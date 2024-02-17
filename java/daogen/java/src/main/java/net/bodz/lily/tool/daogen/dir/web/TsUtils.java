package net.bodz.lily.tool.daogen.dir.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import net.bodz.bas.c.type.TypeId;
import net.bodz.bas.c.type.TypeKind;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.err.LoadException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.esm.EsmModule;
import net.bodz.bas.esm.EsmModules;
import net.bodz.bas.esm.EsmName;
import net.bodz.bas.esm.EsmPackageMap;
import net.bodz.bas.json.JsonObject;
import net.bodz.lily.tool.daogen.util.NpmDir;

public class TsUtils {

    static HashMap<String, EsmName> aliases = new HashMap<>();
    static {
        aliases.put("integer", EsmModules.dba.entity.integer);
        aliases.put("long", EsmModules.dba.entity._long);
        aliases.put("Moment", EsmModules.moment.Moment);
    }

    public static EsmName getAlias(String type) {
        if (type == null)
            throw new NullPointerException("type");
        if (type.isEmpty())
            throw new IllegalArgumentException("empty string");
        return aliases.get(type);
    }

    public static String toTsType(QualifiedName javaType) {
        return toTsType(javaType.getFullName());
    }

    public static String toTsType(String javaType) {
        Class<?> clazz;
        try {
            clazz = Class.forName(javaType);
        } catch (ClassNotFoundException e) {
            return javaType;
        }
        return toTsType(clazz);
    }

    public static String toTsType(Class<?> clazz) {
        int typeId = TypeKind.getTypeId(clazz);
        switch (typeId) {
        case TypeId._char:
        case TypeId.CHARACTER:
            return "string";
        case TypeId._byte:
        case TypeId.BYTE:
        case TypeId._short:
        case TypeId.SHORT:
        case TypeId._int:
        case TypeId.INTEGER:
            return "integer";
        case TypeId._long:
        case TypeId.LONG:
            return "long";
        case TypeId._float:
        case TypeId.FLOAT:
        case TypeId._double:
        case TypeId.DOUBLE:
            return "number";
        case TypeId._boolean:
        case TypeId.BOOLEAN:
            return "boolean";
        case TypeId.BIG_INTEGER:
        case TypeId.BIG_DECIMAL:
            return "BigInteger";

        case TypeId.DATE:
        case TypeId.SQL_DATE:
        case TypeId.TIMESTAMP:
            return "Date";

        case TypeId.INSTANT:
        case TypeId.ZONED_DATE_TIME:
        case TypeId.OFFSET_DATE_TIME:
        case TypeId.LOCAL_DATE_TIME:
        case TypeId.LOCAL_DATE:
        case TypeId.LOCAL_TIME:
            return "Moment";

        case TypeId.STRING:
            return "string";
        }
        return clazz.getName();
    }

    public static EsmPackageMap getPackageMap(File webDir) {
        NpmDir npmDir = NpmDir.closest(webDir);
        EsmModule contextModule = null;

        if (npmDir != null) {
            JsonObject packageJson;
            try {
                packageJson = npmDir.resolve();
            } catch (ParseException | IOException e) {
                throw new LoadException(e.getMessage(), e);
            }
            String pacakgeName = packageJson.getString("name");
            contextModule = new EsmModule(pacakgeName, "src");
        }

        return getPackageMap(contextModule);
    }

    public static EsmPackageMap getPackageMap(EsmModule contextModule) {
        EsmPackageMap packageMap = new EsmPackageMap.Builder()//
                .contextModule(contextModule) //
                .localPriority(EsmModules.PRIORITY_LOCAL)//
                .build();

        packageMap.put("net.bodz.lily.concrete", EsmModules.dba);

        packageMap.put("net.bodz.lily.schema", EsmModules.basic);
        packageMap.put("net.bodz.violet.schema", EsmModules.violet);

        packageMap.put("net.bodz.violet.schema.fab", EsmModules.fab);
        packageMap.put("net.bodz.violet.schema.art.ArtifactModel", EsmModules.fab);

        return packageMap;
    }

}
