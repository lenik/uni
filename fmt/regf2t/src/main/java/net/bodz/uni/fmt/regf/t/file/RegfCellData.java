package net.bodz.uni.fmt.regf.t.file;

import net.bodz.bas.data.block.IMappedBlock;
import net.bodz.bas.data.struct.RstDataStruct;
import net.bodz.uni.fmt.regf.t.IRegfConsts;

public abstract class RegfCellData
        extends RstDataStruct
        implements IMappedBlock, IRegfConsts {

    private static final long serialVersionUID = 1L;

    transient int fileOffset;
    transient int hbinOffset;

    /**
     * Actual or estimated length of the cell. Always in multiples of 8.
     */
    transient int cellLength;

    @Override
    public int getOffset() {
        return hbinOffset;
    }

    @Override
    public int getLength() {
        return cellLength;
    }

}
