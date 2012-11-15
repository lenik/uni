package net.bodz.lapiota.javatools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIAccessor;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.bas.vfs.FileMaskedModifiers;

/**
 * Line-No Fix
 */
@ProgramName("linefix")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class LineNoFix
        extends BatchEditCLI {

    /**
     * @Option(alias = "l", vnam = "REGEX", doc = "line-no pattern")
     */
    Pattern linePattern;

    /**
     * max jump lines
     *
     * for b=n[i], c=n[i+1]: if c - b > maxDelta, then ignore c.
     *
     * @option -J =LINES
     */
    int maxDelta = 100;

    /**
     * remove line numbers from src file
     *
     * @option -k
     */
    boolean killLineNo;

    // not used
    /**
     * @option
     */
    boolean join = true;

    public LineNoFix() {
        CLIAccessor.setIncludeMask(this, new FileMaskedModifiers("fT/fHT"));
    }

    @Override
    protected void _boot()
            throws Exception {
        if (linePattern == null)
            linePattern = Pattern.compile("^\\s*/\\*\\s*(\\d+)\\s*\\*/");
    }

    static class Line {
        public int n;
        public String s;

        public Line(int n, String s) {
            this.n = n;
            this.s = s;
        }

        void chopm() {
            while (!s.isEmpty()) {
                switch (s.charAt(s.length() - 1)) {
                case '\n':
                case '\r':
                    s = s.substring(0, s.length() - 1);
                    continue;
                }
                break;
            }
        }

        void compactLeft() {
            int n = 0;
            while (!s.isEmpty()) {
                switch (s.charAt(0)) {
                case ' ':
                case '\t':
                    s = s.substring(1);
                    n++;
                    continue;
                }
                break;
            }
            if (n > 0)
                s = " " + s;
        }

    }

    @Override
    protected EditResult doEditByLine(Iterable<String> _lines, IPrintOut out)
            throws Exception {
        List<Line> lines = new ArrayList<Line>(10000);
        lines.add(null); // 1-based
        int last = -1;
        for (String s : _lines) {
            Matcher matcher = linePattern.matcher(s);
            int n = 0;
            if (matcher.find()) {
                n = Integer.parseInt(matcher.group(1));
                if (killLineNo) {
                    int start = matcher.start(0);
                    int end = matcher.end(0);
                    String blank = Strings.repeat(end - start, ' ');
                    s = s.substring(0, start) + blank + s.substring(end);
                }
            }
            if (last != -1) {
                if (n > last + maxDelta) {
                    // jump too much, ignore.
                    n = 0;
                } else if (n < last) {
                    // reverse?? ignore
                    n = 0;
                }
            }
            if (n != 0)
                last = n;
            Line line = new Line(n, s);
            lines.add(line);
        }

        int size = lines.size();
        int syncI = 0;
        int syncMark = 0;
        for (int i = 1; i < size; i++) {
            Line line = lines.get(i);
            int mark = line.n;
            if (mark == 0)
                continue;
            assert mark >= syncMark : mark + "/" + syncMark;
            int markDelta = mark - syncMark;
            int delta = i - syncI;
            if (delta > markDelta) {
                int merge = delta - markDelta + 1;
                assert merge >= 2;
                // merge i-1 .. i-merge
                for (int mi = 2; mi <= merge; mi++) {
                    lines.get(i - mi).chopm();
                    lines.get(i - mi + 1).compactLeft();
                }
            } else if (delta < markDelta) {
                int d = markDelta - delta;
                String ins = Strings.repeat(d, "\n");
                line.s = ins + line.s;
            }
            syncI = i;
            syncMark = mark;
        }
        for (int i = 1; i < size; i++)
            out.print(lines.get(i).s);
        return EditResult.compareAndSave();
    }

    public static void main(String[] args)
            throws Exception {
        new LineNoFix().execute(args);
    }

}
