package net.bodz.lily.tool.daogen.config;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.DefaultColumnMetadata;
import net.bodz.bas.t.catalog.ICatalogVisitor;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.lily.tool.daogen.ColumnNaming;
import net.bodz.lily.tool.daogen.util.JavaLang;

public class LangFixupApplier
        implements
            ICatalogVisitor {

    static final Logger logger = LoggerFactory.getLogger(LangFixupApplier.class);

    CatalogConfig config;

    public LangFixupApplier(CatalogConfig config) {
        this.config = config;
    }

    @Override
    public void column(IColumnMetadata column) {
        ColumnNaming cname = config.naming(column);
        String name = cname.fieldName;
        if (JavaLang.isKeyword(name)) {
            DefaultColumnMetadata mutable = (DefaultColumnMetadata) column;
            String renamed = JavaLang.renameKeyword(name);
            mutable.setJavaQName(renamed);
        }
    }

}
