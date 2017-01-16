package com.bohui

import groovy.sql.Sql


/**
 *
 * User: liyangli
 * Date: 2014/6/6
 * Time: 9:54
 */
class JdbcDemo {

    private List<Long> findAreaID(){
         sql.eachRow("select * from t_area"){
             int id = it.ID;
             String[] month = ["201401","201402"
                     ,"201403","201404","201405","201406","201301","201302","201303","201304","201305","201306","201307","201308","201309",
                     "201310","201311","201312"];
             String[] day = ["20140601"
                           ,"20140602","20140603","20140604","20140605","20140606","20140501","20140502","20140503","20140504","20140505"
                           ,"20140506","20140507","20140508","20140509","20140510","20140511"
                           ,"20140512","20140513","20140514","20140515","20140516","20140517","20140518","20140529","20140520"
                           ,"20140521","20140522","20140523","20140524","20140525","20140526","20140527"
                           ,"20140528","20140529","20140530"];
             String[] year = ["2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2011","2012","2013","2014"];
             month.each{
                 sql.execute("insert into t_testcount_month ( AREA_ID, DATETIME, LIVE_CHANNEL_ERROR, VOD_CHANNEL_ERROR, INTRANET_ERROR, EXTRANET_ERROR, TIME_LAPSE_ERROR, LOOK_BACK_ERROR, VOD_VIEW_ERROR, LIVE_CHANNEL_TOTAL, VOD_CHANNEL_TOTAL, INTRANET_TOTAL, EXTRANET_TOTAL, TIME_LAPSE_TOTAL, LOOK_BACK_TOTAL, VOD_VIEW_TOTAL, TIME)\n" +
                         "values ( ?, ?, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 4, 5, 4, 3, to_date('28-05-2014', 'dd-mm-yyyy'))",[id,Integer.parseInt(it)])
             };
             day.each{
                 sql.execute("insert into t_testcount_day ( AREA_ID, DATETIME, LIVE_CHANNEL_ERROR, VOD_CHANNEL_ERROR, INTRANET_ERROR, EXTRANET_ERROR, TIME_LAPSE_ERROR, LOOK_BACK_ERROR, VOD_VIEW_ERROR, LIVE_CHANNEL_TOTAL, VOD_CHANNEL_TOTAL, INTRANET_TOTAL, EXTRANET_TOTAL, TIME_LAPSE_TOTAL, LOOK_BACK_TOTAL, VOD_VIEW_TOTAL, TIME)\n" +
                         "values ( ?, ?, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 4, 5, 4, 3, to_date('28-05-2014', 'dd-mm-yyyy'))",[id,Integer.parseInt(it)])
             }
             year.each{
                 sql.execute("insert into t_testcount_year ( AREA_ID, DATETIME, LIVE_CHANNEL_ERROR, VOD_CHANNEL_ERROR, INTRANET_ERROR, EXTRANET_ERROR, TIME_LAPSE_ERROR, LOOK_BACK_ERROR, VOD_VIEW_ERROR, LIVE_CHANNEL_TOTAL, VOD_CHANNEL_TOTAL, INTRANET_TOTAL, EXTRANET_TOTAL, TIME_LAPSE_TOTAL, LOOK_BACK_TOTAL, VOD_VIEW_TOTAL, TIME)\n" +
                         "values ( ?, ?, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 4, 5, 4, 3, to_date('28-05-2014', 'dd-mm-yyyy'))",[id,Integer.parseInt(it)])
             }
             sql.commit();
System.currentTimeMillis()               S

         }
    }
    static final Sql sql = Sql.newInstance("jdbc:oracle:thin:@172.17.13.108:1521:orcl", "vsm_pt",
            "123456", "oracle.jdbc.driver.OracleDriver");

    private void insertServerIndexVss(){
        //1、组装数据，定时进行执行
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            void run() {
                println("开始执行插入数据操作")
                //开始执行的任务
                def num = Math.random();
                int currentband = num*10000;
                num = Math.random();
                int usernum = num*10000;
                String str = "insert into t_device_server_index(time,deviceserverid,currentband,usernum) values(sysdate,10167,${currentband},${usernum})";
                sql.execute(str);
                sql.commit();
                println("执行完成。执行语句为：${str}");
            }
        },1000,5*60*1000);//每5分钟执行一次数据入库操作
    }
    private void insertSip(){
        def file = new File("C:\\Users\\bohui\\Desktop\\sip_test.sql");
        StringBuilder sqlBuild = new StringBuilder()
        file.text.eachLine {
            def start = it.indexOf("to_date('");
            def line = it;
            if(start != -1){
                def end = it.indexOf("')",start);
                line = it.substring(0,start)+' sysdate '+it.substring(end+2);
            }
            if(line.indexOf(");") > -1){
                def lastIndex = line.lastIndexOf(";");
                sqlBuild.append(line.subSequence(0,lastIndex));
                println sqlBuild.toString()
                sql.execute(sqlBuild.toString())
                sqlBuild = new StringBuilder()
                line = line.substring(lastIndex+1)
            }
            sqlBuild.append(line)
        }
         sql.commit()

    }

    static void main(args) {
        JdbcDemo jdbc = new JdbcDemo();
        jdbc.insertServerIndexVss();
//        jdbc.findAreaID();
//        sql.execute("insert into word (word_id, spelling, part_of_speech) values (${wid}, ${spelling}, ${pospeech})")
//        sql.execute("insert into t_testcount_day ( AREA_ID, DATETIME, LIVE_CHANNEL_ERROR, VOD_CHANNEL_ERROR, INTRANET_ERROR, EXTRANET_ERROR, TIME_LAPSE_ERROR, LOOK_BACK_ERROR, VOD_VIEW_ERROR, LIVE_CHANNEL_TOTAL, VOD_CHANNEL_TOTAL, INTRANET_TOTAL, EXTRANET_TOTAL, TIME_LAPSE_TOTAL, LOOK_BACK_TOTAL, VOD_VIEW_TOTAL, TIME)\n" +
//                "values ( ?, ?, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 4, 5, 4, 3, to_date('28-05-2014', 'dd-mm-yyyy'));",[])
    }
}
