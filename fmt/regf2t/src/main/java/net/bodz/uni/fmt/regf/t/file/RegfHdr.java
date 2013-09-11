package net.bodz.uni.fmt.regf.t.file;

import java.io.IOException;

import net.bodz.bas.data.struct.RstDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.io.StringFlags;
import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.NtTime;

/**
 * "regf" is obviously the abbreviation for "Registry file". "regf" is the signature of the
 * header-block which is always 4kb in size, although only the first 64 bytes seem to be used and a
 * checksum is calculated over the first 0x200 bytes only!
 */
public class RegfHdr
        extends RstDataStruct
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /** 'regf': 114, 101, 103, 102 */
    public final byte[] REGF_ID = new byte[4];

    public int sequence1;
    public int sequence2;
    public final NtTime mtime = new NtTime();
    public final RegfVersion version = new RegfVersion();

    /** Unverified. Set to 0 in all known hives */
    int _type; // = 0

    /** : Unverified. Set to 1 in all known hives */
    int _format; // = 1

    /** Offset to root cell in the first (or any?) hbin block */
    public int rootCellOffset;

    /** Offset to last hbin block in file */
    public int lastBlockOffset;

    /** Unverified. Set to 1 in all known hives */
    int _cluster;

    /** Matches hive's base file name. Stored in UTF-16LE */
    public final char fileName[] = new char[REGF_NAME_SIZE];

    final int[] _rmId = new int[4];
    final int[] _logId = new int[4];
    int _flags;
    final int[] _tmId = new int[4];
    int _guidSignature;

    /** Stored checksum from file */
    public int checksum;

    final int[] _thawTmId = new int[4];
    final int[] _thawRmId = new int[4];
    final int[] _thawLogId = new int[4];
    int _bootType;
    int _bootRecover;

    /**
     * This seems to include random junk. Possibly unsanitized memory left over from when header
     * block was written. For instance, chunks of nk records can be found, though often it's all 0s.
     */
    final byte[] _reserved1 = new byte[REGF_RESERVED1_SIZE];

    /**
     * This is likely reserved and unusued currently. (Should be all 0s.) Included here for easier
     * access in looking for hidden data or doing research.
     */
    final byte[] _reserved2 = new byte[REGF_RESERVED2_SIZE];

    /** Our own calculation of the checksum, XOR of bytes 0-1FBh */
    public int computedChecksum;

    @Override
    public int sizeof() {
        return 4096;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.read(REGF_ID);
        sequence1 = in.readDword();
        sequence2 = in.readDword();
        mtime.readObject(in);
        version.readObject(in);
        _type = in.readDword();
        _format = in.readDword();
        rootCellOffset = in.readDword();
        lastBlockOffset = in.readDword();
        _cluster = in.readDword();

        in.readChars(StringFlags._16BIT, fileName);

        in.readDwords(_rmId);
        in.readDwords(_logId);
        _flags = in.readDword();
        in.readDwords(_tmId);
        _guidSignature = in.readDword();

        in.readBytes(_reserved1);
        checksum = in.readDword();
        in.readBytes(_reserved2);

        in.readDwords(_thawTmId);
        in.readDwords(_thawRmId);
        in.readDwords(_thawLogId);

        _bootType = in.readDword();
        _bootRecover = in.readDword();

        computeChecksum();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.write(REGF_ID);
        out.writeDword(sequence1);
        out.writeDword(sequence2);
        mtime.writeObject(out);
        version.writeObject(out);
        out.writeDword(_type);
        out.writeDword(_format);
        out.writeDword(rootCellOffset);
        out.writeDword(lastBlockOffset);
        out.writeDword(_cluster);

        out.writeChars(StringFlags._16BIT, fileName);

        out.writeDwords(_rmId);
        out.writeDwords(_logId);
        out.writeDword(_flags);
        out.writeDwords(_tmId);
        out.writeDword(_guidSignature);

        out.write(_reserved1);
        out.writeDword(checksum);
        out.write(_reserved2);

        out.writeDwords(_thawTmId);
        out.writeDwords(_thawRmId);
        out.writeDwords(_thawLogId);

        out.writeDword(_bootType);
        out.writeDword(_bootRecover);
    }

    void computeChecksum()
            throws IOException {
        int[] ints = new int[0x1FC / 4];
        transfer(FORMAT_LE).readDwords(ints);
        computedChecksum = 0;
        for (int i = 0; i < ints.length; i++)
            computedChecksum ^= ints[i];
    }

}
