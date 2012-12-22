package net.bodz.lapiota.batch.rewrite;

import java.util.regex.Pattern;

import net.bodz.bas.cli.meta.ProgramName;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIAccessor;
import net.bodz.bas.cli.skel.CLISyntaxException;
import net.bodz.bas.cli.skel.FileHandler;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.fn.IRewriter;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;

/**
 * A Unix diff program implemented in Java
 */
@ProgramName("jrepl")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class FileReplace
        extends BatchEditCLI {

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
    protected void reconfigure()
            throws Exception {
        if ((pattern == null) == (textPattern == null))
            throw new CLISyntaxException(tr._("one and only one of --regexp and --text option must be specified"));

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
