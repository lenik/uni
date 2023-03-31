package net.bodz.uni.jnigen;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import net.bodz.bas.c.string.StringId;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.io.ITreeOut;

public abstract class JNISourceBuilder
        extends SourceBuilder
        implements
            JNIAware {

    protected final Class<?> clazz;
    protected final long lastModifiedTime;

    protected final String simpleName;
    protected final String qualifiedName;
    protected final String underlinedName;
    protected final String underlinedQualifiedName;
    protected final String uppercaseName;
    protected final String uppercaseQualifiedName;
    protected final String uppercasedQualifiedId;

    protected final String namespace;

    protected SourceFormat format = new SourceFormat();
    protected ClassMembers members;
    protected SourceFilesForSingleClass sourceFiles;

    public JNISourceBuilder(Class<?> clazz) {
        this.clazz = clazz;
        String classBasename = clazz.getSimpleName() + ".class";
        URL classResource = clazz.getResource(classBasename);
        if (classResource != null) {
            try {
                URI uri = classResource.toURI();
                Path path = Paths.get(uri);
                FileTime lastModifiedTime = Files.getLastModifiedTime(path);
                this.lastModifiedTime = lastModifiedTime.toMillis();
            } catch (Exception e) {
                throw new UnexpectedException(e);
            }
        } else {
            lastModifiedTime = 0;
        }

        simpleName = clazz.getSimpleName();
        qualifiedName = clazz.getName();
        underlinedName = StringId.UL.breakCamel(simpleName);
        underlinedQualifiedName = StringId.UL.breakQCamel(qualifiedName);
        uppercaseName = underlinedName.toUpperCase();
        uppercaseQualifiedName = underlinedQualifiedName.toUpperCase();
        uppercasedQualifiedId = uppercaseQualifiedName.replace('.', '_');

        String packageName = clazz.getPackage().getName();
        namespace = packageName.replace(".", "::");
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public SourceFormat getFormat() {
        return format;
    }

    public void setFormat(SourceFormat format) {
        if (format == null)
            throw new NullPointerException("format");
        this.format = format;
    }

    public ClassMembers getMembers() {
        return members;
    }

    public void setMembers(ClassMembers members) {
        this.members = members;
    }

    public SourceFilesForSingleClass getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(SourceFilesForSingleClass sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public final File getPreferredFile() {
        return getPreferredFile(sourceFiles);
    }

    public abstract File getPreferredFile(SourceFilesForSingleClass sourceFiles);

    @Override
    public void buildSource(ITreeOut out, File file)
            throws IOException {
        JNISourceWriter jsw = new JNISourceWriter(out);

        String preamble = buildPreamble();
        if (preamble != null)
            jsw.print(preamble);

        buildSource(jsw, file);
    }

    public abstract void buildSource(JNISourceWriter out, File file)
            throws IOException;

}
