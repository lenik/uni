package net.bodz.uni.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.SfsDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class RiBlock
        extends SfsDataStruct {

    private static final long serialVersionUID = 1L;

    /** charset(DOS) */
    public byte[] header = new byte[2];

    public short keyCount;

    /** li/lh offset */
    public int[] offset;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.read(header);
        keyCount = in.readWord();
        offset = new int[keyCount];
        for (int i = 0; i < keyCount; i++)
            offset[i] = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
