package net.bodz.uni.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.SfsDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

/**
 * The vk-record consists information to a single value (value key).
 */
public class VkBlock
        extends SfsDataStruct {

    private static final long serialVersionUID = 1L;

    /** charset(DOS) */
    public byte[] header = new byte[2];

    public short nameLength;

    /** If top-bit set, offset contains the data */
    public int dataLength;
    public int dataOffset;
    public int dataType;

    /** =1, has name, else no name (=Default). */
    public short flag;
    public short unk1;

    /** charset(DOS), length = nameLength */
    public byte[] dataName;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.read(header);
        nameLength = in.readWord();
        dataLength = in.readDword();
        dataOffset = in.readDword();
        dataType = in.readDword();
        flag = in.readWord();
        unk1 = in.readWord();
        dataName = new byte[nameLength];
        in.read(dataName);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
