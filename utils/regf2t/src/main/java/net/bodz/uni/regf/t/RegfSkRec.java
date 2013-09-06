package net.bodz.uni.regf.t;

public class RegfSkRec
        implements IRegfConsts {

    /** Real file offset of this record */
    int offset;

    /** ((start_offset - end_offset) & 0xfffffff8) */
    int cellSize;

    /** The stored Windows security descriptor for this SK record */
    byte[] securityDescriptor;

    /** Offset of this record from beginning of this hbin block */
    int hbinOffset;

    /** Offset of the previous SK record in the linked list of SK records */
    int prevSkOffset;

    /** Offset of the next SK record in the linked list of SK records */
    int nextSkOffset;

    /** Number of keys referencing this SK record */
    int refCount;

    /** Size of security descriptor (sec_desc) */
    int securityDescriptorSize;

    /* XXX: A 2-byte field of unknown purpose */
    int unknownTtag;

    /** The magic number for this record (should be "sk") */
    byte[] magic = new byte[CELL_MAGIC_SIZE];

}
