package net.bodz.lapiota.program.xmlfs;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.AbstractCDATA;

import net.bodz.bas.err.OutOfDomainException;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.vfs.IFile;

public class XmlfsData
        extends AbstractCDATA {

    private static final long serialVersionUID = 8857200444733966492L;

    private Element parent;
    private IFile file;
    private int readSize;
    private Reference<byte[]> data;
    private Charset encoding;

    public XmlfsData(Element parent, IFile file, long readSize, Charset encoding) {
        if (readSize > Integer.MAX_VALUE)
            throw new OutOfDomainException("readSize is too large", readSize, Integer.MAX_VALUE);
        this.parent = parent;
        this.file = file;
        this.readSize = (int) readSize;
        this.encoding = encoding;
    }

    public XmlfsData(XmlfsElement parent, IFile file, long readSize) {
        this(parent, file, readSize, null);
    }

    public XmlfsData(XmlfsElement parent, IFile file) {
        this(parent, file, file.length(), null);
    }

    public IFile getFile() {
        return file;
    }

    public void setFile(IFile file) {
        this.file = file;
    }

    public int getReadSize() {
        return readSize;
    }

    public void setReadSize(int readSize) {
        this.readSize = readSize;
    }

    public Charset getEncoding() {
        if (encoding == null) {
            XmlfsDocument doc = (XmlfsDocument) getDocument();
            encoding = doc.getFileEncoding();
            if (encoding == null)
                encoding = Charset.defaultCharset();
        }
        return encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public byte[] getData() {
        byte[] bytes = null;
        if (data == null || (bytes = data.get()) == null) {
            try {
                bytes = file.tooling()._for(StreamReading.class).read(readSize);
            } catch (IOException e) {
                String s = "<read-error>" + e.getMessage() + "</read-error>";
                bytes = s.getBytes();
            }
            data = new WeakReference<byte[]>(bytes);
        }
        return bytes;
    }

    @Override
    public String getText() {
        byte[] data = getData();
        return new String(data, getEncoding());
    }

    @Override
    public Document getDocument() {
        return parent.getDocument();
    }

    @Override
    public Element getParent() {
        return parent;
    }

    @Override
    public void setParent(Element parent) {
        this.parent = parent;
    }

}
