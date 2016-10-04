package model;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.winpcap.WinPcap;
import org.jnetpcap.winpcap.WinPcapRmtAuth;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by michal on 2.10.2016.
 */
public class Program {

    List<PcapIf> alldevs;
    StringBuilder errbuf;
    WinPcap winPcap0, winPcap1;
    Pcap pcap0, pcap1;
    FrameHandler handler0,handler1;
    SimpleList<String> list;

    private final Object pcapLock0 = new Object();
    private final Object pcapLock1 = new Object();

    public static final int snaplen = 64 * 1024;           // Capture all packets, no trucation
    public static final int flags = Pcap.MODE_PROMISCUOUS;
    public static final int flagsWin = WinPcap.OPENFLAG_NOCAPTURE_LOCAL;
    public static final int timeout = 5;           // 5 millis timeout
    public static final WinPcapRmtAuth auth = null;

    public Program() throws BridgeException {
        alldevs = new ArrayList<PcapIf>();
        errbuf = new StringBuilder(); // For any error msgs
        handler0 = new FrameHandler();
        list = new SimpleList<String>();
        this.getDevices();
    }

    public List<PcapIf> getAlldevs() {
        return alldevs;
    }

    public void getDevices() throws BridgeException {
        if (WinPcap.isSupported() == true) {
            String source = "rpcap://";
            alldevs = new ArrayList<PcapIf>();
            int r = WinPcap.findAllDevsEx(source, null, alldevs, errbuf);
            if (r != Pcap.OK) {
                throw new BridgeException("Cant find devices");
            }
        }
        else {
            int r = Pcap.findAllDevs(alldevs, errbuf);
            if (r == Pcap.ERROR || alldevs.isEmpty()) {
                throw new BridgeException("Cant find devices");
            }
        }
    }

    public void sendFrame(int num){

        System.out.println("Sending button");
        if (WinPcap.isSupported() == true) {
//            PcapIf device = alldevs.get(num);
//            WinPcap pcap0 = WinPcap.open(device.getName(), snaplen, flagsWin, timeout, auth, errbuf);
//            if (pcap0 == null) {
//                System.err.println(errbuf.toString());
//                return;
//            }
            synchronized (pcapLock0) {
                System.out.println("Sending");
                byte[] a = new byte[14];
                Arrays.fill(a, (byte) 0xee);
                ByteBuffer b = ByteBuffer.wrap(a);
                if (winPcap0.sendPacket(b) != Pcap.OK) {
                    System.err.println(winPcap0.getErr());
                }
                System.out.println("Sending2");
            }
        }
        else {
            synchronized (pcapLock0) {
                System.out.println("Sending");
                byte[] a = new byte[14];
                Arrays.fill(a, (byte) 0xee);
                ByteBuffer b = ByteBuffer.wrap(a);
                if (pcap0.sendPacket(b) != Pcap.OK) {
                    System.err.println(winPcap0.getErr());
                }
                System.out.println("Sending2");
            }
        }
    }

    public void openItergaceThread(int num, final SimpleListFunction function) throws BridgeException{
        final int numFinal = num;
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    openInterface(numFinal,function);
                } catch (BridgeException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void openInterface(int num,SimpleListFunction function) throws BridgeException {
        list.addListener(function);
        PcapPacket packet1 = new PcapPacket(JMemory.Type.POINTER);

        if (WinPcap.isSupported() == true) {
            PcapIf device = alldevs.get(num);
            winPcap0 = WinPcap.open(device.getName(), snaplen, flagsWin, timeout, auth, errbuf);
            if (winPcap0 == null) {
                throw new BridgeException("smola");
            }
            while(true) {
                synchronized (pcapLock0) {
                    if(winPcap0.nextEx(packet1) == Pcap.NEXT_EX_OK) {
                        PcapPacket pcapPacketCopy = new PcapPacket(packet1);
                        handler0.nextPacket(packet1,list);
                    }
                }
            }

        }//end of windows function
        else {
            PcapIf device = alldevs.get(num);
            pcap0 = Pcap.openLive(device.getName(), snaplen, flagsWin, timeout, errbuf);
            pcap0.setDirection(Pcap.Direction.IN);
            if (pcap0 == null) {
                throw new BridgeException("smola");
            }
            while(true) {
                synchronized (pcapLock0) {
                    if(pcap0.nextEx(packet1) == Pcap.NEXT_EX_OK) {
                        PcapPacket pcapPacketCopy = new PcapPacket(packet1);
                        handler0.nextPacket(packet1,list);
                    }
                }
            }
        }//end of unix/linux function
    }


}
