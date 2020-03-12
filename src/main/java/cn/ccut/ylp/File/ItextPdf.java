
package cn.ccut.ylp.File;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 创建日期：2017-10-13下午2:28:35
 * 修改日期：
 * 作者：ttan
 * 描述：iText读取PDF
 */
// 本项目未使用该类，只做留存，以改进！
public class ItextPdf {
    public static void main(String[] args) throws IOException {
        String outputPath = "E:\\ReadPdf\\PdfContent_1.txt";
        //PrintWriter writer = new PrintWriter(new FileOutputStream(outputPath));
        String fileName = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\20200308002901.pdf";
        ItextPdf r = new ItextPdf();
        String result = r.getPdfFileText(fileName);
        System.out.println(result);

        //readPdf_filter(fileName);//读取PDF面的某个区域

    }
    public void readPdf(String fileName) throws IOException {

    }
    /**
     * <p>Title: pdf extraction</p>
     * <p>Description: email:chris@matrix.org.cn</p>
     * <p>Copyright: Matrix Copyright (c) 2003</p>
     * <p>Company: Matrix.org.cn</p>
     * @author chris
     * @version 1.0,who use this example pls remain the declare
     */
    public  String getPdfFileText(String fileName) throws IOException {
        PdfReader reader = new PdfReader(fileName);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        StringBuffer buff = new StringBuffer();
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i,
                    new SimpleTextExtractionStrategy());
            buff.append(strategy.getResultantText());
        }
        return buff.toString();
    }





}