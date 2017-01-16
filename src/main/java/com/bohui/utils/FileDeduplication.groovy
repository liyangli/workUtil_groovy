/**
 * 文件内容去重
 * User: liyangli
 * Date: ${date}* Time: ${hour}:${minute}*/

File file = new File("E:\\demo\\guzhou")
File[] files = null;
if(file.isDirectory()){
    files =  file.listFiles();
}
Set<Integer> set = new HashSet<>();
int i = 0;
files.each {it->
    i++;
    println(i)
    it.eachLine {line->
        def index =line.indexOf("tsid=")
        if(index != -1)
            set.add(line.substring(index+5))
    }
}

println(set)