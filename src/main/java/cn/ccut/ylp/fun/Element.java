package cn.ccut.ylp.fun;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * 对账要素,将各个银行的数据汇总到一个模型里面。
 */
public class Element {
    // 日期
    Calendar datetime;
    //金额，正为收入，负为支出。
    BigDecimal amount;
    // 重要信息
    String important;
    // 银行名称
    String bank;

    @Override
    public String toString() {
        return  DateFormatUtils.format(datetime,"yyyy-MM-dd")+
                "|"+amount.setScale(2,BigDecimal.ROUND_HALF_UP)+
                "|"+important+"|"+bank;
    }
}
