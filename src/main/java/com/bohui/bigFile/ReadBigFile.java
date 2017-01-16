package com.bohui.bigFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * 读取大文件
 * User: liyangli
 * Date: 2015/2/28
 * Time: 14:27
 */
public class ReadBigFile {

    private int maxBuf = 6*1024*1024;
    private BlockingQueue<File> queue = new LinkedBlockingQueue<File>(30);
    private ExecutorService exec = Executors.newFixedThreadPool(2);
    public Object readFile() throws Exception{
        File file = new File("E:\\demo\\测试数据\\gztemp\\oraclebase");

        if(file.isDirectory()){
            //单独customer进行入库测试
           File[] files = file.listFiles(new FilenameFilter() {
               @Override
               public boolean accept(File dir, String name) {
                   if( name.lastIndexOf("t_customer.sql") != -1 ){
                       return true;
                   }
                   return false;
               }
           });
//            long allStart = System.currentTimeMillis();
           for(File ff:files){
               queue.put(ff);

           }
        }

        //开始启动线程进行设定
        try {
            List<Future<Integer>> list = new ArrayList<Future<Integer>>();
            Future<Integer> future = exec.submit(new DealFileTask(queue));
//                    future1 = exec.submit(new DealFileTask(queue));
            list.add(future);
//            list.add(future1);
            long allStart = System.currentTimeMillis();
            for(Future<Integer> ff:list){
                int num = ff.get();
                System.out.println("线程执行个数："+num);
            }
            System.out.println("all costTime:"+(System.currentTimeMillis()-allStart)+";");
        } finally {
            exec.shutdown();
            exec.awaitTermination(1,TimeUnit.SECONDS);
        }

        return null;
    }


    public void singlonTheadReadFile() throws Exception{
//        File file = new File("F:\BaiduYunDownload\gztemp\oraclebase");
        File file = new File("C:\\Users\\liyangli\\Desktop\\12345");
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.lastIndexOf(".sql") != -1){
                    return true;
                }
                return false;
            }
        });
//            long allStart = System.currentTimeMillis();
        for(File ff:files){
            queue.add(ff);

        }

        try {
            for(int i=0;i<1;i++){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE,-1);
                exec.execute(new SaveFile(queue).setDateTime(cal.getTime()).setNum(1));
            }
        } finally {
            exec.shutdown();
        }
    }
}

