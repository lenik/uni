package net.bodz.uni.fmt.regf.t;

public class RegfHbin
        implements IRegfConsts {

    /** Offset of this HBIN in the registry file */
    int fileOffset;

    /** Number of active records pointing to this block (not used currently) */
    int refCount;

    /** Offset from first hbin block */
    int firstHbinOffset;

    /** Block size of this block Should be a multiple of 4096 (0x1000) */
    int blockSize;

    /**
     * Relative offset to next block.
     *
     * @note This value may be unreliable!
     */
    int nextBlock;

    /** Magic number for the HBIN (should be "hbin"). */
    byte[] magic = new byte[HBIN_MAGIC_SIZE];

}
