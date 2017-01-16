import java.util.concurrent.Exchanger
import java.util.concurrent.TimeUnit

/**
 *
 * User: liyangli
 * Date: ${date}* Time: ${hour}:${minute}*/
 //生产着
 class Producter extends Thread{
    Exchanger<List<String>> exchanger = null
     Producter(Exchanger<List<String>> exchanger){
         this.exchanger = exchanger
     }
     public void run(){
         while(true){
             List<String> ll = new ArrayList<>()
             for(int i=0;i<10;i++){
                 ll.add(Math.random().toString()+i)
             }
             ll = exchanger.exchange(ll)
             System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%")
             TimeUnit.SECONDS.sleep(10)
         }

     }
}

class Customer extends Thread{
    Exchanger<List<String>> exchanger = null
    Customer(Exchanger<List<String>> exchanger){
        this.exchanger = exchanger
    }
    //消费者
    public void run(){
        while(true){
            List<String> ll = new ArrayList<>()
            ll = exchanger.exchange(ll)
            for(String ss:ll){
                System.out.println(ss)
            }
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%")
            TimeUnit.SECONDS.sleep(10)
//            ll.clear()
//            exchanger.exchange(ll)
        }
    }
}

Exchanger<List<String>> exchanger = new Exchanger<>()
new Producter(exchanger).start()
new Customer(exchanger).start()