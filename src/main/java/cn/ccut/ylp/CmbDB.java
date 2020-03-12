package cn.ccut.ylp;

import cn.ccut.ylp.date.AutoConvertDate;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;

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
            e.date = new Date(AutoConvertDate.convert(m.date).getTime());
            e.time =  AutoConvertDate.getSqlTime(m.time);
            if (!m.income.equals("")){
                e.income = new BigDecimal(m.income);
            }
            if (!m.expenditure.equals("")){
                e.expenditure = new BigDecimal(m.expenditure);
            }
            e.balance = new BigDecimal(m.balance);
            e.datetime = AutoConvertDate.convert(m.datetime);
        } catch (Exception ex){
            System.out.println("数据转换失败，非法数据,未保存到数据库,数据为："+m.datetime+"金额为："+m.balance);
            ex.printStackTrace();
            return null;
        }
        e.transactionType = m.transactionType;
        e.transactionNotes = m.transactionNotes;
        return e;
    }
    /**
     * 将sql查询结果转换为实体数组。
     * @param r sql查询返回的结果
     * @param colNames 查询的字段名称列。
     * @return
     */
    public ArrayList<CmbEntity> readFromResultSet(ResultSet r, String[] colNames){
        ArrayList<CmbEntity> lists = new ArrayList<CmbEntity>();
        // 是否查询全部元素。
        boolean total = colNames==null ? true : false;
        //如果为空，说明查询全部。
        try {
            while (r.next()){
                Cmbm m = new Cmbm();
                if (total){
                    m.datetime = r.getString(1);
                    m.date = r.getString(2);
                    m.time = r.getString(3);
                    m.income = r.getString(4);
                    m.expenditure = r.getString(5);
                    m.balance = r.getString(6);
                    m.transactionType = r.getString(7);
                    m.transactionNotes = r.getString(8);
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
                            case "income":
                                m.income = r.getString(index);
                                break;
                            case "expenditure":
                                m.expenditure = r.getString(index);
                                break;
                            case "balance":
                                m.balance = r.getString(index);
                                break;
                            case "transactionType":
                                m.transactionType = r.getString(index);
                                break;
                            case "transactionNotes":
                                m.transactionNotes = r.getString(index);
                                break;
                            default:
                                index = index - 1;
                        }
                    }
                }
                try {
                    CmbEntity e = convert(m);
                    lists.add(e);
                } catch (ParseException ex){
                    ex.printStackTrace();
                    System.out.println("农行转换数据失败，数据为："+m.datetime+m.expenditure+m.income + m.transactionNotes);
                }
            }
        }catch (SQLException e){
            System.out.println("从数据库中读取数据失败！");
        }
        return lists;
    }
    //select count(*) from information_schema.COLUMNS where table_name='abc';查表中有多少列。
    public ArrayList<CmbEntity> readFromResultSet(ResultSet r){
        return readFromResultSet(r,null);
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
        Cmb a = new Cmb();
        CmbDB d = new CmbDB();
        CmbEntity e = d.convert(m);
        d.insert(e);
    }
}
