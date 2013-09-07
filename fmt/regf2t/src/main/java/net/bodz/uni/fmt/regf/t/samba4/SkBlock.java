package net.bodz.uni.fmt.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.SfsDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

/**
 * sk (Security Key?) is the ACL of the registry.
 */
public class SkBlock
        extends SfsDataStruct {

    private static final long serialVersionUID = 1L;

    /** charset(DOS) */
    public byte[] header = new byte[2];

    public short tag;
    public int prevOffset;
    public int nextOffset;
    public int refCnt;
    public int recSize;
    /** [recSize] */
    public byte[] secDesc;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.read(header);
        tag = in.readWord();
        prevOffset = in.readDword();
        nextOffset = in.readDword();
        refCnt = in.readDword();
        recSize = in.readDword();
        secDesc = new byte[recSize];
        in.read(secDesc);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
