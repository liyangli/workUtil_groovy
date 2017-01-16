import groovy.sql.Sql
import java.util.Calendar

/**
 *
 * User: liyangli
 * Date: 2015/12/22
 * Time: 17:26
 */
//Sql sql = Sql.newInstance("jdbc:oracle:thin:@10.2.98.4:1521:orcl","vsmserver",
//        "123456","oracle.jdbc.driver.OracleDriver");
//设定每天

Calendar cal = Calendar.getInstance();
cal.add(Calendar.DAY_OF_MONTH,-22);

for(int i=1;i<=21;i++){
    cal.add(Calendar.DAY_OF_MONTH,1)
    File file = new File("Demo${cal.getTime().format("yyyy-MM-dd")}.csv");
    String str = "select sn,starttime,endTime from t_customer where groupcode in (select groupid from t_tsid where ipqamid in (select id from t_ipqam where ip in ('10.2.42.202','10.2.42.203'))) and \n" +
            "starttime >= to_date('${cal.getTime().format("yyyy-MM-dd")} 20','yyyy-mm-dd hh24') " +
            "and starttime <= to_date('${cal.getTime().format("yyyy-MM-dd")} 23','yyyy-mm-dd hh24')"
    println(str);
//    sql.eachRow(str,{it->
//        //存放到具体文件中
//        file.withWriterAppend {out->
//
//            //同步把数据写入文件中
//            out.println("${it.sn},${it.starttime},${it.endTime}");
//        }
//    })
}