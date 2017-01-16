import java.util.concurrent.Exchanger

/**
 * 进程列表展示
 * User: liyangli
 * Date: ${date}* Time: ${hour}:${minute}*/

Timer timer = new Timer()
timer.schedule(new TimerTask() {
    @Override
    void run() {
        def tt = "tasklist".execute().text
//println tt
//println tt.size()
        Map<String,Integer> map = new HashMap();
        tt.eachLine {

            if(!it.isEmpty()){
                def strs = it.split("  ");

                if(strs.length > 1){
                    List<String> ll = new ArrayList<>()
                    strs.each {
                        if(!it.trim().isEmpty()){
                            ll.add(it.trim())
                        }
                    }
//            println ll
                    String cpu = ll.get(2);
                    try {
                        int nn = Integer.parseInt(cpu)
                        if(nn >=0)
                            map.put(ll.get(0),nn);
                    } catch (Exception e) {

                    }


                }
            }
        };
//        map.size()
//        println map;
        if(map.isEmpty()){
            return;
        }
        println map.sort {
            a, b -> b.value <=> a.value
        }
//        println map
    }
},1000,1000)
