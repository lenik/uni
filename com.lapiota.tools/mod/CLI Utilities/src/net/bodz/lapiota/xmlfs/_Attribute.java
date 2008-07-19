package net.bodz.lapiota.xmlfs;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.tree.AbstractAttribute;

public abstract class _Attribute extends AbstractAttribute {

    private static final long serialVersionUID = -1680850327528298746L;

    protected Element         parent;
    protected QName           name;

    public _Attribute(Element parent, QName name) {
        this.name = name;
        this.parent = parent;
    }

    public _Attribute(Element parent, String name) {
        this(parent, new QName(name));
    }

    @Override
    public QName getQName() {
        return name;
    }

    @Override
    public Document getDocument() {
        return parent.getDocument();
    }

    @Override
    public Element getParent() {
        return parent.getParent();
    }

    @Override
    public void setParent(Element parent) {
        this.parent = parent;
    }

}
