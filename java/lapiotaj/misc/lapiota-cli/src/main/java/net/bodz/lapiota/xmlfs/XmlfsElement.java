package net.bodz.lapiota.xmlfs;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.tree.AbstractElement;

import net.bodz.bas.vfs.FileFlags;
import net.bodz.bas.vfs.IFile;
import net.bodz.bas.vfs.VfsException;

public class XmlfsElement
        extends AbstractElement {

    private static final long serialVersionUID = 1L;

    protected Document document;
    protected Element parent;
    protected IFile file;
    protected QName qName;
    protected List<Attribute> attributes;
    protected List<Object> content;

    public XmlfsElement(Document document, IFile file) {
        assert file != null;
        this.document = document;
        this.file = file;
    }

    public XmlfsElement(Element parent, IFile file) {
        assert file != null;
        this.parent = parent;
        this.file = file;
    }

    public IFile getFile() {
        return file;
    }

    public void setFile(IFile file) {
        this.file = file;
    }

    @Override
    public QName getQName() {
        if (qName == null) {
            String name = Xmlfs.getXName(file.getName());
            qName = new QName(name, Xmlfs.NS);
        }
        return qName;
    }

    @Override
    public void setQName(QName qname) {
        this.qName = qname;
    }

    @Override
    protected List attributeList() {
        if (attributes == null) {
            attributes = new ArrayList<Attribute>();
            attributes.add(new _Attribute(this, "name") {
                private static final long serialVersionUID = 4612157511767199075L;

                @Override
                public String getValue() {
                    return String.valueOf(file.getName());
                }
            });
            attributes.add(new _Attribute(this, "size") {
                private static final long serialVersionUID = 4612157511767199075L;

                @Override
                public String getValue() {
                    return String.valueOf(file.length());
                }
            });
            attributes.add(new _Attribute(this, "lastModified") {
                private static final long serialVersionUID = 4612157511767199075L;

                @Override
                public String getValue() {
                    return String.valueOf(file.getLastModifiedTime());
                }
            });
            attributes.add(new _Attribute(this, "flags") {
                private static final long serialVersionUID = 4612157511767199075L;

                @Override
                public String getValue() {
                    int modifiers = file.getFlags();
                    return String.valueOf(FileFlags.format(modifiers));
                }
            });
        }
        return attributes;
    }

    @Override
    protected List attributeList(int attributeCount) {
        return new ArrayList<Attribute>(attributeCount);
    }

    @Override
    public void setAttributes(List attributes) {
        this.attributes = attributes;
    }

    @Override
    protected List contentList() {
        if (content == null) {
            content = new ArrayList<Object>();
            if (file.isDirectory()) {
                try {
                    for (IFile childFile : file.children()) {
                        XmlfsElement e = new XmlfsElement(this, childFile);
                        content.add(e);
                    }
                } catch (VfsException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            XmlfsDocument doc = (XmlfsDocument) getDocument();
            if (doc.isLoadData()) {
                if (file.isReadable()) {
                    long size = doc.getLoadDataSize();
                    if (size == 0)
                        size = file.length();
                    XmlfsData data = new XmlfsData(this, file, size);
                    content.add(data);
                }
            }
        }
        return content;
    }

    @Override
    public void clearContent() {
        if (content == null)
            content = new ArrayList<Object>();
        else
            content.clear();
    }

    @Override
    public void setContent(List content) {
        this.content = content;
    }

    @Override
    public Document getDocument() {
        if (document != null)
            return document;
        return parent.getDocument();
    }

    @Override
    public void setDocument(Document document) {
        this.document = document;
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
