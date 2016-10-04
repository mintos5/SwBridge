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
    private Boolean loop0 = true;
    private Boolean loop1 = true;

    public synchronized Boolean getLoop(int port) {
        if (port == 0 ){
            return loop0;
        }
        else {
            return loop1;
        }
    }

    public synchronized void setLoop(int port,Boolean loop) {
        if (port == 0) {
            this.loop0 = loop;
        }
        else {
            this.loop1 = loop;
        }
    }

    public static final int snaplen = 64 * 1024;           // Capture all packets, no trucation
    public static final int flags = Pcap.MODE_PROMISCUOUS;
    public static final int flagsWin = WinPcap.OPENFLAG_NOCAPTURE_LOCAL;
    public static final int timeout = 5;           // 5 millis timeout
    public static final WinPcapRmtAuth auth = null;

    public Program() throws BridgeException {
        alldevs = new ArrayList<PcapIf>();
        errbuf = new StringBuilder(); // For any error msgs
        handler0 = new FrameHandler(this);
        handler1 = new FrameHandler(this);
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

    public void sendFrame(int port,PcapPacket packet){
        System.out.println("Sending button");
        if (WinPcap.isSupported() == true) {
            WinPcap winPcap;
            Object pcapLock;
            if (port == 0) {
                winPcap = winPcap0;
                pcapLock = pcapLock0;
            }
            else {
                winPcap = winPcap1;
                pcapLock = pcapLock1;
            }
//            PcapIf device = alldevs.get(num);
//            WinPcap pcap0 = WinPcap.open(device.getName(), snaplen, flagsWin, timeout, auth, errbuf);
//            if (pcap0 == null) {
//                System.err.println(errbuf.toString());
//                return;
//            }
            synchronized (pcapLock) {
                System.out.println("Sending");
                if (winPcap.sendPacket(packet) != Pcap.OK) {
                    System.err.println(winPcap.getErr());
                }
                System.out.println("Sending2");
            }
        }
        else {
            Pcap pcap;
            Object pcapLock;
            if (port == 0) {
                pcap = pcap0;
                pcapLock = pcapLock0;
            }
            else {
                pcap = pcap1;
                pcapLock = pcapLock1;
            }
            synchronized (pcapLock) {
                System.out.println("Sending");
                if (pcap.sendPacket(packet) != Pcap.OK) {
                    System.err.println(pcap.getErr());
                }
                System.out.println("Sending2");
            }
        }
    }

    public void openIterfaceThread(int num, final int port, final SimpleListFunction function) throws BridgeException{
        final int numFinal = num;
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    openInterface(numFinal,port,function);
                } catch (BridgeException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void openInterface(int num,int port,SimpleListFunction function) throws BridgeException {
        list.addListener(function);
        PcapPacket packet1 = new PcapPacket(JMemory.Type.POINTER);

        if (WinPcap.isSupported() == true) {
            PcapIf device = alldevs.get(num);
            WinPcap winPcap = WinPcap.open(device.getName(), snaplen, flagsWin, timeout, auth, errbuf);
            FrameHandler handler;
            final Object pcapLock;
            if (port == 0) {
                winPcap0 = winPcap;
                handler = handler0;
                pcapLock = pcapLock0;
            }
            else {
                winPcap1 = winPcap;
                handler = handler1;
                pcapLock = pcapLock1;
            }
            if (winPcap == null) {
                throw new BridgeException("smola");
            }
            while(getLoop(port)) {
                synchronized (pcapLock) {
                    if(winPcap.nextEx(packet1) == Pcap.NEXT_EX_OK) {
                        PcapPacket pcapPacketCopy = new PcapPacket(packet1);
                        handler.setWinVersion(true);
                        handler.setPort(port);
                    }
                    else {
                        continue;
                    }
                    handler.nextPacket(packet1,list);

                }
            }

        }//end of windows function
        else {
            PcapIf device = alldevs.get(num);
            Pcap pcap = Pcap.openLive(device.getName(), snaplen, flagsWin, timeout, errbuf);
            pcap.setDirection(Pcap.Direction.IN);
            FrameHandler handler;
            Object pcapLock;
            if (port == 0 ){
                pcap0 = pcap;
                handler = handler0;
                pcapLock = pcapLock0;
            }
            else {
                pcap1 = pcap;
                handler = handler1;
                pcapLock = pcapLock1;
            }
            if (pcap == null) {
                throw new BridgeException("smola");
            }
            while(getLoop(port)) {
                synchronized (pcapLock) {
                    if(pcap.nextEx(packet1) == Pcap.NEXT_EX_OK) {
                        PcapPacket pcapPacketCopy = new PcapPacket(packet1);
                        handler.setWinVersion(false);
                        handler.setPort(port);
                    }
                    else {
                        continue;
                    }
                    handler.nextPacket(packet1,list);
                }
            }
        }//end of unix/linux function
        //reseting Loops
        setLoop(0,true);
        setLoop(1,true);
    }

    public void closeInterface(){
        setLoop(0,false);
        setLoop(1,false);
    }


}
