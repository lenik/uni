package net.bodz.uni.pgputils;

import net.bodz.bas.c.java.util.zip.AbstractZipChecksum;
import net.bodz.bas.data.mem.types.Int32BE;
import net.bodz.bas.data.mem.types.Int32LE;

public class PgpCrc32Checksum
        extends AbstractZipChecksum
        implements IIntKey {

    public static final int STD = 0x04c11db7;
    private static final int INIT = 0;

    private int key = STD;
    private int crc;

    public PgpCrc32Checksum() {
        this.crc = INIT;
    }

    public PgpCrc32Checksum(int crc) {
        this.crc = crc;
    }

    @Override
    public PgpCrc32Checksum clone() {
        PgpCrc32Checksum copy = new PgpCrc32Checksum(crc);
        copy.key = key;
        return copy;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public final void reset() {
        this.crc = INIT;
    }

    @Override
    public final void update(int b) {
        assert b >= 0;
        crc ^= b << 8;
        for (int bit = 0; bit < 8; bit++) {
            int check = crc;
            crc <<= 1;
            if (check < 0)
                crc ^= key;
        }
    }

    @Override
    public long getValue() {
        int x = crc; // ^ 0xffffffff;
        return x & 0xffffffffl;
    }

    public byte[] getBytesLE() {
        byte[] buf = new byte[4];
        Int32LE.write(buf, (int) getValue());
        return buf;
    }

    public byte[] getBytesBE() {
        byte[] buf = new byte[4];
        Int32BE.write(buf, (int) getValue());
        return buf;
    }

}
