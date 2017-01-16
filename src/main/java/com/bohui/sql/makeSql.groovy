package com.bohui.sql

import groovy.sql.Sql

/**
 *
 * 组装需要优化的sql语句
 * User: liyangli
 * Date: 2015/8/8
 * Time: 14:58
 */
/**
 * db操作类
 */
class DBOperator{

    private Sql sql = null;
    public DBOperator(){
        sql = Sql.newInstance("jdbc:oracle:thin:@172.17.13.92:1521:orcl","vsmserver",
        "123456","oracle.jdbc.driver.OracleDriver");
    }

    public List<String> findAllGroup(){
        if(sql == null){
            println("小样，没有连接上数据库哟！");
            return new ArrayList();
        }
        def list = new ArrayList();
        sql.eachRow("select id from t_group",{it->
            list.add(it.ID);
//            list.add(value);
        })
        return list;
    }

   public void execSearch(def ll){
       def start = System.currentTimeMillis();
       def list = new ArrayList();
       ll.each{
           it->
               sql.eachRow(it,{obj->
                   list.add(obj);
               });
       }
       println("cost time:${(System.currentTimeMillis()-start)},list size:"+list.size())
    }
    public void otherCostSql(){
        def execSql = "select  temp.* from (\n" +
                "select  t.*, row_number() OVER(PARTITION BY groupid ORDER BY t.saturation desc) as\n" +
                " row_flg  from ( \n" +
                " SELECT  groupCount.*  from t_group_count  groupCount,t_group  g,t_subarea  subarea\n" +
                "  where groupCount.groupid = g.id  and g.subareaId=subarea.id and subarea.areaid\n" +
                "   in (423,418,436,420,417,419,422,421,416,428)  and time >= to_date('2015-03-03','yyyy-mm-dd') \n" +
                "   and time < to_date('2015-03-04','yyyy-mm-dd') )t ) temp where\n" +
                "    temp.row_flg  = '1' order by saturation desc";
        def start = System.currentTimeMillis();
        def list = new ArrayList();
        sql.eachRow(execSql,{obj->
            list.add(obj);
        });
        println("cost time:${(System.currentTimeMillis()-start)},list size:"+list.size())
    }
}

DBOperator db= new DBOperator();
def list = db.findAllGroup();
println list;
//开始组装具体sql语句。然后进行执行对应语句
StringBuilder sb = new StringBuilder();
def flag = false;
int i =0;
def ll = new ArrayList();
list.each {it->
    if(i == 3){
        ll.add(sb.toString());
        i = 0;
        flag = false;
        sb = new StringBuilder();
    }
    if(flag){
        sb.append(" union all ");
    }else{
        flag = true;
    }
    def execSql = "(select * from (\n" +
            "     SELECT  groupCount.* ,row_number() OVER(PARTITION BY groupCount.groupid ORDER BY groupCount.saturation desc) from t_group_count  groupCount\n" +
            "  where\n" +
            "   time >= to_date('2015-03-03','yyyy-mm-dd') \n" +
            "   and time < to_date('2015-03-04','yyyy-mm-dd')\n" +
            "   and groupid =${it} ) where rownum=1)";

    sb.append(execSql)
    i++;

}
ll.add(sb.toString());
//打印出来具体组装的sql
//println(ll.get(0))
db.execSearch(ll);
db.otherCostSql();


