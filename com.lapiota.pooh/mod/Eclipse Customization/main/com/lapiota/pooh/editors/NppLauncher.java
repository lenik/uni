package com.lapiota.pooh.editors;

import java.io.File;
import java.io.IOException;

import net.bodz.bas.sys.Processes;
import net.bodz.bas.types.util.Types;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

import com.lapiota.Lapiota;

public class NppLauncher implements IEditorLauncher {

    @Override
    public void open(IPath file) {
        try {
            open(file.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void open(File file) throws IOException, InterruptedException {
        Types.load(Lapiota.class);
        File no_bat = new File(Lapiota.lapAbcd, "bin/no.bat");
        String[] cv = { no_bat.getPath(), file.getPath() };
        Processes.shellExec(cv).waitFor();
    }

}
