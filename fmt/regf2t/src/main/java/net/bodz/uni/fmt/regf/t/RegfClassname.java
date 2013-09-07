package net.bodz.uni.fmt.regf.t;

/**
 * Class name structure (used in storing SysKeys)
 */
public class RegfClassname {

    /** As converted to requested REGFI_ENCODING */
    String interpreted;

    /**
     * Represents raw buffer read from classname cell.
     *
     * Length of this item is specified in the size field.
     */
    byte[] raw;

    /**
     * Length of the raw data.
     *
     * May be shorter than that indicated by parent key.
     */
    short size;

}
