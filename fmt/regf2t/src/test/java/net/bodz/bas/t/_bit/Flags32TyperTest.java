package net.bodz.bas.t._bit;

import org.junit.Assert;
import org.junit.Test;

import net.bodz.bas.err.ParseException;

public class Flags32TyperTest
        extends Assert {

    Flags32Typer typer;

    public Flags32TyperTest() {
        typer = new Flags32Typer();
        typer.declare(0x0001, "A");
        typer.declare(0x0002, "B");
        typer.declare(0x0030, "C");
        typer.declare(0x8000, "D");
        typer.declare(0x8800, "E");
    }

    @Test
    public void testFormat() {
        assertEquals("0x1204 C", typer.format(0x1234));
        assertEquals("0x77cc A B C D E", typer.format(0xffff));
        assertEquals("0xffff0000", typer.format(0xffff0000));
    }

    @Test
    public void testParser()
            throws ParseException {
        assertEquals(0x0003, (int) typer.parse("A A A B"));
        assertEquals(0x1234, (int) typer.parse("0x1204 C"));
        assertEquals(0xfffe, (int) typer.parse("C E B 0x77cc D"));
        assertEquals(0xffff0000, (int) typer.parse("0xffff0000"));
    }

}
