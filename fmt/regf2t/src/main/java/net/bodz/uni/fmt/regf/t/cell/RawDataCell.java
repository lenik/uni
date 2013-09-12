package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class RawDataCell
        extends AbstractCell {

    private static final long serialVersionUID = 1L;

    /**
     * 启发式
     */
    static boolean heuristicalDiscover = true;

    public byte[] data;
    String hdType;
    String hdValue;

    @Override
    public void size(int size) {
        super.size(size);
        int dataSize = size - 4;
        data = new byte[dataSize];
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
    }

    @Override
    public void readObject2(IDataIn in)
            throws IOException {
        int remaining = size - 4 - 2;
        in.readBytes(data, 2, remaining);

        if (!heuristicalDiscover)
            return;

        hdType = null;
        hdValue = null;

        int dataSize = size - 4;
        if (dataSize < 4)
            return;

        switch (dataSize) {
        case 2:
            hdType = "short";
            hdValue = "" + getMagic();
            break;

        default:
            if (dataSize > 4) {
                // char/wchar_t *
                int printableAscii = 0;
                int high0 = 0;
                int low0 = 0;
                for (int i = 0; i < dataSize; i++) {
                    int b = data[i];
                    if (b >= 0x20 && b <= 0x7f)
                        printableAscii++;
                    if (b == 0)
                        if (i % 2 == 0)
                            low0++;
                        else
                            high0++;
                }
                if (dataSize % 2 == 0 && 200 * high0 / dataSize > 90 && 200 * low0 / dataSize < 10) {
                    hdType = "UTF-16LE";
                    hdValue = new String(data, "utf-16le");
                }

                else if (100 * printableAscii / dataSize > 95) {
                    hdType = "ASCII";
                    hdValue = new String(data, "ascii");
                }
            }

            if (hdType == null && dataSize % 4 == 0 && dataSize <= 16) {
                // int32[n]
                int n = dataSize / 4;
                hdType = "int";
                if (n > 1)
                    hdType += "[" + n + "]";

                hdValue = "";
                for (int j = 0; j < n; j++) {
                    int dw = 0;
                    for (int i = 0; i < 4; i++) {
                        dw = dw << 8;
                        dw += data[j * 4 + i] & 0xff;
                    }
                    hdValue += dw + " ";
                }
                hdValue = hdValue.trim();
            } // if size % 4
        } // switch size
    }

    @Override
    public void writeObject2(IDataOut out)
            throws IOException {
        int remaining = size - 4 - 2;
        out.write(data, 2, remaining);
    }

}
