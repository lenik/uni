package net.bodz.uni.fmt.regf.t.rec;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.file.RegfCellData;

/**
 * Value structure
 */
public class RegfVkRec
        extends RegfCellData {

    /** VK record's magic number (should be "vk") */
    public final byte[] magic = new byte[CELL_MAGIC_SIZE];

    /** Length of valuename_raw */
    public short valueNameLength;

    /**
     * The raw value name
     *
     * Length of the buffer is stored in name_length.
     */
    public byte[] valueNameRaw;

    /**
     * The name of this value converted to desired REGFI_ENCODING.
     *
     * This conversion typically occurs automatically through REGFI_ITERATOR settings. String is NUL
     * terminated.
     */
    transient String valueName;

    /**
     * Size of the value's data as reported in the VK record.
     *
     * May be different than that obtained while parsing the data cell itself.
     */
    public int dataSize;

    /** Virtual offset of data cell */
    public int dataOffset;

    /** Value's data type */
    public int valueType;

    /** VK record flags */
    public short flags;

    /** A 2-byte field of unknown purpose stored in the VK record */
    short _unknown1;

    /**
     * Whether or not the data record is stored in the VK record's data_off field.
     *
     * This information is derived from the high bit of the raw data size field.
     */
    transient boolean dataInOffset;

    /* XXX: deprecated */
    transient RegfData[] data;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
