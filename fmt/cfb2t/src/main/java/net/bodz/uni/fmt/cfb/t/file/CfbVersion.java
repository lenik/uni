package net.bodz.uni.fmt.cfb.t.file;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.cfb.t.CfbStruct;

/**
 * Registry version number
 * <ul>
 * <li>1.2 for WinNT 3.51
 * <li>1.3 for WinNT 4
 * <li>1.5 for WinXP
 * </ul>
 *
 * <pre>
 * [noprint] struct regf_version {
 *     [value(1)] uint32 major;
 *     uint32 minor;
 * };
 * </pre>
 */
public class CfbVersion
        extends CfbStruct {

    private static final long serialVersionUID = 1L;

    /** Set to 1 in all known hives */
    public int major; // = 1

    /** Set to 3 or 5 in all known hives */
    public int minor;

    @Override
    public int dataSize() {
        return 8;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        major = in.readDword();
        minor = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.writeDword(major);
        out.writeDword(minor);
    }

}
