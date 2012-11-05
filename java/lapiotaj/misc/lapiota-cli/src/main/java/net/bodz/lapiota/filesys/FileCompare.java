package net.bodz.lapiota.filesys;

import java.util.List;

import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.bas.sio.Stdio;
import net.bodz.bas.text.diff.DiffComparator;
import net.bodz.bas.text.diff.DiffComparators;
import net.bodz.bas.text.diff.DiffFormat;
import net.bodz.bas.text.diff.DiffFormats;
import net.bodz.bas.text.diff.DiffInfo;
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
    protected DiffFormat diffFormat = DiffFormats.Simdiff;

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
        DiffComparator gnudiff = DiffComparators.gnudiff;
        List<String> srcl = src.tooling()._for(StreamReading.class).listLines();
        List<String> dstl = dst.tooling()._for(StreamReading.class).listLines();
        List<DiffInfo> diffs = gnudiff.diffCompare(srcl, dstl);
        diffFormat.format(srcl, dstl, diffs, output);
    }

    public static void main(String[] args)
            throws Exception {
        new FileCompare().execute(args);
    }

}
