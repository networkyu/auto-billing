package cn.ccut.ylp;

import org.apache.commons.lang3.time.DateFormatUtils;
import sun.nio.cs.ext.Big5;

import java.math.BigDecimal;
import java.util.Date;

public class AlipayEntity {
    // 交易号
    public String transactionNumber;
    // 商家订单号。
    public String merchantOrderNumber;
    public Date createDate;
    public Date payDate;

    public Date modifyDate;
    public String transactionSource;
    // 对方户名
    public String counterpartyName;
    public String productName;
    public String type;
    public BigDecimal amount;
    //收支类型
    public String ieType;
    public String tradingStatus;
    public BigDecimal serviceFee;
    public BigDecimal successfulRefund;
    public String note;
    public String fundingStatus;

    @Override
    public String toString() {
        return  DateFormatUtils.format(createDate,"yyyy-MM-dd HH:mm:ss") + "|"+ieType + amount.setScale(2,BigDecimal.ROUND_HALF_UP)+"|"+counterpartyName;
    }
}
