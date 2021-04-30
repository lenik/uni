package net.bodz.uni.shelj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.bodz.bas.c.java.io.ConcatReader;
import net.bodz.bas.c.java.io.LineReader;
import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.c.java.util.HashTextMap;
import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.c.java.util.TreeTextMap;
import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.c.string.StringQuoted;
import net.bodz.bas.ctx.sys.UserDirVars;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.err.control.ControlExit;
import net.bodz.bas.fn.IExecutableX;
import net.bodz.bas.jvm.exit.CatchExit;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.IProgram;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * Lapiota Java Shell
 */
@ProgramName("jsh")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class JavaShell
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(JavaShell.class);

    /**
     * Script file to run
     *
     * @option -c =SCRIPT
     */
    File script;
    String[] scriptArgs;

    /**
     * Script file encoding
     *
     * @option =CHARSET
     */
    Charset scriptEncoding;

    boolean keepRun;
    boolean interactive;
    LineReader lineInput;
    boolean exit;
    int exitStatus;
    Object prompt = "# ";

    /**
     * Run script and don't quit
     *
     * @option -k =SCRIPT
     */
    void scriptKeep(File script) {
        this.script = script;
        keepRun = true;
    }

    TextMap<String[]> aliases;
    TextMap<IProgram> commands;

    /**
     * Init environ
     *
     * @option -E =NAM=VAL
     */
    TextMap<String> env;

    public JavaShell() {
        aliases = new TreeTextMap<>();
        commands = new HashTextMap<>();
        commands.put("alias", new Alias());
        commands.put("cwd", new Cwd());
        commands.put("echo", new Echo());
        commands.put("exit", new Exit());
        commands.put("help", new Help());
        commands.put("import", new Import());
        commands.put("set", new Set());
        env = new TreeTextMap<String>(System.getenv());
        env.put("PROMPT", "# ");
        env.put("SHELL", getClass().getName());
    }

    @Override
    protected void reconfigure()
            throws Exception {
        InputStream scriptIn;
        if (script == null) {
            scriptIn = System.in;
            interactive = true;
        } else {
            scriptIn = new FileInputStream(script);
            interactive = false;
            env.put("SCRIPT_FILE", script.getPath());
        }
        if (scriptEncoding == null)
            scriptEncoding = Charset.defaultCharset();

        Reader reader = new InputStreamReader(scriptIn, scriptEncoding);
        if (script != null && keepRun) {
            final Reader sysReader = new InputStreamReader(System.in, scriptEncoding);
            Queue<Reader> trigQueue = new LinkedList<Reader>() {

                private static final long serialVersionUID = 1645685150623267559L;

                @Override
                public Reader remove() {
                    Reader removed = super.remove();
                    if (!isEmpty()) {
                        Reader head = peek();
                        interactive = head == sysReader;
                    }
                    return removed;
                }

            };
            trigQueue.add(reader);
            trigQueue.add(sysReader);
            reader = new ConcatReader(trigQueue);
        }
        lineInput = new LineReader(reader);
    }

    @Override
    protected synchronized void mainImpl(String... scriptArgs)
            throws IOException {
        this.scriptArgs = scriptArgs;

        while (true) {
            if (exit)
                System.exit(exitStatus);
            if (interactive) {
                System.out.print(prompt);
                System.out.flush();
            }
            String line = lineInput.readLine();
            if (line == null)
                break;
            line = line.trim();
            if (line.isEmpty())
                continue;
            if (line.startsWith("#"))
                continue;
            String[] _args = StringQuoted.split(line);
            assert _args.length != 0;
            int n = _args.length;

            final IProgram _command;
            try {
                String name = _args[0];
                String[] expansion = expandAliases(name);
                name = expansion[0];
                _command = commands.get(name);
                if (_command == null) {
                    System.err.println(nls.tr("Command isn\'t defined: ") + name);
                    continue;
                }

                n = _args.length - 1 + expansion.length - 1;
                String[] join = new String[n];
                System.arraycopy(expansion, 1, join, 0, expansion.length - 1);
                System.arraycopy(_args, 1, join, expansion.length - 1, _args.length - 1);
                _args = join;
            } catch (IllegalUsageException e) {
                System.err.println(e.getMessage());
                continue;
            }

            if (n >= 2 && ">>>".equals(_args[n - 2])) {
                // heredoc...
            }
            final String[] args = _args;
            try {
                new CatchExit().catchExit(new IExecutableX<Exception>() {
                    @Override
                    public void execute()
                            throws Exception {
                        _command.execute(args);
                    }
                });
            } catch (ControlExit exit) {
                int status = exit.getStatus();
                logger.info(nls.tr("exit "), status);
                if (logger.isDebugEnabled())
                    exit.printStackTrace();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    static int MAX_NEST = 32;

    String[] expandAliases(String alias) {
        List<String> rev = null;
        String[] exp;
        int nest = 0;
        while ((exp = aliases.get(alias)) != null) {
            nest++;
            if (nest > MAX_NEST)
                throw new IllegalUsageException(nls.tr("alias nest too much: ") + alias);
            assert exp.length != 0;
            alias = exp[0];
            if (exp.length == 1)
                continue;
            if (rev == null)
                rev = new ArrayList<String>();
            for (int i = exp.length - 1; i >= 1; i--)
                rev.add(exp[i]);
        }
        if (rev == null)
            return new String[] { alias };
        rev.add(alias);
        // Collections.reverse(rev);
        int n = rev.size();
        exp = new String[n];
        for (int i = 0; i < n; i++)
            exp[i] = rev.get(n - i - 1);
        return exp;
    }

    public static void main(String[] args)
            throws Exception {
        new JavaShell().execute(args);
    }

    abstract class SimpleCommand
            extends ShellCommand {

        public SimpleCommand() {
            super(JavaShell.this);
        }

    }

    class Alias
            extends SimpleCommand {

        @Override
        protected void mainImpl(String... args)
                throws Exception {
            if (args.length == 0)
                dump();
            else {
                String name = args[0];
                if (args.length == 1)
                    dump(name);
                else {
                    String[] expansion = Arrays.shift(args).array;
                    aliases.put(name, expansion);
                }
            }
        }

        void dump() {
            for (String name : aliases.keySet())
                dump(name);
        }

        void dump(String name) {
            String[] expansion = aliases.get(name);
            String s = StringArray.join(" ", expansion); // quotes
            System.out.println(nls.tr("alias ") + name + " = " + s);
        }

    }

    class Cwd
            extends SimpleCommand {

        @Override
        protected void mainImpl(String... args)
                throws Exception {
            if (args.length == 0)
                System.out.println(UserDirVars.getInstance().get());
            else {
                File dir = UserDirVars.getInstance().join(args[0]);
                if (!dir.isDirectory())
                    throw new IllegalArgumentException(nls.tr("Not a directory: ") + dir);
                UserDirVars.getInstance().set(dir);
            }
        }

    }

    class Echo
            extends SimpleCommand {

        @Override
        protected void mainImpl(String... args)
                throws Exception {
            String line = StringArray.join(" ", args);
            System.out.println(line);
        }

    }

    class Exit
            extends SimpleCommand {

        @Override
        protected void mainImpl(String... args)
                throws Exception {
            if (args.length > 0) {
                int status = Integer.parseInt(args[0]);
                exitStatus = status;
            }
            exit = true;
        }

    }

    class Help
            extends SimpleCommand {

        @Override
        protected void mainImpl(String... args)
                throws Exception {
            String[] cmds = commands.keySet().toArray(new String[0]);
            Arrays.sort(cmds);
            for (String cmd : cmds)
                System.out.println(cmd);
        }

    }

    class Import
            extends SimpleCommand {

        ClassLoader loader;

        public Import() {
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
            commands.put(name, command);
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
            commands.put(member, command);
        }
    }

    class Set
            extends SimpleCommand {

        @Override
        protected void mainImpl(String... args)
                throws Exception {
            if (args.length == 0)
                dumpEnv();
            else {
                String name = args[0];
                if (args.length == 1)
                    dumpEnv(name);
                else
                    args = Arrays.shift(args).array;
                env.put(name, StringArray.join(" ", args));
            }
        }

        void dumpEnv() {
            for (String name : env.keySet())
                dumpEnv(name);
        }

        void dumpEnv(String name) {
            Object value = env.get(name);
            System.out.println(name + " = " + value);
        }

    }

}
