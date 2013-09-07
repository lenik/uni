package net.bodz.uni.fmt.regf.t;

public interface IRegfConsts {

    int OFFSET_NONE = 0xffffffff;

    /* Header sizes and magic number lengths for various records */
    /** Minimum allocation unit for HBINs */
    int HBIN_ALLOC = 0x1000;
    /** "regf" header block size */
    int REGF_SIZE = 0x1000;
    int REGF_MAGIC_SIZE = 4;
    int REGF_NAME_SIZE = 64;
    int REGF_RESERVED1_SIZE = 340;
    int REGF_RESERVED2_SIZE = 3528;
    int HBIN_MAGIC_SIZE = 4;
    int CELL_MAGIC_SIZE = 2;
    int HBIN_HEADER_SIZE = 0x20;
    int NK_MIN_LENGTH = 0x4C;
    int VK_MIN_LENGTH = 0x14;
    int SK_MIN_LENGTH = 0x14;
    int SUBKEY_LIST_MIN_LEN = 0x4;
    int BIG_DATA_MIN_LENGTH = 0xC;

    /** Minimum time is Jan 1, 1990 00:00:00 */
    int MTIME_MIN_HIGH = 0x01B41E6D;
    int MTIME_MIN_LOW = 0x26F98000;

    /** Maximum time is Jan 1, 2290 00:00:00 */
    int MTIME_MAX_HIGH = 0x03047543;
    int MTIME_MAX_LOW = 0xC80A4000;

    int VK_FLAG_ASCIINAME = 0x0001;
    int VK_DATA_IN_OFFSET = 0x80000000;

    /** This is arbitrary. */
    int VK_MAX_DATA_LENGTH = 1024 * 1024;

    /** These two show up on normal-seeming keys in Vista and W2K3 registries */
    int NK_FLAG_UNKNOWN1 = 0x4000;
    int NK_FLAG_UNKNOWN2 = 0x1000;

    /**
     * This shows up in some Vista "software" registries
     *
     * XXX: This shows up in the following two SOFTWARE keys in Vista: /Wow6432Node/Microsoft
     * /Wow6432Node/Microsoft/Cryptography
     *
     * It comes along with UNKNOWN2 and ASCIINAME for a total flags value of 0x10A0
     */
    int NK_FLAG_UNKNOWN3 = 0x0080;

    /**
     * Predefined handle. Rumor has it that the valuelist count for this key is where the handle is
     * stored. http://msdn.microsoft.com/en-us/library/ms724836(VS.85).aspx
     */
    int NK_FLAG_PREDEF_KEY = 0x0040;
    /** The name will be in ASCII if this next bit is set, otherwise UTF-16LE */
    int NK_FLAG_ASCIINAME = 0x0020;

    /**
     * Symlink key.
     *
     * @see http://www.codeproject.com/KB/system/regsymlink.aspx
     */
    int NK_FLAG_LINK = 0x0010;

    /** This key cannot be deleted */
    int NK_FLAG_NO_RM = 0x0008;

    /** Root of a hive */
    int NK_FLAG_ROOT = 0x0004;

    /**
     * Mount point of another hive. NULL/(default) value indicates which hive and where in the hive
     * it points to.
     */
    int NK_FLAG_HIVE_LINK = 0x0002;
    /**
     * These keys shouldn't be stored on disk, according to:
     * http://geekswithblogs.net/sdorman/archive/2007/12/24/volatile-registry-keys.aspx
     */
    int NK_FLAG_VOLATILE = 0x0001;

    /* Useful for identifying unknown flag types */
    int NK_KNOWN_FLAGS = NK_FLAG_PREDEF_KEY //
            | NK_FLAG_ASCIINAME//
            | NK_FLAG_LINK//
            | NK_FLAG_NO_RM//
            | NK_FLAG_ROOT//
            | NK_FLAG_HIVE_LINK//
            | NK_FLAG_VOLATILE//
            | NK_FLAG_UNKNOWN1//
            | NK_FLAG_UNKNOWN2//
            | NK_FLAG_UNKNOWN3;

}
