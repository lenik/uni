package net.bodz.lapiota.filesys;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.sio.ICharOut;
import net.bodz.bas.sio.Stdio;
import net.bodz.bas.text.diff.DiffComparator;
import net.bodz.bas.text.diff.DiffComparators;
import net.bodz.bas.text.diff.DiffFormat;
import net.bodz.bas.text.diff.DiffFormats;
import net.bodz.bas.text.diff.DiffInfo;
import net.bodz.lapiota.nls.CLINLS;

/**
 * A Unix diff program implemented in Java
 */
@ProgramName("jdiff")
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
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
    protected File src;

    /**
     * Compile to
     *
     * @option :1 =FILE required
     */
    protected File dst;

    /**
     * Where to print the diff details
     *
     * @option -o
     */
    protected ICharOut output = Stdio.cout;

    @Override
    protected void doMain(String[] args)
            throws Exception {
        if (args.length > 0)
            throw new IllegalArgumentException(CLINLS.getString("FileCompare.unexpectedArgument") + args[0]); //$NON-NLS-1$
        DiffComparator gnudiff = DiffComparators.gnudiff;
        List<String> srcl = Files.readLines(src);
        List<String> dstl = Files.readLines(dst);
        List<DiffInfo> diffs = gnudiff.diffCompare(srcl, dstl);
        diffFormat.format(srcl, dstl, diffs, output);
    }

    public static void main(String[] args)
            throws Exception {
        new FileCompare().run(args);
    }

}