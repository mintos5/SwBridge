package model;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

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
    Pcap inteface1;
    FrameHandler handler1;
    SimpleList<String> list;

    private final Object pcapLock = new Object();

    int snaplen = 64 * 1024;           // Capture all packets, no trucation
    int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
    int timeout = 5;           // 10 seconds in millis

    public Program() {
        alldevs = new ArrayList<PcapIf>();
        errbuf = new StringBuilder(); // For any error msgs
        handler1 = new FrameHandler();
        list = new SimpleList<String>();
    }

    public List<PcapIf> getAlldevs() {
        return alldevs;
    }

    public void getDevices() {

        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.ERROR || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return;
        }
    }

    public void sendFrame(int num){
        System.out.println("Sending");
        synchronized (pcapLock) {
            byte[] a = new byte[14];
            Arrays.fill(a, (byte) 0xee);
            ByteBuffer b = ByteBuffer.wrap(a);
            if (inteface1.sendPacket(b) != Pcap.OK) {
                System.err.println(inteface1.getErr());
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
        PcapIf device = alldevs.get(num);
        inteface1 = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
        if (inteface1 == null) {
            System.err.printf("Error while opening device for capture: "
                    + errbuf.toString());
            throw new BridgeException("smola");
        }
        list.addListener(function);
        PcapPacket packet1 = new PcapPacket(JMemory.Type.POINTER);
        while(true) {
            synchronized (pcapLock) {
                if(inteface1.nextEx(packet1) == Pcap.NEXT_EX_OK) {
                    PcapPacket pcapPacketCopy = new PcapPacket(packet1);
                    FrameHandler.nextPacket(packet1,list);
                }
            }
        }
        //inteface1.loop(Pcap.LOOP_INFINITE,handler1,list);
    }


}
