package net.bodz.uni.fmt.regf.t.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bodz.bas.data.struct.RstDataStruct;
import net.bodz.bas.io.BByteIn;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.io.data.DataInImplLE;
import net.bodz.bas.t.Cc2Typer;
import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.InvalidMagicException;
import net.bodz.uni.fmt.regf.t.cell.*;

public class RegfHbin
        extends RstDataStruct
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    static final boolean parseData = true;

    static final byte[] magic_hbin = { 'h', 'b', 'i', 'n', };

    /** Magic number for the HBIN (should be "hbin"). */
    public byte[] magic = new byte[HBIN_MAGIC_SIZE];

    /** Offset from first hbin block */
    public int firstHbinOffset;

    /** Block size of this block Should be a multiple of 4096 (0x1000) */
    public int blockSize;

    int _unknown1;
    int _unknown2;
    public long mtime;

    /**
     * Relative offset to next block.
     *
     * This should be the same thing as hbin->block_size but just in case.
     *
     * WARNING: This value may be unreliable!
     */
    public int nextBlock;

    transient byte[] data;
    public final List<AbstractCell> cells = new ArrayList<>();

    /** Offset of this HBIN in the registry file */
    public transient int fileOffset;

    @Override
    public int sizeof() {
        // block hdr size = 32
        return blockSize;
    }

    static final Cc2Typer CC2_TYPER = new Cc2Typer();

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
        mtime = in.readQword();

        nextBlock = in.readDword();

        int remaining = blockSize - HBIN_HEADER_SIZE;
        data = new byte[remaining];
        in.readBytes(data);
        if (!parseData)
            return;

        cells.clear();
        in = DataInImplLE.from(new BByteIn(data));

        while (remaining > 0) {
            int cellLength = in.readDword();
            boolean cellAllocated = cellLength < 0;
            if (cellAllocated)
                cellLength = -cellLength;

            byte[] cellBytes = new byte[cellLength - 4];
            in.readBytes(cellBytes);
            IDataIn _in = DataInImplLE.from(new BByteIn(cellBytes));

            AbstractCell cell = null;
            short magic = _in.readWord();

            switch (cellAllocated ? magic : 0) {
            case 0:
                cell = new FreeCell();
                break;

            case MAGIC_NK:
                cell = new KeyCell();
                break;

            case MAGIC_SK:
                cell = new SecurityCell();
                break;

            case MAGIC_VK:
                cell = new ValueCell();
                break;

            case MAGIC_LF:
            case MAGIC_LH:
                cell = new SubkeyListCell();
                break;

            case MAGIC_RI:
            case MAGIC_LI:
                cell = new SubkeyListCell();
                break;

            default: // Py len=8
                cell = new RawDataCell();
                break;
            }

            cell.setLength(cellLength);
            cell.allocated = cellAllocated;

            cell.setMagic(magic);
            cell.readObject2(_in);

            cells.add(cell);
            remaining -= cellLength;
        } // while hbinRemaining > 0
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.write(magic);
        out.writeDword(firstHbinOffset);
        out.writeDword(blockSize);

        out.writeDword(_unknown1);
        out.writeDword(_unknown2);
        out.writeQword(mtime);

        out.writeDword(nextBlock);
    }

}
