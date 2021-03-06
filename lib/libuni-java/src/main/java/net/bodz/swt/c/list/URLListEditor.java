package net.bodz.swt.c.list;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.widgets.Composite;

import net.bodz.bas.c.string.StringPart;
import net.bodz.swt.c.file.FileSelector;

public class URLListEditor
        extends AbstractListEditor<URL> {

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
            return "(null)";
        String protocol = url.getProtocol();
        String s = url.getPath();
        if ("jar".equals(protocol))
            s = StringPart.afterLast(s, '/');
        else if ("file".equals(protocol))
            s = url.getFile();
        return s;
    }

}
