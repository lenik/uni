package net.bodz.uni.crypt.pgp;

import net.bodz.bas.data.codec.builtin.HexCodec;
import net.bodz.uni.crypt.pgp.Hashes.CRC32_LE;

public class HashesTest {

    public static void main(String[] args) {
        CRC32_LE crc32 = new CRC32_LE();
        byte[] data = "el".getBytes();
        crc32.update(data);
        byte[] digest = crc32.digest();
        System.out.println(HexCodec.getInstance().encode(digest));
    }

}
