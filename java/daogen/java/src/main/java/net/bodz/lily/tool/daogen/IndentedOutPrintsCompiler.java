package net.bodz.lily.tool.daogen;

import java.io.IOException;

import net.bodz.bas.c.java.io.LineReader;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.string.Strings;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.res.ResFn;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

@ProgramName("2print")
public class IndentedOutPrintsCompiler
        extends BasicCLI {

    /**
     * Tab stop size, a hard tab char (\t) will be aligned at each stop of this size. (default 4)
     *
     * The tab stop affects the computed indentation level.
     *
     * @option -t =NUM
     */
    int tabStop = 4;

    /**
     * How many spaces will be considered as a level of indentation. (default 4)
     *
     * @option -n =NUM
     */
    int indentSize = 4;

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        ITreeOut out = Stdio.out.indented();

        for (String path : args) {
            try (LineReader lineReader = ResFn.file(path).newLineReader()) {
                toPrints(lineReader, out);
            }
        }
    }

    void toPrints(LineReader in, ITreeOut out)
            throws IOException {
        String line;
        int lastLevel = 0;
        while ((line = in.readLine()) != null) {
            String leadingSpace = Strings.leadingSpace(line);

            line = line.substring(leadingSpace.length()).trim();
            if (line.isEmpty()) {
                out.println("out.println();");
                continue;
            }

            int level = getIndentLevel(leadingSpace);
            if (level != lastLevel) {
                while (lastLevel < level) {
                    out.println("out.enter();");
                    out.enterln("{");
                    lastLevel++;
                }
                while (lastLevel > level) {
                    out.println("out.leave();");
                    out.leaveln("}");
                    lastLevel--;
                }
            }
            String quoted = StringQuote.qqJavaString(line);
            out.print("out.println(");
            out.print(quoted);
            out.println(");");
        }
    }

    int getWidth(String ws) {
        int n = ws.length();
        int pos = 0;
        for (int i = 0; i < n; i++) {
            char ch = ws.charAt(i);
            if (ch == '\t')
                pos += tabStop - pos % tabStop;
            else
                pos++;
        }
        return pos;
    }

    int getIndentLevel(String ws) {
        int width = getWidth(ws);
        return (width + indentSize - 1) / indentSize;
    }

    void testIndentCompute()
            throws IOException {
        LineReader in = new LineReader(Stdio.cin);
        String line;
        while ((line = in.readLine()) != null) {
            String leadingSpace = Strings.leadingSpace(line);
            int width = getWidth(leadingSpace);
            int indentLevel = getIndentLevel(leadingSpace);
            System.out.printf("width %d, level %d \t[%s]\n", width, indentLevel, leadingSpace);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new IndentedOutPrintsCompiler().execute(args);
    }

}
