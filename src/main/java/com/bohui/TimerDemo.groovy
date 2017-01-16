import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *
 * User: liyangli
 * Date: ${date}* Time: ${hour}:${minute}*/
//ExecutorService exec = Executors.newScheduledThreadPool(10)
//exec.scheduleWithFixedDelay(new Runnable() {
//    @Override
//    void run() {
//        Thread current = Thread.currentThread();
//        println("${current.getId()}::${current.getName()}############${new Date()}")
//        TimeUnit.SECONDS.sleep(10)
//    }
//},0,1,TimeUnit.SECONDS)
//println "${Thread.currentThread().getName()}"
for(int i=0;i<10;i++){
    double dd = Math.random()
    println dd
}

/*
Timer timer = new Timer();
timer.schedule(new TimerTask() {
    @Override
    void run() {
        println("############${new Date()}")
        TimeUnit.SECONDS.sleep(10)
    }
} ,1000,1000)*/
