package com.sinosoft.common.excel.core;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.sinosoft.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.Map;
import java.util.Optional;

/**
 * @program: cms6
 * @description: 锁定首行首列样式处理器
 * @author: zzf
 * @create: 2023-11-28 11:36
 */
@Slf4j
public class FreezePaneHandler implements SheetWriteHandler {
    /**
     * 单选数据Sheet名
     */
    private static final String OPTIONS_SHEET_NAME = "options";
    /**
     * Excel表格中的列名英文
     * 仅为了解析列英文，禁止修改
     */
    private static final String EXCEL_COLUMN_NAME = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * 下拉框map
     */
    private final Map<Integer, ExcelSelectedResolve> selectedMap;
    /**
     * 冻结行数
     */
    private final int freezeRowNumber;
    /**
     * 写入列数
     */
    private final int writeColumns;
    /**
     * 标题
     */
    private final String title;
    /**
     * 当前单选进度
     */
    private int currentOptionsColumnIndex;

    public FreezePaneHandler(int freezeRowNumber, int writeColumns, String title, Map<Integer, ExcelSelectedResolve> selectedMap) {
        this.freezeRowNumber = freezeRowNumber;
        this.writeColumns = writeColumns;
        this.selectedMap = selectedMap;
        this.title = title;
        this.currentOptionsColumnIndex = 0;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet = workbook.getSheetAt(0);
        if (StringUtils.isNotBlank(title)) {
            sheet.createRow(0).createCell(0).setCellValue(title);
            //创建行
            Row row = sheet.createRow(0);
            //设置标题
            row.createCell(0).setCellValue(this.title);
            //设置行高
            row.setHeight((short) 600);
            //合并标题行
            sheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 0, this.writeColumns - 1));
            CellStyle cellStyle = workbook.createCellStyle();
            //设置字体
            Font font = workbook.createFont();
            //设置样式
            setCellStyle(cellStyle, font, (short) 350);
            row.getCell(0).setCellStyle(cellStyle);
        }
        //如果锁定行大于0
        if (freezeRowNumber > 0) {
            //锁定标题行
            sheet.createFreezePane(0, freezeRowNumber);
        }
        // 开始设置下拉框 HSSFWorkbook
        DataValidationHelper helper = sheet.getDataValidationHelper();
        selectedMap.forEach((k, v) -> {
            //设置下拉
            dropDownWithSheet(helper, workbook, sheet, k, v);
        });
    }

    /**
     * <h2>额外表格形式的普通下拉框</h2>
     * 由于下拉框可选值数量过多，为提升Excel打开效率，使用额外表格形式做下拉
     *
     * @param helper   下拉设置
     * @param workbook work对象
     * @param sheet    sheet对象
     * @param celIndex 下拉选
     * @param excelSel 下拉选可选值
     */
    private void dropDownWithSheet(DataValidationHelper helper, Workbook workbook, Sheet sheet, Integer celIndex, ExcelSelectedResolve excelSel) {
        // 创建下拉数据表
        Sheet simpleDataSheet = Optional.ofNullable(workbook.getSheet(WorkbookUtil.createSafeSheetName(OPTIONS_SHEET_NAME)))
            .orElseGet(() -> workbook.createSheet(WorkbookUtil.createSafeSheetName(OPTIONS_SHEET_NAME)));
        // 将下拉表隐藏
        workbook.setSheetHidden(workbook.getSheetIndex(simpleDataSheet), true);
        String[] value = excelSel.getSource();
        // 完善纵向的一级选项数据表
        for (int i = 0; i < value.length; i++) {
            int finalI = i;
            // 获取每一选项行，如果没有则创建
            Row row = Optional.ofNullable(simpleDataSheet.getRow(i)).orElseGet(() -> simpleDataSheet.createRow(finalI));
            // 获取本级选项对应的选项列，如果没有则创建
            Cell cell = Optional.ofNullable(row.getCell(currentOptionsColumnIndex)).orElseGet(() -> row.createCell(currentOptionsColumnIndex));
            // 设置值
            cell.setCellValue(value[i]);
        }
        // 创建名称管理器
        Name name = workbook.createName();
        // 设置名称管理器的别名
        String nameName = String.format("%s_%d", OPTIONS_SHEET_NAME, celIndex);
        name.setNameName(nameName);
        // 以纵向第一列创建一级下拉拼接引用位置
        String function = String.format("%s!$%s$1:$%s$%d", OPTIONS_SHEET_NAME,
            getExcelColumnName(currentOptionsColumnIndex),
            getExcelColumnName(currentOptionsColumnIndex),
            value.length);
        // 设置名称管理器的引用位置
        name.setRefersToFormula(function);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList addressList = new CellRangeAddressList(1, excelSel.getLastRow(), celIndex, celIndex);
        // 数据有效性对象
        DataValidation dataValidation = helper.createValidation(helper.createFormulaListConstraint(nameName), addressList);
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            sheet.addValidationData(dataValidation);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation);
        currentOptionsColumnIndex++;
    }

    /**
     * <h2>依据列index获取列名英文</h2>
     * 依据列index转换为Excel中的列名英文
     * <p>例如第1列，index为0，解析出来为A列</p>
     * 第27列，index为26，解析为AA列
     * <p>第28列，index为27，解析为AB列</p>
     *
     * @param columnIndex 列index
     * @return 列index所在得英文名
     */
    private String getExcelColumnName(int columnIndex) {
        // 26一循环的次数
        int columnCircleCount = columnIndex / 26;
        // 26一循环内的位置
        int thisCircleColumnIndex = columnIndex % 26;
        // 26一循环的次数大于0，则视为栏名至少两位
        String columnPrefix = columnCircleCount == 0
            ? StrUtil.EMPTY
            : StrUtil.subWithLength(EXCEL_COLUMN_NAME, columnCircleCount - 1, 1);
        // 从26一循环内取对应的栏位名
        String columnNext = StrUtil.subWithLength(EXCEL_COLUMN_NAME, thisCircleColumnIndex, 1);
        // 将二者拼接即为最终的栏位名
        return columnPrefix + columnNext;
    }

    /**
     * 设置单元格样式
     *
     * @param newStyle   样式
     * @param fontHeight 字体高度
     */
    public void setCellStyle(CellStyle newStyle, Font font, short fontHeight) {
        // 设置背景色
        newStyle.setFillForegroundColor((short) 44);
        // 设置填充样式
        newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置字体
        font.setFontName("宋体");
        // 设置字体高度
        font.setFontHeight(fontHeight);
        newStyle.setFont(font);
        // 设置垂直居中
        newStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置水平居中
        newStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置边框
        newStyle.setBorderTop(BorderStyle.THIN);
        newStyle.setBorderBottom(BorderStyle.THIN);
        newStyle.setBorderLeft(BorderStyle.THIN);
        newStyle.setBorderRight(BorderStyle.THIN);
    }
}
