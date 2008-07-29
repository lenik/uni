package net.bodz.pooh.editors;

import java.io.File;
import java.io.IOException;

import net.bodz.lapiota.util.Lapiota;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

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
        Lapiota.load();
        File no_bat = Lapiota.findabc("bin/no.bat");
        String[] cv = { no_bat.getPath(), file.getPath() };
        Shell.exec(cv).waitFor();
    }

}
