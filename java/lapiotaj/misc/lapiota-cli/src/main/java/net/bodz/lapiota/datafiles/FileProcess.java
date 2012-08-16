package net.bodz.lapiota.datafiles;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.java.util.regex.PatternProcessor;
import net.bodz.bas.cli.plugin.AbstractCLIPlugin;
import net.bodz.bas.cli.plugin.CLIPlugin;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIException;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.lapiota.util.RefBinding;

/**
 * An extensible file process program
 */
@BootInfo(syslibs = "groovy")
@ProgramName("jfor")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class FileProcess
        extends BatchEditCLI {

    List<Action> actions = new ArrayList<Action>();

    /**
     * add an action
     *
     * @option -a =ACTION=PARAM[,...]
     */
    protected void action(Action action)
            throws CLIException {
        L.debug(CLINLS.getString("FileProcess.action"), action);
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
            String ext = FilePath.getExtension(dst, true);
            dst = Files.canoniOf(dst.getParentFile(), newName + ext);
        }

        public String getExt() {
            return FilePath.getExtension(dst);
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

    protected boolean edit = false;

    public FileProcess() {
        plugins.registerCategory("action", Action.class);
        plugins.register("g", GroovyScript.class, this);
        plugins.register("s", RenamePattern.class, this);
        plugins.register("sg", RenameComponents.class, this);
    }

    @Override
    protected File _getEditTmp(File file)
            throws IOException {
        if (edit)
            return super._getEditTmp(file);
        return null;
    }

    /** canonical file */
    private File currentFile;

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
    protected EditResult doEditByIO(InputStream in, OutputStream out)
            throws Exception {
        return currentAction.run(currentFile, in, out);
    }

    // @Override
    // protected ProtectedShell _getShell() {
    // return new ProtectedShell(!dryRun, L.m);
    // }

    public static void main(String[] args)
            throws Exception {
        new FileProcess().execute(args);
    }

    public interface Action
            extends CLIPlugin {
        boolean isEditor();

        /**
         * @param file
         *            canonical file
         */
        EditResult run(File file, InputStream in, OutputStream out)
                throws Exception;
    }

    abstract class _Action
            extends AbstractCLIPlugin
            implements Action {
        @Override
        public boolean isEditor() {
            return false;
        }
    }

    /**
     * Evaluate groovy script
     */
    class GroovyScript
            extends _Action {

        /**
         * editing mode
         *
         * @option
         */
        private boolean isEditor;

        private String script;

        public GroovyScript() {
        }

        public GroovyScript(String[] args)
                throws IOException, ScriptException {
            if (args.length == 0) {
                System.out.println(CLINLS.getString("FileProcess.enterScript"));
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
        public EditResult run(File file, InputStream in, OutputStream out)
                throws Exception {
            RefBinding binding = new RefBinding();
            binding.bindScriptFields(scope, true);
            binding.setVariable("program", this);
            binding.bindScriptFields(FileProcess.this, true);
            binding.setVariable("file", file);
            binding.setVariable("in", in);

            String enc = parameters().getOutputEncoding().name();
            PrintStream pout = new PrintStream(out, true, enc);
            binding.setVariable("out", pout);

            GroovyShell shell = new GroovyShell(binding);
            Object ret = shell.evaluate(script);
            if (ret instanceof String) {
                String code = (String) ret;
                if ("save".equals(code))
                    return EditResult.compareAndSave();
                if ("same".equals(code))
                    return EditResult.saveSame();
                if ("diff".equals(code))
                    return EditResult.saveDiff();
                if ("rm".equals(code))
                    return EditResult.rm();
                if ("ren".equals(code))
                    return EditResult.ren(scope.dst);
                if ("mv".equals(code))
                    return EditResult.mv(scope.dst);
                if ("cp".equals(code))
                    return EditResult.cp(scope.dst);
            }
            return null;
        }
    }

    /**
     * Rename by regexp: //pattern/replacement/flags
     */
    class RenamePattern
            extends _Action {

        /**
         * replace all occurrences
         *
         * @option
         */
        private boolean replaceAll;

        /**
         * matching pattern with name only (without extension)
         *
         * @option
         */
        private boolean nameOnly;

        /**
         * java regexp pattern
         *
         * @option
         */
        private Pattern pattern;

        /**
         * java regexp replacement string, $N is supported
         *
         * @option
         */
        private String replacement;

        /**
         * combination of i, x, s, m
         *
         * @option
         */
        private String flags;

        public RenamePattern() {
        }

        /** [/]/PATTERN/REPLACEMENT[/FLAGS] */
        public RenamePattern(String exp) {
            if (!exp.startsWith("/"))
                throw new IllegalArgumentException(CLINLS.getString("FileProcess.notSubsRegexp") + exp);
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
            if (L.isDebugEnabled())
                for (int i = 0; i < segs.length; i++)
                    L.debug("seg[", i, "] = ", segs[i]);
            if (segs.length > 3)
                throw new IllegalArgumentException(CLINLS.getString("FileProcess.invalidSubsRegexp") + exp);
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
                        throw new IllegalArgumentException(CLINLS.getString("FileProcess.invalidRegexpFlag") + c);
                    }
                }
            }
            pattern = Pattern.compile(segs[0], flags);
            replacement = segs.length >= 2 ? segs[1] : "";
        }

        @Override
        public EditResult run(File file, InputStream in, OutputStream out)
                throws Exception {
            String name = file.getName();
            if (nameOnly)
                name = FilePath.stripExtension(name);
            Matcher m = pattern.matcher(name);
            if (replaceAll)
                name = m.replaceAll(replacement);
            else
                name = m.replaceFirst(replacement);
            if (nameOnly)
                name += FilePath.getExtension(file, true);
            File newFile = new File(file.getParentFile(), name);
            return EditResult.ren(newFile);
        }
    }

    /**
     * rename by reorganize filename components
     */
    class RenameComponents
            extends _Action {

        /**
         * replace file extension also
         *
         * @option
         */
        private boolean withExt;

        /**
         * replace dot(.) in the filename with space
         *
         * @option
         */
        private boolean dotSpace;

        /**
         * default value of non-existing component
         *
         * @option
         */
        private String nonexist;

        /**
         * punctuation characters, used as separator between components
         *
         * @option
         */
        private String punctsPattern;

        /**
         * punctuation pattern, used as separator between components
         *
         * @option
         */
        private String puncts;

        private String replacement;

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
        public EditResult run(File file, InputStream in, OutputStream out)
                throws Exception {
            String _name = FilePath.stripExtension(file);
            String _ext = FilePath.getExtension(file, true);
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
                name += FilePath.getExtension(file, true);
            File newFile = new File(file.getParentFile(), name);
            return EditResult.ren(newFile);
        }
    }

}
