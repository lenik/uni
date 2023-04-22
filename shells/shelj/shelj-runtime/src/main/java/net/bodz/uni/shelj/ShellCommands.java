package net.bodz.uni.shelj;

import net.bodz.bas.c.java.util.HashTextMap;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.c.type.IndexedTypes;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.IProgram;
import net.bodz.uni.shelj.c.builtin.*;

public class ShellCommands
        extends HashTextMap<IProgram> {

    private static final long serialVersionUID = 1L;

    public void register(String name, IProgram command) {
        put(name, command);
    }

    public void registerBuiltins(JavaShell shell) {
        register("alias", new Alias(shell));
        register("pwd", new Cwd(shell));
        register("echo", new Echo(shell));
        register("exit", new Exit(shell));
        register("help", new Help(shell));
        register("import", new Import(shell));
        register("set", new Set(shell));
    }

    public void registerIndexed(JavaShell shell) {
        for (Class<? extends IProgram> type : IndexedTypes.list(IProgram.class, false)) {
            AutoLoadProgram proxy = new AutoLoadProgram(shell, type);

            String simpleName = type.getSimpleName();
            String name = Strings.lcfirst(simpleName);
            register(name, proxy);

            ProgramName aName = type.getAnnotation(ProgramName.class);
            if (aName != null)
                register(aName.value(), proxy);
        }
    }

}
