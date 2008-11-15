package net.bodz.lapiota.datafiles;

import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.ext.CLIPlugin;
import net.bodz.bas.cli.ext._CLIPlugin;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.lang.script.ScriptException;
import net.bodz.bas.text.interp.Interps;
import net.bodz.bas.text.interp.PatternProcessor;
import net.bodz.lapiota.util.RefBinding;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@BootInfo(syslibs = "groovy")
@Doc("An extensible file process program")
@ProgramName("jfor")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class FileProcess extends BatchProcessCLI {

    List<Action> actions = new ArrayList<Action>();

    @Option(alias = "a", vnam = "ACTION=PARAM[,...]", doc = "add an action")
    protected void action(Action action) throws CLIException {
        L.x.P("action: ", action);
        actions.add(action);
    }

    class ScriptScope {
        public File getDst0() {
            return getOutputFile(currentFile);
        }

        public File dst;

        public String getDir() {
            return dst.getParent();
        }

        public void setDir(String newDir) {
            dst = Files.canoniOf(newDir, dst.getName());
        }

        public String getBase() {
            return dst.getName();
        }

        public void setBase(String newBase) {
            dst = Files.canoniOf(dst.getParentFile(), newBase);
        }

        public String getName() {
            String base = getBase();
            int dot = base.lastIndexOf('.');
            if (dot == -1)
                return base;
            return base.substring(0, dot);
        }

        public void setName(String newName) {
            String ext = Files.getExtension(dst, true);
            dst = Files.canoniOf(dst.getParentFile(), newName + ext);
        }

        public String getExt() {
            return Files.getExtension(dst);
        }

        public void setExt(String newExt) {
            if (newExt == null)
                newExt = "";
            else if (!newExt.isEmpty())
                newExt = "." + newExt;
            dst = Files.canoniOf(dst.getParentFile(), getName() + newExt);
        }

    }

    private ScriptScope scope = new ScriptScope();

    protected boolean   edit  = false;

    public FileProcess() {
        plugins.registerCategory("action", Action.class);
        plugins.register("g", GroovyScript.class, this);
        plugins.register("s", RenamePattern.class, this);
        plugins.register("sg", RenameComponents.class, this);
    }

    @Override
    protected File _getEditTmp(File file) throws IOException {
        if (edit)
            return super._getEditTmp(file);
        return null;
    }

    /** canonical file */
    private File   currentFile;

    private Action currentAction;

    @Override
    protected void _doFile(File file) {
        currentFile = file;
        for (Action action : actions) {
            currentAction = action;
            edit = action.isEditor();
            super._doFile(file);
        }
    }

    @Override
    protected ProcessResult doFileEdit(InputStream in, OutputStream out)
            throws Throwable {
        return currentAction.run(currentFile, in, out);
    }

    // @Override
    // protected ProtectedShell _getShell() {
    // return new ProtectedShell(!dryRun, L.m);
    // }

    public static void main(String[] args) throws Throwable {
        new FileProcess().run(args);
    }

    public interface Action extends CLIPlugin {
        boolean isEditor();

        /**
         * @param file
         *            canonical file
         */
        ProcessResult run(File file, InputStream in, OutputStream out)
                throws Throwable;
    }

    abstract class _Action extends _CLIPlugin implements Action {
        @Override
        public boolean isEditor() {
            return false;
        }
    }

    @Doc("evaluate groovy script")
    class GroovyScript extends _Action {

        @Option(doc = "editing mode")
        private boolean isEditor;

        private String  script;

        public GroovyScript() {
        }

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

    @Doc("rename by regexp: //pattern/replacement/flags")
    class RenamePattern extends _Action {

        @Option(doc = "replace all occurrences")
        private boolean replaceAll;

        @Option(doc = "matching pattern with name only (without extension)")
        private boolean nameOnly;

        @Option(doc = "java regexp pattern")
        private Pattern pattern;

        @Option(doc = "java regexp replacement string, $N is supported")
        private String  replacement;

        @Option(doc = "combination of i, x, s, m")
        private String  flags;

        public RenamePattern() {
        }

        /** [/]/PATTERN/REPLACEMENT[/FLAGS] */
        public RenamePattern(String exp) {
            if (!exp.startsWith("/"))
                throw new IllegalArgumentException("not a subs-regexp: " + exp);
            char sep = exp.charAt(0);
            exp = exp.substring(1);
            if (exp.charAt(0) == sep) {
                replaceAll = true;
                exp = exp.substring(1);
            }
            String sepEscaped = Pattern.quote(String.valueOf(sep)); // ..
            // Pattern segp = ("(\\.|[^" + sepEscaped + "])*");
            // Matcher m = segp.matcher(exp);
            String[] segs = exp.split(sepEscaped);
            if (L.showDebug())
                for (int i = 0; i < segs.length; i++)
                    L.x.P("seg[", i, "] = ", segs[i]);
            if (segs.length > 3)
                throw new IllegalArgumentException(
                        "invalid subs-regexp format: " + exp);
            int flags = 0;
            String _flags = segs.length == 3 ? segs[2] : this.flags;
            if (_flags != null) {
                for (char c : _flags.toCharArray()) {
                    switch (c) {
                    case 'i':
                        flags |= Pattern.CASE_INSENSITIVE;
                        break;
                    case 'x':
                        flags |= Pattern.COMMENTS;
                        break;
                    case 's':
                        flags |= Pattern.DOTALL;
                        break;
                    case 'm':
                        flags |= Pattern.MULTILINE;
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "invalid regexp flag: " + c);
                    }
                }
            }
            pattern = Pattern.compile(segs[0], flags);
            replacement = segs.length >= 2 ? segs[1] : "";
        }

        @Override
        public ProcessResult run(File file, InputStream in, OutputStream out)
                throws Throwable {
            String name = nameOnly ? Files.getName(file) : file.getName();
            Matcher m = pattern.matcher(name);
            if (replaceAll)
                name = m.replaceAll(replacement);
            else
                name = m.replaceFirst(replacement);
            if (nameOnly)
                name += Files.getExtension(file, true);
            File newFile = new File(file.getParentFile(), name);
            return ProcessResult.ren(newFile);
        }
    }

    @Doc("rename by reorganize filename components")
    class RenameComponents extends _Action {

        @Option(doc = "replace file extension also")
        private boolean withExt;

        @Option(doc = "replace dot(.) in the filename with space")
        private boolean dotSpace;

        @Option(doc = "default value of non-existing component")
        private String  nonexist;

        @Option(doc = "punctuation characters, used as separator between components")
        private String  punctsPattern;

        @Option(doc = "punctuation pattern, used as separator between components")
        private String  puncts;

        private String  replacement;

        public RenameComponents() {
        }

        public RenameComponents(String replacement) {
            this.replacement = replacement;
        }

        @Override
        public void setParameters(Map<String, Object> parameters)
                throws CLIException, ParseException {
            super.setParameters(parameters);
            if (puncts != null) {
                int n = puncts.length();
                StringBuffer buf = new StringBuffer(n * 10);
                for (int i = 0; i < n; i++) {
                    String c = String.valueOf(puncts.charAt(i));
                    String cp = Pattern.quote(c);
                    buf.append(cp);
                }
                punctsPattern = buf.toString();
            }
            if (punctsPattern == null)
                punctsPattern = "\\p{Punct}";

            punctsPattern = "[^" + punctsPattern + "]+";
        }

        @Override
        public ProcessResult run(File file, InputStream in, OutputStream out)
                throws Throwable {
            String _name = Files.getName(file);
            String _ext = Files.getExtension(file, true);
            if (dotSpace)
                _name = _name.replace('.', ' ');
            String name = withExt ? _name + _ext : _name;
            name = name.trim();

            final List<String> components = new ArrayList<String>();
            Pattern findComponents = Pattern.compile(punctsPattern);
            new PatternProcessor(findComponents) {
                @Override
                protected void matched(String part) {
                    part = part.trim();
                    if (part.isEmpty())
                        return;
                    components.add(part);
                }
            }.process(name);

            try {
                name = Interps.dereference(replacement, //
                        1, components, nonexist);
            } catch (IndexOutOfBoundsException e) {
                assert nonexist == null;
                return null;
            }

            if (!withExt)
                name += Files.getExtension(file, true);
            File newFile = new File(file.getParentFile(), name);
            return ProcessResult.ren(newFile);
        }
    }

}
