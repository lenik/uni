package net.bodz.lapiota.devhelpers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import net.bodz.bas.types.util.Strings;
import net.bodz.swt.widgets._ListEditor;
import net.bodz.swt.widgets.util.FileSelector;

import org.eclipse.swt.widgets.Composite;

public class URLListEditor extends _ListEditor<URL> {

    private FileSelector fileSelector;

    public URLListEditor(Composite parent, int style) {
        super(parent, style);
        this.fileSelector = new FileSelector();
    }

    @Override
    protected URL createObject() {
        String path = fileSelector.select(getShell(), null);
        if (path == null)
            return null;
        File file = new File(path);
        try {
            URL url = file.toURI().toURL();
            return url;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public FileSelector getFileSelector() {
        return fileSelector;
    }

    public void setFileSelector(FileSelector fileSelector) {
        if (fileSelector == null)
            throw new NullPointerException("fileSelector");
        this.fileSelector = fileSelector;
    }

    @Override
    protected String format(URL url) {
        if (url == null)
            return "<empty>";
        String protocol = url.getProtocol();
        String s = url.getPath();
        if ("jar".equals(protocol))
            s = Strings.afterLast(s, '/');
        else if ("file".equals(protocol))
            try {
                s = new File(url.toURI()).getPath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        return s;
    }

}
