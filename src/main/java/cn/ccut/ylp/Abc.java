package cn.ccut.ylp;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

// 农业银行
public class Abc {
//    public  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    // 首先读取excel文件，然后将文件中的数据读取出来保存到模型中。
    public void read() throws ParseException {
        String filePath = Global.basePath;
        String tempName = "农行-年.xls";
        int starYear = 2018;
        int endYear = 2020;
        AbcDB abcDB = new AbcDB();
        for (int i = starYear;i <= endYear; i++){
            String year = String.valueOf(i);
            String fileName = tempName.replace("-",year);
            String filePathName = filePath + fileName;
            Read read = new Read();
            try {
                ArrayList<ArrayList<String>> rows =  read.readRows(filePathName);
                for (int j = 0;j<rows.size();j++){
                    //将读取到的数据保存到数据库。
                    ArrayList<String> row = rows.get(j);
                    if (row.size() < 11){
                        // 当行数小于11时不是需要的数据。
                        continue;
                    }
                    Abcm m = new Abcm();
                    m.date = row.get(0);
                    m.time = row.get(1);
                    if (m.time.equals("")){
                        //如果为空字符串那么将时间设为00:00：00
                        m.time = "000000";
                    }
                    m.amount = row.get(2);
                    m.balance = row.get(3);
                    m.counterpartyName = row.get(4);
                    m.counterpartyAccount = row.get(5);
                    m.tradingBank = row.get(6);
                    m.tradingChannels = row.get(7);
                    m.transactionType = row.get(8);
                    m.transactionPurpose = row.get(9);
                    m.tradingSummary = row.get(10);
                    m.datetime = m.date + m.time;
                    AbcEntity e = abcDB.convert(m);
                    if (e == null){
                        //数据不合法，继续。
                        continue;
                    }
                    if (abcDB.insert(e)){
                        // 返回false插入成功
                        System.out.println("错误数据："+m.date+":"+m.time+":"+m.amount);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 插入完成关闭数据库连接。
        abcDB.connectionDB.close();
    }
    public static void main(String[] args) throws ParseException {
        Abc abc = new Abc();
        abc.read();
    }
}
