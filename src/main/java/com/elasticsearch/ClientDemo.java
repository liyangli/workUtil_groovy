package com.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量数据入库操作
 * User: liyangli
 * Date: 2016/2/25
 * Time: 09:03
 */
public class ClientDemo {

    private final JestClient client;
    public ClientDemo() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .multiThreaded(true)
                .build());
        client = factory.getObject();
//        Node node = NodeBuilder.nodeBuilder().local(true).node();
    }

    private void createIndex() throws Exception{
        Person person = new Person("liyangli",23);
        Index index = new Index.Builder(person).index("twitter").type("tweet").build();
        JestResult result = client.execute(index);
        System.out.println(result.getJsonString());
    }
    private void createBuld() throws Exception{
        long start = System.currentTimeMillis();
        List<Index> indexs = new ArrayList<>();
        for(int i=0;i<1000000;i++){

            Person person = new Person("liyangli"+i,23);
            indexs.add(new Index.Builder(person).index("twitter").type("tweet").build());
            if(i%10000 == 0){
                List<Index> ll = new ArrayList(indexs);
                indexs.clear();
                Bulk bulk = new Bulk.Builder()
                        .defaultIndex("twitter")
                        .defaultType("tweet")
                        .addAction(ll)
                        .build();

                client.execute(bulk);
            }
        }
        Bulk bulk = new Bulk.Builder()
                .defaultIndex("twitter")
                .defaultType("tweet")
                .addAction(indexs)
                .build();
        client.execute(bulk);
        System.out.println("消耗的时间为："+(System.currentTimeMillis()-start));
    }
    private void close(){
//        if(this.client != null){
//            this.client.close();
//        }
    }
    public static void main(String[] args) throws Exception{
        ClientDemo cd = new ClientDemo();
//        cd.createIndex();
        cd.createBuld();
        cd.close();
    }
}
