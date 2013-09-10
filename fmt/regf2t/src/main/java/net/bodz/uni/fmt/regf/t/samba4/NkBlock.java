package net.bodz.uni.fmt.regf.t.samba4;

import java.io.IOException;

import net.bodz.bas.data.struct.DataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.NtTime;

/**
 * The nk-record can be treated as a combination of tree-record and key-record of the win 95
 * registry.
 */
public class NkBlock
        extends DataStruct {

    private static final long serialVersionUID = 1L;

    public final byte[] header = new byte[2];

    public RegKeyType type;
    public final NtTime lastChange = new NtTime();
    public int uk1;
    public int parentOffset;
    public int numSubkeys;
    public int uk2;
    public int subkeysOffset;
    public int unknownOffset;
    public int numValues;

    /** Points to a list of offsets of vk-records */
    public int valuesOffset;

    public int skOffset;
    public int clsnameOffset;
    public int[] unk3 = new int[5];

    /** = strlen(keyName) */
    public short nameLength;
    public short clsnameLength;

    /** [ nameLength ] */
    public byte[] keyName;

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        lastChange.readObject(in);
        uk1 = in.readDword();
        parentOffset = in.readDword();
        numSubkeys = in.readDword();
        uk2 = in.readDword();
        subkeysOffset = in.readDword();
        unknownOffset = in.readDword();
        numValues = in.readDword();
        valuesOffset = in.readDword();
        skOffset = in.readDword();
        clsnameOffset = in.readDword();

        // unk3

        nameLength = in.readWord();
        clsnameLength = in.readWord();

        keyName = new byte[nameLength];
        in.readFully(keyName);
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        lastChange.writeObject(out);
        out.writeDword(uk1);
        out.writeDword(parentOffset);
        out.writeDword(numSubkeys);
        out.writeDword(uk2);
        out.writeDword(subkeysOffset);
        out.writeDword(unknownOffset);
        out.writeDword(numValues);
        out.writeDword(valuesOffset);
        out.writeDword(skOffset);
        out.writeDword(clsnameOffset);
        // out.writeInt(unk3);
        out.writeWord(nameLength);
        out.writeWord(clsnameLength);
        out.write(keyName);
    }

}
