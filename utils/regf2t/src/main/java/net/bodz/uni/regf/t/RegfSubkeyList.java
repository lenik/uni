package net.bodz.uni.regf.t;

/**
 * Subkey-list structure
 */
public class RegfSubkeyList
        implements IRegfConsts {

    /** Real offset of this record's cell in the file */
    int offset;

    int cellSize;

    /** Number of immediate children */
    int numChildren;

    /** Total number of keys referenced by this list and it's children */
    int numKeys;

    RegfSubkeyListElem[] elements;
    byte[] magic = new byte[CELL_MAGIC_SIZE];

    /** Set if the magic indicates this subkey list points to child subkey lists */
    byte recursiveType;

}
