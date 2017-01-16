package com.bohui.timeten

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * 处理验证缓存数据库
 * User: liyangli
 * Date: 2015/2/4
 * Time: 17:13
 */

//String url = "jdbc:timesten:dsn=vsm_client";
String url = "jdbc:timesten:client:DSN=vsm_client";
String driver = "com.timesten.jdbc.TimesTenClientDriver";
Connection con = null;
try {
    // 加载TT的驱动程序
    Class.forName(driver);
    con = DriverManager.getConnection(url,"vsmserver","vsmserver");// 获得连接
    System.out.println("连接成功");
    java.sql.Statement st = con.createStatement();// 创建jdbc 语句
    java.sql.ResultSet rs = st.executeQuery("select * from sys.dba_users");// 执行sql
    while (rs.next()) {
        System.out.println(rs);// 取出结果集
    }
    con.close();// 关闭连接
} catch (ClassNotFoundException ex) {
    ex.printStackTrace();
} catch (SQLException ex) {
    ex.printStackTrace();
}
