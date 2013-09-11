package net.bodz.uni.fmt.regf.t.rec;

import net.bodz.bas.t._bit.Flags32Typer;
import net.bodz.uni.fmt.regf.t.IRegfConsts;

public class RegfNkFlagsTyper
        extends Flags32Typer
        implements IRegfConsts {

    {
        declare(NK_FLAG_UNKNOWN1, "U1");
        declare(NK_FLAG_UNKNOWN2, "U2");
        declare(NK_FLAG_UNKNOWN3, "U3");
        declare(NK_FLAG_PREDEF_KEY, "PRE");
        declare(NK_FLAG_ASCIINAME, "ASCII");
        declare(NK_FLAG_LINK, "LINK");
        declare(NK_FLAG_NO_RM, "NORM");
        declare(NK_FLAG_ROOT, "ROOT");
        declare(NK_FLAG_HIVE_LINK, "HLNK");
        declare(NK_FLAG_VOLATILE, "VLT");
    }

}
