package net.bodz.uni.fmt.regf;

import java.io.IOException;

import org.junit.Assert;

import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.data.DataInImplLE;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.text.rst.ReflectRstDumper;
import net.bodz.bas.text.rst.RstOutputImpl;
import net.bodz.uni.fmt.regf.t.RegfFile;

public class Regf2tTest
        extends Assert {

    public static void main(String[] args)
            throws IOException {
        RegfFile file = new RegfFile();
        System.out.println(file.sizeof());

        IDataIn in = DataInImplLE.from(new FileResource("NTUser.dat").newByteIn());
        file.readObject(in);

        ReflectRstDumper.dump(RstOutputImpl.from(Stdio.cout), file);
    }

}
