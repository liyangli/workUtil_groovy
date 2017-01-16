package com.bohui.sip

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * http客户端，主要模拟Daemon进行上报数据
 * User: liyangli
 * Date: 2015/7/11
 * Time: 04:16
 */
/**
 * 协议对象，处理对应协议交互信息
 */
class Protocol {
    def url = "http://localhost/servlet/receiver";
    def tasktime=10000;
    def maxThread=1;
    Map<String,Long> map = new ConcurrentHashMap<>();//key : sessionID val:发送在线的时间，超时机制为10分钟
    ExecutorService exec  = Executors.newFixedThreadPool(maxThread);
    /**
     * 真正发送动作
     */
    public void sendThread(){
        //设定定时任务，执行30个线程进行模拟30个地市进行不同方式进行发送数据，每个线程记录自己数据，相互不进行干扰
        Timer timer = new Timer();
        timer.schedule(task(),tasktime,tasktime)
    }

    public void send(){
        //单个线程发送需要模拟测试是否能够成功
        def sendContent = makeSendData(10)
        if(!"false".equals(sendContent)){
            println("send xml:"+sendContent)
            sendPost(url,sendContent)
        }else{
            //没有什么需要进行发送的
            println "没有什么需要发送的数据"
        }
    }

    private TimerTask task(){
        //单个任务快，执行具体发送，具体在线发送个数为随机数，同时最大为1000，周期上报为5s.
        return new TimerTask() {
            @Override
            void run() {
                println "开始执行timeTask"
                //里面进行启动30个线程，模拟不同地市进行上报数据
                for(int i=0;i<maxThread;i++){
                    exec.execute(new Runnable() {
                        @Override
                        void run() {
                            def max = 1000;
                            def onlineUsers = (Math.random()*max).intValue();//5s内最大上线人数最大超不过1000
                            def sendContent = makeSendData(onlineUsers)
                            if(!sendContent instanceof Boolean){
                                //数据写入到文件中；
                                new File("E:\\demo\\liyangli.xml").withPrintWriter { printWriter ->
                                    printWriter.println(sendContent);
                                }
                                println("send xml:"+sendContent)
                                sendPost(url,new String(sendContent.getBytes("UTF-8"),"GB2312"))
                                println "发送成功"
                            }else{
                                //没有什么需要进行发送的
                                println "没有什么需要发送的数据"
                            }
                        }
                    })
                }
            }
        }
    }



