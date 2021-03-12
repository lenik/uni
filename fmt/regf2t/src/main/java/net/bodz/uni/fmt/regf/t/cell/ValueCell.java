package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;
import java.util.Arrays;

import net.bodz.bas.data.address.IAddressedObjectManager;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.rst.ElementHandlerException;
import net.bodz.bas.fmt.rst.IRstOutput;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.win32.RegValueType;

/**
 * Value structure
 */
public class ValueCell
        extends AbstractCell {

    private static final long serialVersionUID = 1L;

    /**
     * Magic number of key
     *
     * Under win2k, typically overwritten with -1.
     */
    public short magic;

    /**
     * Length of valuename_raw
     *
     * Under win2k, typically overwritten with -1.
     */
    public short valueNameLength;

    /**
     * Size of the value's data as reported in the VK record.
     *
     * May be different than that obtained while parsing the data cell itself.
     */
    public int dataSize;

    /** Virtual offset of data cell */
    public int dataOffset;

    /** Value's data type */
    public int valueType;

    /**
     * VK record flags
     *
     * Notice: @Timothy 16-bit flags and 16-bit unknown word.
     * */
    public int flags;

    /**
     * The name of this value converted to desired REGFI_ENCODING.
     *
     * This conversion typically occurs automatically through REGFI_ITERATOR settings. String is NUL
     * terminated.
     */
    public String valueName;

    /**
     * Whether or not the data record is stored in the VK record's data_off field.
     *
     * This information is derived from the high bit of the raw data size field.
     */
    transient boolean dataInOffset;

    public transient RawDataCell data;

    @Override
    public short getMagic() {
        return magic;
    }

    @Override
    public void setMagic(short magic) {
        if (magic != MAGIC_VK)
            throw new IllegalArgumentException("Bad magic: " + magic);
        this.magic = magic;
    }

    @Override
    public void readObject2(IDataIn in)
            throws IOException {
        valueNameLength = in.readWord();
        dataSize = in.readDword();
        dataOffset = in.readDword();

        valueType = in.readDword();
        RegValueType[] array = RegValueType.values();
        if (valueType < 0 || valueType >= array.length)
            // throw new IllegalArgumentException("Bad value type: " + valueType);
            valueType = 0;

        flags = in.readDword();

        dataInOffset = (dataSize & VK_DATA_IN_OFFSET) != 0;
        if (dataInOffset)
            dataSize ^= VK_DATA_IN_OFFSET;

        if (valueNameLength == -1)
            valueName = null;
        else {
            byte[] valueNameRaw = new byte[valueNameLength];
            in.read(valueNameRaw);

            boolean ascii = (flags & VK_FLAG_ASCIINAME) != 0;
            if (ascii)
                valueName = new String(valueNameRaw, "ascii");
            else
                valueName = new String(valueNameRaw, "utf-16le");
        }
    }

    @Override
    public void writeObject2(IDataOut out)
            throws IOException {
        out.writeWord(valueNameLength);

        int _dataSize = dataSize;
        if (dataInOffset)
            _dataSize |= VK_DATA_IN_OFFSET;
        out.writeDword(_dataSize);
        out.writeDword(dataOffset);

        out.writeDword(valueType);
        out.writeDword(flags);

        byte[] valueNameRaw;
        if (valueNameLength == -1) {
            valueNameRaw = new byte[0];
        } else {
            boolean ascii = (flags & VK_FLAG_ASCIINAME) != 0;
            valueNameRaw = valueName.getBytes(ascii ? "ascii" : "utf-16le");

            if (valueNameRaw.length != valueNameLength)
                valueNameRaw = Arrays.copyOf(valueNameRaw, valueNameLength);
        }
        out.write(valueNameRaw);
    }

    @Override
    public boolean writeEntryOverride(IRstOutput out, String field)
            throws IOException {
        switch (field) {
        case "valueType":
            String valueTypeName = RegValueType.values()[valueType].name();
            out._attribute("valueType", valueTypeName);
            return true;
        }
        return super.writeEntryOverride(out, field);
    }

    @Override
    public boolean attribute(String name, String data)
            throws ParseException, ElementHandlerException {
        switch (name) {
        case "valueType":
            RegValueType valueType = RegValueType.valueOf(data);
            this.valueType = valueType.ordinal();
            return true;
        }
        return super.attribute(name, data);
    }

    @Override
    public void afterAddressSet(IAddressedObjectManager<AbstractCell> manager) {
        if (dataInOffset)
            this.data = null;
        else
            this.data = (RawDataCell) manager.get(dataOffset);
    }

}
