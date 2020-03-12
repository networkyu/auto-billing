package cn.ccut.ylp;

import cn.ccut.ylp.date.AutoConvertDate;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;

public class CmbPdfDB {
    public ConnectionDB connectionDB;
    //声名JDBC对象
    public Connection conn=null;
    public PreparedStatement ps=null;
    public ResultSet rs=null;
    public boolean insert(CmbPdfEntity e) {
        String sql = "INSERT INTO `bill`.`cmb_pdf` (`date`, `currency`, `amount`, `balance`, `transaction_type`, `counter_party`) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            ps = connectionDB.conn.prepareStatement(sql);
            ps.setDate(1,e.date);
            ps.setString(2,e.currency);
            ps.setBigDecimal(3,e.amount);
            ps.setBigDecimal(4,e.balance);
            ps.setString(5,e.transactionType);
            ps.setString(6,e.counterParty);
            boolean result = ps.execute();
            return result;
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("数据插入失败！");
        }
        return true;
    }
    public CmbPdfDB(){
        connectionDB = new ConnectionDB();
        conn = connectionDB.conn;
        ps = connectionDB.ps;
        rs = connectionDB.rs;
    }
    public CmbPdfEntity convert(CmbPdfm m) {
        if(m == null) {
            return null;
        }
        CmbPdfEntity e = new CmbPdfEntity();
        try {
            if(m.no != null){
                e.no = Integer.parseInt(m.no);
            }
            e.date = new Date(AutoConvertDate.convert(m.date).getTime());
            e.counterParty = m.currency;
            e.amount = new BigDecimal(m.amount);
            e.balance = new BigDecimal(m.balance);
            e.transactionType = m.transactionType;
            e.counterParty = m.counterParty;
        } catch (Exception ex){
            System.out.println("数据转换失败，非法数据,未保存到数据库,数据为："+m.date+"|金额为："+m.balance);
            ex.printStackTrace();
            return null;
        }
        return e;
    }
    /**
     * 将sql查询结果转换为实体数组。
     * @param r sql查询返回的结果
     * @param colNames 查询的字段名称列。
     * @return
     */
    public ArrayList<CmbPdfEntity> readFromResultSet(ResultSet r, String[] colNames){
        ArrayList<CmbPdfEntity> lists = new ArrayList<CmbPdfEntity>();
        // 是否查询全部元素。
        boolean total = colNames==null ? true : false;
        //如果为空，说明查询全部。
        try {
            while (r.next()){
                CmbPdfm m = new CmbPdfm();
                if (total){
                    m.no = r.getString(1);
                    m.date = r.getString(2);
                    m.currency = r.getString(3);
                    m.amount = r.getString(4);
                    m.balance = r.getString(5);
                    m.transactionType = r.getString(6);
                    m.counterParty = r.getString(7);
                } else {
                    // 列索引，从1开始。
                    int index = 0;
                    for (int i= 0;i<colNames.length;i++){
                        String colName = colNames[i];
                        index = index + 1;
                        switch (colName){
                            case "no":
                                m.no = r.getString(index);
                                break;
                            case "date":
                                m.date = r.getString(index);
                                break;
                            case "currency":
                                m.currency = r.getString(index);
                                break;
                            case "amount":
                                m.amount = r.getString(index);
                                break;
                            case "balance":
                                m.balance = r.getString(index);
                                break;
                            case "transactionType":
                                m.transactionType = r.getString(index);
                                break;
                            case "counterParty":
                                m.counterParty = r.getString(index);
                                break;
                            default:
                                index = index - 1;
                        }
                    }
                }
                CmbPdfEntity e = convert(m);
                lists.add(e);
            }
        }catch (SQLException e){
            System.out.println("从数据库中读取数据失败！");
        }
        return lists;
    }
    //select count(*) from information_schema.COLUMNS where table_name='abc';查表中有多少列。
    public ArrayList<CmbPdfEntity> readFromResultSet(ResultSet r){
        return readFromResultSet(r,null);
    }
    //删除招商银行账单excel中已存在的数据。
    private boolean deleteOverlapping(String cutoff){
        String sql = "DELETE FROM cmb_pdf WHERE date >= " + "'"+cutoff+"'";
        boolean result = connectionDB.execute(sql);
        connectionDB.close();
        return !result;
    }
    public static void main(String[] args) {
        CmbPdfm m = new CmbPdfm();
        m.date = "2020-03-01";
        m.currency = "人民币";
        m.amount ="280";
        m.balance ="500";
        m.transactionType = "测试转账";
        m.counterParty = "测试数据";
        CmbPdf a = new CmbPdf();
        CmbPdfDB d = new CmbPdfDB();
        CmbPdfEntity e = d.convert(m);
        d.insert(e);
        d.deleteOverlapping(Global.CMB_EXCEL_START_DATE_STR);
    }

}
