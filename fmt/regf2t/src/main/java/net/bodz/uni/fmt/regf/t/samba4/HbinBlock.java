package net.bodz.uni.fmt.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.NtTime;

public class HbinBlock
        extends DataStruct {

    private static final long serialVersionUID = 1L;

    /** 'hbin' */
    public final byte HBIN_ID[] = new byte[4];

    public int offsetFromFirst;
    public int offsetToNext;
    public int unknown1;
    public int unknown2;
    public final NtTime lastChange = new NtTime();
    public int blockSize;

    /**
     * data is filled with:
     * <ul>
     * <li>uint32 length; Negative if in use, positive otherwise Always a multiple of 8
     * <li>uint8_t data[length]; Free space marker if 0xffffffff
     * </ul>
     *
     * size = offsetToNext - 0x20
     */
    public byte[] data;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        offsetFromFirst = in.readDword();
        offsetToNext = in.readDword();
        unknown1 = in.readDword();
        unknown2 = in.readDword();
        lastChange.readObject(in);
        blockSize = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
