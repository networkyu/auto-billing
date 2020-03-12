package cn.ccut.ylp;

/**
 * 全局配置类
 */
public class Global {
    public  static String basePath = "C:\\Users\\Administrator\\Desktop\\文件\\账单2017-01-01-2020-02-29\\";
    public static String dbuser = "root";
    public static String dbpass = "root";
    //招商银行只支持导出最近13个月的excel账单。其余均为pdf交易流水。
    public static String CMB_EXCEL_START_DATE_STR = "2019-01-04";
}
