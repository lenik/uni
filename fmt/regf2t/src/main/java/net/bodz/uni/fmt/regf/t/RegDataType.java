package net.bodz.uni.fmt.regf.t;

public enum RegDataType {

    REG_NONE(0),

    REG_SZ(1),

    REG_EXPAND_SZ(2),

    REG_BINARY(3),

    REG_DWORD(4),

    REG_DWORD_BE(5),

    REG_LINK(6),

    REG_MULTI_SZ(7),

    REG_RESOURCE_LIST(8),

    REG_FULL_RESOURCE_DESCRIPTOR(9),

    REG_RESOURCE_REQUIREMENTS_LIST(10),

    /** 64-bit little endian */
    REG_QWORD(11),

    ;

    int value;

    private RegDataType(int value) {
        this.value = value;
    }

}
