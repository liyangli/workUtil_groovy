    package com.bohui.sip

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
        sql = Sql.newInstance("jdbc:oracle:thin:@172.31.26.22:1521:orcl","vsmserver",
                "123456","oracle.jdbc.driver.OracleDriver");
    }

   public void searchDemo(){
       String str = 'select sid,serial,runTime,historyTime from (\n' +
               ' SELECT s.SID as sid,s.serial# as serial,\n' +
               '            sum(S.TIME_REMAINING)  as runTime,\n' +
               '            sum(S.ELAPSED_SECONDS) as historyTime\n' +
               '       FROM V$SESSION_LONGOPS S\n' +
               '      where SID in (select a.sid \n' +
               '  from V$SESSION a, v$sqlarea b \n' +
               'where a.sql_address = b.address and username=\'VSMSERVER\' )\n' +
               '      GROUP BY s.SID,s.serial#) where historytime > 30' ;

       sql.eachRow(str,{it->
           def sid = it.sid;
           def serial = it.serial;
           def runTime = it.runTime;
           def historyTime = it.historyTime;
           if(runTime != 0){
               def nn = 'select a.username, a.sid,b.SQL_TEXT as sqlText, b.SQL_FULLTEXT\n' +
                       '  from v$session a, v$sqlarea b\n' +
                       'where a.sql_address = b.address and a.sid= '+sid;
               sql.eachRow(nn,{aa ->
                   String sqlText = aa.sqlText;
                   File file = new File("killSql.txt");
                   println(file.getAbsolutePath())
                   file.withWriterAppend {out->

                       //同步把数据写入文件中
                       out.println("${new Date().format("yyyy-MM-dd HH:mm:ss")}执行的sql为：${sqlText}");
                   }

                   println("sql:${aa.sqlText},执行时长：${historyTime},还需要执行：${runTime}");
               })

               //进行执行kill操作，需要进行记录对应sql文本
               String killSql = "ALTER SYSTEM DISCONNECT SESSION \'${sid},${serial}\' IMMEDIATE";
               println killSql
               try {
                   sql.executeUpdate(killSql)
               } catch (e) {
               }

           }
       })

   }

    public void insertUser(){
        Calendar calendar = Calendar.getInstance();
        //需要查询所有的地市
        sql.eachRow("select * from t_area",{it->
            def id = it.id;
            sql.eachRow("select * from t_subarea where areaid=${id}",{subarea->
                def subId = subarea.id;
                sql.eachRow("select sn from t_customer where rownum < 100 group by sn",{s->
                    String ss = "insert into T_USER_VOD(sn,time,Vodcount,areaid,Subareaid) values(${s.sn},to_date('${calendar.getTime().format("yyyy-MM-dd")}','yyyy-mm-dd'),${Math.round(100)},${id},${subId})";
                    sql.execute(ss);
                })
            })
        })
        //
    }

    //根据地市查询所有分前端

    //组织对应字符串
    public String makeXml(){
//        <Stb Type=\"0\" Sn=\"8440210211455064\" SnIp=\"10.176.192.20\"
// Time=\"${time}\" AssetID=\"ED555046DB95285C92E602081BB14150\"
// VSS=\"172.26.115.5\" VSSStream=\"172.26.117.136:48156\" GroupCode=\"2085305020\"
// Band=\"3750\" IPQAM=\"192.168.20.136/27\" Freq=\"554000000\" ProgramNo=\"5\"
// SubAreaID=\"20853\" AreaID=\"${areaId}\" SessionID=\"301542884295812\" />

    }
    //模拟对应统计数据
}

/*    Timer timer = new Timer();
    def i = 0;
    timer.schedule(new TimerTask() {
        @Override
        void run() {
            MonitorAlarm ma = new MonitorAlarm();
            println("###########################START RUN STAET##########################################");
            ma.searchDemo();
            println("###########################START RUN end##########################################");
        }
    },1000,1000*60*1);*/
/*MonitorAlarm ma = new MonitorAlarm();
println("###########################START RUN STAET##########################################");
ma.searchDemo();

    println("###########################START RUN end##########################################");*/