    private void sendGet(url,param){
        String result = "";
        BufferedReader br = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void sendPost(url,param){
        PrintWriter out = null;
        BufferedReader br = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
//            conn.setRequestProperty("x-forwarded-for", "CDN");
//            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-type","text/xml;charset=UTF-8")
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
            }
            println result
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(br!=null){
                    br.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

    def makeSendData(onlineUsers){
        //组装真正需要发送的数据
        def msg = this.makeHeader();
        msg += this.makeMsgHeader();
        msg += this.makeRes();
        //组装具体发送数据两种策略：
        //1、随机进行判断需要发送的为新增个数
        //2、随机获取需要设定离线用户个数，如果想设定离线状态，数据时间必须大于10分钟。
        def newUserNum = onlineUsers-(Math.random()*onlineUsers).intValue();
        //组装具体内容，真正变动的地方
        for(def i=0;i<newUserNum;i++){
            msg += this.makeNewContent();
        }
        //组装离线用户数据
        def firstFlag = true;
        for(def k=0;k<(onlineUsers-newUserNum);k++){
            //需要进行组装离线用户，在线时间必须大于10分钟
            def flag = this.makeUnlineUser();
            if(false.equals(flag)){
                //表明在线用户还没有超过10分钟
                println("时间：${new Date().format("yyyy-MM-dd hh:mm:ss")},在线个数为：${map.size()},还没有用户在离线状态，缺少离线的用户个数：${onlineUsers-newUserNum-k}")
                //需要进行判断如果新增用户和离线用户都没有情况下不允许进行发送协议
                if(newUserNum == 0 && firstFlag){
                    return false;
                }
                break;
            }else{
                firstFlag = false;
            }
            msg += flag;
        }
        msg +=  this.makeResFloder();
        msg +=  this.makeMsgFloder();
        return msg;
    }
    /**
     * 制作离线用户
     * @param msg
     * @return
     */
    def makeUnlineUser(){
        def msg = "";
        //离线用户关注sessionID和对应type
        //发送内容为<Stb sessionID="" type="1"/>
        def nowTime = new Date().getTime()-1*60*1000;
        def removeObj = null;
        synchronized (map){
            for(def key in map){
                if(key.value>nowTime){
                    msg += "<Stb Sn=\"${key.key}\" Type=\"1\"/>";
                    removeObj = key.key;
                    break;
                }
            }
            //同步进行需要把对应数据给移除掉
            if(removeObj!=null){
                map.remove(removeObj)
                return msg;
            }
            else{
                //表明没有匹配到需要删除的数据，需要进行等待
                return false
            }
        }
    }
    String makeHeader(){
        //协议头
        return "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>";
    }
    String makeMsgHeader(){
        return "<Msg Version=\"2\" MsgID=\"-1\" Type=\" BSHUp\" DateTime=\"2009-08-17 15:30:00\" SrcCode=\"110000G01\" DstCode=\"110000M01\" Priority=\"1\">";
    }
    String makeMsgFloder(){
        return "</Msg>";
    }
    String makeRes(){
        return "<Response Type=\"StbOnline\" Value=\"0\" Desc=\"desc\" >";
    }
    String makeResFloder(){
        return "</Response>";
    }
    /**
     * 表明新上线用户，需要进行同步记录到map中，方便进行设定下线状态
     * @return
     */
    String makeNewContent(){
        def rand = (Math.random()*100000000).intValue();
        def sn = rand;
        def snIp = "127.0.0.1";
        def time = new Date().format('yyyy-MM-dd h:mm:ss.SSS');
        def assetID= "GDTD"+rand;
        def vss = "172.17.5.71:12206";
        def vssStream="123";
        def groupCode = "1201";
        def band="10000";
        def ipqam="124.12.12.1:12208";
        def freq="189000000";
        def programNo="10";
        def subAreaID="10608";
        def subAreaName="jiangmen";
        def sessionID=rand;
        def type="0";
        def vodType="1";
        def areaId="422";
        def areaIp="10.2.98.41";
        def areaName="tr";
        //主要根据对应sessionID进行存放数据中
        map.put(sn,new Date().getTime());
        //组装具体可变内容，包含多条数据
        /*return "<Stb Sn=\"${sn}\" SnIp=\"${snIp}\" AreaIp=\"${areaIp}\" Time=\"${time}\" AssetID=\"${assetID}\" VSS=\"${vss}\" VSSStream=\"${vssStream}\" GroupCode=\"${groupCode}\" Band=\"${band}\" IPQAM=\"${ipqam}\" Freq=\"${freq}\" " +
                "ProgramNo=\"${programNo}\" SubAreaID=\"${subAreaID}\" SubAreaMame=\"${subAreaName}\" SessionID=\"${sessionID}\" Type=\"${type}\" AreaID=\"${areaId}\" " +
                "AreaName=\"${areaName}\" VodType=\"${vodType}\"/>";*/
        def tt = "2015-09-06 6:02:37.888";
       /* return "<Stb Type=\"0\"  AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210211455064\" SnIp=\"10.176.192.20\" Time=\"${time}\" AssetID=\"ED555046DB95285C92E602081BB14150\" VSS=\"172.26.115.5\" VSSStream=\"172.26.117.136:48156\" GroupCode=\"1001\" Band=\"3750\" IPQAM=\"192.168.20.136/27\" Freq=\"554000000\" ProgramNo=\"5\" SubAreaID=\"4220010\" AreaID=\"${areaId}\" SessionID=\"301542884295812\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210287337600\" SnIp=\"10.176.100.218\" Time=\"${time}\" AssetID=\"B9E9E9ABBDC55DD6F332CF327601A5F1\" VSS=\"172.26.115.5\" VSSStream=\"172.26.117.168:48156\" GroupCode=\"1002\" Band=\"3750\" IPQAM=\"192.168.20.168/27\" Freq=\"514000000\" ProgramNo=\"2\" SubAreaID=\"4220010\" AreaID=\"${areaId}\" SessionID=\"301542884295810\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210446701001\" SnIp=\"10.177.25.231\" Time=\"${time}\" AssetID=\"08C7EC509087F2E398D06FE19E747D1D\" VSS=\"172.26.115.6\" VSSStream=\"172.26.117.98:48156\" GroupCode=\"1001\" Band=\"7581\" IPQAM=\"192.168.20.98/27\" Freq=\"554000000\" ProgramNo=\"9\" SubAreaID=\"4220010\" AreaID=\"${areaId}\" SessionID=\"301542884295811\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210463477352\" SnIp=\"10.177.55.225\" Time=\"${time}\" AssetID=\"1229C0C37600C5C94EB34A3235E1B78D\" VSS=\"172.26.115.5\" VSSStream=\"172.26.117.72:48156\" GroupCode=\"1003\" Band=\"3750\" IPQAM=\"192.168.20.72/27\" Freq=\"546000000\" ProgramNo=\"4\" SubAreaID=\"4220010\" AreaID=\"${areaId}\" SessionID=\"301542884295809\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210446722916\" SnIp=\"10.176.242.126\" Time=\"${tt}\" AssetID=\"61BB324BACEC2FB9A2F751BA2C918DA1\" VSS=\"172.26.115.6\" VSSStream=\"172.26.117.104:48156\" GroupCode=\"1004\" Band=\"3750\" IPQAM=\"192.168.20.104/27\" Freq=\"490000000\" ProgramNo=\"5\" SubAreaID=\"4220010\" AreaID=\"${areaId}\" SessionID=\"301542884295795\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210314947025\" SnIp=\"10.177.32.104\" Time=\"${tt}\" AssetID=\"D76B2DD4A5B2682CA9FE146AE2FAABF4\" VSS=\"172.26.115.5\" VSSStream=\"172.26.117.72:48156\" GroupCode=\"1004\" Band=\"3352\" IPQAM=\"192.168.20.72/27\" Freq=\"490000000\" ProgramNo=\"2\" SubAreaID=\"4220010\" AreaID=\"${areaId}\" SessionID=\"301542884295778\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210337122150\" SnIp=\"10.177.81.113\" Time=\"${tt}\" AssetID=\"D3CDCE306BEEE1DEDE05AB5B0EC20312\" VSS=\"172.26.115.5\" VSSStream=\"172.26.117.34:48156\" GroupCode=\"1005\" Band=\"3750\" IPQAM=\"192.168.20.34/27\" Freq=\"546000000\" ProgramNo=\"9\" SubAreaID=\"4220010\" AreaID=\"${areaId}\" SessionID=\"614916599559969\" />\n" +
                "<Stb Type=\"1\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210337122150\" SnIp=\"10.177.81.113\" Time=\"${tt}\" AssetID=\"D3CDCE306BEEE1DEDE05AB5B0EC20312\" VSS=\"172.26.115.5\" VSSStream=\"172.26.117.34:48156\" GroupCode=\"1006\" Band=\"3750\" IPQAM=\"192.168.20.34/27\" Freq=\"546000000\" ProgramNo=\"9\" SubAreaID=\"4220010\" AreaID=\"${areaId}\" SessionID=\"614916599559969\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8851003762400478\" SnIp=\"172.17.5.71\" Time=\"${time}\" AssetID=\"ED555046DB95285C92E602081BB14150\" VSS=\"10.20.2.20\" VSSStream=\"172.26.117.136:48156\" GroupCode=\"1048834264\" Band=\"3750\" IPQAM=\"10.2.44.120\" Freq=\"554000000\" ProgramNo=\"5\" SubAreaID=\"10488\" AreaID=\"422\" SessionID=\"34929497160512551\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8851003171727081\" SnIp=\"172.17.5.71\" Time=\"${time}\" AssetID=\"ED555046DB95285C92E602081BB14150\" VSS=\"10.20.2.20\" VSSStream=\"172.26.117.136:48156\" GroupCode=\"1067033196\" Band=\"3750\" IPQAM=\"10.2.44.120\" Freq=\"554000000\" ProgramNo=\"5\" SubAreaID=\"10670\" AreaID=\"436\" SessionID=\"34929497160512551\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8851003835860039\" SnIp=\"172.17.5.71\" Time=\"${time}\" AssetID=\"ED555046DB95285C92E602081BB14150\" VSS=\"10.20.2.20\" VSSStream=\"172.26.117.136:48156\" GroupCode=\"1052733381\" Band=\"3750\" IPQAM=\"10.2.44.120\" Freq=\"554000000\" ProgramNo=\"5\" SubAreaID=\"10527\" AreaID=\"436\" SessionID=\"34929497160512551\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8851003762400478\" SnIp=\"172.17.5.71\" Time=\"${time}\" AssetID=\"ED555046DB95285C92E602081BB14150\" VSS=\"10.20.2.20\" VSSStream=\"172.26.117.136:48156\" GroupCode=\"1069732321\" Band=\"3750\" IPQAM=\"10.2.44.120\" Freq=\"554000000\" ProgramNo=\"5\" SubAreaID=\"10697\" AreaID=\"417\" SessionID=\"34929497160512551\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8851003762400478\" SnIp=\"172.17.5.71\" Time=\"${time}\" AssetID=\"ED555046DB95285C92E602081BB14150\" VSS=\"10.20.2.20\" VSSStream=\"172.26.117.136:48156\" GroupCode=\"1058032741\" Band=\"3750\" IPQAM=\"10.2.44.120\" Freq=\"554000000\" ProgramNo=\"5\" SubAreaID=\"10580\" AreaID=\"417\" SessionID=\"34929497160512551\" />\n" +
                "<Stb Type=\"0\" AssetName=\"1\" SmIp=\"192.168.11.35\" Sn=\"8440210399072947\" SnIp=\"10.176.243.18\" Time=\"${tt}\" AssetID=\"BBF2A7DDD2BBD1C2AFCAAE8B7C86C892\" VSS=\"172.26.115.6\" VSSStream=\"172.26.117.104:48156\" GroupCode=\"1068930104\" Band=\"3750\" IPQAM=\"192.168.20.104/27\" Freq=\"546000000\" ProgramNo=\"6\" SubAreaID=\"10689\" AreaID=\"416\" SessionID=\"614916599559882\" />";*/
        //读取文件；
        def file = new File('E:\\demo\\stbOnline.txt');
        def context = file.text;
        return context;

    }



}
def pro = new Protocol();
pro.sendThread()

