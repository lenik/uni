package net.bodz.bas.fmt.excel;

import javax.xml.stream.XMLStreamException;

import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.Element;

import net.bodz.bas.err.LoaderException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.xml.IXmlForm;
import net.bodz.bas.fmt.xml.IXmlOutput;
import net.bodz.bas.fmt.xml.xq.IElement;

public class XWorksheet
        implements
            IXmlForm {

    XWorkbook workbook;

    String name = "";
    XTable table = new XTable(this);

    public XWorksheet(XWorkbook workbook) {
        this.workbook = workbook;
    }

    public XWorkbook getWorkbook() {
        return workbook;
    }

    public XTable getTable() {
        return table;
    }

    public int getRowCount() {
        return table.getRows().size();
    }

    public String getName() {
        return name;
    }

    @Override
    public void writeObject(IXmlOutput out)
            throws XMLStreamException {
        out.beginElement("Worksheet");
        out.attribute("xmlns:ss", "dummy");
        out.attribute("ss:Name", name);
        table.writeObject(out);
        out.endElement();
    }

    @Override
    public void readObject(IElement element)
            throws ParseException, LoaderException {
        name = element.getAttribute("ss:Name");

        for (Element child : dom.childElements(element)) {
            switch (child.getTagName()) {
            case "Table":
                table.readObject(child);
                break;

            case "Names":
                break;
            }
        }
    }

    public void readObject(Sheet sheet, ExcelParseOptions options) {
        name = sheet.getSheetName();
        table.readObject(sheet, options);
    }

}
