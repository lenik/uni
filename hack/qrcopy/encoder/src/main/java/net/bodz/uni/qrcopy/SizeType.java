package net.bodz.uni.qrcopy;

public enum SizeType {

    MINI(2, 1, 1), //

    SHORT(4, 2, 2), //

    LONG(4, 4, 4), //

    LARGE(8, 4, 4), //

    ;

    final int fileSizeBytes;
    final int indexBytes;
    final int checksumBytes;

    private SizeType(int fileSizeBytes, int indexBytes, int checksumBytes) {
        this.fileSizeBytes = fileSizeBytes;
        this.indexBytes = indexBytes;
        this.checksumBytes = checksumBytes;
    }

    public int getFileSizeBytes() {
        return fileSizeBytes;
    }

    public int getIndexBytes() {
        return indexBytes;
    }

    public int getChecksumBytes() {
        return checksumBytes;
    }

    public int getFileSizeBits() {
        return fileSizeBytes * 8;
    }

    public int getIndexBits() {
        return indexBytes * 8;
    }

    public int getChecksumBits() {
        return checksumBytes * 8;
    }

    public boolean isFit(long fileSize, int maxBlockSize) {
        if (octetCount(fileSize) > fileSizeBytes)
            return false;
        long count = (fileSize + maxBlockSize - 1) / maxBlockSize;
        if (octetCount(count) > indexBytes)
            return false;
        return true;
    }

    static int octetCount(long n) {
        int c = 0;
        while (n != 0) {
            // n >>= 8;
            n = Long.divideUnsigned(n, 256);
            c++;
        }
        return c;
    }

    static SizeType[] values = values();

    public static SizeType get(int ord) {
        return values[ord];
    }

    public static SizeType get(int ord, SizeType fallback) {
        if (ord < 0 || ord >= values.length)
            return fallback;
        else
            return values[ord];
    }

}
