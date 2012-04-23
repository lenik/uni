package net.bodz.lapiota.xmlfs;

import java.io.File;
import java.nio.charset.Charset;

import org.dom4j.DocumentType;

public class XmlfsDocument
        extends _Document {

    private File rootFile;
    private Charset fileEncoding;

    private boolean loadData;
    private long loadDataSize;

    public XmlfsDocument(File rootFile) {
        setRootFile(rootFile);
    }

    public XmlfsDocument(File rootFile, String name, DocumentType docType) {
        super(name, null, docType);
        setRootFile(rootFile);
    }

    public File getRootFile() {
        return rootFile;
    }

    public void setRootFile(File rootFile) {
        this.rootFile = rootFile;
        setRootElement(new XmlfsElement(this, rootFile));
    }

    public Charset getFileEncoding() {
        return fileEncoding;
    }

    public void setFileEncoding(Charset fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public boolean isLoadData() {
        return loadData;
    }

    public void setLoadData(boolean loadData) {
        this.loadData = loadData;
    }

    public long getLoadDataSize() {
        return loadDataSize;
    }

    public void setLoadDataSize(long loadDataSize) {
        this.loadDataSize = loadDataSize;
    }

    private static final long serialVersionUID = 4249094491195638025L;

}
