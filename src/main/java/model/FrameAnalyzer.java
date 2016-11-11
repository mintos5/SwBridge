package model;

import java.util.Arrays;

/**
 * Created by root on 10.11.2016.
 */
public class FrameAnalyzer {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static boolean isDestMac(byte[] payload,String compare){
        String address = bytesToHex(Arrays.copyOfRange(payload,0,5));
        if (address.equals(compare)){
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isSourceMac(byte[] payload,String compare){
        String address = bytesToHex(Arrays.copyOfRange(payload,6,11));
        if (address.equals(compare)){
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isArp(byte[] payload){
        return false;
    }
    public static boolean isIpv4(byte[] payload){
        //TODO Otestuj to na tomto mieste
        byte version = payload[14];
        version = (byte)(version & 0xff);
        if (version==(byte)0x40){
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isIpv6(byte[] payload){
        return false;
    }
    public static boolean isTcp(byte[] payload){
        return false;
    }
    public static boolean isUdp(byte[] payload){
        return false;
    }
    public static boolean isPort(byte[] payload,int portNumber){
        return false;
    }
}
