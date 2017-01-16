package com.bohui

/**
 *
 * User: liyangli
 * Date: 2014/7/23
 * Time: 18:26
 */
class XMLParse {

    String readXml(){
        //读取xml文件
       String file = "E:\\actionRule.xml";
        def rows = new XmlSlurper().parse(file);
        PrintWriter pw = new PrintWriter("E:\\actionRule1.xml");
        def xml = new groovy.xml.MarkupBuilder(pw)
        xml.Types{
            for(row in rows.ROW){
                Type(ID:"${row.ID}",Name:"${row.ACTIONNAME}")
            }
        }
        return ""
    }

    static void main(args) {
       XMLParse xmlParse = new XMLParse();
       String content = xmlParse.readXml();
        println content
    }
}
