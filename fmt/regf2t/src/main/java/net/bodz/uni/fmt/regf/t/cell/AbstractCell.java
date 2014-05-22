package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;
import java.lang.reflect.Field;

import net.bodz.bas.data.address.IAddressed;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.rst.ElementHandlerException;
import net.bodz.bas.fmt.rst.IRstOutput;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.t.Cc2Typer;
import net.bodz.uni.fmt.regf.t.RegfStruct;

public abstract class AbstractCell
        extends RegfStruct
        implements IAddressed {

    private static final long serialVersionUID = 1L;

    /**
     * hbin offset.
     */
    private int address;

    /**
     * Cell size in bytes(including these 4 bytes)..
     *
     * Negative if allocated, positive if free. If a cell becomes unallocated and is adjacent to
     * another unallocaeted cell, they are merged by having the earlier cell's length extended.
     */
    protected int size;
    public transient boolean allocated;

    public abstract short getMagic();

    public abstract void setMagic(short magic);

    @Override
    public int address() {
        return address;
    }

    @Override
    public int size() {
        return size;
    }

    public void address(int address) {
        this.address = address;
    }

    public void size(int size) {
        this.size = size;
    }

    @Override
    public final void readObject(IDataIn in)
            throws IOException {
        readObject1(in);
        readObject2(in);
    }

    @Override
    public final void writeObject(IDataOut out)
            throws IOException {
        writeObject1(out);
        writeObject2(out);
    }

    protected void readObject1(IDataIn in)
            throws IOException {
        short magic = in.readWord();
        setMagic(magic);
    }

    protected void writeObject1(IDataOut out)
            throws IOException {
        short magic = getMagic();
        out.write(magic);
    }

    public abstract void readObject2(IDataIn in)
            throws IOException;

    public abstract void writeObject2(IDataOut out)
            throws IOException;

    @Override
    public String[] getElementArguments() {
        String type = getClass().getSimpleName();
        return new String[] { type };
    }

    static final Cc2Typer CC2_TYPER = new Cc2Typer();

    @Override
    public boolean writeObjectFieldOverride(IRstOutput out, Field field)
            throws IOException {
        switch (field.getName()) {
        case "magic":
            out.attribute("magic", CC2_TYPER.format(getMagic()));
            return true;
        }
        return super.writeObjectFieldOverride(out, field);
    }

    @Override
    public boolean attribute(String name, String data)
            throws ParseException, ElementHandlerException {
        switch (name) {
        case "magic":
            short magic = CC2_TYPER.parse(data);
            setMagic(magic);
            return true;
        }
        return super.attribute(name, data);
    }

}
