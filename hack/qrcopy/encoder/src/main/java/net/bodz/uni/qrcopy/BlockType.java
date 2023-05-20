package net.bodz.uni.qrcopy;

public enum BlockType {

    FILEINFO,

    DATA,

    FILEINFO_AND_DATA,

    RESERVED,

    ;

    static BlockType[] values = values();

    public static BlockType get(int ord) {
        return values[ord];
    }

    public static BlockType get(int ord, BlockType fallback) {
        if (ord < 0 || ord >= values.length)
            return fallback;
        else
            return values[ord];
    }

}
