package net.bodz.uni.fmt.regf.t;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.text.rst.IRstSerializable;

/**
 * <pre>
 * #define TIME_T_MIN ((time_t)0 < (time_t) -1 ? (time_t) 0 \
 *                     : ~ (time_t) 0 << (sizeof (time_t) * CHAR_BIT - 1))
 * #define TIME_T_MAX (~ (time_t) 0 - TIME_T_MIN)
 * #define TIME_FIXUP_CONSTANT (369.0*365.25*24*60*60-(3.0*24*60*60+6.0*60*60))
 * </pre>
 */
public class NtTime
        extends DataStruct
        implements IRstSerializable {

    private static final long serialVersionUID = 1L;

    public int low;
    public int high;

    @Override
    public int sizeof() {
        return 8;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        low = in.readDword();
        high = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.writeDword(low);
        out.writeDword(high);
    }

}
