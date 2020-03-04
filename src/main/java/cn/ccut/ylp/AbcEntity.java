package cn.ccut.ylp;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Date;

public class AbcEntity {
    // 唯一日期+时间
    public java.util.Date datetime;
    public Date date;
    public Time time;
    public BigDecimal amount;
    public BigDecimal balance;
    // 对方户名
    public String counterpartyName;
    //对方账号
    public String counterpartyAccount;
    public String tradingBank;
    public String tradingChannels;
    public String transactionType;
    // 交易用途
    public String transactionPurpose;
    // 交易摘要
    public String tradingSummary;

}
