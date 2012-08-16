package net.bodz.lapiota.filesys;

import net.bodz.bas.text.codec.builtin.HexCodec;

public class PartialCopyTest {

    public static void main(String[] args) {
        byte[] bytes = "123".getBytes();
        bytes = Bytes.copyOf(bytes, bytes.length + 1);
        System.out.println(bytes.length);
        System.out.println(HexCodec.getInstance().encode(bytes));
    }

}
