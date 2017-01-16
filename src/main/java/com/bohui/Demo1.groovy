println "%%%%%%%%%%%%%"
def nn = "172.18.252.55\n" +
        "172.18.254.38\n" +
        "172.18.252.39\n" +
        "172.18.254.15\n" +
        "172.18.252.38\n" +
        "172.18.254.9\n" +
        "172.18.254.36\n" +
        "172.18.254.34\n" +
        "172.18.254.10\n" +
        "172.18.255.4\n" +
        "172.18.252.37\n" +
        "172.18.254.35\n" +
        "172.18.254.16\n" +
        "172.18.252.45\n" +
        "172.18.254.37";
//Set<String> ss = new HashSet<>();
nn.split("\n").each {it->
//    ss.add(it);
    //直接组织
    println "insert into t_server (TYPE, IP, NAME, REMARK, AREAID, MAXBAND, THRESHOLD, ALARMSWITCH)\n" +
            "values ( 4, '${it}', '${it}', '测试', 464, 5, 80, 1);";
}

//list.each {it->
//    println(it)
//}
