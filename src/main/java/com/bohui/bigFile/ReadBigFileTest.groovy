package com.bohui.bigFile

//import org.junit.Test

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 * User: liyangli
 * Date: 2015/2/28
 * Time: 16:18
 */
class ReadBigFileTest  {
//    @Test
    void testReadFile() {
        ExecutorService exec = Executors.newCachedThreadPool(); //6个线程同时去执行
        //开启消费者
        try {
            final ReadBigFile readBigFile = new ReadBigFile();

            exec.execute(new Runnable() {
                @Override
                void run() {
                    readBigFile.readFile();
//                    readBigFile.singlonTheadReadFile();
                }
            })
            println("######################整体线程开启的时间：${new Date().format("yyyy-mm-dd HH:mm:ss")},long:${new Date().getTime()}")
           for(int i=0;i<5;i++){
                exec.execute(new CustomerTask());
//                exec.execute(new ElacisearchTask());
            }
        } finally {
            exec.shutdown();
        }
//        readBigFile.singlonTheadReadFile();
    }

    public final static void main(String[] args){
        ReadBigFileTest rbf = new ReadBigFileTest();
        rbf.testReadFile();

        /*ReadBigFile readBigFile = new ReadBigFile();
        println(1232)
        readBigFile.singlonTheadReadFile();*/
    }
}
