package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;
import java.lang.reflect.Field;

import net.bodz.bas.data.block.IMappedBlock;
import net.bodz.bas.data.struct.RstDataStruct;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.t.Cc2Typer;
import net.bodz.bas.text.rst.ElementHandlerException;
import net.bodz.bas.text.rst.IRstOutput;
import net.bodz.uni.fmt.regf.t.IRegfConsts;

public abstract class AbstractCell
        extends RstDataStruct
        implements IMappedBlock, IRegfConsts {

    private static final long serialVersionUID = 1L;

    /**
     * Cell length (including these 4 bytes)..
     *
     * Negative if allocated, positive if free. If a cell becomes unallocated and is adjacent to
     * another unallocaeted cell, they are merged by having the earlier cell's length extended.
     */
    protected int length;
    public transient boolean allocated;

    transient int fileOffset;
    transient int hbinOffset;

    public abstract short getMagic();

    public abstract void setMagic(short magic);

    @Override
    public int getOffset() {
        return hbinOffset;
    }

    @Override
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public final int sizeof() {
        return length - 4;
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
