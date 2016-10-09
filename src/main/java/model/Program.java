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
    Pcap pcap0, pcap1;
    FrameHandler handler0,handler1;
    SimpleList<String> list;
    public static final int snaplen = 64 * 1024;
    public static final int flags = Pcap.MODE_PROMISCUOUS;
    public static final int timeout = 1;

    private final Object pcapLock0 = new Object();
    private final Object pcapLock1 = new Object();
    private final Object test = new Object();
    private volatile Boolean loop0 = true;
    private volatile Boolean loop1 = true;
    private volatile Boolean senderFirst0 = false;
    private volatile Boolean senderFirst1 = false;

    public Boolean getLoop(int port) {
        if (port == 0 ){
            return loop0;
        }
        else {
            return loop1;
        }
    }

    public void setLoop(int port,Boolean loop) {
        if (port == 0) {
            this.loop0 = loop;
        }
        else {
            this.loop1 = loop;
        }
    }

    public Boolean getSenderFirst(int port) {
        if (port == 0) {
            return senderFirst0;
        }
        else {
            return senderFirst1;
        }
    }

    public void setSenderFirst(int port,Boolean senderFirst) {
        if (port == 0) {
            this.senderFirst0 = senderFirst;
        }
        else {
            this.senderFirst1 = senderFirst;
        }
    }

    public FrameHandler getHandler(int port) {
        if (port == 0){
            return handler0;
        }
        else {
            return handler1;
        }
    }

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
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.ERROR || alldevs.isEmpty()) {
            throw new BridgeException("Cant find devices");
        }
    }

    public void sendFrame(int port,PcapPacket packet){
        //System.out.println("Sending button");

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
        setSenderFirst(port,true);
        synchronized (pcapLock) {
            System.out.println("Sending from port "+port);
            if (pcap.sendPacket(packet) != Pcap.OK) {
                System.err.println(pcap.getErr());
            }
            //System.out.println("Sending2");
            setSenderFirst(port,false);
            pcapLock.notify();
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

        PcapIf device = alldevs.get(num);
        Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
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
        //reseting Loops
        setLoop(0,true);
        setLoop(1,true);
        while(getLoop(port)) {
            int result = 0;
            synchronized (pcapLock) {
                //System.out.println("Citanie "+port);
                if (getSenderFirst(port)){
                    try {
                        pcapLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                result = pcap.nextEx(packet1);
            }
            if(result == Pcap.NEXT_EX_OK) {
                PcapPacket pcapPacketCopy = new PcapPacket(packet1);
                handler.setPort(port);
                handler.nextPacket(packet1,list);
            }
            else {
                continue;
            }
        }
    }

    public void closeInterface(){
        setLoop(0,false);
        setLoop(1,false);
        System.out.println("SHUTDOWN");
    }


}
