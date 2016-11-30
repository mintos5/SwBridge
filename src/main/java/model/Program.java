package model;

import gui.GuiFilterTabModel;
import gui.GuiMacTableModel;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michal on 2.10.2016.
 * Main program...
 */
public class Program {

    private List<PcapIf> alldevs;
    private StringBuilder errbuf;
    private Pcap pcap0, pcap1;
    private FrameHandler handler0,handler1;
    private MacTable table;
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
//        if (loop==false){
//            table.disableTimer();
//        }
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

    public Program(GuiMacTableModel guiMacTableModel, GuiFilterTabModel[] filtersTabGui,
                   StatisticsGroup[] statistics) throws BridgeException {
        alldevs = new ArrayList<PcapIf>();
        errbuf = new StringBuilder(); // For any error msgs
        table= new MacTable(guiMacTableModel);
        handler0 = new FrameHandler(0,this,table,filtersTabGui[0],statistics[0]);
        handler1 = new FrameHandler(1,this,table,filtersTabGui[1],statistics[1]);
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

    public boolean sendFrame(int port,PcapPacket packet){
        //System.out.println("Sending button");

        Pcap pcap;
        Object pcapLock;
        FrameHandler handler;
        if (port == 0) {
            pcap = pcap0;
            pcapLock = pcapLock0;
            handler = handler0;
        }
        else {
            pcap = pcap1;
            pcapLock = pcapLock1;
            handler = handler1;
        }
        boolean test = handler.getFilter().filterPacket(packet,false);
        if (test){
            System.out.println("Blocking outgoing");
            return false;
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
        return true;
    }

    public void openIterfaceThread(int num, final int port) throws BridgeException{
        final int numFinal = num;
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    openInterface(numFinal,port);
                } catch (BridgeException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void openInterface(int num,int port) throws BridgeException {
        PcapPacket packet1 = new PcapPacket(JMemory.Type.POINTER);

        PcapIf device = alldevs.get(num);
        Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
        //pcap.setDirection(Pcap.Direction.INOUT);
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
                handler.nextPacket(packet1);
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

    public void resetMacTable() {
        this.table.reset();
    }

    public void addFilter(){

    }
    public void setMacTableTimer(int num){
        table.setDefaulTTL(num);
    }


}
