package net.bodz.uni.fmt.cfb;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.rst.RstObject;
import net.bodz.bas.io.ICharOut;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.adapter.WriterCharOut;
import net.bodz.bas.io.data.DataInImplLE;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.uni.fmt.cfb.t.file.CfbFile;

public class Cfb2tTest
        extends Assert {

    public static void main(String[] args)
            throws IOException, ParseException {
        CfbFile file = new CfbFile();

        IDataIn in = DataInImplLE.from(new FileResource("/tmp/fmt/a.xls").newByteIn());
        file.readObject(in);

        ICharOut out = Stdio.cout;
        out = new WriterCharOut(new FileWriter("/tmp/dump"));

        RstObject.fn.dump(file, out);
    }

}