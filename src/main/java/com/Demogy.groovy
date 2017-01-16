package com

import groovy.sql.Sql

/**
 *
 * User: liyangli
 * Date: 2015/12/22
 * Time: 17:40
 */
class GuiYang{

    Sql sql = Sql.newInstance("jdbc:mysql://101.200.78.195:3306/demo?useUnicode=true&amp;characterEncoding=GBK","root",
        "hello123","com.mysql.jdbc.Driver");
    public void save(){
        String ss = "insert into t_demo(name) values('栗阳力你好')";
        sql.execute(ss);
    }
    public void search(){
        sql.eachRow("select * from t_demo order by id desc",{
            it-> println it;
        })
    }
}
GuiYang gy = new GuiYang();
gy.save();
gy.search();
