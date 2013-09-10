package net.bodz.uni.fmt.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class HashRecord
        extends DataStruct {

    private static final long serialVersionUID = 1L;

    public int nkOffset;

    /** charset(DOS) */
    public final byte[] hash = new byte[4];

    @Override
    public int sizeof() {
        return 8;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        nkOffset = in.readDword();
        in.read(hash);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.writeDword(nkOffset);
        out.write(hash);
    }

}
