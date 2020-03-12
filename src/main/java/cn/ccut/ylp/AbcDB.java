package cn.ccut.ylp;

import cn.ccut.ylp.date.AutoConvertDate;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.xmlbeans.impl.piccolo.util.DuplicateKeyException;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;

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
        m.datetime="20200301225630";
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
            e.date = new Date(AutoConvertDate.convert(m.date).getTime());
            int[] times = AutoConvertDate.getTime(m.time);
            e.time =  new Time(times[0],times[1],times[2]);
            e.amount = new BigDecimal(m.amount);
            e.balance = new BigDecimal(m.balance);
            e.datetime = AutoConvertDate.convert(m.datetime);
        } catch (Exception ex){
            System.out.println("数据转换失败，非法数据,数据为："+m.datetime+"金额为："+m.amount);
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

    /**
     * 将sql查询结果转换为实体数组。
     * @param r sql查询返回的结果
     * @param colNames 查询的字段名称列。
     * @return
     */
    public ArrayList<AbcEntity> readFromResultSet(ResultSet r,String[] colNames){
        ArrayList<AbcEntity> lists = new ArrayList<AbcEntity>();
        // 是否查询全部元素。
        boolean total = colNames==null ? true : false;
        //如果为空，说明查询全部。
        try {
            while (r.next()){
                Abcm m = new Abcm();
                if (total){
                    m.datetime = r.getString(1);
                    m.date = r.getString(2);
                    m.time = r.getString(3);
                    m.amount = r.getString(4);
                    m.balance = r.getString(5);
                    m.counterpartyName = r.getString(6);
                    m.counterpartyAccount = r.getString(7);
                    m.tradingBank = r.getString(8);
                    m.tradingChannels = r.getString(9);
                    m.transactionType = r.getString(10);
                    m.transactionPurpose = r.getString(11);
                    m.tradingSummary = r.getString(12);
                } else {
                    // 列索引，从1开始。
                    int index = 0;
                    for (int i= 0;i<colNames.length;i++){
                        String colName = colNames[i];
                        index = index + 1;
                        switch (colName){
                            case "datetime":
                                m.datetime = r.getString(index);
                                break;
                            case "date":
                                m.date = r.getString(index);
                                break;
                            case "time":
                                m.time = r.getString(index);
                                break;
                            case "amount":
                                m.amount = r.getString(index);
                                break;
                            case "balance":
                                m.balance = r.getString(index);
                                break;
                            case "counterpartyName":
                                m.counterpartyName = r.getString(index);
                                break;
                            case "counterpartyAccount":
                                m.counterpartyAccount = r.getString(index);
                                break;
                            case "tradingBank":
                                m.tradingBank = r.getString(index);
                                break;
                            case "tradingChannels":
                                m.tradingChannels = r.getString(index);
                                break;
                            case "transactionType":
                                m.transactionType = r.getString(index);
                                break;
                            case "transactionPurpose":
                                m.transactionPurpose = r.getString(index);
                                break;
                            case "tradingSummary":
                                m.tradingSummary = r.getString(index);
                                break;
                            default:
                                index = index - 1;
                        }
                    }
                }
                try {
                    AbcEntity e = convert(m);
                    lists.add(e);
                } catch (ParseException ex){
                    ex.printStackTrace();
                    System.out.println("农行转换数据失败，数据为："+m.datetime+m.amount+m.counterpartyName);
                }
            }
        }catch (SQLException e){
            System.out.println("从数据库中读取数据失败！");
        }
        return lists;
    }
    //select count(*) from information_schema.COLUMNS where table_name='abc';查表中有多少列。
    public ArrayList<AbcEntity> readFromResultSet(ResultSet r){
        return readFromResultSet(r,null);
    }
}
