package net.bodz.uni.regf.t.samba4;

public enum RegKeyType {

    REG_ROOT_KEY(0x2C),

    REG_SUB_KEY(0x20),

    REG_SYM_LINK(0x10),

    ;

    int value;

    private RegKeyType(int value) {
        this.value = value;
    }

}
