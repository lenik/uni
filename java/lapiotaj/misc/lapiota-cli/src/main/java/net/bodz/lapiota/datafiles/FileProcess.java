package net.bodz.lapiota.datafiles;

import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.java.util.regex.PatternProcessor;
import net.bodz.bas.c.string.IndexVarSubst;
import net.bodz.bas.cli.plugin.AbstractCLIPlugin;
import net.bodz.bas.cli.plugin.CLIPlugin;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIException;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.resource.builtin.InputStreamSource;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.vfs.IFile;
import net.bodz.bas.vfs.impl.javaio.JavaioFile;
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
        L.debug(tr._("action: "), action);
        actions.add(action);
    }

    class ScriptScope {
        public IFile getDst0() {
            return getOutputFile(currentFile);
        }

        public IFile dst;

        public IFile getDir() {
            return dst.getParentFile();
        }

        public void setDir(IFile newDir) {
            dst = newDir;
        }

        public String getBase() {
            return dst.getName();
        }

        public void setBase(String newBase) {
            dst = dst.getParentFile().getChild(newBase);
        }

        public String getName() {
            String base = getBase();
            int dot = base.lastIndexOf('.');
            if (dot == -1)
                return base;
            return base.substring(0, dot);
        }

        public void setName(String newName) {
            String ext = FilePath.getExtension(dst.getName(), true);
            dst = dst.getParentFile().getChild(newName + ext);
        }

        public String getExt() {
            return FilePath.getExtension(dst.getName());
        }

        public void setExt(String newExt) {
            if (newExt == null)
                newExt = "";
            else if (!newExt.isEmpty())
                newExt = "." + newExt;
            dst = dst.getParentFile().getChild(getName() + newExt);
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
    protected IFile _getEditTmp(IFile file)
            throws IOException {
        if (edit)
            return super._getEditTmp(file);
        return null;
    }

    /** canonical file */
    private IFile currentFile;

    private Action currentAction;

    @Override
    protected void _processFile(IFile file) {
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
        EditResult run(IFile file, InputStream in, OutputStream out)
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
                System.out.println(tr._("Enter the processing groovy script: "));
                script = new InputStreamSource(System.in).tooling()._for(StreamReading.class).readTextContents();
            } else {
                String scriptPath = args[0];
                IFile scriptFile = new JavaioFile(scriptPath);
                scriptFile.setPreferredCharset(parameters().getInputEncoding());
                script = scriptFile.tooling()._for(StreamReading.class).readTextContents();
            }
        }

        @Override
        public boolean isEditor() {
            return isEditor;
        }

        @Override
        public EditResult run(IFile file, InputStream in, OutputStream out)
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
                throw new IllegalArgumentException(tr._("not a subs-regexp: ") + exp);
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
                throw new IllegalArgumentException(tr._("invalid subs-regexp format: ") + exp);
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
                        throw new IllegalArgumentException(tr._("invalid regexp flag: ") + c);
                    }
                }
            }
            pattern = Pattern.compile(segs[0], flags);
            replacement = segs.length >= 2 ? segs[1] : "";
        }

        @Override
        public EditResult run(IFile file, InputStream in, OutputStream out)
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
                name += FilePath.getExtension(file.getName(), true);
            IFile newFile = file.getParentFile().getChild(name);
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
        public EditResult run(IFile file, InputStream in, OutputStream out)
                throws Exception {
            String _name = FilePath.stripExtension(file.getName());
            String _ext = FilePath.getExtension(file.getName(), true);
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
                name = IndexVarSubst.subst(replacement, //
                        1, components, nonexist);
            } catch (IndexOutOfBoundsException e) {
                assert nonexist == null;
                return null;
            }

            if (!withExt)
                name += FilePath.getExtension(file.getName(), true);
            IFile newFile = file.getParentFile().getChild(name);
            return EditResult.ren(newFile);
        }
    }

}
