package net.bodz.lapiota.filesys;

import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.text.codec.builtin.HexCodec;

public class PartialCopyTest {

    public static void main(String[] args) {
        byte[] bytes = "123".getBytes();
        bytes = Arrays.copyOf(bytes, bytes.length + 1);
        System.out.println(bytes.length);
        System.out.println(HexCodec.getInstance().encode(bytes));
    }

}
