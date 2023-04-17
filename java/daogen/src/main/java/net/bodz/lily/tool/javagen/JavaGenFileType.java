package net.bodz.lily.tool.javagen;

import java.io.File;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.codegen.IFileInfo;
import net.bodz.bas.codegen.JavaOrXmlSourceBuilder;
import net.bodz.bas.codegen.MutableFileInfo;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.codegen.UpdateMethod;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.t.catalog.ITableMetadata;

public abstract class JavaGenFileType
        extends JavaOrXmlSourceBuilder<ITableMetadata> {

    static final Logger logger = LoggerFactory.getLogger(JavaGenFileType.class);

    protected static final String JAVA = "java";
    protected static final String XML = "xml";

    public final JavaGenProject project;
    protected final MiscTemplates templates;
    private final ClassPathInfo pathInfo;

    public JavaGenFileType(JavaGenProject project, ClassPathInfo pathInfo) {
        this.project = project;
        this.templates = new MiscTemplates(project);
        this.pathInfo = pathInfo;
    }

    @Override
    protected UpdateMethod getPreferredUpdateMethod(ITableMetadata model) {
        return project.getPreferredUpdateMethod();
    }

    protected abstract String getExtension();

    @Override
    protected IFileInfo getFileInfo(ITableMetadata model) {
        String extension = getExtension();
        File baseDir = pathInfo.getBaseDir();
        if (JAVA.equals(extension))
            baseDir = new File(baseDir, pathInfo.getJavaDir());
        else
            baseDir = new File(baseDir, pathInfo.getResourceDir());
        String localPath = pathInfo.getLocalPath(extension);
        return new MutableFileInfo(baseDir, localPath);
    }

    @Override
    protected QualifiedName getClassName(ITableMetadata model) {
        return pathInfo;
    }

}
