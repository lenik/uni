package net.bodz.uni.fmt.regf.t.file;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.data.address.IAddressedObjectManager;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.InvalidMagicException;
import net.bodz.uni.fmt.regf.t.RegfStruct;
import net.bodz.uni.fmt.regf.t.cell.AbstractCell;

public class RegfFile
        extends RegfStruct {

    private static final long serialVersionUID = 1L;

    public transient int fileLength;

    public final RegfHdr hdr = new RegfHdr();
    public List<RegfHbin> hbins = new ArrayList<>();

    @Override
    public void readObject(IDataIn in)
            throws IOException {
        hdr.readObject(in);

        hbins.clear();
        while (true) {
            RegfHbin hbin = new RegfHbin();
            System.out.print(".");
            System.out.flush();
            try {
                hbin.readObject(in);
            } catch (EOFException e) {
                break;
            } catch (InvalidMagicException e) {
                int skipBytesToResume = e.getSkipBytesToResume();
                int skipped = (int) in.skip(skipBytesToResume);
                if (skipped < skipBytesToResume)
                    break;
                continue;
            }
            hbins.add(hbin);
        }
    }

    @Override
    public void writeObject(IDataOut out)
            throws IOException {
        hdr.writeObject(out);

        for (RegfHbin hbin : hbins)
            hbin.writeObject(out);
    }

    @Override
    public void afterAddressSet(IAddressedObjectManager<AbstractCell> manager) {
        hdr.afterAddressSet(manager);
        for (RegfHbin hbin : hbins)
            hbin.afterAddressSet(manager);
    }

}
