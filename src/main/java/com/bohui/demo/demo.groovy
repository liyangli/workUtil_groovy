package com.bohui.demo

import groovy.sql.Sql

/**
 *
 * User: liyangli
 * Date: 2015/7/12
 * Time: 17:08
 */
class MysqlDB{
    private Sql sql = null;
    public  MysqlDB(){
        sql = Sql.newInstance("jdbc:mysql://172.17.5.58:13306/ums?useUnicode=true&amp;characterEncoding=UTF-8","root",
                "Bohui@123","com.mysql.jdbc.Driver");
    }

    public void demo(){
        sql.eachRow("select * from security_user ",{it->
            println it;
        })
    }
}
MysqlDB db = new MysqlDB();
db.demo();