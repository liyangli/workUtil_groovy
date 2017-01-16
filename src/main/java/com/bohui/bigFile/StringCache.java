package com.bohui.bigFile;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * 字符串缓存，处理文件读取内容
 * 对外提供方法：
 * 1、数据需要自动追加
 * 2、获取所有组装好的inster字符串
 * 内部处理方式：
 * 1、数据进行存放，需要区分不同文件内容，主要为了防止多线程导致多个文件数据错乱
 * 2、过滤注释字符串、清空表内容字符串
 * 3、组装过程中需要针对没有拼接完成的字符串处于等待状态。
 * User: liyangli
 * Date: 2015/3/2
 * Time: 10:43
 */
public enum StringCache {
    CACHE;

    private Date dateTime =  new Date();//指定的日期，从那天开始计算
    private int  num = 30;//执行间隔天数



    /**
     * 设置需要计算的时间，默认为当前日期，一天数据
     * @param dateTime
     * @param num
     */
    public void  setDateTime(Date dateTime,int num){
        if(null != dateTime  ){
            this.dateTime = dateTime;
        }
        if(num != 0){
            this.num = num;
        }
    }

    private Map<String,String> fileLastInsert = new ConcurrentHashMap<String, String>();//记录文件最后一次没有组装好的insert字符串
    private BlockingQueue<String> sqlQueue = new LinkedBlockingDeque<String>(100000); //多线程处理时候用,生产者2个线程
    /**
     * 文件需要追加内容
     * @param data 需要追加的数据
     * @param fileName 文件名称
     */
    public List<String> append(String data,String fileName) throws Exception{
        List<String> list = new ArrayList<String>();
        //内容进行过滤
        String[] ss = data.split("\r\n");

        String insertStart = fileLastInsert.get(fileName);
        if(insertStart == null){
            insertStart = "";
        }
        boolean flag = false;//由于需要多线程处理，隐藏去除掉所有commit；
        for(String line : ss){
            //需要判断是否需要进行处理
            if(line.startsWith("prompt") || line.startsWith("set") || line.startsWith("alter") || line.startsWith("delete") ){
                System.out.println(line);
                continue;
            }
            //需要进行自动组装数据了
            if(line.startsWith("commit")){
                //需要进行追加进去
                if(!flag){
                    continue;
                }
            }
            if(line.startsWith("insert") && line.lastIndexOf(";") != -1){
                //完整
                makeSQL(insertStart,list);
            }else if(line.startsWith("insert") ){
                //没有结束
                line=line.replaceAll("insert into","insert into /*+ append */ ");
                insertStart = line;
            }else{
                //结束
                insertStart += line;
            }
            if(insertStart.endsWith(";")){
                //组装完成后，需要设定并且清空
                makeSQL(insertStart,list);
                insertStart = "";
            }
        }
        fileLastInsert.put(fileName,insertStart);
        return list;
    }

    private void makeSQL(String sqlStr, List<String> list){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            //开始计算跨天个数
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTime);
            for(int i=0;i<num;i++){
                String date = sdf.format(cal.getTime());//获取需要处理的日期
                String dateSql = "";
                dateSql = sqlStr.replaceAll("\\d\\d-\\d\\d-\\d\\d\\d\\d",date);
                if(sqlStr.indexOf("T_INTELLIGENT_ALARM") != -1 ||
                        sqlStr.indexOf("T_AREA_COUNT") != -1 ||
                        sqlStr.indexOf("T_CUSTOMER") != -1 ||
                        sqlStr.indexOf("T_GROUP_COUNT") != -1 ||
                        sqlStr.indexOf("T_IPQAM_COUNT") != -1 ||
                        sqlStr.indexOf("T_SERVER_COUNT") != -1 ||
                        sqlStr.indexOf("T_SIP") != -1 ||
                        sqlStr.indexOf("T_SIP_ALARM") != -1 ||
                        sqlStr.indexOf("T_STATUSCODE_COUNT") != -1 ||
                        sqlStr.indexOf("T_STB_ONLINE") != -1 ||
                        sqlStr.indexOf("T_SUBAREA_COUNT") != -1 ){
                    dateSql=dateSql.replaceAll("(\\((\\d)*,)","(null,");
                }

//                list.add(dateSql);
//                sqlQueue.put(dateSql);
                cal.add(Calendar.DATE,1);
                sqlQueue.put(dateSql);
            }
        } catch (Exception e) {
            System.out.println("获取数据获得错误了："+e.getMessage());
        }
    }

    /**
     * 获取组装好的数据,
     * @return
     */
    public String findInstallSql() throws  Exception{
        String sql = this.sqlQueue.poll(10,TimeUnit.SECONDS);
        return sql;
    }

    /**
     * 把相关数据存放到换乘中
     * @param list
     */
    public void putCacheData(List<String> list) {
        sqlQueue.addAll(list);
    }
}
