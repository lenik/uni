package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

/**
 * Data record structure.
 *
 * Windows XP and later will look for a big data record under the following conditions: the registry
 * major/minor versionis 1.4 or later and the data size is greater than 16344 bytes in length.
 *
 * At that point, Windows will attempt to validate the cell referenced by the value record as a big
 * data record. The big data record will indicate the number of data chunk fragments are stored on
 * disk as well as a pointer to an indirect block of offsets for the fragment cells.
 */
public class BigDataCell
        extends AbstractCell {

    private static final long serialVersionUID = 1L;

    /** Magic number of key */
    public short magic;

    /** Number of data fragments */
    public short fragmentCount;

    /** Pointer to big data indirect cell */
    public int indirectsOffset;

    /** unknown */
    int _unused;

    public transient int[] fragmentOffsets;

    @Override
    public short getMagic() {
        return magic;
    }

    @Override
    public void setMagic(short magic) {
        if (magic != MAGIC_BD)
            throw new IllegalArgumentException("Bad magic: " + magic);
        this.magic = magic;
    }

    @Override
    public void readObject2(IDataIn in)
            throws IOException {
        fragmentCount = in.readWord();
        indirectsOffset = in.readDword();
    }

    @Override
    public void writeObject2(IDataOut out)
            throws IOException {
        out.writeWord(fragmentCount);
        out.writeDword(indirectsOffset);
    }

}
