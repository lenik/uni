package net.bodz.lapiota.hacks;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.bodz.bas.types.util.Strings;

import org.junit.Test;

public class Fix_BatBBTest {

    @Test
    public void testDoFileEditInputStreamOutputStream() throws Throwable {
        Fix_BatBB app = new Fix_BatBB();
        Fix_BatBB.remPrefix = "::F".getBytes();
        app.blockSize = 10;
        app.fillChar = '*';

        String src = ("" + //
                "123456789/" + //
                ":l/" + //
                ":la/" + //
                ":lab/" + //
                ":labe/" + //
                ":label/" + //
                ":label6/" + //
                " :label7/" + //
                "  :label8/" + //
                "   :label9/" + //
                "   :label10/" + //
                "").replace('/', '\n');
        System.out.println(src);
        System.out.println("------------------------------");
        ByteArrayInputStream in = new ByteArrayInputStream(src.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        app.doFileEdit(in, out);
        String dst = new String(out.toByteArray());
        dst = dst.replace(' ', '_');
        System.out.println(dst);
        System.out.println("------------------------------");
        System.out.println("Block: ");
        dst = dst.replace('\n', '/');
        String[] oblks = Strings.splitBySize(dst, app.blockSize);
        for (String oblk : oblks) {
            System.out.println(oblk);
        }

        assertEquals(
                "123456789/:l/:la/::F/:lab/::F/:labe/::F/:label/::F/:label6/::F/_:label7/::F****/__:label8/::F******/___:label9/::F*****/___:label10/",
                dst);
    }

}
