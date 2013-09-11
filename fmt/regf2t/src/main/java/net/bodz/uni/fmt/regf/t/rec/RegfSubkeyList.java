package net.bodz.uni.fmt.regf.t.rec;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.file.RegfCellData;

/**
 * Subkey-list structure
 */
public class RegfSubkeyList
        extends RegfCellData
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /**
     * lf, lh, ri, li
     */
    public final byte[] magic = new byte[CELL_MAGIC_SIZE];

    /** Number of immediate children */
    public short elementCount;

    /** Total number of keys referenced by this list and it's children */
    transient int numKeys;

    /** Set if the magic indicates this subkey list points to child subkey lists */
    transient byte recursiveType;

    transient RegfSubkeyListElem[] elements;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
