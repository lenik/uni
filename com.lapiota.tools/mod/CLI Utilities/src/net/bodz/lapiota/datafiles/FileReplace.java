package net.bodz.lapiota.datafiles;

import java.util.regex.Pattern;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.ProcessResult;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.lang.Filter;
import net.bodz.bas.types.TypeParsers.GetInstanceParser;
import net.bodz.lapiota.annotations.ProgramName;
import net.bodz.lapiota.wrappers.BatchProcessCLI;

@Doc("A Unix diff program implemented in Java")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
@ProgramName("jrepl")
public class FileReplace extends BatchProcessCLI {

    @Option(alias = "p", vnam = "REGEXP", doc = "replace by regexp")
    protected Pattern                regexp;

    @Option(alias = "T", vnam = "TEXT", doc = "replace by literal text")
    protected String                 text;

    @Option(alias = "t", vnam = "TEXT", //
    doc = "may contains \\n (or $n) group reference for regexp replace")
    protected String                 replacement;

    @Option(vnam = "CLASS(Filter)", parser = GetInstanceParser.class, //
    doc = "using custom filter, this will ignore -PTt options")
    protected Filter<String, String> filter;

    @Override
    protected void _boot() throws Throwable {
        if ((regexp == null) == (text == null))
            throw new CLIException(
                    "one and only one of --regexp and --text option must be specified");

        if (ignoreCase) {
            if (regexp != null)
                regexp = Pattern.compile(regexp.pattern(),
                        Pattern.CASE_INSENSITIVE);
            if (text != null)
                text = text.toLowerCase();
        }

        if (filter == null)
            if (regexp != null)
                filter = new Filter<String, String>() {
                    @Override
                    public String filter(String input) {
                        return regexp.matcher(input).replaceAll(replacement);
                    }
                };
            else
                filter = new Filter<String, String>() {
                    @Override
                    public String filter(String input) {
                        return input.replace(text, replacement);
                    }
                };
    }

    @Override
    protected ProcessResult doFileEdit(Iterable<String> lines, CharOut out)
            throws Throwable {
        for (String line : lines)
            out.println(filter.filter(line));
        return ProcessResult.compareAndSave();
    }

    public static void main(String[] args) throws Throwable {
        new FileReplace().run(args);
    }

}
