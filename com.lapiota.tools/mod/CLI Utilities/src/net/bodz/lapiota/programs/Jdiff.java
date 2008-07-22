package net.bodz.lapiota.programs;

import java.io.File;
import java.util.List;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.CharOuts;
import net.bodz.bas.io.Files;
import net.bodz.bas.types.diff.DiffComparator;
import net.bodz.bas.types.diff.DiffComparators;
import net.bodz.bas.types.diff.DiffFormat;
import net.bodz.bas.types.diff.DiffFormats;
import net.bodz.bas.types.diff.DiffInfo;
import net.bodz.lapiota.wrappers.BasicCLI;

@Doc("A Unix diff program implemented in Java")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
public class Jdiff extends BasicCLI {

    @Option(alias = "F", vnam = "FORMAT", doc = "Simdiff, Context, ED, Unified")
    protected DiffFormat diffFormat = DiffFormats.Simdiff;

    @Option(fileIndex = 0, vnam = "FILE", required = true, doc = "compare from")
    protected File       src;
    @Option(fileIndex = 1, vnam = "FILE", required = true, doc = "compile to")
    protected File       dst;

    @Option(alias = "o", doc = "where to print the diff details")
    protected CharOut    output     = CharOuts.stdout;

    @Override
    protected void _main(String[] args) throws Throwable {
        if (args.length > 0)
            throw new IllegalArgumentException("unexpected argument: "
                    + args[0]);
        DiffComparator gnudiff = DiffComparators.gnudiff;
        List<String> srcl = Files.readLines(src);
        List<String> dstl = Files.readLines(dst);
        List<DiffInfo> diffs = gnudiff.diffCompare(srcl, dstl);
        diffFormat.format(srcl, dstl, diffs, output);
    }

    public static void main(String[] args) throws Throwable {
        new Jdiff().climain(args);
    }

}
