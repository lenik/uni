package net.bodz.uni.fmt.regf;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;

import net.bodz.bas.data.address.AddressedObjectManager;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.fmt.rst.RstFn;
import net.bodz.bas.io.ICharOut;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.adapter.WriterCharOut;
import net.bodz.bas.io.data.DataInImplLE;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.uni.fmt.regf.t.cell.AbstractCell;
import net.bodz.uni.fmt.regf.t.file.RegfFile;
import net.bodz.uni.fmt.regf.t.file.RegfHbin;
import net.bodz.uni.fmt.regf.t.file.RegfStat;

public class Regf2tTest
        extends Assert {

    public static void main(String[] args)
            throws IOException, ParseException {
        RegfFile file = new RegfFile();

        IDataIn in = DataInImplLE.from(new FileResource("system.dat").newByteIn());
        file.readObject(in);

        AddressedObjectManager<AbstractCell> manager = new AddressedObjectManager<>();
        for (RegfHbin hbin : file.hbins)
            for (AbstractCell cell : hbin.cells) {
                AbstractCell overlapped = manager.add(cell);
                if (overlapped != null)
                    throw new UnexpectedException("cell overlapped");
            }

        file.afterAddressSet(manager);

        RegfStat.getInstance().test();

        ICharOut out = Stdio.cout;
        out = new WriterCharOut(new FileWriter("/tmp/dump"));

        String rst = RstFn.toString(file);
        out.write(rst);
    }

}
