package net.bodz.uni.qrcopy;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;

public class IOUtils {

    public static long readLongOfSize(int sizeInBytes, IDataIn in)
            throws IOException {
        long val = 0;
        switch (sizeInBytes) {
        case 1:
            val = in.readByte() & 0xFF;
            break;
        case 2:
            val = in.readWord() & 0xFFFF;
            break;
        case 4:
            val = in.readDword() & 0xFFFF_FFFF;
            break;
        case 8:
            val = in.readQword();
            break;
        }
        return val;
    }

    public static void writeLongOfSize(int sizeInBytes, IDataOut out, long val)
            throws IOException {
        switch (sizeInBytes) {
        case 1:
            out.writeByte((byte) val);
            break;
        case 2:
            out.writeWord((short) val);
            break;
        case 4:
            out.writeDword((int) val);
            break;
        case 8:
            out.writeQword(val);
            break;
        }
    }

}
