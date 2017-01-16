package com.bohui.utils

/**
 *   执行cmd中的ping命令，如果ping通自动弹出提示框
 * User: liyangli
 * Date: 2014/6/6
 * Time: 13:18
 */
String process = 'ping -t 172.17.13.108'

// Split the string into sections based on |
// And pipe the results together
//Process result = process.tokenize( '|' ).inject( null ) { p, c ->
//    if( p )
//        p | c.execute()
//    else
//        c.execute()
//}
Process result = process.execute();
// Print out the output and error streams
result.waitForProcessOutput( System.out, System.out )
