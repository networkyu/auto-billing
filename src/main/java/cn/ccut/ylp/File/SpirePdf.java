package cn.ccut.ylp.File;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import sun.nio.ch.SelectorImpl;

/**
 * 使用spire pdf

 */
public class SpirePdf {
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static void main(String[] args) {
        String fileName = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\20200308002901.pdf";
        String htmlName = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\20200308002901.html";
        String writeName = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\20200308002901.txt";
        String xpsName = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\pdf文件测试\\xps.xps";
        String docName = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\pdf文件测试\\doc.doc";
        SpirePdf s = new SpirePdf();
        //s.filter(fileName);
        //s.xpsConvertByPdf(fileName,xpsName);
        s.convertWord(fileName,docName);
    }
    public void convertPdf(String pdfpath,String htmlpath){
        //加载PDF
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(pdfpath);
//保存为HTML格式
        pdf.saveToFile(htmlpath, FileFormat.HTML);
    }
    public void convertWord(String pdfpath,String wordpath){
        //加载PDF
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(pdfpath);
//保存为Word格式
        pdf.saveToFile(wordpath, FileFormat.DOCX);
    }
    public void xpsConvertByPdf(String pdfpath,String xpspath){
        //加载PDF
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(pdfpath);
//保存为XPS格式
        pdf.saveToFile(xpspath, FileFormat.XPS);
    }
    public StringBuilder read(String fileName){
        //创建PdfDocument实例
        PdfDocument doc= new PdfDocument();
        //加载PDF文件
        doc.loadFromFile(fileName);
        StringBuilder sb= new StringBuilder();
        PdfPageBase page;
        //遍历PDF页面，获取文本
        for(int i=0;i<doc.getPages().getCount();i++){
            page=doc.getPages().get(i);
            String txt = page.extractText(true);
            sb.append(txt);
        }
        doc.close();
        return sb;
//        FileWriter writer;
//
//        try {
//            //将文本写入文本文件
//            writer = new FileWriter(writeFile);
//            writer.write(sb.toString());
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
    public ArrayList<ArrayList<String>> filter(String fillPath){
        String str = read(fillPath).toString();
        //每行分隔
        String[] childs = str.split("\r\n");
        ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
        for (int i=0;i<childs.length;i++){
            String rowStr = childs[i];
            //取掉每一行首位空格。
            rowStr = rowStr.trim();
            if(!rowStr.equals("")){
                //首先截取字符串的10位字符，如果是日期，那么就从这儿开始，然匹配到下一个日期结束。
                if (rowStr.length() > 10){
                    String dateStr = rowStr.substring(0,10);
                    try {
                        Date startDate = simpleDateFormat.parse(dateStr);
                        String replaceStr = rowStr.replace("\r"," ");
                        replaceStr = replaceStr.replace("\n"," ");
                        String[] splits = replaceStr.split(" ");
                        ArrayList<String> cells = new ArrayList<String>();
                        for (int j = 0;j<splits.length;j++){
                            String str1 = splits[j].trim();
                            if (!str1.equals("")){
                                cells.add(str1);
                            }
                        }
                        if (cells.size() > 0){
                            rows.add(cells);
                        }
                    } catch (ParseException ex){
                        //解析失败不是开始日期，直接将数据丢弃
                        //筛选带金额的字符串，以核实丢弃有用数据。
                        if(!rowStr.equals("Evaluation Warning : The document was created with Spire.PDF for Java.")){
                            if (rowStr.contains(".")){
                                System.out.println("非正常格式数据，丢弃："+rowStr);
                            }
                        }
                    }
                } else {
                    System.out.println("行数据小于10个日期字符数据为："+rowStr);
                }
            }
        }
        return rows;
    }
}
