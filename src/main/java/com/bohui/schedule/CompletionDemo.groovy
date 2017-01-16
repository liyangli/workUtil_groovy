package com.bohui.schedule

import java.util.concurrent.Callable
import java.util.concurrent.CompletionService
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * 并行计算
 * User: liyangli
 * Date: ${date}* Time: ${hour}:${minute}
 * */
ExecutorService exec = Executors.newScheduledThreadPool(10)
ExecutorService exec1 = Executors.newScheduledThreadPool(10)
final CompletionService<ImgDate> cs = new ExecutorCompletionService<>(exec)
final List<Future> ll = new ArrayList<>()

exec.submit(new Runnable() {
    @Override
    void run() {
        for( int i=0;i<10;i++){
            final int num = i;
            cs.submit(new Callable<ImgDate>() {
                @Override
                ImgDate call() throws Exception {
                    double dd = Math.random()
                    int sellp = dd*100
                    TimeUnit.SECONDS.sleep(sellp)
                    return new ImgDate("name":"lyl"+num,"num":num,"sleep":sellp)
                }
            })

        }
        for(int i=0;i<10;i++){
            Future<ImgDate> f = cs.take()
            ImgDate im = f.get()
            println("CompletionDemo:${im}")

        }
    }
})
exec.submit(new Runnable() {
    @Override
    void run() {
        for( int i=0;i<10;i++){
            final int num = i;
            ll.add(exec1.submit(new Callable() {
                @Override
                Object call() throws Exception {
                    double dd = Math.random()
                    int sellp = dd*100
                    TimeUnit.SECONDS.sleep(sellp)
                    return new ImgDate("name":"lyl"+num,"num":num,"sleep":sellp)
                }
            }))
        }
        for(int i=0;i<10;i++){
            Future ff = ll.get(i)
            println("Future::"+ff.get())
        }
    }
})





