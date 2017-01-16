package com.bohui.bigFile;

import com.elasticsearch.Person;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 索引引擎入库处理
 * User: liyangli
 * Date: 2016/2/25
 * Time: 10:36
 */
public class ElacisearchTask implements Runnable {
    private final JestClient client;
    private final int MAXCOMMIT = 1000;
    public ElacisearchTask() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .multiThreaded(true)
                .build());
        client = factory.getObject();
    }

    private void commit(List<Index> indexs){
       Bulk bulk = new Bulk.Builder()
                .defaultIndex("vsmserver")
                .defaultType("table")
                .addAction(indexs)
                .build();
        try {
            long start = System.currentTimeMillis();
            client.execute(bulk);
            System.out.println("保存执行消耗时间为："+(System.currentTimeMillis()-start));
//            System.out.println(result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 制作sql
     * @param sql
     * @return
     */
    private Index makeIndex(String sql){
        //组装后成的语句为//insert
       String str = sql.replace("insert into /*+ append */  ","").replace("insert into ","");
        int tableIndex = str.indexOf(" ");
        String tableName = str.substring(0,tableIndex);
        str = str.substring(tableIndex);
        List<String> list = findContent(str);
        //数据已经含有了。下一步需要进行设定每一行的数据了
        String column = list.get(0);
        String[] columns = column.split(",");

        Map<String,String> map = new HashMap<>();
        map.put("index","not_analyzed");
        String valueContent = list.get(1);
        for(String key:columns){
            //需要获取另外一个对应属性数据
            //逐步每个字节进行判断
            char[] chars = valueContent.toCharArray();
//            StringBuilder sb = new StringBuilder();
            String sb = "";
            int num = 0;
            int strNum = 0;
            for(char conChar:chars){
                strNum ++;
                //如果含有，则进行停止
                if(conChar == ','){

                    //需要判断里面是否含有‘或者（

                    int khIndex = sb.indexOf("to_date(");
                    if(khIndex == -1){
                        int index = sb.indexOf("'");
                        if(index == -1){
                            valueContent = valueContent.substring(strNum);
                            chars = valueContent.toCharArray();
                            break;
                        }else{
                            //判断是否成对出现了
                            if(num%2 == 0){
                                //表明正常情况
                                valueContent = valueContent.substring(strNum);
                                chars = valueContent.toCharArray();
                                break;
                            }
                        }
                    }else{
                        int khEnd = sb.indexOf(")");
                        if(khEnd != -1){
                            //需要进行绩效
                            valueContent = valueContent.substring(strNum);
                            chars = valueContent.toCharArray();
                            break;
                        }
                    }


                }
                if(conChar == '\''){
                    num ++;
                }
                sb += new String(new char[]{conChar});

            }
            map.put(key.trim(),sb.replace("to_date('","").replace("', 'dd-mm-yyyy hh24:mi:ss')","").replace("'","").trim());

        }
        Index index = new Index.Builder(map).index("vsmserver").type(tableName).build();
        return index;
    }
    private void saveBulk() throws Exception{
        StringDemoCache cache = StringDemoCache.CACHE;
        List<Index> list = new ArrayList<>();
        int num = 1;
        while(true){
            //表明该线程一直执行
            String sqlStr = cache.findInstallSql();
            if(sqlStr== null){
                if(!list.isEmpty()){
                    commit(list);
                    list.clear();
                }
                return;
            }
            //需要根据sqlStr进行组装对应Index的内容
            list.add(makeIndex(sqlStr));
            if(num % MAXCOMMIT == 0){
                List<Index> indes = new ArrayList<>(list);
                list.clear();
                commit(indes);
            }
            num ++;
        }
    }

    @Override
    public void run() {
        try {
            saveBulk();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {



    }

    private static List<String> findContent(String src){
        //进行for循环。获取所有指定字符中字符串
       char preffix = '(';
        char reffix = ')';
        char[] chars = src.toCharArray();
        List<String> list = new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
        String sb = "";
        boolean flag = false;
        int preNum = 0;
        int reNum = 0;
        for(char charStr:chars){
            if(!flag && charStr != preffix && charStr != reffix ){
                continue;
            }
            if(charStr == preffix && !flag){
                //开始进行添加
                flag = true;
                continue;
            }else if(charStr ==reffix && flag){
                //需要判断当前sb中是否含有reffix.如果含有则直接添加。关闭。
               int preffixIndex = sb.indexOf(new String(new char[]{preffix}));
                if(preffixIndex == -1){
                    preNum = 0;
                    reNum = 0;
                    flag = false;
                    list.add(sb);
                    sb = "";
                    continue;
                }
                //需要判断关闭有多少个
                if(reNum == preNum){
                    flag = false;
                    list.add(sb);
                    sb = "";
                    continue;
                }
            }
            if(charStr == '('){
                preNum ++;
            }
            if(charStr == ')'){
                reNum ++;
            }
            sb += new String(new char[]{charStr});
        }
      return  list;
    }
}
