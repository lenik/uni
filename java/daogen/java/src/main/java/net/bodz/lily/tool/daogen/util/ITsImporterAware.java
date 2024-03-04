package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.esm.ITsImporter;
import net.bodz.lily.tool.daogen.dir.web.TsTypeInfoResolver;
import net.bodz.lily.tool.daogen.dir.web.TsTypeResolver;

public interface ITsImporterAware {

    ITsImporter getTsImporter();

    default TsTypeResolver typeResolver() {
        return new TsTypeResolver(getTsImporter());
    }

    default TsTypeInfoResolver typeInfoResolver() {
        return new TsTypeInfoResolver(getTsImporter());
    }

}
