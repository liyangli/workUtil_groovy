package com.bohui.schedule;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * User: liyangli
 * Date: 2014/11/10
 * Time: 12:36
 */
public class OutOfTimer {

    static class ThrowTask extends TimerTask{
        @Override
        public void run() {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) throws Exception{
        Timer timer = new Timer();
        timer.schedule(new ThrowTask(),0);
        System.out.println("##################"+1);
        TimeUnit.SECONDS.sleep(1);
//        timer.schedule(new ThrowTask(),1);
        System.out.println("##################"+2);
        TimeUnit.SECONDS.sleep(5);
        System.out.println("##################"+3);
    }
}
