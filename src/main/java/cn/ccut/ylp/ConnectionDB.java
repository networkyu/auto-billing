package cn.ccut.ylp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
//import java.sql.PseudoColumnUsage;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/bill?serverTimezone=UTC",Global.dbuser,Global.dbpass);
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
    public void exe(String sqlStr){
        //创建sql命令
        String sql="select * from abc where userName=? and pwd=?";
        //创建sql命令对象
        try {
            ps=conn.prepareStatement(sql);
            //给占位符赋值
//            ps.setString(1, uname);
//            ps.setString(2, pwd);
            //执行
            rs=ps.executeQuery();
            //遍历结果
            while (rs.next()) {
                System.out.printf("日期为："+ rs.getString("date") );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        ConnectionDB connectionDB = new ConnectionDB();
    }

}