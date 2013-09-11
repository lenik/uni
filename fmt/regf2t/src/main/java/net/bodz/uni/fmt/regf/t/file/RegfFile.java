package net.bodz.uni.fmt.regf.t.file;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class RegfFile
        extends DataStruct {

    private static final long serialVersionUID = 1L;

    public final RegfHdr hdr = new RegfHdr();

    public transient int fileLength;
//    RangeList hbins;
//    lruCache skCache;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        hdr.readObject(in);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        hdr.writeObject(out);
    }

}
