package net.bodz.bas.fmt.excel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;

public class ExcelUtil {

    public static String getCellText(Cell cell) {
        // The following won't work. So here the code.
        // String str = cell.toString();
        String asText = "";

        switch (cell.getCellType()) {
        case BLANK: // 空值
            asText = "";
            break;
        case BOOLEAN: // 布尔
            asText = String.valueOf(cell.getBooleanCellValue());
            break;
        case ERROR: // 故障
            asText = null;
            break;
        case FORMULA: // 公式
            Workbook wb = cell.getSheet().getWorkbook();
            CreationHelper helper = wb.getCreationHelper();
            FormulaEvaluator evaluator = helper.createFormulaEvaluator();
            Cell result = evaluator.evaluateInCell(cell);
            asText = getCellText(result);
            break;
        case NUMERIC: // 数字
            if (DateUtil.isCellDateFormatted(cell)) { // 如果是时间格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date theDate = cell.getDateCellValue();
                asText = sdf.format(theDate);
            } else {
                asText = NumberToTextConverter.toText(cell.getNumericCellValue());
            }
            break;
        case STRING: // 字符串
            asText = cell.getRichStringCellValue().getString();
            break;
        case _NONE:
        default:
            asText = null;
        }
        return asText;
    }

}
