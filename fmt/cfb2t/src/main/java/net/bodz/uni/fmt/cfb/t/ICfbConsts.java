package net.bodz.uni.fmt.cfb.t;

import net.bodz.bas.sugar.IConstants;

public interface ICfbConsts
        extends IConstants {

    /**
     * d0 cf 11 e0 a1 b1 1a e1
     */
    long CFB_SIGNATURE = 0xE11AB1A1E011CFD0L;

    short LITTLE_ENDIAN = (short) 0xfffe;
    short BIG_ENDIAN = (short) 0xfeff;

    /**
     * denotes an unused sector.
     * 
     * Free sector, may exist in the file, but is not part of any stream.
     */
    int FREESECT = -1;

    /**
     * marks the last sector in a SAT chain
     * 
     * Trailing SecID in a SecID chain
     */
    int ENDOFCHAIN = -2;

    /**
     * marks a sector used to store part of the SAT
     * 
     * 
     * Sector is used by the sector allocation table
     */
    int SATSECT = -3;

    /**
     * marks a sector used to store part of the MSAT
     * 
     * Sector is used by the master sector allocation table.
     */
    int MSATSECT = -4;

    byte OBJ_INVALID = 0; // EMPTY
    byte OBJ_STORAGE = 1;
    byte OBJ_STREAM = 2;
    byte OBJ_LOCKBYTES = 3;
    byte OBJ_PROPERTY = 4;
    byte OBJ_ROOT_STORAGE = 5;

    byte COLOR_RED = 0;
    byte COLOR_BLACK = 1;

    int ID_NONE = -1;

    Guid CLSID_STORAGE = new Guid(0x11CEC15456616100L, 0xAA005385, 0x5BF9A100);
    Guid CLSID_ROOT_DIR = new Guid(0x11CEC15456616700L, 0xAA005385, 0x5BF9A100);

}
