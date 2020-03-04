//package dao.impl;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.PseudoColumnUsage;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import dao.logindao;
//import model.Users;
//
//public  class loginDaoImpl implements logindao {
//
//    @Override
//    public Users checkLoginDao(String uname, String pwd) {
//        //声名JDBC对象
//        Connection conn=null;
//        PreparedStatement ps=null;
//        ResultSet rs=null;
//        //声名存储对象
//        Users users=null;
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            //获取连接对象
//            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/testjdbc","root","mysql");
//            //创建sql命令
//            String sql="select * from t_user where userName=? and pwd=?";
//            //创建sql命令对象
//            ps=conn.prepareStatement(sql);
//            //给占位符赋值
//            ps.setString(1, uname);
//            ps.setString(2, pwd);
//            //执行
//            rs=ps.executeQuery();
//            //遍历结果
//            while (rs.next()) {
//                users=new Users();
//                users.setId(rs.getInt("id"));
//                users.setName(rs.getNString("userName"));
//                users.setPwd(rs.getNString("pwd"));
//                users.setTime(rs.getTimestamp("regTime"));
//            }
//            //关闭
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            try {
//                rs.close();
//                ps.close();
//                conn.close();
//            } catch (SQLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        return users;
//    }
//
//}