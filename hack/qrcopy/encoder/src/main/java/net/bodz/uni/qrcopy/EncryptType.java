package net.bodz.uni.qrcopy;

public enum EncryptType {

    PLAIN,

    ;

    static EncryptType[] values = values();

    public static EncryptType get(int ord) {
        return values[ord];
    }

    public static EncryptType get(int ord, EncryptType fallback) {
        if (ord < 0 || ord >= values.length)
            return fallback;
        else
            return values[ord];
    }

}
