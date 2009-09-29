package net.bodz.lapiota.datafiles;

import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
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
import net.bodz.bas.cli.BatchEditCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.ext.CLIPlugin;
import net.bodz.bas.cli.ext._CLIPlugin;
import net.bodz.bas.io.Files;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.lang.script.ScriptException;
import net.bodz.bas.text.util.Interps;
import net.bodz.bas.text.util.PatternProcessor;
import net.bodz.lapiota.nls.CLINLS;
import net.bodz.lapiota.util.RefBinding;

@BootInfo(syslibs = "groovy")
@Doc("An extensible file process program")
@ProgramName("jfor")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class FileProcess extends BatchEditCLI {

    List<Action> actions = new ArrayList<Action>();

    @Option(alias = "a", vnam = "ACTION=PARAM[,...]", doc = "add an action")
    protected void action(Action action) throws CLIException {
        L.debug(CLINLS.getString("FileProcess.action"), action); //$NON-NLS-1$
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
                newExt = ""; //$NON-NLS-1$
            else if (!newExt.isEmpty())
                newExt = "." + newExt; //$NON-NLS-1$
            dst = Files.canoniOf(dst.getParentFile(), getName() + newExt);
        }

    }

    private ScriptScope scope = new ScriptScope();

    protected boolean   edit  = false;

    public FileProcess() {
        plugins.registerCategory("action", Action.class); //$NON-NLS-1$
        plugins.register("g", GroovyScript.class, this); //$NON-NLS-1$
        plugins.register("s", RenamePattern.class, this); //$NON-NLS-1$
        plugins.register("sg", RenameComponents.class, this); //$NON-NLS-1$
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
    protected void _processFile(File file) {
        currentFile = file;
        for (Action action : actions) {
            currentAction = action;
            edit = action.isEditor();
            super._processFile(file);
        }
    }

    @Override
    protected EditResult doEditByIO(InputStream in, OutputStream out) throws Exception {
        return currentAction.run(currentFile, in, out);
    }

    // @Override
    // protected ProtectedShell _getShell() {
    // return new ProtectedShell(!dryRun, L.m);
    // }

    public static void main(String[] args) throws Exception {
        new FileProcess().run(args);
    }

    public interface Action extends CLIPlugin {
        boolean isEditor();

        /**
         * @param file
         *            canonical file
         */
        EditResult run(File file, InputStream in, OutputStream out) throws Exception;
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
                System.out.println(CLINLS.getString("FileProcess.enterScript")); //$NON-NLS-1$
                script = Files.readAll(System.in);
            } else {
                String scriptFile = args[0];
                Charset enc = parameters().getInputEncoding();
                script = Files.readAll(scriptFile, enc);
            }
        }

        @Override
        public boolean isEditor() {
            return isEditor;
        }

        @Override
        public EditResult run(File file, InputStream in, OutputStream out) throws Exception {
            RefBinding binding = new RefBinding();
            binding.bindScriptFields(scope, true);
            binding.setVariable("program", this); //$NON-NLS-1$
            binding.bindScriptFields(FileProcess.this, true);
            binding.setVariable("file", file); //$NON-NLS-1$
            binding.setVariable("in", in); //$NON-NLS-1$

            String enc = parameters().getOutputEncoding().name();
            PrintStream pout = new PrintStream(out, true, enc);
            binding.setVariable("out", pout); //$NON-NLS-1$

            GroovyShell shell = new GroovyShell(binding);
            Object ret = shell.evaluate(script);
            if (ret instanceof String) {
                String code = (String) ret;
                if ("save".equals(code)) //$NON-NLS-1$
                    return EditResult.compareAndSave();
                if ("same".equals(code)) //$NON-NLS-1$
                    return EditResult.saveSame();
                if ("diff".equals(code)) //$NON-NLS-1$
                    return EditResult.saveDiff();
                if ("rm".equals(code)) //$NON-NLS-1$
                    return EditResult.rm();
                if ("ren".equals(code)) //$NON-NLS-1$
                    return EditResult.ren(scope.dst);
                if ("mv".equals(code)) //$NON-NLS-1$
                    return EditResult.mv(scope.dst);
                if ("cp".equals(code)) //$NON-NLS-1$
                    return EditResult.cp(scope.dst);
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
            if (!exp.startsWith("/")) //$NON-NLS-1$
                throw new IllegalArgumentException(
                        CLINLS.getString("FileProcess.notSubsRegexp") + exp); //$NON-NLS-1$
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
                    L.debug("seg[", i, "] = ", segs[i]); //$NON-NLS-1$ //$NON-NLS-2$
            if (segs.length > 3)
                throw new IllegalArgumentException(CLINLS
                        .getString("FileProcess.invalidSubsRegexp") + exp); //$NON-NLS-1$
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
                        throw new IllegalArgumentException(CLINLS
                                .getString("FileProcess.invalidRegexpFlag") + c); //$NON-NLS-1$
                    }
                }
            }
            pattern = Pattern.compile(segs[0], flags);
            replacement = segs.length >= 2 ? segs[1] : ""; //$NON-NLS-1$
        }

        @Override
        public EditResult run(File file, InputStream in, OutputStream out) throws Exception {
            String name = nameOnly ? Files.getName(file) : file.getName();
            Matcher m = pattern.matcher(name);
            if (replaceAll)
                name = m.replaceAll(replacement);
            else
                name = m.replaceFirst(replacement);
            if (nameOnly)
                name += Files.getExtension(file, true);
            File newFile = new File(file.getParentFile(), name);
            return EditResult.ren(newFile);
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
        public void setParameters(Map<String, Object> parameters) throws CLIException,
                ParseException {
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
                punctsPattern = "\\p{Punct}"; //$NON-NLS-1$

            punctsPattern = "[^" + punctsPattern + "]+"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        @Override
        public EditResult run(File file, InputStream in, OutputStream out) throws Exception {
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
            return EditResult.ren(newFile);
        }
    }

}
