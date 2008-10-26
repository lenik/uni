package net.bodz.lapiota.filesys;

import static net.bodz.bas.types.util.ArrayOps.Bytes;
import net.bodz.bas.text.encodings.Encodings;

public class PartialCopyTest {

    public static void main(String[] args) {
        byte[] bytes = "123".getBytes();
        bytes = Bytes.copyOf(bytes, bytes.length + 1);
        System.out.println(bytes.length);
        System.out.println(Encodings.HEX.encode(bytes));
    }

}
