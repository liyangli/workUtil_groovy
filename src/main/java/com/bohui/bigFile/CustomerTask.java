package com.bohui.bigFile;

import groovy.sql.Sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 消费者任务
 * 进行处理缓存中对应sql数据，需要进行获取数据然后进行入库
 * User: liyangli
 * Date: 2015/3/2
 * Time: 15:08
 */
public class CustomerTask implements Runnable{

    Sql sql = null;
//    List list = new ArrayList<>();
//    {
//        list.add("t_customer");
//        list.add("T_MONITOR_FAILURE_STATISTIC");
//        list.add("T_SERVICE_ALARM_STATISTIC");
//    }

    public CustomerTask(){
        try {
            sql = Sql.newInstance("jdbc:oracle:thin:@172.17.13.108:1521:orcl", "vsmserver",
                    "Bohui@123", "oracle.jdbc.driver.OracleDriver");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        //开始执行任务，
        com.bohui.bigFile.StringCache cache = com.bohui.bigFile.StringCache.CACHE;
        int num = 0;//5000个提交一次
        while(true){
            //该线程在没有数据超过1s后自动挂断

            try {
                String sqlStr = cache.findInstallSql();
                /*if(sqlStr.indexOf("T_MONITOR_ALARM_STATISTIC") != -1 ||
                        sqlStr.indexOf("T_MONITOR_FAILURE_STATISTIC") != -1 ||
                sqlStr.indexOf("T_SERVICE_ALARM_STATISTIC") != -1){
                    continue;
                }*/
                if(sqlStr== null){
                    commit();
                    return;
                }
                num ++;
               try {
                    sql.execute(sqlStr.substring(0, sqlStr.length() - 1));
                } catch (SQLException e) {
                    System.out.println("出现异常："+e.getMessage());
                    //防止关闭
                    sql = Sql.newInstance("jdbc:oracle:thin:@172.17.13.108:1521:orcl", "vsmserver",
                            "Bohui@123", "oracle.jdbc.driver.OracleDriver");
                    sql.execute(sqlStr.substring(0, sqlStr.length() - 1));
                }

                System.out.println(sqlStr);
                //开始执行sql
            } catch (Exception e) {
                //表面没有数据了，需要进行提交
                commit();
                return ;
            }
            if(num == 10000){
                //开始提交；
                commit();
                num = 0;
            }
        }

    }

    private void commit() {
        try {
            sql.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
