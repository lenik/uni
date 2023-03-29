package net.bodz.uni.fmt.cfb.t.file;

import java.io.IOException;
import java.util.Arrays;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.cfb.t.CfbStruct;
import net.bodz.uni.fmt.cfb.t.Guid;
import net.bodz.uni.fmt.cfb.t.ICfbConsts;

public class CfbHdr
        extends CfbStruct
        implements ICfbConsts {

    private static final long serialVersionUID = 1L;

    public long SIGNATURE = CFB_SIGNATURE;

    /** reserved must be zero (WriteClassStg/GetClassFile uses root directory class id) */
    public final Guid clsid = new Guid();

    /** minor version of the format: 33 is written by reference implementation */
    public short minorVersion = 0x003E;

    /** major version of the dll/format: 3 for 512-byte sectors, 4 for 4 KB sectors */
    public short majorVersion = 3;

    /** 0xFFFE: indicates Intel byte-ordering */
    public short byteOrder = LITTLE_ENDIAN;

    /** size of sectors in power-of-two; // typically 9 indicating 512-byte sectors */
    public short sectorSizeShift = 9;

    /** size of mini-sectors in power-of-two; typically 6 indicating 64-byte mini-sectors */
    public short miniStreamSectorSizeShift = 6;

    short reserved1;
    int reserved2;

    /**
     * must be zero for 512-byte sectors, number of SECTs in directory chain for 4 KB sectors
     *
     * @version 4
     */
    public int nsectDir = 0;

    /** Total number of sectors used for the sector allocation table */
    public int nsectSAT = 1;

    /** Directory Starting Sector Location (sector #1 for Directory) */
    public int dirStartSector = 1;

    /**
     * signature used for transactions; must be zero. The reference implementation does not support
     * transactions
     */
    public int transactionSignature = 0;

    /** maximum size for a mini stream; typically 4096 bytes */
    public int miniStreamSizeCutoff = 4096;

    /** Mini SAT Starting Sector Location (sector #2 for Mini SAT) */
    public int miniSATStartSector = 2;

    /** 1 Mini SAT sector */
    public int nsectMiniSAT = 1;

    /** MSAT Start Sector Location */
    public int MSATStartSector = ENDOFCHAIN;

    /** 0: no MSAT, less than 7MB */
    public int nsectMSAT = 0;

    /**
     * The header contains the entire MSAT if there are no more than 109 SAT sectores.
     */
    public final int MSAT[] = new int[109];

    public CfbHdr() {
        Arrays.fill(MSAT, FREESECT);
        MSAT[0] = 0;
    }

    public long sectorPosition(int sect) {
        long bs = 1 << sectorSizeShift;
        return 512 + bs * sect;
    }

    public long miniSectorPosition(int sect) {
        long bs = 1 << miniStreamSectorSizeShift;
        return bs * sect;
    }

    @Override
    public int dataSize() {
        return 512;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        SIGNATURE = in.readQword();
        clsid.readObject(in);
        minorVersion = in.readWord();
        majorVersion = in.readWord();
        byteOrder = in.readWord();
        sectorSizeShift = in.readWord();
        miniStreamSectorSizeShift = in.readWord();
        reserved1 = in.readWord();
        reserved2 = in.readDword();
        nsectDir = in.readDword();
        nsectSAT = in.readDword();
        dirStartSector = in.readDword();
        transactionSignature = in.readDword();
        miniStreamSizeCutoff = in.readDword();
        miniSATStartSector = in.readDword();
        nsectMiniSAT = in.readDword();
        MSATStartSector = in.readDword();
        nsectMSAT = in.readDword();
        in.readDwords(MSAT);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.writeQword(SIGNATURE);
        clsid.writeObject(out);
        out.writeWord(minorVersion);
        out.writeWord(majorVersion);
        out.writeWord(byteOrder);
        out.writeWord(sectorSizeShift);
        out.writeWord(miniStreamSectorSizeShift);
        out.writeWord(reserved1);
        out.writeDword(reserved2);
        out.writeDword(dirStartSector);
        out.writeDword(transactionSignature);
        out.writeDword(miniStreamSizeCutoff);
        out.writeDword(miniSATStartSector);
        out.writeDword(nsectMiniSAT);
        out.writeDword(MSATStartSector);
        out.writeDword(nsectMSAT);
        out.writeDwords(MSAT);
    }

}
