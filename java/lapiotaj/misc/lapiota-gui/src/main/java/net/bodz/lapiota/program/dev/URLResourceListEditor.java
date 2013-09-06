package net.bodz.lapiota.program.dev;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.widgets.Composite;

import net.bodz.bas.c.string.StringPart;
import net.bodz.bas.io.res.builtin.URLResource;
import net.bodz.swt.c3.file.FileSelector;
import net.bodz.swt.c3.list.AbstractListEditor;

public class URLResourceListEditor
        extends AbstractListEditor<URLResource> {

    private FileSelector fileSelector;

    public URLResourceListEditor(Composite parent, int style) {
        super(parent, style);
        this.fileSelector = new FileSelector();
    }

    @Override
    protected URLResource createObject() {
        String path = fileSelector.select(getShell(), null);
        if (path == null)
            return null;
        File file = new File(path);
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        URLResource resource = new URLResource(url);
        return resource;
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
    protected String format(URLResource resource) {
        if (resource == null)
            return "(null)";
        URL url = resource.getURL();
        String protocol = url.getProtocol();
        String s = url.getPath();
        if ("jar".equals(protocol))
            s = StringPart.afterLast(s, '/');
        else if ("file".equals(protocol))
            s = url.getFile();
        return s;
    }

}
