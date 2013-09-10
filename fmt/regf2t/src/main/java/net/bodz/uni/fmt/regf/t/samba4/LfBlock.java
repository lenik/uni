package net.bodz.uni.fmt.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

/**
 * The lf-record is the counterpart to the RGKN-recod (the hash-function).
 */
public class LfBlock
        extends DataStruct {

    private static final long serialVersionUID = 1L;

    /** charset(DOS) */
    public final byte[] header = new byte[2];

    public short keyCount;

    /** Array of hash records, depending on keyCount */
    public HashRecord[] hr;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.read(header);
        keyCount = in.readWord();
        hr = new HashRecord[keyCount];
        for (int i = 0; i < keyCount; i++)
            hr[i].readObject(in);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.write(header);
        out.write(keyCount);
        for (int i = 0; i < keyCount; i++)
            hr[i].writeObject(out);
    }

}
