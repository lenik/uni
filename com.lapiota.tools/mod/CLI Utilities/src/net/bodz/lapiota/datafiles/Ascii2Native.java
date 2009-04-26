package net.bodz.lapiota.datafiles;

import java.nio.CharBuffer;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.text.interp.Unescape;
import net.bodz.bas.text.util.BufParsers;
import net.bodz.lapiota.wrappers.BatchEditCLI;

@Doc("convert ascii chars or \\uNNNN to native chars")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
public class Ascii2Native extends BatchEditCLI {

    // default inputEncoding == native(includes ascii)

    // default outputEncoding == native

    class Udecode extends Unescape {

        public Udecode() {
            super("\\"); //$NON-NLS-1$
        }

        @Override
        protected String decode(CharBuffer in) {
            char c = in.get();
            int v;
            switch (c) {
            case 'u':
                // Character.MAX_CODE_POINT;
                v = BufParsers.getInt(in, 16, Character.MAX_VALUE);
                if (v != -1)
                    c = (char) v;
                break;
            }
            return String.valueOf(c);
        }

    }

    @Override
    protected EditResult doEditByLine(Iterable<String> lines, CharOut out) throws Throwable {
        Udecode decoder = new Udecode();
        for (String line : lines) {
            line = decoder.process(line);
            out.println(line);
        }
        return EditResult.compareAndSave();
    }

    public static void main(String[] args) throws Throwable {
        new Ascii2Native().run(args);
    }

}
