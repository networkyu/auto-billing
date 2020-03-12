package cn.ccut.ylp;

import cn.ccut.ylp.File.SpirePdf;
import cn.ccut.ylp.date.AutoConvertDate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CmbPdf {

    private void readPdf(){

        String fileName1 = "20200308002901.pdf";
        String fileName2 = "20200308203403.pdf";
        SpirePdf spirePdf = new SpirePdf();
        ArrayList<ArrayList<String>> result1 = spirePdf.filter(Global.basePath + fileName1);
        if(pdfDataSave(result1)){
            System.out.println("第一部分保存成功！");
        }
        String endStr = "";
        //第一部分最后日期为，
        if (result1.size() > 0){
            for (int i=result1.size()-1;i>=0;i--){
                ArrayList<String> row = result1.get(i);
                if (row.size() > 4){
                    endStr = row.get(0);
                    break;
                }
            }
        }
        ArrayList<ArrayList<String>> result2 = spirePdf.filter(Global.basePath + fileName2);
        String startStr2 = "";//第二部分开始日期！
        if (endStr.equals("")){
            //无起始日期，全部执行。
            if(pdfDataSave(result2)){
                System.out.println("第二部分保存成功！");
            }
        } else {
            for (int i=0;i<result2.size();i++){
                ArrayList<String> row = result2.get(i);
                if (row.size() > 4){
                    String startStr = row.get(0);
                    Date endDate = AutoConvertDate.convert(endStr);
                    Date startDate = AutoConvertDate.convert(startStr);
                    if (startDate.after(endDate)){
                        //开始执行！
                        startStr2 = startStr;
                        break;
                    } else {
                        result2.remove(i);
                    }
                } else {
                    result2.remove(i);
                }
                i = i-1;
            }
        }
        System.out.println("第一部分结束日期："+endStr+" 第二部分开始日期:"+startStr2);
        if(pdfDataSave(result2)){
            System.out.println("第二部分成功结束！");
        }
    }
    private boolean pdfDataSave(ArrayList<ArrayList<String>> rows){
        CmbPdfDB cmbPdfDB = new CmbPdfDB();
        for (int j = 0;j<rows.size();j++){
            //将读取到的数据保存到数据库。
            ArrayList<String> row = rows.get(j);
            if (row.size() < 5){
                StringBuilder stringBuilder = new StringBuilder();
                for(String r:row){
                    stringBuilder.append(r);
                    stringBuilder = stringBuilder.append("|");
                }
                // 当行数小于7时不是需要的数据。
                System.out.println("小于5列被抛弃的数据为："+stringBuilder.toString());
                continue;
            }
            CmbPdfm m = new CmbPdfm();
            m.date = row.get(0);
            m.currency = row.get(1);
            String tepAmount = row.get(2);
            m.amount = tepAmount.replace(",","");//取掉万位分隔符。
            String balanceStr = row.get(3);
            m.balance = balanceStr.replace(",","");;
            m.transactionType = row.get(4);
            if(row.size()>=6){
                m.counterParty = row.get(5);
            }
            CmbPdfEntity e = null;
            e = cmbPdfDB.convert(m);
            if (e == null){
                //数据不合法，继续。
                continue;
            }
            if (cmbPdfDB.insert(e)){
                // 返回false插入成功
                System.out.println("错误数据："+m.date+":"+m.date+"| 余额:"+m.balance);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        CmbPdf cmbPdf = new CmbPdf();
        //cmbPdf.readPdf();

    }
}
