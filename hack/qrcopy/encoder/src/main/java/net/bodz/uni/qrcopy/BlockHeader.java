package net.bodz.uni.qrcopy;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.err.FormatException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.json.IJsonForm;
import net.bodz.bas.fmt.json.IJsonOut;
import net.bodz.bas.fmt.json.JsonFormOptions;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.json.JsonObject;

public class BlockHeader
        extends DataStruct
        implements
            IJsonForm {

    private static final long serialVersionUID = 1L;

    public SizeType sizeType = SizeType.LONG;
    public BlockType blockType = BlockType.DATA;
    public boolean hasChecksum = true; // override SizeType.checksumBytes

    public int blockIndex;
    public int blockChecksum;

    public int getFlags() {
        int flags = (sizeType.ordinal() << 6) //
                | (blockType.ordinal() << 4);
        if (hasChecksum)
            flags |= 0x08;
        return flags;
    }

    public void setFlags(int flags) {
        sizeType = SizeType.get((flags >> 6) & 0x3);
        blockType = BlockType.get((flags >> 4) & 0x3);
        hasChecksum = (flags & 0x08) != 0;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException, ParseException {
        int flags = in.read();
        setFlags(flags);

        blockIndex = (int) IOUtils.readLongOfSize(sizeType.indexBytes, in);

        if (hasChecksum)
            blockChecksum = (int) IOUtils.readLongOfSize(sizeType.checksumBytes, in);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.writeByte(getFlags());

        IOUtils.writeLongOfSize(sizeType.indexBytes, out, blockIndex);
        IOUtils.writeLongOfSize(sizeType.checksumBytes, out, blockChecksum);
    }

    private static final String K_SIZE_TYPE = "sizeType";
    private static final String K_BLOCK_TYPE = "blockType";
    private static final String K_HAS_CHECKSUM = "hasChecksum";
    private static final String K_BLOCK_INDEX = "blockIndex";
    private static final String K_BLOCK_CHECKSUM = "blockChecksum";

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        sizeType = o.getEnum(SizeType.class, K_SIZE_TYPE);
        blockType = o.getEnum(BlockType.class, K_BLOCK_TYPE);
        hasChecksum = o.getBoolean(K_HAS_CHECKSUM, hasChecksum);
        blockIndex = o.getInt(K_BLOCK_INDEX, blockIndex);
        blockChecksum = o.getInt(K_BLOCK_CHECKSUM, blockChecksum);
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        out.entryNotNull(K_SIZE_TYPE, sizeType);
        out.entryNotNull(K_BLOCK_TYPE, blockType);
        out.entry(K_HAS_CHECKSUM, hasChecksum);
        out.entry(K_BLOCK_INDEX, blockIndex);
        out.entry(K_BLOCK_CHECKSUM, blockChecksum);
    }

}
