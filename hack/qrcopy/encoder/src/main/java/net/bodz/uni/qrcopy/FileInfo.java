package net.bodz.uni.qrcopy;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import net.bodz.bas.c.java.util.DateTimes;
import net.bodz.bas.data.codec.builtin.HexCodec;
import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.err.FormatException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.json.IJsonForm;
import net.bodz.bas.fmt.json.IJsonOut;
import net.bodz.bas.fmt.json.JsonFormOptions;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.json.JsonObject;

public class FileInfo
        extends DataStruct
        implements
            IJsonForm {

    private static final long serialVersionUID = 1L;

    public final BlockHeader blockHeader;

    public EncryptType encryptionType = EncryptType.PLAIN;
    public CompressType compressType = CompressType.NONE;
    public boolean includeFileTime;
    public boolean includeSHA1;
    public boolean includeName;

    public long fileSize;
    public int blockSize;
    public ZonedDateTime fileTime;
    public byte[] sha1; // 160-bit
    private int nameLength;
    private String name;

    public FileInfo() {
        blockHeader = new BlockHeader();
        blockHeader.blockType = BlockType.FILEINFO;
    }

    public FileInfo(BlockHeader blockHeader) {
        if (blockHeader == null)
            throw new NullPointerException("blockHeader");
        this.blockHeader = blockHeader;
    }

    public int getFlags() {
        int flags = (encryptionType.ordinal() << 6) //
                | (compressType.ordinal() << 4);
        if (includeFileTime)
            flags |= 0x08;
        if (includeSHA1)
            flags |= 0x04;
        if (includeName)
            flags |= 0x02;
        return flags;
    }

    public void setFlags(int flags) {
        encryptionType = EncryptType.get((flags >> 6) & 0x3);
        compressType = CompressType.get((flags >> 4) & 0x3);
        includeFileTime = (flags & 0x08) != 0;
        includeSHA1 = (flags & 0x04) != 0;
        includeName = (flags & 0x02) != 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            throw new NullPointerException(" name");
        this.name = name;
        this.nameLength = name.length();
    }

    public ZonedDateTime getFileTime() {
        return fileTime;
    }

    public void setFileTime(ZonedDateTime fileTime) {
        this.fileTime = fileTime;
    }

    public void setFileTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        fileTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public String getFileTimeString() {
        if (fileTime == null)
            return null;
        else
            return DateTimes.ISO8601.format(fileTime);
    }

    public void setFileTime(String s) {
        if (s == null)
            fileTime = null;
        else
            fileTime = ZonedDateTime.parse(s, DateTimes.ISO8601);
    }

    public String getSha1String() {
        if (sha1 == null)
            return null;
        return HexCodec.getInstance().encode(sha1);
    }

    public void setSha1String(String sha1String) {
        byte[] bin = HexCodec.getInstance().decode(sha1String);
        System.arraycopy(bin, 0, sha1, 0, 20);
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException, ParseException {
        int flags = in.readByte() & 0xFF;
        setFlags(flags);

        fileSize = IOUtils.readLongOfSize(blockHeader.sizeType.fileSizeBytes, in);

        blockSize = in.readWord() & 0xFFFF;

        if (includeFileTime)

            setFileTime(in.readQword());

        if (includeSHA1)
            sha1 = in.readBytes(20);

        if (includeName) {
            nameLength = in.read() & 0xFF;
            name = in.readUtf8String(nameLength).string;
        }
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.write(getFlags());

        IOUtils.writeLongOfSize(blockHeader.sizeType.fileSizeBytes, out, fileSize);

        out.writeWord(blockSize);

        if (includeFileTime)
            if (fileTime == null)
                out.writeQword(0);
            else
                out.writeQword(fileTime.toInstant().toEpochMilli());

        if (includeSHA1)
            out.write(sha1, 0, 20);

        if (includeName) {
            int len = nameLength & 0xFF;
            out.writeByte(len);
            out.writeUtf8StringOfLength(len, name);
        }
    }

    private static final String K_BLOCK_HEADER = "blockHeader";
    private static final String K_FLAGS = "flags";
    private static final String K_ENCRYPTION_TYPE = "encryptionType";
    private static final String K_COMPRESS_TYPE = "compressType";
    private static final String K_INCLUDE_FILE_TIME = "includeFileTime";
    private static final String K_INCLUDE_SH_A1 = "includeSHA1";
    private static final String K_INCLUDE_NAME = "includeName";
    private static final String K_FILE_SIZE = "fileSize";
    private static final String K_BLOCK_SIZE = "blockSize";
    private static final String K_FILE_TIME = "fileTime";
    private static final String K_SHA1 = "sha1";
    private static final String K_NAME = "name";

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
        if (o.containsKey(K_BLOCK_HEADER))
            blockHeader.jsonIn(o.getJsonObject(K_BLOCK_HEADER));

        Integer flags = o.getInt(K_FLAGS);
        if (flags != null)
            setFlags(flags.intValue());
        else {
            encryptionType = o.getEnum(EncryptType.class, K_ENCRYPTION_TYPE);
            compressType = o.getEnum(CompressType.class, K_COMPRESS_TYPE);
            includeFileTime = o.getBoolean(K_INCLUDE_FILE_TIME, includeFileTime);
            includeSHA1 = o.getBoolean(K_INCLUDE_SH_A1, includeSHA1);
            includeName = o.getBoolean(K_INCLUDE_NAME, includeName);
        }

        fileSize = o.getLong(K_FILE_SIZE, fileSize);
        blockSize = o.getInt(K_BLOCK_SIZE, blockSize);

        String fileTimeStr = o.getString(K_FILE_TIME);
        if (fileTimeStr != null)
            setFileTime(fileTimeStr);

        String sha1Str = o.getString(K_SHA1);
        if (sha1Str != null)
            setSha1String(sha1Str);

        setName(o.getString(K_NAME));
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
        out.entryNotNull(K_BLOCK_HEADER, blockHeader);

        out.entry(K_FLAGS, getFlags());
        out.entryNotNull(K_ENCRYPTION_TYPE, encryptionType);
        out.entryNotNull(K_COMPRESS_TYPE, compressType);
        out.entry(K_INCLUDE_FILE_TIME, includeFileTime);
        out.entry(K_INCLUDE_SH_A1, includeSHA1);
        out.entry(K_INCLUDE_NAME, includeName);

        out.entry(K_FILE_SIZE, fileSize);
        out.entry(K_BLOCK_SIZE, blockSize);
        out.entry(K_FILE_TIME, getFileTimeString());
        out.entryNotNull(K_SHA1, getSha1String());
        out.entryNotNull(K_NAME, getName());
    }

}
