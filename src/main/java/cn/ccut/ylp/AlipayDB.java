package cn.ccut.ylp;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;

public class AlipayDB {
    public ConnectionDB connectionDB;
    //声名JDBC对象
    public Connection conn=null;
    public PreparedStatement ps=null;
    public ResultSet rs=null;
    public boolean insert(AlipayEntity e){
        String sql = "INSERT INTO `bill`.`alipay` (" +
                "`transaction_number`, `merchant_order_number`, `create_date`, `pay_date`, " +
                "`modify_date`, `transaction_source`, `counterparty_name`, `product_name`," +
                " `type`, `amount`,`ie_type`, `trading_status`," +
                " `service_fee`, `successful_refund`,`note`, `funding_status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = connectionDB.conn.prepareStatement(sql);
            ps.setString(1,e.transactionNumber);
            ps.setString(2,e.merchantOrderNumber);
            ps.setTimestamp(3,new Timestamp(e.createDate.getTime()));
            if(e.payDate != null){
                ps.setTimestamp(4,new Timestamp(e.payDate.getTime()));
            } else {
                ps.setTimestamp(4,null);
            }
            if(e.modifyDate != null) {
                ps.setTimestamp(5,new Timestamp(e.modifyDate.getTime()));
            } else {
                ps.setTimestamp(5,null);
            }
            ps.setString(6,e.transactionSource);
            ps.setString(7,e.counterpartyName);
            ps.setString(8,e.productName);

            ps.setString(9,e.type);
            ps.setBigDecimal(10,e.amount);
            ps.setString(11,e.ieType);
            ps.setString(12,e.tradingStatus);

            ps.setBigDecimal(13,e.serviceFee);
            ps.setBigDecimal(14,e.successfulRefund);
            ps.setString(15,e.note);
            ps.setString(16,e.fundingStatus);

            boolean result = ps.execute();
            return result;
        } catch (Exception ex){
            if(ex instanceof SQLIntegrityConstraintViolationException){
                //主键冲突返回false，说明已经插入了。
                return false;
            }
        }
        return true;
    }
    public AlipayDB(){
        connectionDB = new ConnectionDB();
        conn = connectionDB.conn;
        ps = connectionDB.ps;
        rs = connectionDB.rs;
    }
    public AlipayEntity convert(Alipaym m) throws ParseException {
        if(m == null) {
            return null;
        }
        AlipayEntity e = new AlipayEntity();
        try {
//            DateConverter converter = new DateConverter();
//            converter.setPattern(new String("yyyy/MM/dd HH:mm:ss"));
//            BeanUtils.register(converter,Date.class);
//            BeanUtils.copyProperties(e,m);
            e.transactionNumber = m.transactionNumber;
            // 商家订单号。
            e.merchantOrderNumber = m.merchantOrderNumber;
            e.transactionSource = m.transactionSource;
            // 对方户名
            e.counterpartyName = m.counterpartyName;
            e.productName = m.productName;
            e.type = m.type;
            //收支类型
            e.ieType = m.ieType;
            e.tradingStatus=m.tradingStatus;
            e.note = m.note;
            e.fundingStatus = m.fundingStatus;
            e.createDate = DateUtils.parseDate(m.createDate,"yyyy-MM-dd HH:mm:ss");
            if(!m.payDate.equals("")){
                e.payDate = DateUtils.parseDate(m.payDate,"yyyy-MM-dd HH:mm:ss");
            }
            if (!m.modifyDate.equals("")){
                e.modifyDate = DateUtils.parseDate(m.modifyDate,"yyyy-MM-dd HH:mm:ss");
            }
            e.amount = new BigDecimal(m.amount);
            e.serviceFee = new BigDecimal(m.serviceFee);
            e.successfulRefund = new BigDecimal(m.successfulRefund);
        } catch (Exception ex){
            System.out.println("数据转换失败，非法数据,未保存到数据库,数据为："+m.transactionNumber+"金额为："+m.amount);
            ex.printStackTrace();
            return null;
        }
        return e;
    }
    public static void main(String[] args) throws ParseException {
        Alipaym m = new Alipaym();
// 交易号
        m.transactionNumber = "222";
        // 商家订单号。
        m.merchantOrderNumber = "333 ";
        m.createDate = "2020/03/03 12:22:22";
        m.payDate = "2020/03/03 12:22:22";

        m.modifyDate = "2020/03/03 12:22:22";
        m.transactionSource = "2020/03/03 12:22:22";
        // 对方户名
        m.counterpartyName = "2020/03/03 12:22:22";
        m.productName = "11";
        m.type = "22";
        m.amount="3";
        //收支类型
        m.ieType = "3";
        m.tradingStatus="2";
        m.serviceFee = "0";
        m.successfulRefund = "0";
        m.note = "22";
        m.fundingStatus ="edd";
        AlipayDB d = new AlipayDB();
        AlipayEntity e = d.convert(m);
        d.insert(e);
    }
}
