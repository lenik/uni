package net.bodz.uni.fmt.regf.t.rec;

import java.io.IOException;
import java.lang.reflect.Field;

import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.BByteOut;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.bas.io.StringFlags;
import net.bodz.bas.io.data.DataOutImplLE;
import net.bodz.bas.text.rst.ElementHandlerException;
import net.bodz.bas.text.rst.IRstOutput;
import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.NtTime;
import net.bodz.uni.fmt.regf.t.file.RegfCellData;

public class RegfNkRec
        extends RegfCellData
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /** Magic number of key (should be "nk") */
    public final byte[] magic = new byte[CELL_MAGIC_SIZE];

    /** Key flags */
    public short flags;

    /** Key's last modification time */
    public final NtTime mtime = new NtTime();

    int _unknown1;

    /** Virutal offset of parent key */
    public int parentOffset;

    /** Number of subkeys (stable) */
    public int subkeyCount;

    /** Number of subkeys (volatile) */
    public int volatileSubkeyCount;

    /** Virtual offset of subkey-list */
    public int subkeysOffset;

    /** Virtual offset of subkey-list */
    public int volatileSubkeysOffset;

    /** Number of values for this key */
    public int valueCount;

    /** Virtual offset of value-list */
    public int valuesOffset;

    /** Virtual offset of SK record */
    public int skOffset;

    /** Virutal offset of classname key */
    public int classNameOffset;

    /** in bytes: max subkey name * 2 */
    public int subkeyNameMaxSize;

    /** in bytes: max subkey classname length (as if) */
    public int subkeyClassNameMaxSize;

    /** in bytes: max valuename * 2 */
    public int valueNameMaxSize;

    /** in bytes: max value data size */
    public int valueDataMaxSize;

    /** possibly some sort of run-time index) */
    int _unknown2;

    /** Length of keyname_raw */
    public short keyNameSize;

    /** Length of referenced classname */
    public short classNameLength;

    /**
     * The name of this key converted to desired REGFI_ENCODING.
     *
     * This conversion typically occurs automatically through REGFI_ITERATOR settings. String is NUL
     * terminated.
     */
    public String keyName;

    /**
     * Preloaded value-list for this key. This element is loaded automatically when using the
     * iterator interface and possibly some lower layer interfaces.
     */
    transient RegfValueList[] values;

    /**
     * Preloaded subkey-list for this key. This element is loaded automatically when using the
     * iterator interface and possibly some lower layer interfaces.
     */
    transient RegfSubkeyList[] subkeys;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        in.readBytes(magic);
        flags = in.readWord();
        mtime.readObject(in);
        _unknown1 = in.readDword();
        parentOffset = in.readDword();
        subkeyCount = in.readDword();
        volatileSubkeyCount = in.readDword();
        subkeysOffset = in.readDword();
        volatileSubkeysOffset = in.readDword();
        valueCount = in.readDword();
        valuesOffset = in.readDword();
        skOffset = in.readDword();
        classNameOffset = in.readDword();
        subkeyNameMaxSize = in.readDword();
        subkeyClassNameMaxSize = in.readDword();
        valueNameMaxSize = in.readDword();
        valueDataMaxSize = in.readDword();
        _unknown2 = in.readDword();
        keyNameSize = in.readWord();
        classNameLength = in.readWord();

        byte[] keyNameBuf = new byte[keyNameSize];
        in.readBytes(keyNameBuf);
        if ((flags & NK_FLAG_ASCIINAME) != 0)
            keyName = new String(keyNameBuf, "utf-8");
        else
            keyName = new String(keyNameBuf, "utf-16le");
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        BByteOut bo = new BByteOut();
        IDataOut bdo = DataOutImplLE.from(bo);
        if ((flags & NK_FLAG_ASCIINAME) != 0)
            bdo.writeString(StringFlags.NULL_TERM, keyName);
        else
            bdo.writeString(StringFlags._16BIT | StringFlags.NULL_TERM, keyName);
        byte[] keyNameRaw = bo.toByteArray();
        keyNameSize = (short) keyNameRaw.length;

        out.write(magic);
        out.writeWord(flags);
        mtime.writeObject(out);
        out.writeDword(_unknown1);
        out.writeDword(parentOffset);
        out.writeDword(subkeyCount);
        out.writeDword(volatileSubkeyCount);
        out.writeDword(subkeysOffset);
        out.writeDword(volatileSubkeysOffset);
        out.writeDword(valueCount);
        out.writeDword(valuesOffset);
        out.writeDword(skOffset);
        out.writeDword(classNameOffset);
        out.writeDword(subkeyNameMaxSize);
        out.writeDword(subkeyClassNameMaxSize);
        out.writeDword(valueNameMaxSize);
        out.writeDword(valueDataMaxSize);
        out.writeDword(_unknown2);
        out.writeDword(keyNameSize);
        out.writeDword(classNameLength);

        out.write(keyNameRaw);
    }

    static final RegfNkFlagsTyper flagsTyper = new RegfNkFlagsTyper();

    @Override
    public boolean writeObjectFieldOverride(IRstOutput out, Field field)
            throws IOException {
        switch (field.getName()) {
        case "flags":
            out.attribute("flags", flagsTyper.format(flags & 0xffff));
            return true;
        }
        return false;
    }

    @Override
    public boolean attribute(String name, String data)
            throws ParseException, ElementHandlerException {
        switch (name) {
        case "flags":
            flags = flagsTyper.parse(data).shortValue();
            return true;
        }
        return super.attribute(name, data);
    }

}
