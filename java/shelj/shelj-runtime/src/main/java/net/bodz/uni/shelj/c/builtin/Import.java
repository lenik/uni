package net.bodz.uni.shelj.c.builtin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.program.IProgram;
import net.bodz.uni.shelj.JavaShell;
import net.bodz.uni.shelj.ShellCommand;

public class Import
        extends ShellCommand {

    ClassLoader loader;

    public Import(JavaShell shell) {
        super(shell);
        loader = ClassLoader.getSystemClassLoader();
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        int i = 0;
        if (args.length == 0)
            throw new IllegalArgumentException(nls.tr("no spec"));
        boolean isStatic = "static".equals(args[0]);
        if (isStatic)
            i++;
        for (; i < args.length; i++)
            if (isStatic)
                doImportStatic(args[i]);
            else
                doImport(args[i]);
    }

    String name2path(String name) {
        String path = name.replace('.', '/');
        // path = "/" + path;
        return path;
    }

    String path2name(String path) {
        String name = path.replace('/', '.');
        name = name.replace('$', '.');
        while (name.startsWith("."))
            name = name.substring(1);
        return name;
    }

    void doImport(String spec)
            throws Exception {
        if (spec.endsWith(".*")) {
            // String path = name2path(spec);
            throw new NotImplementedException();
        }
        int dot = spec.lastIndexOf('.');
        String name = dot == -1 ? spec : spec.substring(dot + 1);
        Class<?> clazz = Class.forName(spec);
        if (!IProgram.class.isAssignableFrom(clazz))
            throw new IllegalArgumentException(nls.tr("not a program: ") + spec);
        IProgram command;
        try {
            Constructor<?> ctor1 = clazz.getConstructor(JavaShell.class);
            command = (IProgram) ctor1.newInstance(this);
        } catch (NoSuchMethodException e) {
            command = (IProgram) clazz.newInstance();
        } catch (Exception e) {
            throw e;
        }
        shell.commands.put(name, command);
    }

    void doImportStatic(String spec)
            throws Exception {
        if (spec.endsWith(".*")) {
            // String path = name2path(spec);
            throw new NotImplementedException();
        }
        int dot = spec.lastIndexOf('.');
        if (dot == -1)
            throw new IllegalArgumentException(nls.tr("static import without member name"));
        String className = spec.substring(0, dot);
        String member = spec.substring(dot + 1);
        Class<?> declType = Class.forName(className);
        Field field = declType.getField(member);
        Class<?> clazz = field.getType();
        if (!IProgram.class.isAssignableFrom(clazz))
            throw new IllegalArgumentException(nls.tr("not a command: ") + clazz);
        int mod = field.getModifiers();
        if (!Modifier.isStatic(mod))
            throw new IllegalArgumentException(nls.tr("not static: ") + field);
        IProgram command = (IProgram) field.get(null);
        shell.commands.put(member, command);
    }

}
