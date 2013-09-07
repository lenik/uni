package net.bodz.uni.fmt.regf.t;

/**
 * Data record structure.
 */
public class RegfData {

    /** Data type of this data, as indicated by the referencing VK record. */
    int type;

    /** Length of the raw data. */
    int size;

    /** This is always present, representing the raw data cell contents. */
    byte[] raw;

    /** Represents the length of the interpreted value. Meaning is type-specific. */
    int interpretedSize;

    byte[] interpreted;

}
