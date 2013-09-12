package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;

import net.bodz.bas.data.address.IAddressedObjectManager;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.IRegfConsts;

public class SecurityCell
        extends AbstractCell
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /** Magic number of key */
    public short magic;

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
    public short getMagic() {
        return magic;
    }

    @Override
    public void setMagic(short magic) {
        if (magic != MAGIC_SK)
            throw new IllegalArgumentException("Bad magic: " + magic);
        this.magic = magic;
    }

    @Override
    public void readObject2(IDataIn in)
            throws IOException {
        _unknown = in.readWord();
        prevSkOffset = in.readDword();
        nextSkOffset = in.readDword();
        refCount = in.readDword();
        securityDescriptorSize = in.readDword();
        securityDescriptor = new byte[securityDescriptorSize];
        in.readBytes(securityDescriptor);
    }

    @Override
    public void writeObject2(IDataOut out)
            throws IOException {
        securityDescriptorSize = securityDescriptor.length;

        out.writeDword(_unknown);
        out.writeDword(prevSkOffset);
        out.writeDword(nextSkOffset);
        out.writeDword(refCount);
        out.writeDword(securityDescriptorSize);
        out.write(securityDescriptor);
    }

    @Override
    public void afterAddressSet(IAddressedObjectManager<AbstractCell> manager) {
        SecurityCell prevSk = (SecurityCell) manager.get(prevSkOffset);
        SecurityCell nextSk = (SecurityCell) manager.get(nextSkOffset);
    }

}
