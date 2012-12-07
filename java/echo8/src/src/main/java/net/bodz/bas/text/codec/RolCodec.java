package net.bodz.bas.text.codec;

import java.util.Arrays;

/**
 * Read out loud codec.
 */
public class RolCodec {

    // Ambioguous characters: [01lr] are removed from the alphabet.
    static char[] alphabet = "23456789abcdefghijkmnopqstuvwxyz".toCharArray();
    static int[] index;
    static {
        assert alphabet.length == 32;
        index = new int[128];
        Arrays.fill(index, -1);
        for (int i = 0; i < alphabet.length; i++) {
            char ch = alphabet[i];
            index[ch] = i;
        }
    }

    public static String encode(long val) {
        return encode(val, 0);
    }

    public static String encode(long val, int minLength) {
        StringBuilder buf = new StringBuilder();
        while (val != 0) {
            int b5 = (int) (val & 0x1f);
            val >>= 5;
            char ch = alphabet[b5];
            buf.append(ch);
        }
        while (minLength > buf.length()) {
            buf.insert(0, alphabet[0]);
            minLength--;
        }
        return buf.toString();
    }

    public static long decode(String str) {
        long val = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch >= index.length || index[ch] == -1)
                throw new IllegalArgumentException("Illegal char: " + ch);
            int b5 = index[ch];
            val <<= 5;
            val |= b5;
        }
        return val;
    }

}
