package net.bodz.lily.tool.daogen.dir.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
        aliases.put("byte", EsmModules.core.type._byte);
        aliases.put("short", EsmModules.core.type._short);
        aliases.put("integer", EsmModules.core.type.integer);
        aliases.put("long", EsmModules.core.type._long);
        aliases.put("float", EsmModules.core.type._float);
        aliases.put("double", EsmModules.core.type._double);
        aliases.put("char", EsmModules.core.type._char);
        aliases.put("Moment", EsmModules.moment.Moment);
    }

    public static EsmName getAlias1(String type) {
        if (type == null)
            throw new NullPointerException("type");
        if (type.isEmpty())
            throw new IllegalArgumentException("empty string");
        return aliases.get(type);
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
