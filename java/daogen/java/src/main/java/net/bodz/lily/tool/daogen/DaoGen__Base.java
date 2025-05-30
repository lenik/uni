package net.bodz.lily.tool.daogen;

import java.nio.file.Path;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.codegen.IClassPathInfo;
import net.bodz.bas.codegen.IFileInfo;
import net.bodz.bas.codegen.MutableFileInfo;
import net.bodz.bas.codegen.SourceBuilder;
import net.bodz.bas.codegen.UpdateMethod;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.ITableMetadata;

public abstract class DaoGen__Base
        extends SourceBuilder<ITableMetadata> {

    static final Logger logger = LoggerFactory.getLogger(DaoGen__Base.class);

    protected static final String JAVA = "java";
    protected static final String XML = "xml";
    protected static final String TS = "ts";
    protected static final String VUE = "vue";

    public final DaoGenProject project;
    protected final MiscTemplates templates;
    protected final IClassPathInfo pathInfo;

    public DaoGen__Base(DaoGenProject project, ClassPathInfo pathInfo) {
        this.project = project;
        this.templates = new MiscTemplates(project);
        this.pathInfo = pathInfo;
    }

    @Override
    protected UpdateMethod getPreferredUpdateMethod(ITableMetadata model) {
        return project.getPreferredUpdateMethod();
    }

    protected abstract String getExtension();

    protected boolean isTestScoped() {
        return false;
    }

    @Override
    protected IFileInfo getFileInfo(ITableMetadata model) {
        String extension = getExtension();
        boolean test = isTestScoped();
        Path parent = pathInfo.getPreferredDir(extension, test);
        String localPath = pathInfo.getQName().getLocalPath(extension);
        return new MutableFileInfo(parent, localPath);
    }

}
