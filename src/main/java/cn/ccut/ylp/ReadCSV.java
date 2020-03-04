package cn.ccut.ylp;

import java.io.BufferedReader;
import java.io.FileReader;


import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ReadCSV {
    public static char separator = ',';
    public static void main(String[] args) throws Exception {
//        // 测试导出
//        String filePath = "D:/scoreInfo.csv";
//        List<String[]> dataList = new ArrayList<String[]>();
//        //添加标题
//        dataList.add(new String[]{"学号", "姓名", "分数"});
//        for (int i = 0; i < 10; i++) {
//            dataList.add(new String[]{"2010000" + i, "张三" + i, "8" + i});
//        }
//        createCSV(dataList, filePath);

        // 读取CSV文件
        String filePath = Global.basePath;
        String fileName = "招商全部账单.csv";
        readCSV(filePath + fileName);
    }

    /**
     * 读取CSV文件
     * @param filePath:全路径名
     */
    public static ArrayList<ArrayList<String>> readCSV(String filePath) throws Exception {
        CsvReader reader = null;
        ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
        try {
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            reader = new CsvReader(filePath, separator, Charset.forName("utf-8"));
            // 读取表头
            reader.readHeaders();
            // 逐条读取记录，直至读完
            while (reader.readRecord()) {
                // 读一整行
//                System.out.println(reader.getRawRecord());
                String row = reader.getRawRecord();
                // 返回列数。
                ArrayList<String> cells = new ArrayList<String>();
                for (int i = 0 ;i < reader.getColumnCount();i++){
                    String cell = reader.get(i);
                    cell = cell.trim();
                    cells.add(cell);
                }
                rows.add(cells);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
        return rows;
    }
    /**
     * 生成CSV文件
     * @param dataList:数据集
     * @param filePath:全路径名
     */
    public static boolean createCSV(List<String[]> dataList, String filePath) throws Exception {
        boolean isSuccess = false;
        CsvWriter writer = null;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath, true);
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            writer = new CsvWriter(out, separator, Charset.forName("GBK"));
            for (String[] strs : dataList) {
                writer.writeRecord(strs);
            }
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSuccess;
    }
}



