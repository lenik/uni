package net.bodz.lily.tool.daogen.config;

import java.util.HashSet;
import java.util.Set;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.DefaultColumnMetadata;
import net.bodz.bas.t.catalog.ICatalogVisitor;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.lily.tool.daogen.ColumnName;

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
        ColumnName cname = config.columnName(column);
        String name = cname.field;
        if (javaKeywords.contains(name)) {
            name += "_";
            DefaultColumnMetadata mutable = (DefaultColumnMetadata) column;
            mutable.setJavaName(name);
        }
    }

    static final Set<String> javaKeywords = new HashSet<>();

    static {
        String[] keywords = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
                "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
                "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new",
                "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
                "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", };
        for (String kw : keywords)
            javaKeywords.add(kw);
    }

}
