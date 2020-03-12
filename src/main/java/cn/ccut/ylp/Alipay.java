package cn.ccut.ylp;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class Alipay {
    // 首先读取excel文件，然后将文件中的数据读取出来保存到模型中。
    public void read() throws ParseException {
        String filePath = Global.basePath;
        String fileName = "alipay_record_20200229_2124_1.csv";
        AlipayDB alipayDB = new AlipayDB();
        String filePathName = filePath + fileName;
        ReadCSV read = new ReadCSV();
        try {
            ArrayList<ArrayList<String>> rows =  read.readCSV(filePathName,"gbk");
            for (int j = 0;j<rows.size();j++){
                //将读取到的数据保存到数据库。
                ArrayList<String> row = rows.get(j);
                if (row.size() < 16){
                    // 当行数小于11时不是需要的数据。
                    continue;
                }
                Alipaym m = new Alipaym();
                // 交易号
                m.transactionNumber = row.get(0);
                // 商家订单号。
                m.merchantOrderNumber = row.get(1);;
                m.createDate = row.get(2);;
                m.payDate = row.get(3);

                m.modifyDate = row.get(4);
                m.transactionSource = row.get(5);
                m.type = row.get(6);
                // 对方户名
                m.counterpartyName = row.get(7);

                m.productName = row.get(8);

                m.amount=row.get(9);
                //收支类型
                m.ieType = row.get(10);
                m.tradingStatus=row.get(11);

                m.serviceFee = row.get(12);
                m.successfulRefund = row.get(13);
                m.note = row.get(14);
                m.fundingStatus =row.get(15);
                AlipayEntity e = alipayDB.convert(m);
                if (e == null){
                    //数据不合法，继续。
                    continue;
                }
                if (alipayDB.insert(e)){
                    // 返回false插入成功
                    System.out.println("错误数据："+m.transactionNumber+":"+m.amount+":"+m.ieType);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 插入完成关闭数据库连接。
        alipayDB.connectionDB.close();
    }
    public static void main(String[] args) throws ParseException {
        Alipay alipay = new Alipay();
        alipay.read();
    }
}
