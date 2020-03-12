package cn.ccut.ylp;

import cn.ccut.ylp.File.SpirePdf;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class Cmb {
    // 首先读取excel文件，然后将文件中的数据读取出来保存到模型中。
    public void read() throws ParseException {
        String filePath = Global.basePath;
        String fileName = "招商全部账单.csv";
        CmbDB cmbDB = new CmbDB();
        String filePathName = filePath + fileName;
        ReadCSV read = new ReadCSV();
        try {
            ArrayList<ArrayList<String>> rows =  read.readCSV(filePathName,"gbk");
            for (int j = 0;j<rows.size();j++){
                //将读取到的数据保存到数据库。
                ArrayList<String> row = rows.get(j);
                if (row.size() < 7){
                    // 当行数小于7时不是需要的数据。
                    continue;
                }
                Cmbm m = new Cmbm();
                m.date = row.get(0);
                m.time = row.get(1);
                m.income = row.get(2);
                m.expenditure = row.get(3);
                m.balance = row.get(4);
                m.transactionType = row.get(5);
                m.transactionNotes = row.get(6);
                if(m.time.equals("")){
                    m.datetime = m.date+"00:00:00";
                } else {
                    m.datetime = m.date + m.time;
                }
                CmbEntity e = cmbDB.convert(m);
                if (e == null){
                    //数据不合法，继续。
                    continue;
                }
                if (cmbDB.insert(e)){
                    // 返回false插入成功
                    System.out.println("错误数据："+m.date+":"+m.time+":"+m.balance);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 插入完成关闭数据库连接。
        cmbDB.connectionDB.close();
    }
    public static void main(String[] args) throws ParseException {
        Cmb cmb = new Cmb();
        cmb.read();
    }
}
