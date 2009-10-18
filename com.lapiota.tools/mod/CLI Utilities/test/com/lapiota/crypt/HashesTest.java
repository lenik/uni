package com.lapiota.crypt;

import com.lapiota.crypt.Hashes.CRC32_LE;

import net.bodz.bas.text.encodings.Encodings;

public class HashesTest {

    public static void main(String[] args) {
        CRC32_LE crc32 = new CRC32_LE();
        byte[] data = "el".getBytes(); //$NON-NLS-1$
        crc32.update(data);
        byte[] digest = crc32.digest();
        System.out.println(Encodings.HEX.encode(digest));
    }

}
