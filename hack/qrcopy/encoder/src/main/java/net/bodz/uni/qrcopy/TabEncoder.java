package net.bodz.uni.qrcopy;

import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;

public class TabEncoder {

    final int base;
    final int[] int2char;

    final char charMax;
    final int[] char2int;

    public TabEncoder(char[] table) {
        base = table.length;
        int2char = new int[base];
        char chmax = 0;
        for (int i = 0; i < base; i++) {
            char ch = table[i];
            int2char[i] = ch;
            if (ch > chmax)
                chmax = ch;
        }
        charMax = chmax;
        char2int = new int[charMax];
        Arrays.fill(char2int, -1);
        for (int i = 0; i < base; i++) {
            char ch = table[i];
            char2int[ch] = i;
        }
    }

    class Encoder {

        final int blockSizeInByte;
        final int blockItemSizeInBytes;
        final int blockItemCount;

        // in reversed order, and one item less than total item count.
        final long[] blockItemsR;
        long lastItem;

        int blockItemIndex;
        int itemByteIndex;

        public Encoder(int blockSizeInBytes) {
            this.blockSizeInByte = blockSizeInBytes;
            this.blockItemSizeInBytes = Long.BYTES;
            this.blockItemCount = blockSizeInBytes / blockItemSizeInBytes;
            this.blockItemsR = new long[blockItemCount - 1];
        }

        public void put(byte[] bin) {
            put(bin, 0, bin.length);
        }

        public void put(byte[] bin, int off, int len) {
            if (itemByteIndex != 0) {
                int n = blockItemSizeInBytes - itemByteIndex;
                if (n > len)
                    n = len;
                for (int i = 0; i < n; i++) {
                    lastItem <<= 8;
                    lastItem |= bin[off++] & 0xFF;
                    len--;
                    itemByteIndex++;
                }
                if (itemByteIndex == blockItemSizeInBytes) {

                }
            }

        }

    }

    class Decoder {

        OutputStream out;

        public void decode(Reader reader) {

        }
    }

}
