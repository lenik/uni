package net.bodz.uni.fmt.regf.t.rec;

import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.NtTime;

public class RegfNkRec
        implements IRegfConsts {

    /** Real offset of this record's cell in the file */
    int offset;

    /**
     * Actual or estimated length of the cell. Always in multiples of 8.
     */
    int cellSize;

    /**
     * Preloaded value-list for this key. This element is loaded automatically when using the
     * iterator interface and possibly some lower layer interfaces.
     */
    RegfValueList[] values;

    /**
     * Preloaded subkey-list for this key. This element is loaded automatically when using the
     * iterator interface and possibly some lower layer interfaces.
     */
    RegfSubkeyList[] subkeys;

    /** Key flags */
    short flags;

    /** Magic number of key (should be "nk") */
    byte[] magic = new byte[CELL_MAGIC_SIZE];

    /** Key's last modification time */
    NtTime mtime;

    /** Length of keyname_raw */
    short nameLength;

    /** Length of referenced classname */
    short classnameLength;

    /**
     * The name of this key converted to desired REGFI_ENCODING.
     *
     * This conversion typically occurs automatically through REGFI_ITERATOR settings. String is NUL
     * terminated.
     */
    char[] keyname;

    /**
     * The raw key name
     *
     * Length of the buffer is stored in name_length.
     */
    byte[] keynameRaw;

    /** Virutal offset of parent key */
    int parentOffset;

    /** Virutal offset of classname key */
    int classnameOffset;

    /** XXX: max subkey name * 2 */
    int maxBytesSubkeyname;

    /** XXX: max subkey classname length (as if) */
    int maxBytesSubkeyclassname;

    /** XXX: max valuename * 2 */
    int maxBytesValuename;

    /** XXX: max value data size */
    int maxBytesValue;

    /** XXX: Fields of unknown purpose */
    int unknown1;
    int unknown2;
    int unknown3;
    /** nigel says run time index ? */
    int unkIndex;

    /** Number of subkeys */
    int numSubkeys;

    /** Virtual offset of subkey-list */
    int subkeysOffset;

    /** Number of values for this key */
    int numValues;

    /** Virtual offset of value-list */
    int valuesOffset;

    /** Virtual offset of SK record */
    int skOffset;

}
