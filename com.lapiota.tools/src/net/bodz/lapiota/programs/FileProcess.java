package net.bodz.lapiota.programs;

import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.ext.CLIPlugin;
import net.bodz.bas.cli.ext._CLIPlugin;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.script.ScriptException;
import net.bodz.lapiota.ant.tasks.ProgramName;
import net.bodz.lapiota.util.BatchProcessCLI;
import net.bodz.lapiota.util.RefBinding;

@Doc("An extensible file process program")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
@RunInfo(lib = "groovy")
@ProgramName("jfor")
public class FileProcess extends BatchProcessCLI {

    private List<Action> actions = new ArrayList<Action>();

    @Option(alias = "a", vnam = "ACTION=PARAM[,...]", doc = "add an action")
    protected void action(Action action) throws CLIException {
        L.x.P("action: ", action);
        actions.add(action);
    }

    public FileProcess() {
        plugins.registerPluginType("action", Action.class);
        plugins.register("groovy", GroovyScript.class, this);
        plugins.register("ren", RenamePattern.class, this);
        plugins.register("reng", RenameComponents.class, this);
    }

    @Override
    protected InputStream _getDefaultIn() {
        return null;
    }

    public static void main(String[] args) throws Throwable {
        new FileProcess().climain(args);
    }

    public interface Action extends CLIPlugin {
        boolean isEditor();

        ProcessResult run(File file, InputStream in, OutputStream out)
                throws Throwable;
    }

    abstract class _Action extends _CLIPlugin implements Action {
        @Override
        public boolean isEditor() {
            return false;
        }
    }

    class ScriptScope {
        public Object dst;
    }

    class GroovyScript extends _Action {
        private boolean isEditor;
        private String  script;

        public GroovyScript(String[] args) throws IOException, ScriptException {
            if (args.length == 0) {
                System.out.println("enter the processing groovy script: ");
                script = Files.readAll(System.in);
            } else {
                String scriptFile = args[0];
                script = Files.readAll(scriptFile, inputEncoding);
            }
        }

        @Override
        public boolean isEditor() {
            return isEditor;
        }

        @Override
        public ProcessResult run(File file, InputStream in, OutputStream out)
                throws Throwable {
            ScriptScope scope = new ScriptScope();
            RefBinding binding = new RefBinding();
            binding.bindScriptFields(scope, true);
            binding.setVariable("program", this);
            binding.bindScriptFields(FileProcess.this, true);
            binding.setVariable("file", file);
            binding.setVariable("in", in);
            PrintStream pout = new PrintStream(out, true, outputEncoding.name());
            binding.setVariable("out", pout);

            GroovyShell shell = new GroovyShell(binding);
            Object ret = shell.evaluate(script);
            if (ret instanceof String) {
                String code = (String) ret;
                if ("save".equals(code))
                    return ProcessResult.compareAndSave();
                if ("same".equals(code))
                    return ProcessResult.saveSame();
                if ("diff".equals(code))
                    return ProcessResult.saveDiff();
                if ("rm".equals(code))
                    return ProcessResult.rm();
                if ("ren".equals(code))
                    return ProcessResult.ren(scope.dst);
                if ("mv".equals(code))
                    return ProcessResult.mv(scope.dst);
                if ("cp".equals(code))
                    return ProcessResult.cp(scope.dst);
            }
            return null;
        }
    }

    class RenamePattern extends _Action {

        public RenamePattern() {
            super();
        }

        @Override
        public ProcessResult run(File file, InputStream in, OutputStream out)
                throws Throwable {
            return null;
        }
    }

    class RenameComponents extends _Action {

        @Override
        public ProcessResult run(File file, InputStream in, OutputStream out)
                throws Throwable {
            return null;
        }

    }

}
