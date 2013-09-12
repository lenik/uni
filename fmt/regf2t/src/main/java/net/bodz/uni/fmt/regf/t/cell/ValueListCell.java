package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.RegfStruct;

public class ValueListCell
        extends RegfStruct {

    private static final long serialVersionUID = 1L;

    /**
     * Actual number of values referenced by this list.
     *
     * May differ from parent key's num_values if there were parsing errors.
     */
    int numValues;

    int /* RegfValueListElem */[] elements;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
    }

}
