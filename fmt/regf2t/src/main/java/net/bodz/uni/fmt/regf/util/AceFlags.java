package net.bodz.uni.fmt.regf.util;

public interface AceFlags {

    int OBJECT_INHERIT = 0x01;
    int CONTAINER_INHERIT = 0x02;
    int NON_PROPAGATE = 0x04;
    int INHERIT_ONLY = 0x08;
    int INHERITED_ACE = 0x10;

    int OI = OBJECT_INHERIT;
    int CI = CONTAINER_INHERIT;
    int NP = NON_PROPAGATE;
    int IO = INHERIT_ONLY;
    int IA = INHERITED_ACE;

    int PERM_QRY_VAL = 0;
    int PERM_SET_VAL = 0;
    int PERM_CREATE_KEYS = 0;
    int PERM_ENUM_KEYS = 0;
    int PERM_NOTIFY = 0;
    int PERM_CREATE_LNK = 0;
    int PERM_WOW64_64 = 0;
    int PERM_WOW64_32 = 0;
    int PERM_DELETE = 0;
    int PERM_R_CONT = 0;
    int PERM_W_DAC = 0;
    int PERM_W_OWNER = 0;
    int PERM_SYNC = 0;
    int PERM_SYS_SEC = 0;
    int PERM_MAX_ALLWD = 0;
    int PERM_GEN_A = 0;
    int PERM_GEN_X = 0;
    int PERM_GEN_W = 0;
    int PERM_GEN_R = 0;

}
