package net.bodz.uni.fmt.regf.t;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.text.rst.IRstSerializable;

/**
 * "regf" is obviously the abbreviation for "Registry file". "regf" is the signature of the
 * header-block which is always 4kb in size, although only the first 64 bytes seem to be used and a
 * checksum is calculated over the first 0x200 bytes only!
 */
public class RegfHdr
        extends DataStruct
        implements IRstSerializable {

    private static final long serialVersionUID = 1L;

    /** 'regf' */
    public final byte[] REGF_ID = new byte[4];

    public int updateCounter1;
    public int updateCounter2;
    public NtTime modtime;

    public final RegfVersion version = new RegfVersion();
    public int dataOffset;
    public int lastBlock;

    /** 1 */
    public int uk7;

    /** charset(UTF16) - char[0x20] */
    public String description;

    public int[] padding = new int[99];

    /** Checksum of first 0x200 bytes XOR-ed */
    public int chksum;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.read(REGF_ID);
        updateCounter1 = in.readDword();
        updateCounter2 = in.readDword();
        modtime.readObject(in);
        version.readObject(in);
        dataOffset = in.readDword();
        lastBlock = in.readDword();
        uk7 = in.readDword();

        byte buf[] = new byte[0x20 * 2];
        in.readFully(buf);
        description = new String(buf, "utf16-le").trim();

        byte _pad[] = new byte[99 * 4];
        in.readFully(_pad);
        // padding = _pad;

        chksum = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.write(REGF_ID);
        out.writeDword(updateCounter1);
        out.writeDword(updateCounter2);
        modtime.writeObject(out);
        version.writeObject(out);
        out.writeDword(dataOffset);
        out.writeDword(lastBlock);
        out.writeDword(uk7);

        byte[] buf = description.getBytes("utf16-le");
        byte[] buf20 = new byte[0x20 * 2];
        System.arraycopy(buf, 0, buf20, 0, Math.min(0x20, buf.length));
        out.write(buf20);

        // out.write(_pad);

        out.writeDword(chksum);
    }

}
