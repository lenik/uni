package net.bodz.bas.fmt.excel;

import javax.xml.stream.XMLStreamException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.xssf.usermodel.XSSFCell;

import net.bodz.bas.err.LoaderException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.excel.ss.SsGroup;
import net.bodz.bas.fmt.xml.IXmlForm;
import net.bodz.bas.fmt.xml.IXmlOutput;
import net.bodz.bas.fmt.xml.xq.IElement;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class XCell
        implements
            IXmlForm {

    static final Logger logger = LoggerFactory.getLogger(XCell.class);

    XRow row;

    int index;
    SsGroup ss = new SsGroup();

    CellType type = CellType._NONE;
    Object value;
    String text;

    public XCell(XRow row, int index) {
        this.row = row;
        this.index = index;
    }

    public XRow getRow() {
        return row;
    }

    public XTable getTable() {
        return row.getTable();
    }

    public XWorksheet getSheet() {
        return row.getTable().getSheet();
    }

    public XWorkbook getWorkbook() {
        return row.getTable().getSheet().getWorkbook();
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        if (type == null)
            throw new NullPointerException("type");
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value, String text) {
        this.value = value;
        this.text = text;
    }

    public void _setValue(Object value) {
        this.value = value;
        if (value == null)
            this.text = null;
        else
            this.text = value.toString();
    }

    public String getText() {
        return text;
    }

    @Override
    public void writeObject(IXmlOutput out)
            throws XMLStreamException {
        out.beginElement("Cell");
        if (text != null)
            out.writeCharacters(text);
        out.endElement();
    }

    @Override
    public void readObject(IElement element)
            throws ParseException, LoaderException {
        value = text = element.getTextContent();
    }

    public void readObject(Cell pCell, ExcelParseOptions options) {
        if (pCell == null)
            throw new NullPointerException("pCell");

        type = pCell.getCellType();
        // text = pCell.toString();
        text = ExcelUtil.getCellText(pCell);

        if (options.useRawText) {
            if (pCell instanceof XSSFCell) {
                XSSFCell xssfCell = (XSSFCell) pCell;
                text = xssfCell.getRawValue();
            }
        }

        switch (type) {
        case BOOLEAN:
            value = pCell.getBooleanCellValue();
            break;

        case ERROR:
            byte errorByte = pCell.getErrorCellValue();
            FormulaError error = FormulaError.forInt(errorByte);
            value = error;
            break;

        case FORMULA:
            CellValue val = getWorkbook().evaluator.evaluate(pCell);
            readObjectFromEvaluated(val);
            break;

        case NUMERIC:
            value = pCell.getNumericCellValue();
            break;

        case BLANK:
        case STRING:
            value = pCell.getStringCellValue();
            break;

        default:
            logger.error("invalid cell type: " + type);
            value = null;
        }
    }

    public void readObjectFromEvaluated(CellValue pCellVal) {
        type = pCellVal.getCellType();
        text = pCellVal.formatAsString();
        switch (type) {
        case BOOLEAN:
            value = pCellVal.getBooleanValue();
            break;

        case ERROR:
            byte errorByte = pCellVal.getErrorValue();
            FormulaError error = FormulaError.forInt(errorByte);
            value = error;
            break;

        case NUMERIC:
            value = pCellVal.getNumberValue();
            break;

        case BLANK:
        case STRING:
            value = pCellVal.getStringValue();
            break;

        default:
            logger.error("invalid cell value type: " + type);
            value = null;
        }
    }

    @Override
    public String toString() {
        return text;
    }

}
