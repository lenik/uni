package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;

import net.bodz.bas.data.address.IAddressedObjectManager;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.RegfStruct;

public class SubkeyElement
        extends RegfStruct {

    private static final long serialVersionUID = 1L;

    transient SubkeyListCell parent;

    public int offset;
    public int hash;

    public transient AbstractCell target;

    public SubkeyElement(SubkeyListCell parent) {
        this.parent = parent;
    }

    @Override
    public void readObject(IDataIn in)
            throws IOException {
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

    @Override
    public void afterAddressSet(IAddressedObjectManager<AbstractCell> manager) {
        target = manager.get(offset);
    }

}
