package cn.ccut.ylp;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

public class CmbEntity {
    public Date datetime;
    //交易日期
    public java.sql.Date date;
    //交易时间
    public Time time;
    public BigDecimal income;
    public BigDecimal expenditure;
    public BigDecimal balance;
    public String transactionType;
    // 交易备注
    public String transactionNotes;
}
