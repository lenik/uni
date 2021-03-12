package net.bodz.uni.fmt.cfb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.data.mem.IMemory;
import net.bodz.bas.data.mem.MemoryAccessException;
import net.bodz.bas.data.mem.MemoryIn;
import net.bodz.bas.data.mem.RandomAccessFileMemory;
import net.bodz.bas.err.BadFormatException;
import net.bodz.bas.err.OutOfDomainException;
import net.bodz.bas.io.BByteIn;
import net.bodz.bas.io.IByteIn;
import net.bodz.bas.io.IDataIn;
import net.bodz.bas.io.data.DataInImplLE;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.io.res.tools.StreamWriting;
import net.bodz.uni.fmt.cfb.t.ICfbConsts;
import net.bodz.uni.fmt.cfb.t.file.CfbFile;
import net.bodz.uni.fmt.cfb.t.file.DirectoryEntry;

public class CfbMmap
        implements
            ICfbConsts {

    IMemory mem;

    CfbFile cfbFile;
    int[] SAT;
    int[] MSAT;

    public CfbMmap(RandomAccessFile raf)
            throws IOException {
        this(new RandomAccessFileMemory(raf, 0L));
    }

    public CfbMmap(IMemory mem)
            throws IOException {
        if (mem == null)
            throw new NullPointerException("mem");
        this.mem = mem;

        MemoryIn memIn = new MemoryIn(mem);
        IDataIn in = DataInImplLE.from((IByteIn) memIn);

        cfbFile = new CfbFile();
        cfbFile.readObject(in);

        byte[] sector = new byte[1 << cfbFile.sectorSizeShift];

        SAT = new int[cfbFile.nsectSAT * sector.length];
        int SAToff = 0;

        for (int ind = 0; ind < cfbFile.nsectSAT; ind++) {
            int sect = cfbFile.MSAT[ind];
            IMemory ptr = mem.offset(cfbFile.sectorPosition(sect));
            try {
                ptr.read(0, sector);
            } catch (MemoryAccessException e) {
                throw new BadFormatException(e.getMessage(), e);
            }
            DataInImplLE sectorIn = DataInImplLE.from(new BByteIn(sector));
            sectorIn.readDwords(SAT, SAToff, sector.length / 4);
            SAToff += sector.length / 4;
        }

        // int nsectDir = cfbFile.nsectDir == 0 ? 1 : cfbFile.nsectDir;
        scan(cfbFile.dirStartSector, 0);
    }

    int[] chain(int startSector, int nsect) {
        if (nsect == 0)
            return chain(startSector);
        int next = startSector;
        int[] v = new int[nsect];
        v[0] = next;
        for (int i = 1; i < nsect; i++) {
            next = SAT[next];
            if (next == ENDOFCHAIN)
                throw new OutOfDomainException();
            v[i] = next;
        }
        assert SAT[next] == ENDOFCHAIN;
        return v;
    }

    int[] chain(int startSector) {
        int next = startSector;
        List<Integer> list = new ArrayList<>();
        list.add(next);
        while (true) {
            next = SAT[next];
            if (next == ENDOFCHAIN || next == FREESECT)
                break;
            list.add(next);
        }
        int[] v = new int[list.size()];
        for (int i = 0; i < v.length; i++) {
            v[i] = list.get(i);
        }
        return v;
    }

    byte[] loadStream(int startSector, int nsect)
            throws BadFormatException {
        byte[] sector = new byte[1 << cfbFile.sectorSizeShift];
        int[] sects = chain(startSector, nsect);
        byte[] buf = new byte[sects.length * sector.length];
        int off = 0;
        for (int sect : sects) {
            IMemory ptr = mem.offset(cfbFile.sectorPosition(sect));
            try {
                ptr.read(0, sector);
            } catch (MemoryAccessException e) {
                throw new BadFormatException(e.getMessage(), e);
            }
            System.arraycopy(sector, 0, buf, off, sector.length);
            off += sector.length;
        }
        return buf;
    }

    Directory loadDir(int startSector)
            throws IOException {
        byte[] data = loadStream(startSector, 0);
        DataInImplLE dataIn = DataInImplLE.from(new BByteIn(data));

        int nent = data.length / 0x80;
        Directory dir = new Directory();
        for (int i = 0; i < nent; i++) {
            DirectoryEntry dirent = new DirectoryEntry();
            dirent.readObject(dataIn);
            dir.add(dirent);
        }
        return dir;
    }

    Map<Integer, Directory> dircache = new HashMap<>();

    void scan(int sect, int id)
            throws IOException {
        scan(sect, id, 0, "");
    }

    void scan(int sect, int id, int depth, String prefix)
            throws IOException {
        Directory dir = dircache.get(sect);
        if (dir == null) {
            dir = loadDir(sect);
            dircache.put(sect, dir);
        }

        DirectoryEntry dirent = dir.get(id);
        if (dirent.leftSiblingId != ID_NONE)
            scan(sect, dirent.leftSiblingId, depth, prefix);

        String path = prefix + dirent.entryName;
        System.out.println(path);
        if (dirent.streamSize < cfbFile.miniStreamSizeCutoff) {
        } else {
            // int[] sects = chain(dirent.startingSector);
            // System.out.print(": " + StringArray.join(",", sects));
            byte[] data = loadStream(dirent.startingSector, 0);

            File out = new File("/tmp/export/" + path);
            out.getParentFile().mkdirs();
            new FileResource(out).to(StreamWriting.class).write(data);
        }

        if (dirent.childId != ID_NONE)
            scan(sect, dirent.childId, depth + 1, prefix + dirent.entryName + "/");

        if (dirent.rightSiblingId != ID_NONE)
            scan(sect, dirent.rightSiblingId, depth, prefix);

    }

    public static void main(String[] args)
            throws Exception {
        RandomAccessFile raf = new RandomAccessFile("/tmp/fmt/a.SchLib", "r");
        new CfbMmap(raf);
    }

}
