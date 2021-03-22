package net.bodz.uni.catme.trie;

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
        if (!lookbuf.isEmpty()) {
            int cc = 0;
            while (!lookbuf.isEmpty()) {
                cbuf[off++] = lookbuf.remove();
                cc++;
            }
            return cc;
        }
        // divide the read for block aligning.
        return in.read(cbuf, off, len);
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
