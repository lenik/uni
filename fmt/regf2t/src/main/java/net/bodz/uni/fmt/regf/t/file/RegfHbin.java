package net.bodz.uni.fmt.regf.t.file;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.NtTime;

public class RegfHbin
        extends DataStruct
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /** Magic number for the HBIN (should be "hbin"). */
    public byte[] magic = new byte[HBIN_MAGIC_SIZE];

    /** Offset from first hbin block */
    public int firstHbinOffset;

    /** Block size of this block Should be a multiple of 4096 (0x1000) */
    public int blockSize;

    int _unknown1;
    int _unknown2;
    public final NtTime mtime = new NtTime();

    /**
     * Relative offset to next block.
     *
     * This should be the same thing as hbin->block_size but just in case.
     *
     * WARNING: This value may be unreliable!
     */
    public int nextBlock;

    /** Offset of this HBIN in the registry file */
    public transient int fileOffset;
    public transient byte[] data;

    @Override
    public int sizeof() {
        return 32;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.readBytes(magic);
        firstHbinOffset = in.readDword();
        blockSize = in.readDword();
        assert (blockSize & 0xFFF) == 0;

        _unknown1 = in.readDword();
        _unknown2 = in.readDword();
        mtime.readObject(in);

        nextBlock = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.write(magic);
        out.writeDword(firstHbinOffset);
        out.writeDword(blockSize);

        out.writeDword(_unknown1);
        out.writeDword(_unknown2);
        mtime.writeObject(out);

        out.writeDword(nextBlock);
    }

}
