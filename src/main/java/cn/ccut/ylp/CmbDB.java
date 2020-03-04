package cn.ccut.ylp;

import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;

public class CmbDB {
    public ConnectionDB connectionDB;
    //声名JDBC对象
    public Connection conn=null;
    public PreparedStatement ps=null;
    public ResultSet rs=null;
    public boolean insert(CmbEntity e){
        String sql = "INSERT INTO `bill`.`cmb` (`date`, `time`, `income`, `expenditure`, `balance`, `transaction_type`, `transaction_notes`,`datetime`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = connectionDB.conn.prepareStatement(sql);
            ps.setDate(1,e.date);
            ps.setTime(2,e.time);
            ps.setBigDecimal(3,e.income);
            ps.setBigDecimal(4,e.expenditure);
            ps.setBigDecimal(5,e.balance);
            ps.setString(6,e.transactionType);
            ps.setString(7,e.transactionNotes);
            ps.setTimestamp(8,new Timestamp(e.datetime.getTime()));
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
    public CmbDB(){
        connectionDB = new ConnectionDB();
        conn = connectionDB.conn;
        ps = connectionDB.ps;
        rs = connectionDB.rs;
    }
    public CmbEntity convert(Cmbm m) throws ParseException {
        if(m == null) {
            return null;
        }
        CmbEntity e = new CmbEntity();
        try {
            e.date = new Date(DateUtils.parseDate(m.date,"yyyyMMdd").getTime());
            if (m.time.equals("")){
                e.time = null;
            } else {
                int hour = Integer.valueOf(m.time.substring(0,2));
                int minute =  Integer.valueOf(m.time.substring(3,5));
                int second =  Integer.valueOf(m.time.substring(6,8));
                e.time =  new Time(hour,minute,second);
            }
            if (!m.income.equals("")){
                e.income = new BigDecimal(m.income);
            }
            if (!m.expenditure.equals("")){
                e.expenditure = new BigDecimal(m.expenditure);
            }
            e.balance = new BigDecimal(m.balance);
            e.datetime = DateUtils.parseDate(m.datetime,"yyyyMMddHH:mm:ss");
        } catch (Exception ex){
            System.out.println("数据转换失败，非法数据,未保存到数据库,数据为："+m.datetime+"金额为："+m.balance);
            ex.printStackTrace();
            return null;
        }
        e.transactionType = m.transactionType;
        e.transactionNotes = m.transactionNotes;
        return e;
    }
    public static void main(String[] args) throws ParseException {
        Cmbm m = new Cmbm();
        m.datetime = "20200301225630";
        m.date = "20200301";
        m.time = "225630";
        m.income ="280";
        m.expenditure="";
        m.balance ="500";
        m.transactionType = "转账";
        m.transactionNotes = "测试数据";
        Abc a = new Abc();
        CmbDB d = new CmbDB();
        CmbEntity e = d.convert(m);
        d.insert(e);
    }
}
