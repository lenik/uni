package net.bodz.lapiota.javashell;

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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.bodz.bas.c.java.util.HashTextMap;
import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.c.java.util.TreeTextMap;
import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.io.ConcatReader;
import net.bodz.bas.io.LineReader;
import net.bodz.bas.jvm.exit.CatchExit;
import net.bodz.bas.jvm.exit.ExitableProgram;
import net.bodz.bas.lang.ControlExit;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.info.Doc;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.meta.util.ValueType;
import net.bodz.bas.vfs.CurrentDirectoryColo;
import net.bodz.lapiota.nls.CLINLS;

@Doc("Lapiota Java Shell")
@ProgramName("jl")
@RcsKeywords(id = "$Id$")
@Version({ 0, 0 })
public class JavaShell
        extends BasicCLI {

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
    Object prompt = "# "; //$NON-NLS-1$

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
    TextMap<Command> commands;

    /**
     * Init environ
     *
     * @option -E =NAM=VAL
     */
    @ValueType(String.class)
    TextMap<Object> env;

    public JavaShell() {
        aliases = new TreeTextMap<String[]>();
        commands = new HashTextMap<Command>();
        commands.put("alias", new Alias()); //$NON-NLS-1$
        commands.put("cwd", new Cwd()); //$NON-NLS-1$
        commands.put("echo", new Echo()); //$NON-NLS-1$
        commands.put("exit", new Exit()); //$NON-NLS-1$
        commands.put("help", new Help()); //$NON-NLS-1$
        commands.put("import", new Import()); //$NON-NLS-1$
        commands.put("set", new Set()); //$NON-NLS-1$
        env = new TreeTextMap<Object>(System.getenv());
        env.put("PROMPT", "# "); //$NON-NLS-1$ //$NON-NLS-2$
        env.put("SHELL", getClass().getName()); //$NON-NLS-1$
    }

    @Override
    protected void _boot()
            throws Exception {
        InputStream scriptIn;
        if (script == null) {
            scriptIn = System.in;
            interactive = true;
        } else {
            scriptIn = new FileInputStream(script);
            interactive = false;
            env.put("SCRIPT_FILE", script.getPath()); //$NON-NLS-1$
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
    protected synchronized void doMain(String[] scriptArgs)
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
            if (line.startsWith("#")) //$NON-NLS-1$
                continue;
            String[] _args = StringArray.split(line);
            assert _args.length != 0;
            int n = _args.length;

            final Command _command;
            try {
                String name = _args[0];
                String[] expansion = expandAliases(name);
                name = expansion[0];
                _command = commands.get(name);
                if (_command == null) {
                    System.err.println(CLINLS.getString("JavaShell.commandIsntDefined") + name); //$NON-NLS-1$
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

            if (n >= 2 && ">>>".equals(_args[n - 2])) { //$NON-NLS-1$
                // heredoc...
            }
            final String[] args = _args;
            try {
                new CatchExit().catchExit(new ExitableProgram<Exception>() {
                    @Override
                    public void execute()
                            throws Exception {
                        _command.main(args);
                    }
                });
            } catch (ControlExit exit) {
                int status = exit.getStatus();
                L.detail(CLINLS.getString("JavaShell.exit"), status); //$NON-NLS-1$
                if (L.showDebug())
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
                throw new IllegalUsageException(CLINLS.getString("JavaShell.aliasNestTooMuch") + alias); //$NON-NLS-1$
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
        new JavaShell().run(args);
    }

    abstract class BuiltIn
            extends _Command {

        public BuiltIn() {
            super(JavaShell.this);
        }

    }

    class Alias
            extends BuiltIn {

        @Override
        public int main(String... args)
                throws Exception {
            if (args.length == 0)
                dump();
            else {
                String name = args[0];
                if (args.length == 1)
                    dump(name);
                else {
                    String[] expansion = ArrayOps.Strings.shift(args);
                    aliases.put(name, expansion);
                }
            }
            return 0;
        }

        void dump() {
            for (String name : aliases.keySet())
                dump(name);
        }

        void dump(String name) {
            String[] expansion = aliases.get(name);
            String s = StringArray.join(" ", expansion); // quotes //$NON-NLS-1$
            System.out.println(CLINLS.getString("JavaShell.alias") + name + " = " + s); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    class Cwd
            extends BuiltIn {

        @Override
        public int main(String... args)
                throws Exception {
            if (args.length == 0)
                System.out.println(CurrentDirectoryColo.getInstance().get());
            else {
                try {
                    File dir = CWD.get(args[0]);
                    if (!dir.isDirectory()) {
                        System.err.println(CLINLS.getString("JavaShell.notDirectory") + dir); //$NON-NLS-1$
                        return 1;
                    }
                    CWD.chdir(dir);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return 2;
                }
            }
            return 0;
        }

    }

    class Echo
            extends BuiltIn {

        @Override
        public int main(String... args)
                throws Exception {
            String line = StringArray.join(" ", args); //$NON-NLS-1$
            System.out.println(line);
            return 0;
        }

    }

    class Exit
            extends BuiltIn {

        @Override
        public int main(String... args)
                throws Exception {
            if (args.length > 0) {
                int status = Integer.parseInt(args[0]);
                exitStatus = status;
            }
            exit = true;
            return 0;
        }

    }

    class Help
            extends BuiltIn {

        @Override
        public int main(String... args)
                throws Exception {
            String[] cmds = commands.keySet().toArray(new String[0]);
            Arrays.sort(cmds);
            for (String cmd : cmds)
                System.out.println(cmd);
            return 0;
        }

    }

    class Import
            extends BuiltIn {

        ClassLoader loader;

        public Import() {
            loader = ClassLoader.getSystemClassLoader();
        }

        @Override
        public int main(String... args)
                throws Exception {
            int i = 0;
            if (args.length == 0)
                throw new IllegalArgumentException(CLINLS.getString("JavaShell.noSpec")); //$NON-NLS-1$
            boolean isStatic = "static".equals(args[0]); //$NON-NLS-1$
            if (isStatic)
                i++;
            for (; i < args.length; i++)
                if (isStatic)
                    doImportStatic(args[i]);
                else
                    doImport(args[i]);
            return 0;
        }

        String name2path(String name) {
            String path = name.replace('.', '/');
            // path = "/" + path;
            return path;
        }

        String path2name(String path) {
            String name = path.replace('/', '.');
            name = name.replace('$', '.');
            while (name.startsWith(".")) //$NON-NLS-1$
                name = name.substring(1);
            return name;
        }

        void doImport(String spec)
                throws Exception {
            if (spec.endsWith(".*")) { //$NON-NLS-1$
                // String path = name2path(spec);
                throw new NotImplementedException();
            }
            int dot = spec.lastIndexOf('.');
            String name = dot == -1 ? spec : spec.substring(dot + 1);
            Class<?> clazz = Class.forName(spec);
            if (!Command.class.isAssignableFrom(clazz))
                throw new IllegalArgumentException(CLINLS.getString("JavaShell.notCommand") + spec); //$NON-NLS-1$
            Command command;
            try {
                Constructor<?> ctor1 = clazz.getConstructor(JavaShell.class);
                command = (Command) ctor1.newInstance(this);
            } catch (NoSuchMethodException e) {
                command = (Command) clazz.newInstance();
            } catch (Exception e) {
                throw e;
            }
            commands.put(name, command);
        }

        void doImportStatic(String spec)
                throws Exception {
            if (spec.endsWith(".*")) { //$NON-NLS-1$
                // String path = name2path(spec);
                throw new NotImplementedException();
            }
            int dot = spec.lastIndexOf('.');
            if (dot == -1)
                throw new IllegalArgumentException(CLINLS.getString("JavaShell.staticImportWithoutMember")); //$NON-NLS-1$
            String className = spec.substring(0, dot);
            String member = spec.substring(dot + 1);
            Class<?> declType = Class.forName(className);
            Field field = declType.getField(member);
            Class<?> clazz = field.getType();
            if (!Command.class.isAssignableFrom(clazz))
                throw new IllegalArgumentException(CLINLS.getString("JavaShell.notCommand") + clazz); //$NON-NLS-1$
            int mod = field.getModifiers();
            if (!Modifier.isStatic(mod))
                throw new IllegalArgumentException(CLINLS.getString("JavaShell.notStatic") + field); //$NON-NLS-1$
            Command command = (Command) field.get(null);
            commands.put(member, command);
        }
    }

    class Set
            extends BuiltIn {

        @Override
        public int main(String... args)
                throws Exception {
            if (args.length == 0)
                dumpEnv();
            else {
                String name = args[0];
                if (args.length == 1)
                    dumpEnv(name);
                else
                    args = ArrayOps.Strings.shift(args);
                env.put(name, StringArray.join(" ", args)); //$NON-NLS-1$
            }
            return 0;
        }

        void dumpEnv() {
            for (String name : env.keySet())
                dumpEnv(name);
        }

        void dumpEnv(String name) {
            Object value = env.get(name);
            System.out.println(name + " = " + value); //$NON-NLS-1$
        }

    }

}