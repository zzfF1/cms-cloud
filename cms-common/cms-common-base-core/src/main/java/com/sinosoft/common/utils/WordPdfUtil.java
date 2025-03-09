package com.sinosoft.common.utils;


import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @program: cms6
 * @description: word转pdf
 * @author: zzf
 * @create: 2024-01-16 11:09
 */
@Slf4j
public class WordPdfUtil {

    /**
     * doc转pdf
     *
     * @param wordPath
     * @param pdfPath
     */
    public static void doc2pdf(String wordPath, String pdfPath) {
        try {
            long old = System.currentTimeMillis();
            //新建一个pdf文档
            File file = new File(pdfPath);
            FileOutputStream os = new FileOutputStream(file);
            //Address是将要被转化的word文档
            Document doc = new Document(wordPath);
            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            doc.save(os, SaveFormat.PDF);
            long now = System.currentTimeMillis();
            os.close();
            //转化用时
            System.out.println("Word 转 Pdf 共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            System.out.println("Word 转 Pdf 失败..."+e.getMessage());
        }
    }

    /**
     * doc转pdf
     * @param fileInputStream
     * @param response
     */
    public static void doc2pdf(FileInputStream fileInputStream, HttpServletResponse response) {
        try {
            long old = System.currentTimeMillis();
            //Address是将要被转化的word文档
            Document doc = new Document(fileInputStream);
            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            doc.save(response.getOutputStream(), SaveFormat.PDF);
            long now = System.currentTimeMillis();
            //转化用时
            System.out.println("Word 转 Pdf 共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            System.out.println("Word 转 Pdf 失败..."+e.getMessage());
        }finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
