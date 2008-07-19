package net.bodz.lapiota.xmlfs;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

import net.bodz.bas.io.Files;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.AbstractCDATA;

public class XmlfsData extends AbstractCDATA {

    private static final long serialVersionUID = 8857200444733966492L;

    private Element           parent;
    private File              file;
    private long              readSize;
    private Reference<byte[]> data;
    private Charset           encoding;

    public XmlfsData(Element parent, File file, long readSize, Charset encoding) {
        this.parent = parent;
        this.file = file;
        this.readSize = readSize;
        this.encoding = encoding;
    }

    public XmlfsData(XmlfsElement parent, File file, long readSize) {
        this(parent, file, readSize, null);
    }

    public XmlfsData(XmlfsElement parent, File file) {
        this(parent, file, file.length(), null);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getReadSize() {
        return readSize;
    }

    public void setReadSize(long readSize) {
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
                bytes = Files.readBytes(file, readSize);
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
