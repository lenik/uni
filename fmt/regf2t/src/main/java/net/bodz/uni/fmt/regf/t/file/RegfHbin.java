package net.bodz.uni.fmt.regf.t.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bodz.bas.io.BByteIn;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.io.data.DataInImplLE;
import net.bodz.bas.t.Cc2Typer;
import net.bodz.uni.fmt.regf.t.InvalidMagicException;
import net.bodz.uni.fmt.regf.t.RegfStruct;
import net.bodz.uni.fmt.regf.t.cell.*;

public class RegfHbin
        extends RegfStruct
        implements IAddressed {

    private static final long serialVersionUID = 1L;

    static final boolean parseData = true;

    static final byte[] magic_hbin = { 'h', 'b', 'i', 'n', };

    /** Magic number for the HBIN (should be "hbin"). */
    public byte[] magic = new byte[HBIN_MAGIC_SIZE];

    /** Offset from first hbin block */
    public int address;

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

    @Override
    public int address() {
        return address;
    }

    @Override
    public int size() {
        // int dataSize = blockSize - HBIN_HEADER_SIZE;
        return blockSize;
    }

    static final Cc2Typer CC2_TYPER = new Cc2Typer();

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.readBytes(magic);
        if (!Arrays.equals(magic, magic_hbin))
            throw new InvalidMagicException(magic_hbin, magic, HBIN_ALLOC - HBIN_MAGIC_SIZE);

        address = in.readDword();
        blockSize = in.readDword();
        if ((blockSize & 0xFFF) != 0)
            throw new IllegalArgumentException("Block is unaligned: " + blockSize);
        if (blockSize == 0)
            throw new IllegalArgumentException("Block size is 0.");

        _unknown1 = in.readDword();
        _unknown2 = in.readDword();
        mtime = in.readQword();

        nextBlock = in.readDword();

        int offset = address + HBIN_HEADER_SIZE;
        int remaining = blockSize - HBIN_HEADER_SIZE;
        data = new byte[remaining];
        in.readBytes(data);
        if (!parseData)
            return;

        cells.clear();
        in = DataInImplLE.from(new BByteIn(data));

        while (remaining > 0) {
            int cellSize = in.readDword();
            boolean cellAllocated = cellSize < 0;
            if (cellAllocated)
                cellSize = -cellSize;

            byte[] cellData = new byte[cellSize - 4];
            in.readBytes(cellData);
            IDataIn _in = DataInImplLE.from(new BByteIn(cellData));

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

            cell.address(offset);
            cell.size(cellSize);
            cell.allocated = cellAllocated;

            cell.setMagic(magic);
            cell.readObject2(_in);

            cells.add(cell);

            offset += cellSize;
            remaining -= cellSize;
        } // while remaining > 0
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.write(magic);
        out.writeDword(address);
        out.writeDword(blockSize);

        out.writeDword(_unknown1);
        out.writeDword(_unknown2);
        out.writeQword(mtime);

        out.writeDword(nextBlock);
    }

}
