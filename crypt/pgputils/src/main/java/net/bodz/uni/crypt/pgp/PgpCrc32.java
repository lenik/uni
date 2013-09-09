package net.bodz.uni.crypt.pgp;

import net.bodz.bas.data.mem.IMemory;
import net.bodz.bas.data.mem.MemoryAccessException;
import net.bodz.bas.i18n.nls.II18nCapable;

/**
 * PGP CRC32
 */
public class PgpCrc32
        implements II18nCapable {

    /**
     * Specify a relative range in the process region, to fill with the pad-bytes
     *
     * @option =FROM,TO
     */
    IntRange fillRange;

    /**
     * Bytes to pad
     *
     * @option =HEX
     */
    byte[] padBytes = { 0, 0, 0, 0 };

    public PgpCrc32() {
    }

    public byte[] process(IMemory src, long lenl)
            throws MemoryAccessException {
        if (lenl >= Integer.MAX_VALUE)
            throw new UnsupportedOperationException(tr._("unsupport to get crc32 from >2G block"));
        int len = (int) lenl;
        byte[] bigEndian = new byte[len];
        src.read(0, bigEndian);
        if (len % 4 != 0)
            throw new IllegalArgumentException(tr._("PGP-CRC32 is DWORD aligned"));
        if (fillRange != null) { // fill before switch byte-order
            int padIndex = 0;
            for (int i = fillRange.from; i < fillRange.to; i++) {
                byte padByte = padBytes[padIndex];
                padIndex = (padIndex + 1) % padBytes.length;
                bigEndian[i] = padByte;
            }
        }
        for (int i = 0; i < len; i += 4) {
            byte x0 = bigEndian[i + 0];
            byte x1 = bigEndian[i + 1];
            bigEndian[i + 0] = bigEndian[i + 3];
            bigEndian[i + 1] = bigEndian[i + 2];
            bigEndian[i + 2] = x1;
            bigEndian[i + 3] = x0;
        }
        PgpCrc32Checksum crc32 = new PgpCrc32Checksum();
        crc32.update(bigEndian, 0, len);
        byte[] v = crc32.getBytesLE();
        return v;
    }

}
