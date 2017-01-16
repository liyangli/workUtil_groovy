/**
 *
 * User: liyangli
 * Date: 2014/11/14
 * Time: 18:48
 */
def address="http://172.17.5.71:8111"
URL url = new URL(address);
con = url.openConnection();
//con.setConnectTimeout(Integer.MAX_VALUE);
con.setDoOutput(true);
con.setDoInput(true)
def file = new File("F:\\项目\\广州运维管理\\测试文件")
def files = file.listFiles();
def out = con.getOutputStream()
while(true){
    long start = System.currentTimeMillis();
    int num = Math.random()*10;
    def ff = files[num];
    ff.eachLine {
        out.write(it.getBytes())
    }
    out.write("\r\n".getBytes())
    out.flush()
//    out.close()
    println("cost Time:${System.currentTimeMillis()-start}")
}

