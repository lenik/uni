package net.bodz.lapiota.xmlfs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.io.FileMask;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.tree.AbstractElement;

@SuppressWarnings("unchecked")
public class XmlfsElement extends AbstractElement {

    private static final long serialVersionUID = -6930383484711901118L;

    protected Document        document;
    protected Element         parent;
    protected File            file;
    protected QName           qName;
    protected List            attributes;
    protected List            content;

    public XmlfsElement(Document document, File file) {
        assert file != null;
        this.document = document;
        this.file = file;
    }

    public XmlfsElement(Element parent, File file) {
        assert file != null;
        this.parent = parent;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
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
            attributes.add(new _Attribute(this, "name") { //$NON-NLS-1$
                private static final long serialVersionUID = 4612157511767199075L;

                @Override
                public String getValue() {
                    return String.valueOf(file.getName());
                }
            });
            attributes.add(new _Attribute(this, "size") { //$NON-NLS-1$
                private static final long serialVersionUID = 4612157511767199075L;

                @Override
                public String getValue() {
                    return String.valueOf(file.length());
                }
            });
            attributes.add(new _Attribute(this, "lastModified") { //$NON-NLS-1$
                private static final long serialVersionUID = 4612157511767199075L;

                @Override
                public String getValue() {
                    return String.valueOf(file.lastModified());
                }
            });
            attributes.add(new _Attribute(this, "flags") { //$NON-NLS-1$
                private static final long serialVersionUID = 4612157511767199075L;

                @Override
                public String getValue() {
                    return String.valueOf(FileMask.format(file));
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
            content = new ArrayList();
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    XmlfsElement e = new XmlfsElement(this, child);
                    content.add(e);
                }
            }
            XmlfsDocument doc = (XmlfsDocument) getDocument();
            if (doc.isLoadData()) {
                if (file.canRead()) {
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
            content = new ArrayList();
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
