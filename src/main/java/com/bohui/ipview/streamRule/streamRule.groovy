package com.bohui.ipview.streamRule

/**
 * 规则转换
 * 操作步骤：
 * １、读取对应文件
 * ２、根据文件进行解析，进行规则组装
 *    2.1、解析文件，需要进行拆分，主要获取channel和对应别名
 *    2.2、根据规则，默认2种服务器地址、8种匹配流规则
 * ３、输出指定格式xml文件。
 * User: liyangli
 * Date: 2015/8/12
 * Time: 11:34
 */
/**
 * 规则处理类
 */

class DealRule{
    private String line;

    public DealRule(String line){
        this.line = line;
    }

    public String[] findRule(){
        List<String> list = new ArrayList<>();
        if(line == null){
            return list;
        }
        def lines  = line.replace("/index.m3u8",",");
        return lines.split(",");
    }
}
class RateRule{
    final def rate = ["01","02","03","04","1","2","3","4"];
    final def serverIP = ["201.157.125.53","201.157.125.54"];
    DealRule rule;
    public RateRule(DealRule rule){
        this.rule = rule;
    }
    /**
     *
     * @return
     */
    public List<String>  contentLine(){
//        <Stream rule="201.157.125.53,01000000000000000000000000000012,01" aliaName="unicable_1" />
        List<String> list = new ArrayList<>();
        serverIP.each {it->
            rate.each {rateIt->
                String[] rules = rule.findRule();
                if(rules.length != 0){
                    println(it)
                    String last= it.split("\\.")[3]
                    list.add("<Stream rule=\"${it},${rules[0].trim()},${rateIt}\" aliaName=\"${rules[1].trim()}_${rateIt}_${last}\" />")
                }
            }
        }
        return list;
    }

}
class XMLDeal{
    private List<String> contents;
    public XMLDeal(List<String> contents){
        this.contents = contents;
    }

    public void saveFile(){
        StringBuffer sw = new StringBuffer();
        sw.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Streams>")
        contents.each {it->
            sw.append(it.toString());
        }
        sw.append("</Streams>")
        def f = new File("F:\\工作\\软件\\ipview\\规则文件\\myRule.xml")
        f.write(sw.toString())
    }
}
def file = new File("F:\\工作\\软件\\ipview\\规则文件\\OTT-CHANNEL-INDEX2.txt");
List<String> list = new ArrayList<>();
file.eachLine {it->
    if(!it.trim().isEmpty()){
        def rule = new DealRule(it);
        RateRule rr = new RateRule(rule);
        list.addAll(rr.contentLine());
    }
}
println list.size()
//new XMLDeal(list).saveFile()
