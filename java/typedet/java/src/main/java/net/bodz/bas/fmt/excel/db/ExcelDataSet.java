package net.bodz.bas.fmt.excel.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.bodz.bas.err.NoSuchKeyException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.excel.XWorkbook;
import net.bodz.bas.fmt.excel.XWorksheet;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.site.json.JsonResult;
import net.bodz.bas.t.catalog.DefaultCatalogMetadata;
import net.bodz.bas.t.catalog.IColumnMetadata;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.catalog.LoadFromJDBCOptions;
import net.bodz.bas.t.catalog.MutableTable;
import net.bodz.bas.t.catalog.TableOid;
import net.bodz.bas.t.tuple.Split;

public class ExcelDataSet {

    static final Logger logger = LoggerFactory.getLogger(ExcelDataSet.class);

    String defaultSchemaName; // = "public";
    String defaultTableName;

    boolean ignoreExcelSchemaName = true;
    boolean convertExcelNamesToLowerCase = true;
    boolean convertJDBCNamesToLowerCase = false;
    boolean ignoreMissingFields = true;
    JsonResult resp;

    Connection connection;
    DefaultCatalogMetadata catalog = new DefaultCatalogMetadata();
    private Map<TableOid, MutableTable> excelTables = new HashMap<>();

    public ExcelDataSet(Connection connection) {
        this.connection = connection;
    }

    public void setUp() {
        catalog.getSearchPath().add(defaultSchemaName);
    }

    public Collection<MutableTable> getExcelTables() {
        return excelTables.values();
    }

    public MutableTable getExcelTable(TableOid tableId) {
        if (ignoreExcelSchemaName)
            tableId = new TableOid(null, null, tableId.getTableName());
        return excelTables.get(tableId);
    }

    public void loadWorkbook(XWorkbook workbook) {
        int n = workbook.getSheetCount();
        for (int sheetIndex = 0; sheetIndex < n; sheetIndex++) {
            XWorksheet sheet = workbook.getSheet(sheetIndex);
            if (sheet.getName() == null) {
                logger.warn("ignored unnamed sheet. ");
                continue;
            }
            if (sheet.getRowCount() == 0) {
                logger.info("ignore empty sheet: " + sheet.getName());
                continue;
            }

            TableOid qName = new TableOid();
            String sheetName = sheet.getName();
            if (convertExcelNamesToLowerCase)
                sheetName = sheetName.toLowerCase();

            if (ignoreExcelSchemaName) {
                Split split = Split.headDomain(sheetName);
                sheetName = split.b; // ignore schema name.
            }

            qName.setFullName(sheetName);

            if (qName.getSchemaName() == null)
                qName.setSchemaName(defaultSchemaName);

            loadWorksheet(qName, sheet);
        }
    }

    MutableTable loadWorksheet(TableOid tableName, XWorksheet sheet) {
        ITableMetadata metadata;
        LoadFromJDBCOptions options = new LoadFromJDBCOptions();
        try {
            metadata = catalog.loadTableFromJDBC(tableName, connection, options);
        } catch (SQLException e) {
            resp.fail(e, "failed to get table metadata: " + e.getMessage());
            return null;
        }

        MutableTable table = new MutableTable(metadata);
        try {
            ExcelConv parser = new ExcelConv();

            Map<String, Integer> headerMap = convert(sheet.getTable().getRow(0).toMap(SortOrder.NONE));

            RowListFn.readFromSheet(table, sheet, parser, 1, (IColumnMetadata column) -> {
                String columnName = column.getName();
                if (convertJDBCNamesToLowerCase)
                    columnName = columnName.toLowerCase();
                Integer iCell = headerMap.get(columnName);
                if (iCell == null)
                    if (ignoreMissingFields)
                        return null;
                    else
                        throw new NoSuchKeyException(columnName);
                return iCell;
            });
        } catch (ParseException e) {
            e.printStackTrace();
            resp.fail(e, "parse error: " + e.getMessage());
            return null;
        }

        if (ignoreExcelSchemaName)
            tableName = new TableOid(null, null, tableName.getTableName());
        excelTables.put(tableName, table);
        return table;
    }

    <V> Map<String, V> convert(Map<String, V> map) {
        if (convertExcelNamesToLowerCase) {
            Map<String, V> conv = new HashMap<>();
            for (String k : map.keySet())
                conv.put(k.toLowerCase(), map.get(k));
            return conv;
        } else {
            return map;
        }
    }

}