class SaveFile implements Runnable{
    private int maxBuf = 1*1024*1024;
    private BlockingQueue<File> queue ;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private Map<String,String> fileLastInsert = new ConcurrentHashMap<String, String>();
    private Date dateTime;
    private int num = 1;
    SaveFile(BlockingQueue<File> queue ){
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SaveFile setDateTime(Date dateTime){
        if(dateTime == null){
            dateTime = new Date();
        }
        this.dateTime = dateTime;
        return this;
    }

    public SaveFile setNum(int num){
        this.num = num;
        return this;
    }
    public void save() throws Exception{

        while(true){
            File ff = queue.poll();
            if(ff == null){
                return ;
            }
            FileChannel fc = new RandomAccessFile(ff, "r").getChannel();
            File pff = ff.getParentFile();

            ByteBuffer byteBuffer = ByteBuffer.allocate(maxBuf);
            byte[] bytes = new byte[maxBuf];
            String tempString = null;
            long start = System.currentTimeMillis();
            /*String ss = pff.getAbsolutePath()+"/cp/"+sdf.format(this.dateTime)+"_"+ff.getName();
            File cpFile = new File(ss);
            if(!cpFile.exists()){
                File cppFile = cpFile.getParentFile();
                if(!cppFile.exists()){
                    cppFile.mkdirs();
                }
                cpFile.createNewFile();
            }
            FileChannel out = new RandomAccessFile(cpFile, "rw").getChannel();*/
            int num = 0;
            while(fc.read(byteBuffer) != -1){
                //每5M进行读取数据
                int rsize = byteBuffer.position();
                byteBuffer.rewind();
                byteBuffer.get(bytes);
                byteBuffer.clear();
                tempString = new String(bytes,0,rsize,"GBK");
                //内容进行处理
                String dealSave = append(tempString,ff.getName());
                if(dealSave == null || dealSave.isEmpty()){
                    continue;
                }


                String ss = pff.getAbsolutePath()+"/cp/"+sdf.format(this.dateTime)+"_"+num+"_"+ff.getName();
                File cpFile = new File(ss);
                if(!cpFile.exists()){
                    File cppFile = cpFile.getParentFile();
                    if(!cppFile.exists()){
                        cppFile.mkdirs();
                    }
                    cpFile.createNewFile();
                }
                num ++;
                FileChannel out = new RandomAccessFile(cpFile, "rw").getChannel();
                writeFileByLine(out,byteBuffer,dealSave);
            }
            long size = fc.size();
            System.out.println("costTime:"+(System.currentTimeMillis()-start)+";size:"+size+";fileName:"+ff.getName());
            fc.close();
        }
    }
    /*写文件*/
    public static void writeFileByLine(FileChannel fcout, ByteBuffer wBuffer, String line){
        try {
            //write on file head
//            fcout.write(wBuffer.wrap(line.getBytes()));
            //wirte append file on foot
            fcout.write(wBuffer.wrap(line.getBytes()), fcout.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件需要追加内容
     * @param data 需要追加的数据
     * @param fileName 文件名称
     */
    private String append(String data,String fileName) throws Exception{
        //内容进行过滤
        String[] ss = data.split("\r\n");

        String insertStart = fileLastInsert.get(fileName);
        if(insertStart == null){
            insertStart = "";
        }
        boolean flag = false;//由于需要多线程处理，隐藏去除掉所有commit；
        StringBuilder str = new StringBuilder();
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
                str.append(makeSQL(line));
            }else if(line.startsWith("insert") ){
                //没有结束
                insertStart = line;
            }else{
                //结束
                insertStart += line;
            }
            if(insertStart.endsWith(";")){
                //组装完成后，需要设定并且清空
                str.append(makeSQL(insertStart));
                insertStart = "";
            }
        }
        fileLastInsert.put(fileName,insertStart);
//        str.append("commit;");//文件执行完成需要单独执行commit;
        return str.toString();
    }

    private String  makeSQL(String sqlStr){

        StringBuilder str = new StringBuilder();
        try {
            //开始计算跨天个数
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTime);
            if(sqlStr.indexOf("commit") != -1){
                str.append("\r\n").append(sqlStr);
                return str.toString();
            }
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

                //数据需要放到文件中
                str.append("\r\n").append(dateSql);
                cal.add(Calendar.DATE,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取数据获得错误了："+e.getMessage());
        }
        return str.toString();
    }
}
class DealFileTask implements Callable<Integer>{
    private int maxBuf = 10*1024*1024;
    private BlockingQueue<File> queue ;
    DealFileTask(BlockingQueue<File> queue){
        this.queue = queue;
    }
    @Override
    public Integer call() throws Exception {
        int num = 0;
        StringCache cache =StringCache.CACHE;
//        StringDemoCache cache = StringDemoCache.CACHE;
       /* Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-2);
        cache.setDateTime(cal.getTime(),3);*/
        while(true){
            File ff = queue.poll();
            if(ff == null){
                return num;
            }
            num ++;
            FileChannel fc = new RandomAccessFile(ff, "r").getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(maxBuf);
            byte[] bytes = new byte[maxBuf];
            String tempString = null;
            long start = System.currentTimeMillis();

            while(fc.read(byteBuffer) != -1){
                //每5M进行读取数据
                int rsize = byteBuffer.position();
                byteBuffer.rewind();
                byteBuffer.get(bytes);
                byteBuffer.clear();
                /*tempString = new String(bytes,0,rsize);
                System.out.println(tempString);*/
                tempString = new String(bytes,0,rsize,"GBK");
                //单独文件读需要耗费时间
                cache.append(tempString,ff.getName());
            }
            long size = fc.size();
            System.out.println("costTime:"+(System.currentTimeMillis()-start)+";size:"+size+";fileName:"+ff.getName());
            fc.close();
        }
    }
}