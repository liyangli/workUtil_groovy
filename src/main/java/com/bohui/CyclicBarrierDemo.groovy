import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit

/**
 *
 * User: liyangli
 * Date: ${date}* Time: ${hour}:${minute}*/

class SunTask extends Thread{
    private CyclicBarrier cycli;
    private String name;
    public SunTask(CyclicBarrier cycli,String name){
        this.cycli = cycli;
        this.name = name;
    }
    public void run(){
        TimeUnit.SECONDS.sleep(3);
        System.out.println(name+" 开始执行任务了。")
        try{
            cycli.await();
        }catch(Exception e){
        }
    }
}

CyclicBarrier cycli = new CyclicBarrier(2,new Runnable(){
    public void run(){
        System.out.println("主线程，看什么时候开始执行");
    }
})
new SunTask(cycli,"lyl").start();
TimeUnit.SECONDS.sleep(2);
new SunTask(cycli,'gyj').start();