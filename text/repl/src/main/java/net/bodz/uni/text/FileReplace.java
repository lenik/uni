package net.bodz.uni.text;

import java.util.regex.Pattern;

import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.fn.IRewriter;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.model.IAppLifecycleListener;
import net.bodz.bas.program.skel.BatchEditCLI;
import net.bodz.bas.program.skel.CLIAccessor;
import net.bodz.bas.program.skel.FileHandler;

/**
 * A Unix diff program implemented in Java
 */
@MainVersion({ 0, 1 })
@ProgramName("jrepl")
@RcsKeywords(id = "$Id$")
public class FileReplace
        extends BatchEditCLI
        implements
            IAppLifecycleListener<FileReplace> {

    /**
     * replace by regexp
     *
     * @option -P --regex =REGEXP
     */
    protected Pattern pattern;

    /**
     * Replace by literal text
     *
     * @option -F --fixed-string =TEXT
     */
    protected String textPattern;

    /**
     * The replacement. May include \\n (or $n) reference in regexp mode.
     *
     * @option -t --replacement =TEXT
     */
    protected String replacement;

    /**
     * Use the specific rewriter impl, this will ignore -P, -T, -t options
     *
     * @option =CLASS(IRewriter)
     */
    protected IRewriter<String> rewriter;

    @Override
    public void initDefaults(FileReplace app) {
        if ((pattern == null) == (textPattern == null))
            throw new IllegalArgumentException(
                    nls.tr("one and only one of --regexp and --text option must be specified"));

        if (CLIAccessor.isIgnoreCase(FileReplace.this)) {
            if (pattern != null)
                pattern = Pattern.compile(pattern.pattern(), Pattern.CASE_INSENSITIVE);
            if (textPattern != null)
                textPattern = textPattern.toLowerCase();
        }

        if (rewriter == null)
            if (pattern != null)
                rewriter = new IRewriter<String>() {
                    @Override
                    public String rewrite(String input) {
                        return pattern.matcher(input).replaceAll(replacement);
                    }
                };
            else
                rewriter = new IRewriter<String>() {
                    @Override
                    public String rewrite(String input) {
                        return input.replace(textPattern, replacement);
                    }
                };
    }

    @Override
    public void processFile(FileHandler handler)
            throws Exception {
        throw new NotImplementedException();
    }

    public static void main(String[] args)
            throws Exception {
        new FileReplace().execute(args);
    }

}
