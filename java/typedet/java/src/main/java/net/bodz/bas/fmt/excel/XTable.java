package net.bodz.bas.fmt.excel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.Element;

import net.bodz.bas.err.LoaderException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.xml.IXmlForm;
import net.bodz.bas.fmt.xml.IXmlOutput;
import net.bodz.bas.fmt.xml.xq.IElement;

public class XTable
        implements
            IXmlForm {

    XWorksheet sheet;
    List<XColumn> columns = new ArrayList<>();
    List<XRow> rows = new ArrayList<>();

    public XTable(XWorksheet sheet) {
        this.sheet = sheet;
    }

    public XWorksheet getSheet() {
        return sheet;
    }

    public XWorkbook getWorkbook() {
        return sheet.getWorkbook();
    }

    public List<XColumn> getColumns() {
        return columns;
    }

    public List<XRow> getRows() {
        return rows;
    }

    public int getRowCount() {
        return rows.size();
    }

    public XRow getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    public int computeColumnCount() {
        int cc = 0;
        for (XRow row : rows) {
            int n = row.getCellCount();
            if (n > cc)
                cc = n;
        }
        return cc;
    }

    @Override
    public void writeObject(IXmlOutput out)
            throws XMLStreamException {
        out.beginElement("Table");
        for (XColumn column : columns)
            column.writeObject(out);
        for (XRow row : rows)
            row.writeObject(out);
        out.endElement();
    }

    @Override
    public void readObject(IElement element)
            throws ParseException, LoaderException {
        for (Element child : dom.childElements(element)) {
            switch (child.getTagName()) {
            case "Column":
                XColumn column = new XColumn();
                column.readObject(child);
                columns.add(column);
                break;

            case "Row":
                XRow row = new XRow(this, rows.size());
                row.readObject(child);
                rows.add(row);
                break;
            }
        }
    }

    public void readObject(Sheet pSheet, ExcelParseOptions options) {
        int first = pSheet.getFirstRowNum();
        int last = pSheet.getLastRowNum();

        for (int rowIndex = first; rowIndex <= last; rowIndex++) {
            if (rowIndex == -1)
                continue;
            XRow row = new XRow(this, rowIndex);
            Row pRow = pSheet.getRow(rowIndex);
            row.readObject(pRow, options);
            rows.add(row);
        }
    }

}
