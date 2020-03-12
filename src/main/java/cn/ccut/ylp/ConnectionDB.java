package cn.ccut.ylp;
import com.mysql.cj.xdevapi.SqlDataResult;

import java.math.BigDecimal;
import java.sql.*;
//import java.sql.PseudoColumnUsage;


public  class ConnectionDB  {
    //声名JDBC对象
    public Connection conn=null;
    public PreparedStatement ps=null;
    public ResultSet rs=null;
    public ConnectionDB() {
        //声名存储对象
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //获取连接对象
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/bill?serverTimezone=Asia/Shanghai",Global.dbuser,Global.dbpass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            if (rs != null){
                rs.close();
            }
            if(ps != null){
                ps.close();
            }
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 参考模型
     * @param sqlStr
     */
    public ResultSet exe(String sqlStr){
        //创建sql命令
        //创建sql命令对象
        try {
            ps=conn.prepareStatement(sqlStr);
            //执行
            rs=ps.executeQuery();
            //返回结果
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 执行sql其他为包含参数，参数请用？代替。
     * @param sql
     * @param strings
     * @param dates
     * @param bigDecimals
     */
    public ResultSet exe(String sql, String[] strings, Date[] dates, BigDecimal[] bigDecimals){
        try {
            ps=conn.prepareStatement(sql);
            //ps.setString(1,"2020-02-03");
            int index = 1;
            if (strings != null && strings.length > 0){
                for (int i = 0;i<strings.length;i++){
                    ps.setString(index,strings[i]);
                    index = index + 1;
                }
            }
            if (dates != null && dates.length > 0){
                for (int i = 0;i<dates.length;i++){
                    ps.setTimestamp(index,new Timestamp(dates[i].getTime()));
                    index = index + 1;
                }
            }
            if (bigDecimals != null && bigDecimals.length > 0){
                for (int i = 0;i<bigDecimals.length;i++){
                    ps.setBigDecimal(index,bigDecimals[i]);
                    index = index + 1;
                }
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param sqlstr
     * @return true为执行失败，false为执行成功。
     */
    public boolean execute(String sqlstr){
        try {
            ps = conn.prepareStatement(sqlstr);
            return  ps.execute();
        } catch (SQLException ex){
            ex.printStackTrace();;
        }
        return true;
    }
    public static void main(String[] args) {
        ConnectionDB connectionDB = new ConnectionDB();
    }

}