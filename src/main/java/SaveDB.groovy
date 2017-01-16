import groovy.sql.Sql

/**
 *
 * User: liyangli
 * Date: 2016/6/4
 * Time: 11:16
 */
def sql = Sql.newInstance("jdbc:oracle:thin:@172.17.7.109:1521:orcl","sop",
            "Bohui@123","oracle.jdbc.driver.OracleDriver");

//组装需要的数据
def file = new File("E:\\jszs\\url.txt");
List<String> urls = file.readLines();
def file1 = new File("E:\\jszs\\jmsj.txt");
List<String> jmsjs = file1.readLines();
int len = urls.size();

int start = 3000;
for(int i=0;i<len;i++){
    //开始组装对应的url。
    def name = jmsjs.get(i);
    def url = urls.get(i);
    def index = start+i;
    println("insert into T_SIGNAL (ID, ALIAS, ENABLE, ORGNETID, STREAMID, URL,  MONITORDEVICEID, SIGNALTYPEID, TSID,   SIGNALTYPENAME, SIGNALNODEID) values (${index}, '${name}', 1,  '0', '${index}', '${url}',  1020, 16, '0','IP', -1);");
}

