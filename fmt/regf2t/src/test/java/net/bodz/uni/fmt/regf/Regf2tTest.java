package net.bodz.uni.fmt.regf;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.ICharOut;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.adapter.WriterCharOut;
import net.bodz.bas.io.data.DataInImplLE;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.text.rst.ReflectRstDumper;
import net.bodz.bas.text.rst.RstOutputImpl;
import net.bodz.uni.fmt.regf.t.file.RegfFile;

public class Regf2tTest
        extends Assert {

    public static void main(String[] args)
            throws IOException, ParseException {
        RegfFile file = new RegfFile();

        IDataIn in = DataInImplLE.from(new FileResource("system.dat").newByteIn());
        file.readObject(in);

        ICharOut out = Stdio.cout;
        out = new WriterCharOut(new FileWriter("/tmp/dump"));

        ReflectRstDumper.getInstance().dump(RstOutputImpl.from(out), file);
    }

}
