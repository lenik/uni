package net.bodz.uni.fmt.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.SfsDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class LiBlock
        extends SfsDataStruct {

    private static final long serialVersionUID = 1L;

    /** charset(DOS) */
    public byte[] header = new byte[2];

    public short keyCount;
    public int[] nkOffset;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.read(header);
        keyCount = in.readWord();
        nkOffset = new int[keyCount];
        for (int i = 0; i < keyCount; i++)
            nkOffset[i] = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
