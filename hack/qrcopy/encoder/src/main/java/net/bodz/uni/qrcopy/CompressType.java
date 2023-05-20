package net.bodz.uni.qrcopy;

public enum CompressType {

    NONE,

    GZIP,

    LZMA,

    ;

    static CompressType[] values = values();

    public static CompressType get(int ord) {
        return values[ord];
    }

    public static CompressType get(int ord, CompressType fallback) {
        if (ord < 0 || ord >= values.length)
            return fallback;
        else
            return values[ord];
    }

}
