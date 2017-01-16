package com.bohui

/**
 * 读取文件并且字符串进行替换
 * User: liyangli
 * Date: 2014/7/25
 * Time: 14:12
 */
class FileTextReplace {

    static main(args){
        def file = new File("C:\\Users\\bohui\\Desktop\\sip_test.sql");
        def writeFile = new File("C:\\Users\\bohui\\Desktop\\sip_test1.sql");

        file.text.eachLine {
           def start = it.indexOf("to_date('");
            def line = it;
            if(start != -1){
                def end = it.indexOf("')",start);
                line = it.substring(0,start)+' sysdate '+it.substring(end+2);
            }
            writeFile.append(line+"\r\n");
            println line
        }

    }
}
