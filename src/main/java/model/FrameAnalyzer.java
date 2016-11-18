package model;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

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


    public static boolean isDestMac(PcapPacket packet,String compare){
        String address = bytesToHex(packet.getByteArray(0,6));
        if (address.equals(compare)){
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isSourceMac(PcapPacket packet,String compare){
        String address = bytesToHex(packet.getByteArray(6,6));
        if (address.equals(compare)){
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isArp(PcapPacket packet){
        Arp arp = new Arp();
        if (packet.hasHeader(arp)){
            return true;
        }
        return false;
    }
    public static boolean isIpv4(PcapPacket packet){
        Ip4 ip = new Ip4();
        if (packet.hasHeader(ip)){
            //System.out.print("ISIP");
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isTcp(PcapPacket packet){
        Tcp tcp = new Tcp();
        if (packet.hasHeader(tcp)){
            return true;
        }
        else {
            //System.out.print("NTCP");
            return false;
        }
    }

    public static boolean isUdp(PcapPacket packet){
        Udp udp = new Udp();
        if (packet.hasHeader(udp)){
            //System.out.println("is UDP");
            return true;
        }
        else {
            //System.out.println("NUDP");
            return false;
        }
    }

    public static boolean isIcmp(PcapPacket packet){
        Icmp icmp = new Icmp();
        if (packet.hasHeader(icmp)){
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isFiltered(PcapPacket packet,boolean incoming,FilterEntry[] compare){
        //IT ALWAYS returns true if filters found something
        FilterEntry packetInfo = new FilterEntry();
        Ip4 ip = new Ip4();
        Tcp tcp = new Tcp();
        Udp udp = new Udp();
        //checking if filters exist
        if (compare.length==0){
            System.out.println("No filters");
            return false;
        }
        //getting MAC addresses
        packetInfo.setDestMac(bytesToHex(packet.getByteArray(0,6)));
        packetInfo.setSrcMac(bytesToHex(packet.getByteArray(6,6)));
        //getting IP addresses
        if (packet.hasHeader(ip)){
            packetInfo.setSrcIp(ip.sourceToInt());
            packetInfo.setDestIp(ip.destinationToInt());
        }
        else {
            System.out.println("Not Ip packet");
            return false;
        }
        if (packet.hasHeader(tcp)){
            packetInfo.setSrcProtocol(tcp.source());
            packetInfo.setDestProtocol(tcp.destination());
        }
        if (packet.hasHeader(udp)){
            packetInfo.setSrcProtocol(udp.source());
            packetInfo.setDestProtocol(udp.destination());
        }
        boolean result = false;
        boolean slowTest = true;
        for (int i=0;i<compare.length;i++){
            //DON`T TEST OUTGOING/INCOMING filters
            if (compare[i].isIncoming()!=incoming){
                continue;
            }
            boolean test = false;
            //destination MAC Test
            if (slowTest && compare[i].getDestMac().equals("ALL")){
                test = true;
            }
            else {
                if (slowTest && compare[i].getDestMac().equals(packetInfo.getDestMac())){
                    test = true;
                }
                else {
                    test = false;
                    slowTest = false;
                }
            }
            //source MAC Test
            if (slowTest && compare[i].getSrcMac().equals("ALL")){
                test = true;
            }
            else {
                if (slowTest && compare[i].getSrcMac().equals(packetInfo.getSrcMac())){
                    test = true;
                }
                else {
                    test = false;
                    slowTest = false;
                }
            }
            //destination IP Test
            if (slowTest && compare[i].getDestIp() == 0){
                test = true;
            }
            else {
                if (slowTest && compare[i].getDestIp() == packetInfo.getDestIp()){
                    test = true;
                    System.out.println("Similiar dest IP");
                }
                else {
                    test = false;
                    slowTest = false;
                }
            }
            //source IP Test
            if (slowTest && slowTest && compare[i].getSrcIp() == 0){
                test = true;
            }
            else {
                if (slowTest && compare[i].getSrcIp() == packetInfo.getSrcIp()){
                    test = true;
                    System.out.println("Similiar source IP");
                }
                else {
                    test = false;
                    slowTest = false;
                }
            }
            int protocol;
            if (incoming){
                protocol = packetInfo.getDestProtocol();
            }
            else {
                protocol = packetInfo.getSrcProtocol();
            }
            //PROTOCOL Testing
            if (slowTest && compare[i].getProtocol() == 0){
                test = true;
            }
            else {
                if (slowTest && compare[i].getProtocol() == protocol){
                    test = true;
                }
                else {
                    test = false;
                    slowTest = false;
                }
            }
            if (compare[i].allow){
                if (test == true){
                    result = false;
                }
            }
            else {
                //DISABLE RULE
                if (result == true){
                    result = true;
                }
                else {
                    result = test;
                }
            }
            System.out.println(i+". test> "+result);
        }
        return result;
    }
}
