package com.bohui.sql

import groovy.sql.Sql

/**
 *
 * 组装需要优化的sql语句
 * User: liyangli
 * Date: 2015/8/8
 * Time: 14:58
 */
/**
 * db操作类
 */
class Demo{

    private Sql sql = null;
    public DBOperator(){
        sql = Sql.newInstance("jdbc:oracle:thin:@172.16.250.104:1521:orcl","vsmserver",
        "123456","oracle.jdbc.driver.OracleDriver");
    }

    public void insert(String beginTime,String endTime){
        def sm = "insert into T_SMMAX(maxvodcount,areaid,time)\n" +
                "select vodcount,t.areaid,time from t_area_count t,\n" +
                "(select max(vodcount) countMax,areaid from t_area_count where  time >= to_date('${beginTime}','yyyy-mm-dd') and time < to_date('${endTime}','yyyy-mm-dd') and areaid>0 group by areaid) tm \n" +
                "where tm.areaid = t.areaid and t.vodcount=tm.countMax and time >= to_date('${beginTime}','yyyy-mm-dd') and\n" +
                " time < to_date('${endTime}','yyyy-mm-dd') and tm.areaid > 0";
        def vss ="insert into T_VSSMAX(maxband,areaid,time) select band,t.areaid,time from T_VSSAREA t,\n" +
                " (select max(band) bandMax,areaid from T_VSSAREA where  time >= to_date('${beginTime}','yyyy-mm-dd') and time < to_date('${endTime}','yyyy-mm-dd') and areaid > 0 group by areaid) tm \n" +
                " where tm.areaid = t.areaid and band=tm.bandMax and time >= to_date('${beginTime}','yyyy-mm-dd') and time < to_date('${endTime}','yyyy-mm-dd')";
        def smMin = " insert into T_SMMINI(minivodcount,areaid,time) select vodcount,t.areaid,time from t_area_count t,\n" +
                "(select min(vodcount) countMin,areaid from t_area_count where  time >= to_date('${beginTime}','yyyy-mm-dd') and time < to_date('${endTime}','yyyy-mm-dd') and areaid > 0 group by areaid) tm \n" +
                "where tm.areaid = t.areaid and vodcount=tm.countMin and time >= to_date('${beginTime}','yyyy-mm-dd') and time < to_date('${endTime}','yyyy-mm-dd');";
        def vssMin = "insert into T_VSSMINI(miniband,areaid,time) select band,t.areaid,time from T_VSSAREA t,\n" +
                "(select min(band) bandMin,areaid from T_VSSAREA where  time >= to_date('${beginTime}','yyyy-mm-dd') and time < to_date('${endTime}','yyyy-mm-dd') and areaid> 0 group by areaid) tm \n" +
                "where tm.areaid = t.areaid and band=tm.bandMin and time >= to_date('${beginTime}','yyyy-mm-dd') and time < to_date('${endTime}','yyyy-mm-dd')";
        sql.executeUpdate(sm);
        sql.executeUpdate(vss);
        sql.executeUpdate(smMin);
        sql.executeUpdate(vssMin);
    }
    
    

}
Demo dd = new Demo();
def startTime = Date.parse("yyyy-MM-dd","2016-01-10");
def end = Date.parse("yyyy-MM-dd","2016-07-04");

while(startTime.time <  end.time){
    dd.insert(startTime.format("yyyy-MM-dd"),startTime.next().format("yyyy-MM-dd"));
    startTime = startTime.next();
}





