package cn.ccut.ylp;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.xmlbeans.impl.piccolo.util.DuplicateKeyException;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;

public class AbcDB {
    public ConnectionDB connectionDB;
    //声名JDBC对象
    public Connection conn=null;
    public PreparedStatement ps=null;
    public ResultSet rs=null;
    public boolean insert(AbcEntity e){
        String sql = "INSERT INTO `bill`.`abc` (`date`, `time`, `amount`, `balance`, `counterparty_name`,`counterparty_account`, `trading_bank`, `trading_channels`, `transaction_type`, `transaction_purpose`, `trading_summary`,`datetime`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        try {
            ps = connectionDB.conn.prepareStatement(sql);
            ps.setDate(1,e.date);
            ps.setTime(2,e.time);
            ps.setBigDecimal(3,e.amount);
            ps.setBigDecimal(4,e.balance);
            ps.setString(5,e.counterpartyName);
            ps.setString(6,e.counterpartyAccount);
            ps.setString(7,e.tradingBank);
            ps.setString(8,e.tradingChannels);
            ps.setString(9,e.transactionType);
            ps.setString(10,e.transactionPurpose);
            ps.setString(11,e.tradingSummary);
            ps.setTimestamp(12,new Timestamp(e.datetime.getTime()));
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
    public AbcDB(){
        connectionDB = new ConnectionDB();
        conn = connectionDB.conn;
        ps = connectionDB.ps;
        rs = connectionDB.rs;
    }

    public static void main(String[] args) throws ParseException {
        Abcm m = new Abcm();
        m.date = "20200301";
        m.time = "225630";
        m.amount ="280";
        m.balance ="500";
        m.counterpartyName = "姓名";
        m.counterpartyAccount = "6217000989990987";
        m.tradingBank = "农业银行";
        m.tradingChannels = "电子商务";
        m.transactionType = "转账";
        m.transactionPurpose = "";
        m.tradingSummary = "转存";
        Abc a = new Abc();
        AbcDB d = new AbcDB();
        AbcEntity e = d.convert(m);
        d.insert(e);
    }
    public AbcEntity convert(Abcm m) throws ParseException {
        if(m == null) {
            return null;
        }
        AbcEntity e = new AbcEntity();
        try {
            e.date = new Date(DateUtils.parseDate(m.date,"yyyyMMdd").getTime());
            int hour = Integer.valueOf(m.time.substring(0,2));
            int minute =  Integer.valueOf(m.time.substring(2,4));
            int second =  Integer.valueOf(m.time.substring(4,6));
            e.time =  new Time(hour,minute,second);
            e.amount = new BigDecimal(m.amount);
            e.balance = new BigDecimal(m.balance);
            e.datetime = DateUtils.parseDate(m.datetime,"yyyyMMddHHmmss");
//            e.datetime = simpleDateFormat.parse(m.datetime);
        } catch (Exception ex){
            System.out.println("数据转换失败，非法数据,未保存到数据库,数据为："+m.datetime+"金额为："+m.amount);
            ex.printStackTrace();
            return null;
        }

        e.counterpartyName = m.counterpartyName;
        e.counterpartyAccount = m.counterpartyAccount;
        e.tradingBank  = m.tradingBank;
        e.tradingChannels = m.tradingChannels;
        e.transactionType = m.transactionType;
        e.transactionPurpose = m.transactionPurpose;
        e.tradingSummary = m.tradingSummary;
        return e;
    }
}
