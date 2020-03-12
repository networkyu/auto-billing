package cn.ccut.ylp.date;

import org.apache.commons.lang3.time.DateUtils;

import java.sql.Time;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class AutoConvertDate {
    public static String formart = "yyyyMMdd";
    public static String formartExact = "yyyyMMddHHmmss";
    public static String formartTime = "HHmmss";
    public static Date convert(String  str){
        Date date = null;
        if (str.contains("-")){
            str = str.replace("-","");
        }
        str = str.replace(":","");
        str = str.replace(" ","");
        str = str.replace("  ","");
        try {
            if (str.length() == 8){
                date = DateUtils.parseDate(str,formart);
            } else {
                if (str.length() == 14){
                    date = DateUtils.parseDate(str,formartExact);
                }
            }
        } catch (ParseException ex){
            ex.printStackTrace();
        }
        return date;
    }
    /**
     * 通过时间字符串获得时间的时，分，秒。
     * @param str
     * @return
     */
    public static int[] getTime(String str){
        int[] r = new int[3];
        String newStr = str.replace(":","");
        newStr = newStr.replace("-","");
        int hour = Integer.valueOf(newStr.substring(0,2));
        int minute =  Integer.valueOf(newStr.substring(2,4));
        int second =  Integer.valueOf(newStr.substring(4,6));
        r[0] = hour;
        r[1] = minute;
        r[2] = second;
        return r;
    }
    public static Time getSqlTime(String str) {
        String newStr = str.replace(":","");
        newStr = newStr.replace(".","");
        try {
            Date d = DateUtils.parseDate(newStr,formartTime);
            Time t = new Time(d.getTime());
            return t;
        } catch (ParseException ex1){

            ex1.printStackTrace();
        }
        return null;
    }
    public static Calendar convert(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }
    //将时分秒设为零
    public static Calendar setTimeZero(Calendar c){
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c;
    }
}
