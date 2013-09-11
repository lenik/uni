package net.bodz.uni.fmt.regf.t.file;

import java.io.IOException;
import java.util.Arrays;

import net.bodz.bas.data.struct.RstDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.InvalidMagicException;
import net.bodz.uni.fmt.regf.t.NtTime;

public class RegfHbin
        extends RstDataStruct
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    static final byte[] magic_hbin = { 'h', 'b', 'i', 'n', };

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

    public byte[] data;

    /** Offset of this HBIN in the registry file */
    public transient int fileOffset;

    @Override
    public int sizeof() {
        // block hdr size = 32
        return blockSize;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.readBytes(magic);
        if (!Arrays.equals(magic, magic_hbin))
            throw new InvalidMagicException(magic_hbin, magic, HBIN_ALLOC - HBIN_MAGIC_SIZE);

        firstHbinOffset = in.readDword();
        blockSize = in.readDword();
        if ((blockSize & 0xFFF) != 0)
            throw new IllegalArgumentException("Block is unaligned: " + blockSize);
        if (blockSize == 0)
            throw new IllegalArgumentException("Block size is 0.");

        _unknown1 = in.readDword();
        _unknown2 = in.readDword();
        mtime.readObject(in);

        nextBlock = in.readDword();

        if (blockSize == 0)
            data = new byte[0];
        else {
            int dataSize = blockSize - HBIN_HEADER_SIZE;
            data = new byte[dataSize];
            in.readBytes(data);
        }
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
