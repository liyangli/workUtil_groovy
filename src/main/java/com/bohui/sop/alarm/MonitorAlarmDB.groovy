package com.bohui.sop.alarm

import groovy.sql.Sql

/**
 *
 * User: liyangli
 * Date: 2015/11/18
 * Time: 13:52
 */

/**
 * 前端告警统计，模拟数据
 */
class MonitorAlarm{
    private Sql sql = null;
    public  MonitorAlarm(){
        sql = Sql.newInstance("jdbc:oracle:thin:@172.17.13.27:1521:orcl","sop1",
                "123456","oracle.jdbc.driver.OracleDriver");
    }

    public Set findAlarmType(){
        Set set = new HashSet();
        sql.eachRow("select * from T_ALARM_TYPE where alarmclass in (0,7)",{it-> set.add(it.id)});
        println set;
        return set;
    }

    public Set findMonitor(){
        Set set = new HashSet();
        sql.eachRow("select * from t_monitor_device",{it-> set.add(it.id)});
        println set;
        return  set;
    }

    public void insertMonitorAlarm(){
        Set set = findAlarmType();
        Set monitors = findMonitor();
        set.forEach({at -> monitors.forEach({m->
            //真正执行插入操作
//            Random rand = new Random(10000);
//            int alarmCount = rand.nextInt()
            int alarmCount = Math.random()*10;
            //当前时间往前推从凌晨开始入库
            Calendar calendar = Calendar.getInstance();
//            int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
//            nowHour=nowHour-1;
            for(int i=0;i<=23;i++){
                calendar.set(Calendar.HOUR_OF_DAY,i);
                String masql = "insert into T_Monitor_Alarm_Statistic(time,Monitorid,Alarmtypeid,alarmcount) values (to_date( '${calendar.getTime().format("yyyy-MM-dd HH")}' , 'YYYY-MM-DD HH24:MI' ),${m},${at},${alarmCount})";
                sql.executeInsert(masql);
            }

//            sql.executeInsert(masql);
//            String masql = "insert into T_Monitor_Alarm_Statistic(time,Monitorid,Alarmtypeid,alarmcount,StdServiceID) values (to_date ( '${date.format("yyyy-MM-dd HH")}' , 'YYYY-MM-DD HH24:MI' ),${m},${at},${alarmCount})";
        })});



    }

    public Set findService(){
        Set set = new HashSet();
        sql.eachRow("select * from T_STANDARD_SERVICE",{it-> set.add(it.id)});
        println set;
        return  set;
    }

    public void insertServiceAlarm(){
        Set set = findAlarmType();
        Set monitors = findMonitor();
        Set service = findService();
        set.forEach({at -> monitors.forEach({m-> service.forEach({s->
            //真正执行插入操作
            int alarmCount = Math.random()*10;
            //当前时间往前推从凌晨开始入库
            Calendar calendar = Calendar.getInstance();
//            int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
//            nowHour=nowHour-1;
            for(int i=0;i<=23;i++){
                calendar.set(Calendar.HOUR_OF_DAY,i);
                String masql = "insert into T_Service_Alarm_Statistic(time,Monitorid,Alarmtypeid,alarmcount,StdServiceID) values (to_date( '${calendar.getTime().format("yyyy-MM-dd HH")}' , 'YYYY-MM-DD HH24:MI' ),${m},${at},${alarmCount},${s})";
                sql.executeInsert(masql);
            }
        })

        })});



    }
}
MonitorAlarm ma = new MonitorAlarm();
println("###########################START RUN STAET##########################################");
ma.insertMonitorAlarm();
println("###########################START Service RUN##########################################");
ma.insertServiceAlarm();
println("###########################START RUN END##########################################");
