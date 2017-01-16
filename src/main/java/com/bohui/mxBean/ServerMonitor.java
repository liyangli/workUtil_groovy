package com.bohui.mxBean;

import com.bohui.ServerImpl;

/**
 * User: liyangli
 * Date: 2014/11/7
 * Time: 14:27
 */
public class ServerMonitor implements ServerMonitorMBean {
    private final ServerImpl target;
    public ServerMonitor(ServerImpl target){
        this.target = target;
    }
    public long getUpTime(){
        return System.currentTimeMillis() - target.startTime;
    }
}
