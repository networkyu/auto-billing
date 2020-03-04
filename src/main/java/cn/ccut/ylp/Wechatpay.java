package cn.ccut.ylp;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Wechatpay {
    public static void main(String[] args) throws ParseException {
       Wechatpay wechatpay = new Wechatpay();
       wechatpay.read();
    }
    public void read()throws ParseException{
        // 间隔三个月。
        int gap = 3;
        String dateFS = "yyyyMMdd";
        String basePath = Global.basePath;
        String fixedName ="微信支付账单";
        String zipSuffix = ".zip";
        String csvSuffix = ".csv";
        /*微信导出账单带密码，请按开始日期，依次将密码填入*/
        String[] pass = {
                "741488","115132","110156","532550",
                "449752","669098","698232","581645",
//                581645 18 10-12
                "630106","463158","635159","804042",
                "671738","","","",
                "","","","",
                "","","","",
                "","","","",
                "","","","",

        };
        // 微信账单开始日期和结束日期。
        Date startD = DateUtils.parseDate("20170101",dateFS);
        Date cuurtD = DateUtils.parseDate("20200229",dateFS);
        UnZipUtils unZipUtils = new UnZipUtils();
        int i = 0 ;
        // 保存数据用
        WechatpayDB wechatpayDB = new WechatpayDB();
        ReadCSV read = new ReadCSV();
        while (startD.before(cuurtD)){
            // TODO 解压文件
            Date endD = DateUtils.addMonths(startD,gap);
            if (endD.after(cuurtD)){
                endD = cuurtD;
            }
            Date tempDate = endD;
            if (endD.before(cuurtD)){
                endD = DateUtils.addDays(endD,-1);
            }
            StringBuilder spliceString = new StringBuilder(fixedName);
            spliceString.append("(");
            spliceString.append(DateFormatUtils.format(startD,dateFS));
            spliceString.append("-");
            spliceString.append(DateFormatUtils.format(endD,dateFS));
            spliceString.append( ")");
            String fileName = spliceString.toString();
            startD = tempDate;
            spliceString.append(zipSuffix);
            String unzipFile = basePath + fileName + "\\" + fileName + csvSuffix;
            File file = new File(unzipFile);
            if (!file.exists()){
                unZipUtils.unZip(spliceString.insert(0,basePath).toString(),basePath,pass[i]);
            } else {
                spliceString.insert(0,basePath);
            }
            i = i + 1;
            // TODO 储存数据
            // 目标文件名为
            spliceString = spliceString.delete(spliceString.length() - 4,spliceString.length());
            spliceString.append("\\");
            spliceString.append(fileName);
            spliceString.append(csvSuffix);
            try {
                //System.out.println("读取文件路径为："+ spliceString.toString());
                ArrayList<ArrayList<String>> rows =  read.readCSV(spliceString.toString());
                for (int j = 0;j<rows.size();j++){
                    //将读取到的数据保存到数据库。
                    ArrayList<String> row = rows.get(j);
                    if (row.size() < 11){
                        // 当行数小于11时不是需要的数据。
                        continue;
                    }
                    Wechatpaym m = new Wechatpaym();
                    m.datetime = row.get(0);
                    m.transactionType = row.get(1);
                    m.counterpartyName = row.get(2);
                    m.productName = row.get(3);
                    m.ieType = row.get(4);
                    m.amount = row.get(5);
                    String s1 = m.amount.substring(0,1);
                    // 处理金额前面的金钱￥符号，符号是从日志中复制的。
                    if (s1.equals("¥")){
                        m.amount = m.amount.substring(1,m.amount.length() - 1);
                    }
                    m.paymentMethod = row.get(6);
                    m.currentStatus = row.get(7);
                    m.transactionNumber = row.get(8);
                    m.merchantOrderNumber = row.get(9);
                    m.note = row.get(10);
                    WechatpayEntity e = wechatpayDB.convert(m);
                    if (e == null){
                        //数据不合法，继续。
                        continue;
                    }
                    if (wechatpayDB.insert(e)){
                        // 返回false插入成功
                        System.out.println("错误数据："+m.transactionNumber+":"+m.datetime+":"+m.amount);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 插入完成关闭数据库连接。
        wechatpayDB.connectionDB.close();
    }
}
