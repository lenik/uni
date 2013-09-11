package net.bodz.uni.fmt.regf.t.rec;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.IRegfConsts;

public class RegfSkRec
        extends DataStruct
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /** The magic number for this record (should be "sk") */
    public byte[] magic = new byte[CELL_MAGIC_SIZE];

    /** ((start_offset - end_offset) & 0xfffffff8) */
    public int cellSize;

    /** The stored Windows security descriptor for this SK record */
    public byte[] securityDescriptor;

    /** Offset of this record from beginning of this hbin block */
    public int hbinOffset;

    /** Offset of the previous SK record in the linked list of SK records */
    public int prevSkOffset;

    /** Offset of the next SK record in the linked list of SK records */
    public int nextSkOffset;

    /** Number of keys referencing this SK record */
    public int refCount;

    /** Size of security descriptor (sec_desc) */
    public int securityDescriptorSize;

    /* XXX: A 2-byte field of unknown purpose */
    public int unknownTtag;

    /** Real file offset of this record */
    public transient int fileOffset;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
