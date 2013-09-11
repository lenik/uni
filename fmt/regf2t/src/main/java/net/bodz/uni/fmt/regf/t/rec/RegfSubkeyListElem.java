package net.bodz.uni.fmt.regf.t.rec;

/**
 * Subkey List -- list of key offsets and hashed names for consistency
 */
public class RegfSubkeyListElem {

    /**
     * Virtual offset of NK record or additional subkey list, depending on this list's type.
     */
    int nkOffset;

    int hash;

}
