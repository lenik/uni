package net.bodz.uni.fmt.regf.t.rec;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.file.RegfCellData;

public class RegfSkRec
        extends RegfCellData
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /** The magic number for this record (should be "sk") */
    public final byte[] magic = new byte[CELL_MAGIC_SIZE];

    /** A 2-byte field of unknown purpose */
    short _unknown;

    /** Offset of the previous SK record in the linked list of SK records */
    public int prevSkOffset;

    /** Offset of the next SK record in the linked list of SK records */
    public int nextSkOffset;

    /** Number of keys referencing this SK record */
    public int refCount;

    /** Size of security descriptor (sec_desc) */
    public int securityDescriptorSize;

    /** The stored Windows security descriptor for this SK record */
    public byte[] securityDescriptor;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.readBytes(magic);
        _unknown = in.readWord();
        prevSkOffset = in.readDword();
        nextSkOffset = in.readDword();
        refCount = in.readDword();
        securityDescriptorSize = in.readDword();
        securityDescriptor = new byte[securityDescriptorSize];
        in.readBytes(securityDescriptor);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        securityDescriptorSize = securityDescriptor.length;

        out.write(magic);
        out.writeDword(_unknown);
        out.writeDword(prevSkOffset);
        out.writeDword(nextSkOffset);
        out.writeDword(refCount);
        out.writeDword(securityDescriptorSize);
        out.write(securityDescriptor);
    }

}
