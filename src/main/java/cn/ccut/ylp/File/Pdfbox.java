package cn.ccut.ylp.File;



import java.io.*;
import java.util.List;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.PDFTextStripper;

// 本项目未使用该类，只做留存，以改进！
public class Pdfbox {

    public static void main(String[] args){
        String fileName = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\20200308002901.pdf";
        File pdfFile = new File(fileName);
        Pdfbox pdfbox = new Pdfbox();
        //pdfbox.readMethod2(fileName);
        String  result = pdfbox.getTextFromPDF(fileName);
        System.out.println(result);
        //pdfbox.test(fileName);
    }
    /**
     * 读取pdf中文字信息(全部)
     */
    public void readMethod1(String inputFile){
        //创建文档对象
        PDDocument doc =null;
        String content="";
        try {
            //加载一个pdf对象
            doc =PDDocument.load(new File(inputFile));
            //获取一个PDFTextStripper文本剥离对象
            PDFTextStripper textStripper =new PDFTextStripper();
            content=textStripper.getText(doc);
            System.out.println("内容:"+content);
            System.out.println("全部页数"+doc.getNumberOfPages());
            //关闭文档
            doc.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void readMethod2(String fileName){
        File file = new File(fileName);
        PDDocument document = null;
        try
        {
            // 方式一：
            /**
             InputStream input = null;
             input = new FileInputStream( pdfFile );
             //加载 pdf 文档
             PDFParser parser = new PDFParser(new RandomAccessBuffer(input));
             parser.parse();
             document = parser.getPDDocument();
             **/

            // 方式二：
            document=PDDocument.load(file);

            // 获取页码
            int pages = document.getNumberOfPages();

            // 读文本内容
            PDFTextStripper stripper=new PDFTextStripper();
            // 设置按顺序输出
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(pages);
            String content = stripper.getText(document);
            System.out.println(content);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    public void test(String fileName) {

        try {
            File file = new File(fileName);
            PDDocument doc = PDDocument.load(file);
//            COSDocument cosDocument = doc.getDocument();
//            List<COSObject> cosObjects = cosDocument.getObjects();
//            for (COSObject cosObject : cosObjects){
//                System.out.println(cosObject.toString());
//            }
            int noOfPage = doc.getNumberOfPages();
            //按页读取内容。
//            for (int i = 0;i<noOfPage;i++){
//                PDPage page = doc.getPage(i);
//                PDPageContentStream pd = new PDPageContentStream(doc,page);
//                COSStream cosStream = new COSStream();
//                PDFTextStripper textStripper = new PDFTextStripper();
//                int j = 9;
//                System.out.printf("j");
//            }
            PDFTextStripper textStripper = new PDFTextStripper();

            String str = textStripper.getText(doc);
            FileWriter writer;
            String writeFile = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\pdf文件测试\\text.txt";
            //将文本写入文本文件
            writer = new FileWriter(writeFile);
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  String getTextFromPDF(String pdfFilePath)
    {
        String result = null;
        PDDocument document = null;
        File file = new File(pdfFilePath);
        try {
            PDFParser parser = new PDFParser((RandomAccessRead) new RandomAccessFile(file,"rw"));
            parser.parse();
            document = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            result = stripper.getText(document);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


}

