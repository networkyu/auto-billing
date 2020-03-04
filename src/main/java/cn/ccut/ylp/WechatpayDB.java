package cn.ccut.ylp;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;

public class WechatpayDB {
    public ConnectionDB connectionDB;
    //声名JDBC对象
    public Connection conn=null;
    public PreparedStatement ps=null;
    public ResultSet rs=null;
    public boolean insert(WechatpayEntity e){
        String sql = "INSERT INTO `bill`.`wechatpay` (`datetime`, `transaction_type`, `counterparty_name`, `product_name`, `ie_type`, `amount`, `payment_method`, `current_status`, `transaction_number`, `merchant_order_number`, `note`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = connectionDB.conn.prepareStatement(sql);

            ps.setTimestamp(1,new Timestamp(e.datetime.getTime()));
            ps.setString(2,e.transactionType);
            ps.setString(3,e.counterpartyName);
            ps.setString(4,e.productName);
            ps.setString(5,e.ieType);
            ps.setBigDecimal(6,e.amount);
            ps.setString(7,e.paymentMethod);
            ps.setString(8,e.currentStatus);
            ps.setString(9,e.transactionNumber);
            ps.setString(10,e.merchantOrderNumber);
            ps.setString(11,e.note);
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
    public WechatpayDB(){
        connectionDB = new ConnectionDB();
        conn = connectionDB.conn;
        ps = connectionDB.ps;
        rs = connectionDB.rs;
    }
    public WechatpayEntity convert(Wechatpaym m) throws ParseException {
        if(m == null) {
            return null;
        }
        WechatpayEntity e = new WechatpayEntity();
        try {
            e.datetime = DateUtils.parseDate(m.datetime,"yyyy-MM-dd HH:mm:ss");
            e.transactionType=m.transactionType;

            // 对方户名
            e.counterpartyName = m.counterpartyName;
            e.productName = m.productName;
            //收支类型
            e.ieType = m.ieType;
            e.amount = new BigDecimal(m.amount);
            e.paymentMethod = m.paymentMethod;
            e.currentStatus = m.currentStatus;
            e.transactionNumber = m.transactionNumber;
            // 商家订单号。
            e.merchantOrderNumber = m.merchantOrderNumber;
            e.note = m.note;
        } catch (Exception ex){
            System.out.println("数据转换失败，非法数据,未保存到数据库,数据为："+m.transactionNumber+"金额为："+m.amount);
            ex.printStackTrace();
            return null;
        }
        return e;
    }
    public static void main(String[] args) throws ParseException {
        Wechatpaym m = new Wechatpaym();
        m.datetime = "2020-03-03 12:22:22";
        m.transactionType = "2";

        // 对方户名
        m.counterpartyName = "2";
        m.productName = "2";
        //收支类型
        m.ieType = "2";
        m.amount = "2";
        m.paymentMethod = "2";
         m.currentStatus = "2";
        m.transactionNumber = "2";
        // 商家订单号。
        m.merchantOrderNumber = "2";

        WechatpayDB d = new WechatpayDB();
        WechatpayEntity e = d.convert(m);
        d.insert(e);
    }
}
