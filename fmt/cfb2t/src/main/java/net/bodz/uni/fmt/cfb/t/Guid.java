package net.bodz.uni.fmt.cfb.t;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class Guid
        extends CfbStruct {

    private static final long serialVersionUID = 1L;

    public long a;
    public int b;
    public int c;

    public Guid() {
    }

    public Guid(long a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public Guid clone() {
        return new Guid(a, b, c);
    }

    public void clear() {
        a = 0L;
        b = c = 0;
    }

    public void set(Guid o) {
        a = o.a;
        b = o.b;
        c = o.c;
    }

    @Override
    public int dataSize() {
        return 16;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        a = in.readQword();
        b = in.readDword();
        c = in.readDword();
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        out.writeDword(c);
        out.writeDword(b);
        out.writeQword(a);
    }

}
