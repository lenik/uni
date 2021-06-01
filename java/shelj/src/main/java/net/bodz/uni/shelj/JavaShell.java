package net.bodz.uni.shelj;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

import net.bodz.bas.c.java.io.ConcatReader;
import net.bodz.bas.c.java.io.LineReader;
import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.c.java.util.TreeTextMap;
import net.bodz.bas.c.string.StringQuoted;
import net.bodz.bas.err.IllegalUsageException;
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

    boolean exitRequest;
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

    public final ShellAliases aliases = new ShellAliases();
    public final ShellCommands commands = new ShellCommands();

    /**
     * Init environ
     *
     * @option -E =NAM=VAL
     */
    public TextMap<String> env;

    public PrintStream out = System.out;

    public JavaShell() {
        commands.registerBuiltins(this);

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
            if (exitRequest)
                System.exit(exitStatus);
            if (interactive) {
                out.print(prompt);
                out.flush();
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
                String[] expansion = aliases.expandAliases(name);
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

    public static void main(String[] args)
            throws Exception {
        new JavaShell().execute(args);
    }

    public void requestExit(int exitStatus) {
        this.exitStatus = exitStatus;
        this.exitRequest = true;
    }

}
