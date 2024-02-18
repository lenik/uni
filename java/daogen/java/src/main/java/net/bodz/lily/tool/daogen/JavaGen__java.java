package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.io.BCharOut;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.impl.TreeOutImpl;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;

public abstract class JavaGen__java
        extends JavaGenFileType {

    public JavaGen__java(JavaGenProject project, ClassPathInfo pathInfo) {
        super(project, pathInfo);
    }

    @Override
    protected String getExtension() {
        return JAVA;
    }

    protected QualifiedName getClassName(ITableMetadata model) {
        return pathInfo.getQName();
    }

    @Override
    public void build(ITreeOut out, ITableMetadata model) {
        buildClass(out, model);
    }

    protected final void buildClass(ITreeOut out, ITableMetadata model) {
        QualifiedName name = getClassName(model);
        out.println("package " + name.packageName + ";");
        out.println();

        BCharOut body = new BCharOut();
        JavaSourceWriter buffer = new JavaSourceWriter(name.packageName, TreeOutImpl.from(body));
        buildClassBody(buffer, model);

        int n = buffer.im.dump(out);
        if (n != 0)
            out.println();

        out.print(body.toString());
        out.flush();
    }

    protected abstract void buildClassBody(JavaSourceWriter out, ITableMetadata model);

}
