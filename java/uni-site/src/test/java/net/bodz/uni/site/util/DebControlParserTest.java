package net.bodz.uni.site.util;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.bodz.bas.c.java.io.FileData;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.impl.TreeOutImpl;

public class DebControlParserTest {

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    public static void main(String[] args)
            throws IOException {
        String s = FileData.readString(new File("/home/lenik/tasks/1-uni/devel/lddd/debian/control"));
        DebControl control = new DebControlParser().parse(s);
        ITreeOut out = TreeOutImpl.from(Stdio.cout);
        control.dump(out);
    }

}
