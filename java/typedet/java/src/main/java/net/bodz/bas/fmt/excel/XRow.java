package net.bodz.bas.fmt.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.stream.XMLStreamException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Element;

import net.bodz.bas.err.LoaderException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.xml.IXmlForm;
import net.bodz.bas.fmt.xml.IXmlOutput;
import net.bodz.bas.fmt.xml.xq.IElement;
import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.t.pojo.Pair;

public class XRow
        implements
            IXmlForm {

    XTable table;
    int index;

    List<XCell> cells = new ArrayList<>();

    public XRow(XTable table, int index) {
        this.table = table;
        this.index = index;
    }

    public XTable getTable() {
        return table;
    }

    public XWorksheet getSheet() {
        return table.getSheet();
    }

    public XWorkbook getWorkbook() {
        return table.getSheet().getWorkbook();
    }

    public List<XCell> getCells() {
        return cells;
    }

    public int getCellCount() {
        return cells.size();
    }

    public XCell get(int column) {
        return cells.get(column);
    }

    public String getText(int column) {
        return cells.get(column).getText();
    }

    @Override
    public void writeObject(IXmlOutput out)
            throws XMLStreamException {
        out.beginElement("Row");
        for (XCell cell : cells)
            cell.writeObject(out);
        out.endElement();
    }

    @Override
    public void readObject(IElement element)
            throws ParseException, LoaderException {
        for (Element child : dom.childElements(element)) {
            switch (child.getTagName()) {
            case "Cell":
                XCell cell = new XCell(this, cells.size());
                cell.readObject(child);
                cells.add(cell);
                break;
            }
        }
    }

    public void readObject(Row pRow, ExcelParseOptions options) {
        int first = pRow.getFirstCellNum();
        int last = pRow.getLastCellNum();
        for (int iCell = first; iCell < last; iCell++) {
            if (iCell == -1)
                continue;

            XCell cell = new XCell(this, cells.size());
            Cell pCell = pRow.getCell(iCell);
            if (pCell != null)
                cell.readObject(pCell, options);

            cells.add(cell);
        }
    }

    @Override
    public String toString() {
        return cells.toString();
    }

    public Map<String, Integer> toMap(SortOrder order) {
        return toMap(order, null, null);
    }

    public Map<String, Integer> toMap(SortOrder order, Function<String, String> norm, Predicate<String> predicate) {
        if (norm == null)
            norm = String::trim;
        if (predicate == null)
            predicate = ((Predicate<String>) String::isEmpty).negate();
        Map<String, Integer> map = order.newMap();
        int nCell = getCellCount();
        for (int iCell = 0; iCell < nCell; iCell++) {
            XCell cell = get(iCell);
            String cellText = cell.getText();
            if (cellText == null)
                continue;
            cellText = norm.apply(cellText);
            if (!predicate.test(cellText))
                continue;
            map.put(cellText, iCell);
        }
        return map;
    }

    public List<Pair<Integer, String>> toPairs() {
        return toPairs((String text) -> !text.isEmpty());
    }

    public List<Pair<Integer, String>> toPairs(Predicate<String> predicate) {
        List<Pair<Integer, String>> pairs = new ArrayList<>();
        int nCell = getCellCount();
        for (int iCell = 0; iCell < nCell; iCell++) {
            XCell cell = get(iCell);
            String cellText = cell.getText();
            if (cellText == null)
                continue;
            cellText = cellText.trim();
            if (!predicate.test(cellText))
                continue;
            pairs.add(Pair.of(iCell, cellText));
        }
        return pairs;
    }

}
