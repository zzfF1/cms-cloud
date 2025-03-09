package com.sinosoft.common.excel.core;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;


/**
 * @program: cms6
 * @description: 表头样式
 * @author: zzf
 * @create: 2023-11-28 11:36
 */
@Slf4j
public class HeadStyleHandler implements RowWriteHandler {

    /**
     * 表头行数
     */
    private final int headRowNumber;

    public HeadStyleHandler(int headRowNumber) {
        this.headRowNumber = headRowNumber;
    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        Sheet sheet = row.getSheet();
        //不是首行
        if (((row.getRowNum() + 1) - headRowNumber) >= 0) {
            row.setHeight((short) 400);
            //循环单元格
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                //设置单元格背景色为蓝色
                CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
                Font font = sheet.getWorkbook().createFont();
                setCellStyle(cellStyle, font);
                cell.setCellStyle(cellStyle);

                //获取单元格内容
                String cellValue = cell.getStringCellValue();
                if (cellValue.contains("*")) {
                    Font redFont = sheet.getWorkbook().createFont();
                    redFont.setColor(IndexedColors.RED.getIndex());
                    redFont.setBold(true);  // 设置加粗
                    RichTextString richTextString = sheet.getWorkbook().getCreationHelper().createRichTextString(cellValue);
                    richTextString.applyFont(cellValue.indexOf("*"), cellValue.indexOf("*") + 1, redFont);
                    cell.setCellValue(richTextString);

                    // 添加批注
                    Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
                    Comment comment = drawingPatriarch.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 1, 0, (short) 2, 1));
                    // 输入批注信息
                    comment.setString(new XSSFRichTextString("必录"));
                    cell.setCellComment(comment);
                }
            }
        }
    }

    /**
     * 设置单元格样式
     *
     * @param newStyle 样式
     */
    public void setCellStyle(CellStyle newStyle, Font font) {
        // 设置背景色
        newStyle.setFillForegroundColor((short) 44);
        // 设置填充样式
        newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置字体
        font.setFontName("宋体");
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
