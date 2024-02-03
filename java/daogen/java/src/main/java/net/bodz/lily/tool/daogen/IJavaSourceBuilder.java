package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.JavaSourceWriter;
import net.bodz.bas.codegen.QualifiedName;
import net.bodz.bas.io.BCharOut;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.impl.TreeOutImpl;

public interface IJavaSourceBuilder<model_t> {

    QualifiedName getClassName(model_t model);

    default void buildClass(ITreeOut out, model_t model) {
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

    void buildClassBody(JavaSourceWriter out, model_t model);

}
