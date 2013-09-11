package net.bodz.uni.fmt.regf.t.file;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.data.struct.RstDataStruct;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.IDataOut;
import net.bodz.uni.fmt.regf.t.InvalidMagicException;

public class RegfFile
        extends RstDataStruct {

    private static final long serialVersionUID = 1L;

    public final RegfHdr hdr = new RegfHdr();
    public List<RegfHbin> hbins = new ArrayList<>();

    public transient int fileLength;

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

}
