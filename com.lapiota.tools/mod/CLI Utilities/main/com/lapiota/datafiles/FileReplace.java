package com.lapiota.datafiles;

import java.util.regex.Pattern;

import com.lapiota.nls.CLINLS;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.BatchEditCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.a.ParseBy;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.lang.Filt1;
import net.bodz.bas.types.TypeParsers.GetInstanceParser;

@Doc("A Unix diff program implemented in Java")
@ProgramName("jrepl")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class FileReplace extends BatchEditCLI {

    @Option(alias = "p", vnam = "REGEXP", doc = "replace by regexp")
    protected Pattern               regexp;

    @Option(alias = "T", vnam = "TEXT", doc = "replace by literal text")
    protected String                text;

    @Option(alias = "t", vnam = "TEXT", //
    doc = "may contains \\n (or $n) group reference for regexp replace")
    protected String                replacement;

    @Option(vnam = "CLASS(Filter)", doc = "using custom filter, this will ignore -PTt options")
    @ParseBy(GetInstanceParser.class)
    protected Filt1<String, String> filter;

    @Override
    protected void _boot() throws Exception {
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
    protected EditResult doEditByLine(Iterable<String> lines, CharOut out) throws Exception {
        for (String line : lines)
            out.println(filter.filter(line));
        return EditResult.compareAndSave();
    }

    public static void main(String[] args) throws Exception {
        new FileReplace().run(args);
    }

}
