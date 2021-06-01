package net.bodz.uni.shelj;

import java.lang.reflect.Constructor;

import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.LoadException;
import net.bodz.bas.program.IProgram;
import net.bodz.bas.program.model.IOptionGroup;

public class AutoLoadProgram
        implements
            IProgram {

    JavaShell shell;

    Class<? extends IProgram> programClass;
    IProgram instance;

    public AutoLoadProgram(JavaShell shell, Class<? extends IProgram> programClass) {
        if (shell == null)
            throw new NullPointerException("shell");
        if (programClass == null)
            throw new NullPointerException("programClass");
        this.shell = shell;
        this.programClass = programClass;
    }

    public synchronized IProgram getInstance() {
        if (instance == null)
            try {
                instance = loadInstance();
            } catch (ReflectiveOperationException e) {
                throw new LoadException(e.getMessage(), e);
            }
        return instance;
    }

    IProgram loadInstance()
            throws ReflectiveOperationException {
        for (Constructor<?> ctor : programClass.getDeclaredConstructors()) {
            Class<?>[] args = ctor.getParameterTypes();
            if (args.length == 1 && args[0] == JavaShell.class) {
                return (IProgram) ctor.newInstance(shell);
            }
            if (args.length == 0) {
                return (IProgram) ctor.newInstance();
            }
        }
        throw new IllegalUsageException("No suitable constructor within: " + programClass);
    }

    @Override
    public IOptionGroup getOptionModel() {
        return getInstance().getOptionModel();
    }

    @Override
    public void execute(String... args)
            throws Exception {
        getInstance().execute(args);
    }

}
