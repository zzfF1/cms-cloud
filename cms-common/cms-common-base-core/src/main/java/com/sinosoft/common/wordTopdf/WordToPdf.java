package com.sinosoft.common.wordTopdf;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import com.sinosoft.common.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class WordToPdf extends HttpServlet {

    /**
     * 将word转成pdf并下载
     *
     * @param name     转换后的pdf文件名称
     * @param map      要填充的数据
     * @param wordPath word模板的路径
     * @param wordName word模板的名称
     */
    public void toPdf(String name, Map<String, Object> map, String wordPath, String wordName, HttpServletResponse response) {
        try {
            // 随机生成十位数
            UUID uuid = UUID.randomUUID();
            String replace = uuid.toString().replace("-", "");
            // 更换文件名称
            String newName = wordName + replace.substring(0, 10);
            //定义编码方式
            Configuration configuration = new Configuration();
            configuration.setDefaultEncoding("UTF-8");
            // 指定word模板目录路径
            configuration.setDirectoryForTemplateLoading(new File(wordPath));
            // 指定我们要使用的word模板
            Template template = configuration.getTemplate(wordName + ".ftl", "UTF-8");
            // word输出的路径
            String filePath = wordPath + "\\" + newName + ".doc";
            // 指定输出的位置
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
            // 执行填充数据
            template.process(map, bufferedWriter);
            // 将word转换为pdf
            FileInputStream docInputStream = new FileInputStream(filePath);
            // 获取pdf文件流
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO-8859-1") + ".pdf");
            // 获取响应的输出流
            OutputStream outputStream = response.getOutputStream();
            IConverter build = LocalConverter.builder().build();
            build.convert(docInputStream).as(DocumentType.DOC).to(outputStream).as(DocumentType.PDF).execute();
            build.shutDown();
            docInputStream.close();
            outputStream.close();
            bufferedWriter.flush();
            bufferedWriter.close();
            // 删除word文件
            Files .deleteIfExists(new File(filePath).toPath());
        } catch (Exception e) {
            log.error("word转pdf失败", e);
            throw new ServiceException("打印模板下载失败");
        }
    }
}
