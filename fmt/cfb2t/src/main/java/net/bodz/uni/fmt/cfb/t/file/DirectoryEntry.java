package net.bodz.uni.fmt.cfb.t.file;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.cfb.t.CfbStruct;
import net.bodz.uni.fmt.cfb.t.Guid;

public class DirectoryEntry
        extends CfbStruct {

    private static final long serialVersionUID = 1L;

    public String entryName;

    /** Type of the entry: */
    public byte objectType;

    public byte colorFlag;
    public int leftSiblingId;
    public int rightSiblingId;

    /** DirID of the root node entry of the red-black tree of all storage members. */
    public int childId;

    /** Unique identifier, if this is a storage (not of interest in the following, may be all 0) */
    public final Guid clsid = new Guid();

    public int flags;

    /**
     * Time stamp of creation of this entry. Most implementations do not write a valid time stamp,
     * but fill up this space with zero bytes.
     */
    public long creationTime;

    /**
     * Time stamp of last modification of this entry. Most implementations do not write a valid time
     * stamp, but fill up this space with zero bytes.
     */
    public long modificationTime;

    /**
     * SecID of first sector or short-sector, if this entry refers to a stream, SecID of first
     * sector of the short-stream container stream , if this is the root storage entry, 0 otherwise
     *
     * The directory entry of a stream contains the SecID of the first sector or short-sector
     * containing the stream data. All streams that are shorter than a specific size given in the
     * header are stored as a short-stream, thus inserted into the short-stream container stream. In
     * this case the SecID specifies the first short-sector inside the short-stream container
     * stream, and the short-sector allocation table is used to build up the SecID chain of the
     * stream.
     */
    public int startingSector;

    /**
     * Total stream size in bytes, if this entry refers to a stream, total size of the short-stream
     * container stream , if this is the root storage entry, 0 otherwise
     */
    public long streamSize;

    public void init() {
        entryName = "";
        leftSiblingId = ID_NONE;
        rightSiblingId = ID_NONE;
        childId = ID_NONE;
        clsid.clear();
        flags = 0;
        modificationTime = creationTime = 0L;
        startingSector = 0;
        streamSize = 0;
    }

    public void initFree() {
        init();
        objectType = OBJ_INVALID;
        colorFlag = COLOR_RED;
        childId = ID_NONE;
    }

    public void initStream() {
        init();
        objectType = OBJ_STREAM;
        colorFlag = COLOR_BLACK;
        childId = ID_NONE;
    }

    public void initStorage() {
        objectType = OBJ_STORAGE;
        colorFlag = COLOR_BLACK;
        clsid.set(CLSID_STORAGE);
    }

    public void initRootDir() {
        entryName = "Root Entry";
        objectType = OBJ_ROOT_STORAGE;
        colorFlag = COLOR_BLACK;
        clsid.set(CLSID_ROOT_DIR);
    }

    @Override
    public int dataSize() {
        return 0x80;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        byte[] buf = new byte[0x40];
        in.readBytes(buf);
        int cb = in.readWord() & 0xFFFF;
        if (cb >= 2)
            cb -= 2; // exclude the \0 terminator.
        entryName = new String(buf, 0, cb, "UTF-16LE");

        objectType = in.readByte();
        colorFlag = in.readByte();
        leftSiblingId = in.readDword();
        rightSiblingId = in.readDword();
        childId = in.readDword();
        clsid.readObject(in);
        flags = in.readDword();
        creationTime = in.readQword();
        modificationTime = in.readQword();
        startingSector = in.readDword();
        streamSize = in.readQword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        byte[] bytes = entryName.getBytes("UTF-16LE");
        int cb = Math.min(bytes.length, 0x40 - 2);
        out.write(bytes, 0, cb);
        for (int i = cb; i < 0x40; i++)
            out.writeByte(0);
        out.writeWord(cb + 2);

        out.writeByte(objectType);
        out.writeByte(colorFlag);
        out.writeDword(leftSiblingId);
        out.writeDword(rightSiblingId);
        out.writeDword(childId);
        clsid.writeObject(out);
        out.writeDword(flags);
        out.writeQword(creationTime);
        out.writeQword(modificationTime);
        out.writeDword(startingSector);
        out.writeQword(streamSize);
    }

}
