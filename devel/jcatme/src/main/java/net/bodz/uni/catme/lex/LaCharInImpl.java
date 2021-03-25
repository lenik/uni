package net.bodz.uni.catme.lex;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import net.bodz.bas.io.AbstractCharIn;
import net.bodz.bas.io.ICharIn;

public class LaCharInImpl
        extends AbstractCharIn
        implements
            ILaCharIn {

    ICharIn in;
    Queue<Character> lookbuf = new LinkedList<>();

    public LaCharInImpl(ICharIn in) {
        this.in = in;
    }

    @Override
    public synchronized int read(char[] cbuf, int off, int len)
            throws IOException {
        if (len == 0)
            return 0;
        // divide the read for block aligning.
        if (!lookbuf.isEmpty()) {
            int cc = 0;
            while (!lookbuf.isEmpty()) {
                cbuf[off++] = lookbuf.remove();
                cc++;
            }
            return cc;
        } else {
            return in.read(cbuf, off, len);
        }
    }

    @Override
    public synchronized int look()
            throws IOException {
        if (lookbuf.isEmpty()) {
            int c = in.read();
            if (c == -1)
                return -1;
            char ch = (char) c;
            lookbuf.add(ch);
        }
        return lookbuf.peek();
    }

    @Override
    public synchronized int look(char[] cbuf, int off, int len)
            throws IOException {
        int looked = lookbuf.size();
        if (looked < len) {
            int nmore = len - looked;
            char[] morebuf = new char[nmore];
            int nRead = in.read(morebuf);
            for (int i = 0; i < nRead; i++) {
                lookbuf.add(morebuf[i]);
            }
        }
        int n = 0;
        for (Character ch : lookbuf) {
            if (n >= len)
                break;
            cbuf[off + n++] = ch;
        }
        return n;
    }

}
