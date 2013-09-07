package net.bodz.uni.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.SfsDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class LhBlock
        extends SfsDataStruct {

    private static final long serialVersionUID = 1L;

    /** charset(DOS) */
    public byte[] header = new byte[2];

    public short keyCount;
    public LhHash[] hr;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.read(header);
        keyCount = in.readWord();
        hr = new LhHash[keyCount];
        for (int i = 0; i < keyCount; i++)
            hr[i].readObject(in);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
