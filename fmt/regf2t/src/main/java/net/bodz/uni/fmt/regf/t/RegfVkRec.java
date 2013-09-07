package net.bodz.uni.fmt.regf.t;

/**
 * Value structure
 */
public class RegfVkRec
        implements IRegfConsts {

    /** Real offset of this record's cell in the file */
    int offset;

    /** ((start_offset - end_offset) & 0xfffffff8) */
    int cellSize;

    /* XXX: deprecated */
    RegfData[] data;

    /**
     * The name of this value converted to desired REGFI_ENCODING.
     *
     * This conversion typically occurs automatically through REGFI_ITERATOR settings. String is NUL
     * terminated.
     */
    String valuename;

    /**
     * The raw value name
     *
     * Length of the buffer is stored in name_length.
     */
    byte[] valuenameRaw;

    /** Length of valuename_raw */
    short nameLength;

    /** Offset from beginning of this hbin block */
    int hbinOffset;

    /**
     * Size of the value's data as reported in the VK record.
     *
     * May be different than that obtained while parsing the data cell itself.
     */
    int dataSize;

    /** Virtual offset of data cell */
    int dataOffset;

    /** Value's data type */
    int type;

    /** VK record's magic number (should be "vk") */
    byte[] magic = new byte[CELL_MAGIC_SIZE];

    /** VK record flags */
    short flags;

    /* XXX: A 2-byte field of unknown purpose stored in the VK record */
    short unknown1;

    /**
     * Whether or not the data record is stored in the VK record's data_off field.
     *
     * This information is derived from the high bit of the raw data size field.
     */
    byte dataInOffset;

}
