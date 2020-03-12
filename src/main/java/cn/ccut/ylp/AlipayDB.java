package cn.ccut.ylp;

import cn.ccut.ylp.date.AutoConvertDate;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;

public class AlipayDB {
    public ConnectionDB connectionDB;
    //声名JDBC对象
    public Connection conn=null;
    public PreparedStatement ps=null;
    public ResultSet rs=null;
    protected static final Logger logger = LogManager.getLogger();
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
            e.createDate = AutoConvertDate.convert(m.createDate);
            if(!m.payDate.equals("")){
                e.payDate = AutoConvertDate.convert(m.payDate);
            }
            e.modifyDate = AutoConvertDate.convert(m.modifyDate);
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
    /**
     * 将sql查询结果转换为实体数组。
     * @param r sql查询返回的结果
     * @param colNames 查询的字段名称列。
     * @return
     */
    public ArrayList<AlipayEntity> readFromResultSet(ResultSet r, String[] colNames){
        ArrayList<AlipayEntity> lists = new ArrayList<AlipayEntity>();
        // 是否查询全部元素。
        boolean total = colNames==null ? true : false;
        //如果为空，说明查询全部。
        try {
            while (r.next()){
                Alipaym m = new Alipaym();
                if (total){

                    m.transactionNumber = r.getString(1);
                    m.merchantOrderNumber = r.getString(2);
                    m.createDate = r.getString(3);
                    m.payDate = r.getString(4);
                    m.modifyDate = r.getString(5);
                    m.transactionSource = r.getString(6);
                    m.counterpartyName = r.getString(7);
                    m.productName = r.getString(8);
                    m.type = r.getString(9);
                    m.amount = r.getString(10);
                    m.ieType = r.getString(11);
                    m.tradingStatus = r.getString(12);
                    m.serviceFee = r.getString(13);
                    m.successfulRefund = r.getString(14);
                    m.note = r.getString(15);
                    m.fundingStatus = r.getString(16);
                } else {
                    // 列索引，从1开始。
                    int index = 0;
                    for (int i= 0;i<colNames.length;i++){
                        String colName = colNames[i];
                        index = index + 1;
                        switch (colName){
                            case "transactionNumber":
                                m.transactionNumber = r.getString(index);
                                break;
                            case "merchantOrderNumber":
                                m.merchantOrderNumber = r.getString(index);
                                break;
                            case "createDate":
                                m.createDate = r.getString(index);
                                break;
                            case "payDate":
                                m.payDate = r.getString(index);
                                break;
                            case "modifyDate":
                                m.modifyDate = r.getString(index);
                                break;
                            case "transactionSource":
                                m.transactionSource = r.getString(index);
                                break;
                            case "counterpartyName":
                                m.counterpartyName = r.getString(index);
                                break;
                            case "productName":
                                m.productName = r.getString(index);
                                break;
                            case "type":
                                m.type = r.getString(index);
                                break;
                            case "amount":
                                m.amount = r.getString(index);
                                break;
                            case "ieType":
                                m.ieType = r.getString(index);
                                break;
                            case "tradingStatus":
                                m.tradingStatus = r.getString(index);
                                break;
                            case "serviceFee":
                                m.serviceFee = r.getString(index);
                                break;
                            case "successfulRefund":
                                m.successfulRefund = r.getString(index);
                                break;
                            case "note":
                                m.note = r.getString(index);
                                break;
                            case "fundingStatus":
                                m.fundingStatus = r.getString(index);
                                break;
                            default:
                                index = index - 1;
                        }
                    }
                }
                try {
                    AlipayEntity e = convert(m);
                    lists.add(e);
                } catch (ParseException ex){
                    ex.printStackTrace();
                    System.out.println("支付宝转换数据失败，数据为："+m.payDate+m.amount+m.counterpartyName);
                }
            }
        }catch (SQLException e){
            System.out.println("从数据库中读取数据失败！");
        }
        return lists;
    }
    //select count(*) from information_schema.COLUMNS where table_name='abc';查表中有多少列。
    public ArrayList<AlipayEntity> readFromResultSet(ResultSet r){
        return readFromResultSet(r,null);
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
