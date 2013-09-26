package net.bodz.uni.shelj.c.builtin;

import java.util.List;

import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.meta.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.text.diff.DiffComparators;
import net.bodz.bas.text.diff.DiffEntry;
import net.bodz.bas.text.diff.IDiffComparator;
import net.bodz.bas.text.diff.IDiffFormat;
import net.bodz.bas.vfs.IFile;

/**
 * A Unix diff program implemented in Java
 */
@ProgramName("jdiff")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class FileCompare
        extends BasicCLI {

    /**
     * Simdiff, Context, ED, Unified
     *
     * @option -F =FORMAT
     */
    protected IDiffFormat diffFormat = IDiffFormat.SIMPLE;

    /**
     * Compare from
     *
     * @option :0 =FILE required
     */
    protected IFile src;

    /**
     * Compile to
     *
     * @option :1 =FILE required
     */
    protected IFile dst;

    /**
     * Where to print the diff details
     *
     * @option -o
     */
    protected IPrintOut output = Stdio.cout;

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (args.length > 0)
            throw new IllegalArgumentException(tr._("unexpected argument: ") + args[0]);
        IDiffComparator gnudiff = DiffComparators.gnudiff;
        List<String> srcl = src.to(StreamReading.class).readLines();
        List<String> dstl = dst.to(StreamReading.class).readLines();
        List<DiffEntry> diffs = gnudiff.compareDiff(srcl, dstl);
        diffFormat.printDiffs(output, srcl, dstl, diffs);
    }

    public static void main(String[] args)
            throws Exception {
        new FileCompare().execute(args);
    }

}
