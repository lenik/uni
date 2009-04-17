package net.bodz.lapiota.jaep;

import java.io.Serializable;

import net.bodz.bas.types.util.Strings;

public class QName implements Serializable, Comparable<QName> {

    private static final long serialVersionUID = 3751298694872291945L;

    private String[]          vec;

    public QName(String... elements) {
        this.vec = elements;
    }

    public QName(String simpleName) {
        if (simpleName == null)
            throw new NullPointerException("term");
        String[] terms = { simpleName };
        this.vec = terms;
    }

    @Override
    public String toString() {
        return Strings.join(".", vec);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QName) {
            QName that = (QName) obj;
            assert this.vec != null;
            assert that.vec != null;
            if (this.vec == that.vec)
                return true;
            if (this.vec.length != that.vec.length)
                return false;
            for (int i = 0; i < vec.length; i++) {
                if (!this.vec[i].equals(that.vec[i]))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (String t : vec)
            h = (h >>> 13) ^ t.hashCode();
        return h;
    }

    @Override
    public int compareTo(QName o) {
        int len = Math.min(vec.length, o.vec.length);
        for (int i = 0; i < len; i++) {
            int c = vec[i].compareTo(o.vec[i]);
            if (c != 0)
                return c;
        }
        if (vec.length < len)
            return -1;
        if (vec.length > len)
            return 1;
        return 0;
    }

}
