package net.bodz.bas.fmt.excel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.w3c.dom.Element;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.err.LoaderException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.xml.IXmlForm;
import net.bodz.bas.fmt.xml.IXmlOutput;
import net.bodz.bas.fmt.xml.xq.IElement;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.impl.TreeOutImpl;

public class XWorkbook
        implements
            IXmlForm {

    XDocumentProperties documentProperties = new XDocumentProperties();
    List<XWorksheet> sheets = new ArrayList<>();

    FormulaEvaluator evaluator;

    public List<XWorksheet> getSheets() {
        return sheets;
    }

    public int getSheetCount() {
        return sheets.size();
    }

    public XWorksheet getSheet(int i) {
        return sheets.get(i);
    }

    @Override
    public void writeObject(IXmlOutput out)
            throws XMLStreamException {
        out.beginElement("Workbook");
        documentProperties.writeObject(out);
        for (XWorksheet sheet : sheets)
            sheet.writeObject(out);
        out.endElement();
    }

    @Override
    public void readObject(IElement element)
            throws ParseException, LoaderException {
        for (Element child : dom.childElements(element)) {
            switch (child.getTagName()) {
            case "DocumentProperties":
                documentProperties.readObject(child);
                break;

            case "Worksheet":
                XWorksheet sheet = new XWorksheet(this);
                sheet.readObject(child);
                sheets.add(sheet);
                break;

            case "ExcelWorkbook":
            case "Styles":
            default:
                break;

            }
        }
    }

    public void readObject(Workbook pWorkbook, ExcelParseOptions options) {
        evaluator = pWorkbook.getCreationHelper().createFormulaEvaluator();
        int nSheet = pWorkbook.getNumberOfSheets();
        for (int i = 0; i < nSheet; i++) {
            Sheet pSheet = pWorkbook.getSheetAt(i);
            XWorksheet sheet = new XWorksheet(this);
            sheet.readObject(pSheet, options);
            sheets.add(sheet);
        }
    }

    public void dump(IPrintOut out) {
        dump(TreeOutImpl.from(out));
    }

    public void dump(ITreeOut out) {
        for (XWorksheet sheet : getSheets()) {
            // int nRow = sheet.getRowCount();
            out.println("Sheet: " + sheet.getName());
            out.enter();
            for (XRow row : sheet.getTable().getRows()) {
                int w = row.getCellCount();
                for (int i = 0; i < w; i++) {
                    XCell cell = row.get(i);
                    CellType type = cell.getType();
                    if (type == CellType._NONE)
                        continue;

                    String text = cell.getText();
                    // Object value = cell.getValue();

                    if (i != 0)
                        out.print("\t");
                    if (type == CellType.STRING) {
                        out.print(StringQuote.qqJavaString(text));
                    } else {
                        out.print(type);
                        out.print(":");
                        out.print(text);
                    }
                }
                out.println();
            }
            out.leave();
            out.println();
        }
    }

}
