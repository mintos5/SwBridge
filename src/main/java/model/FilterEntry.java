package model;

import java.util.Arrays;

/**
 * Created by root on 10.11.2016.
 */
public class FilterEntry {
    boolean allow;
    boolean incoming;
    String srcMac;
    String destMac;
    byte[] srcIp;// = new byte[4];
    byte[] destIp;// = new byte[4];
    int protocol;


    public void test(){
        //Arrays.equals()
    }
}
