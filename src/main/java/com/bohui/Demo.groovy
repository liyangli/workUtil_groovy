import java.io.File;
//println "%%%%%%%%%%%%%"
File file = new File("e:/ta.txt");
PrintWriter out = file.newPrintWriter();
out.println("hello");     1
out.println("hi");
out.println("liyangli")
out.flush();
out.close();

file.eachLine {it->
    println it;
}
  println ("###################################################")
file.withWriterAppend {
    it->
        it.println("lllll");
}
file.eachLine {it->
    println it;
}
/*Set<String> ss = new HashSet<>();
file.eachLine {it->
    if(!it.isEmpty()){
        String[] nns = it.split("  ");
        if(!nns[1].trim().isEmpty()){
            ss.add(nns[1]);
        }
    }
}
List<String> list  = ss.sort {a,b->
    if(a.isEmpty()|| a.split(":").length != 2){
        return -1;
    }
    if(b.isEmpty() || b.split(":").length != 2){
        return -1;
    }
    return  a.split(":")[1].toInteger().compareTo(b.split(":")[1].toInteger())
}
list.each {it->
    println(it)
}*/
