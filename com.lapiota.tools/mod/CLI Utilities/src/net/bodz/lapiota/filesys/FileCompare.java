package net.bodz.lapiota.filesys;

import java.io.File;
import java.util.List;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.CharOuts;
import net.bodz.bas.io.Files;
import net.bodz.bas.text.diff.DiffComparator;
import net.bodz.bas.text.diff.DiffComparators;
import net.bodz.bas.text.diff.DiffFormat;
import net.bodz.bas.text.diff.DiffFormats;
import net.bodz.bas.text.diff.DiffInfo;
import net.bodz.lapiota.nls.CLINLS;

@Doc("A Unix diff program implemented in Java")
@ProgramName("jdiff")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class FileCompare extends BasicCLI {

    @Option(alias = "F", vnam = "FORMAT", doc = "Simdiff, Context, ED, Unified")
    protected DiffFormat diffFormat = DiffFormats.Simdiff;

    @Option(fileIndex = 0, vnam = "FILE", required = true, doc = "compare from")
    protected File       src;
    @Option(fileIndex = 1, vnam = "FILE", required = true, doc = "compile to")
    protected File       dst;

    @Option(alias = "o", doc = "where to print the diff details")
    protected CharOut    output     = CharOuts.stdout;

    @Override
    protected void doMain(String[] args) throws Exception {
        if (args.length > 0)
            throw new IllegalArgumentException(
                    CLINLS.getString("FileCompare.unexpectedArgument") + args[0]); //$NON-NLS-1$
        DiffComparator gnudiff = DiffComparators.gnudiff;
        List<String> srcl = Files.readLines(src);
        List<String> dstl = Files.readLines(dst);
        List<DiffInfo> diffs = gnudiff.diffCompare(srcl, dstl);
        diffFormat.format(srcl, dstl, diffs, output);
    }

    public static void main(String[] args) throws Exception {
        new FileCompare().run(args);
    }

}
