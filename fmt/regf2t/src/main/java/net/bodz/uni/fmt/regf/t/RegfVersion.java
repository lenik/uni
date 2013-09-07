package net.bodz.uni.fmt.regf.t;

import java.io.IOException;

import net.bodz.bas.data.struct.SfsDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

/**
 * Registry version number
 * <ul>
 * <li>1.2.0.1 for WinNT 3.51
 * <li>1.3.0.1 for WinNT 4
 * <li>1.5.0.1 for WinXP
 * </ul>
 *
 * <pre>
 * [noprint] struct regf_version {
 *     [value(1)] uint32 major;
 *     uint32 minor;
 *     [value(0)] uint32 release;
 *     [value(1)] uint32 build;
 * };
 * </pre>
 */
public class RegfVersion
        extends SfsDataStruct {

    private static final long serialVersionUID = 1L;

    public int major;
    public int minor;
    public int release;
    public int build;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        major = in.readDword();
        minor = in.readDword();
        release = in.readDword();
        build = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.writeDword(major);
        out.writeDword(minor);
        out.writeDword(release);
        out.writeDword(build);
    }

}
