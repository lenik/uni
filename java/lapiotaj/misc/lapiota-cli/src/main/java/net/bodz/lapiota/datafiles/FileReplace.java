package net.bodz.lapiota.datafiles;

import java.util.regex.Pattern;

import net.bodz.bas.cli.BatchEditCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.info.Doc;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.mode._fn.Filt1;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.bas.trait.spi.extra.GetInstanceParser;
import net.bodz.lapiota.nls.CLINLS;

/**
 * A Unix diff program implemented in Java
 */
@Doc("")
@ProgramName("jrepl")
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
public class FileReplace
        extends BatchEditCLI {

    /**
     * replace by regexp
     *
     * @option -p =REGEXP
     */
    protected Pattern regexp;

    /**
     * replace by literal text
     *
     * @option -T =TEXT
     */
    protected String text;

    /**
     * may contains \\n (or $n) group reference for regexp replace
     *
     * @option -t =TEXT
     */
    protected String replacement;

    /**
     * using custom filter, this will ignore -PTt options
     *
     * @option =CLASS(Filter)
     */
    @ParseBy(GetInstanceParser.class)
    protected Filt1<String, String> filter;

    @Override
    protected void _boot()
            throws Exception {
        if ((regexp == null) == (text == null))
            throw new CLIException(CLINLS.getString("FileReplace.regexpOrText")); //$NON-NLS-1$

        if (parameters().isIgnoreCase()) {
            if (regexp != null)
                regexp = Pattern.compile(regexp.pattern(), Pattern.CASE_INSENSITIVE);
            if (text != null)
                text = text.toLowerCase();
        }

        if (filter == null)
            if (regexp != null)
                filter = new Filt1<String, String>() {
                    @Override
                    public String filter(String input) {
                        return regexp.matcher(input).replaceAll(replacement);
                    }
                };
            else
                filter = new Filt1<String, String>() {
                    @Override
                    public String filter(String input) {
                        return input.replace(text, replacement);
                    }
                };
    }

    @Override
    protected EditResult doEditByLine(Iterable<String> lines, IPrintOut out)
            throws Exception {
        for (String line : lines)
            out.println(filter.filter(line));
        return EditResult.compareAndSave();
    }

    public static void main(String[] args)
            throws Exception {
        new FileReplace().run(args);
    }

}
