package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.IRegfConsts;

/**
 * Subkey-list structure
 */
public class SubkeyListCell
        extends AbstractCell
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /** Magic number of key */
    public short magic;

    /**
     * Number of immediate children
     *
     * Set to 0 after deletion.
     */
    public short elementCount;

    /** Total number of keys referenced by this list and it's children */
    transient int numKeys;

    /** Set if the magic indicates this subkey list points to child subkey lists */
    transient byte recursiveType;

    transient SubkeyElement[] elements;

    @Override
    public short getMagic() {
        return magic;
    }

    @Override
    public void setMagic(short magic) {
        switch (magic) {
        case MAGIC_LF:
        case MAGIC_LH:
        case MAGIC_RI:
        case MAGIC_LI:
            this.magic = magic;
            break;
        default:
            throw new IllegalArgumentException("Bad magic: " + magic);
        }
    }

    @Override
    public void readObject2(IDataIn in)
            throws IOException {
    }

    @Override
    public void writeObject2(IDataOut out)
            throws IOException {
    }

}
