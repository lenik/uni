package net.bodz.uni.fmt.regf.t;

import net.bodz.bas.data.address.IAddressedObjectManager;
import net.bodz.bas.fmt.rst.RstDataStruct;
import net.bodz.uni.fmt.regf.t.cell.AbstractCell;

public abstract class RegfStruct
        extends RstDataStruct
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    public void afterAddressSet(IAddressedObjectManager<AbstractCell> manager) {
    }

}
