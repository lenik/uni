package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class RawDataCell
        extends AbstractCell {

    private static final long serialVersionUID = 1L;

    String rawhdr;
    public byte[] data;

    @Override
    public void setLength(int length) {
        super.setLength(length);
        int size = length - 4;
        data = new byte[size];
    }

    @Override
    public short getMagic() {
        int a = data[0] & 0xff;
        int b = data[0] & 0xff;
        return (short) ((b << 8) | a);
    }

    @Override
    public void setMagic(short magic) {
        int a = magic & 0xff;
        int b = (magic >> 8) & 0xff;
        data[0] = (byte) a;
        data[1] = (byte) b;

        this.rawhdr = new String(new char[] { (char) a, (char) b });
    }

    @Override
    public void readObject2(IDataIn in)
            throws IOException {
        int remaining = length - 4 - 2;
        in.readBytes(data, 2, remaining);
    }

    @Override
    public void writeObject2(IDataOut out)
            throws IOException {
        int remaining = length - 4 - 2;
        out.write(data, 2, remaining);
    }

}
