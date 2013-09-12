package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class FreeCell
        extends AbstractCell {

    private static final long serialVersionUID = 1L;

    @Override
    public short getMagic() {
        return 0;
    }

    @Override
    public void setMagic(short magic) {
    }

    @Override
    public void readObject2(IDataIn in)
            throws IOException {
        int size = length - 4 - 2;
        in.skip(size);
    }

    @Override
    public void writeObject2(IDataOut out)
            throws IOException {
        int size = length - 4 - 2;
        byte[] zeroBytes = new byte[size];
        out.write(zeroBytes);
    }

}
