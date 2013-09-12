package net.bodz.uni.fmt.regf.t.cell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.data.address.IAddressedObjectManager;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.IRegfConsts;
import net.bodz.uni.fmt.regf.t.file.RegfStat;

/**
 * Subkey-list structure
 */
public class SubkeyListCell
        extends AbstractCell
        implements IRegfConsts {

    private static final long serialVersionUID = 1L;

    /** Magic number of key */
    public short magic;

    /**
     * Number of immediate children
     *
     * Set to 0 after deletion.
     */
    public short elementCount;

    public transient KeyCell parent;
    public transient List<SubkeyElement> elements = new ArrayList<>();

    @Override
    public short getMagic() {
        return magic;
    }

    @Override
    public void setMagic(short magic) {
        switch (magic) {
        case MAGIC_LF:
        case MAGIC_LH:
        case MAGIC_RI:
        case MAGIC_LI:
            this.magic = magic;
            break;
        default:
            throw new IllegalArgumentException("Bad magic: " + magic);
        }
    }

    @Override
    public void readObject2(IDataIn in)
            throws IOException {
        elementCount = in.readWord();

        elements.clear();
        for (int i = 0; i < elementCount; i++) {
            int offset = in.readDword();

            int hash = 0;
            switch (magic) {
            case MAGIC_LF:
            case MAGIC_LH:
                hash = in.readDword();
                break;
            case MAGIC_RI:
            case MAGIC_LI:
                // offset => another subkey list..?
            }

            SubkeyElement element = new SubkeyElement(this);
            element.offset = offset;
            element.hash = hash;
            elements.add(element);
        }
    }

    @Override
    public void writeObject2(IDataOut out)
            throws IOException {
        out.writeWord(elementCount);

        switch (magic) {
        case MAGIC_LF:
        case MAGIC_LH:
        case MAGIC_RI:
        case MAGIC_LI:
        }
    }

    @Override
    public void afterAddressSet(IAddressedObjectManager<AbstractCell> manager) {
        for (SubkeyElement element : elements)
            element.afterAddressSet(manager);

        RegfStat.getInstance().subkeyLists.add(this);
    }

}
