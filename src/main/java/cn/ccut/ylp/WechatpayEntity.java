package cn.ccut.ylp;

import java.math.BigDecimal;
import java.util.Date;

public class WechatpayEntity {
    //交易日期
    public Date datetime;
    public String transactionType;
    // 交易对方
    public String counterpartyName;
    public String productName;
    //收支类型
    public String ieType;
    public BigDecimal amount;
    // 支付方式
    public String paymentMethod;
    public String currentStatus;
    // 交易号
    public String transactionNumber;
    // 商家订单号。
    public String merchantOrderNumber;
    public String note;
}
