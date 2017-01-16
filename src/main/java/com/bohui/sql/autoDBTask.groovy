package com.bohui.sql
import groovy.sql.Sql
/**
 * 自动运行存储过程
 * User: liyangli
 * Date: 2016/12/19
 * Time: 11:16
 */
class ExecDB {
    private Sql sql = null;
    public ExecDB(){
        sql = Sql.newInstance("jdbc:oracle:thin:@172.17.13.108:1521:orcl","vsmserver",
                "Bohui@123","oracle.jdbc.driver.OracleDriver");
    }

    public void autoExceProc(){
        sql.call("{call PROC_DBA_AUTORUNSCRIPT()}");
        sql.close();
    }
}
def execDB = new ExecDB();
execDB.autoExceProc();
