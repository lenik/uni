package net.bodz.lapiota.batch.crypt;

import net.bodz.bas.text.codec.builtin.HexCodec;
import net.bodz.lapiota.batch.crypt.Hashes.CRC32_LE;

public class HashesTest {

    public static void main(String[] args) {
        CRC32_LE crc32 = new CRC32_LE();
        byte[] data = "el".getBytes();
        crc32.update(data);
        byte[] digest = crc32.digest();
        System.out.println(HexCodec.getInstance().encode(digest));
    }

}
