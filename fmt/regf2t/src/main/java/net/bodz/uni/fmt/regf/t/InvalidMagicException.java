package net.bodz.uni.fmt.regf.t;

import net.bodz.bas.c.string.StringArray;

public class InvalidMagicException
        extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private int skipBytesToResume;

    public InvalidMagicException(byte[] expected, byte[] actual, int skipBytesToResume) {
        super("expected " + StringArray.join(" ", expected)//
                + ", but actual " + StringArray.join(" ", actual));
        this.skipBytesToResume = skipBytesToResume;
    }

    public int getSkipBytesToResume() {
        return skipBytesToResume;
    }

}
