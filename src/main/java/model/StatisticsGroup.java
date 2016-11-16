package model;

import org.jnetpcap.packet.PcapPacket;

import java.util.ArrayList;

/**
 * Created by root on 10.11.2016.
 */
public class StatisticsGroup {
    long forwarded = 0;
    long frames = 0;
    long arp = 0;
    long ipv4 = 0;
    long tcp = 0;
    long udp = 0;
    //protocols, 80,
    long[] protocols = new long[100];
    private SimpleList<String> simpleList;
    private ArrayList<StatisticsFunc> listeners;

    public StatisticsGroup(SimpleList<String> simpleList,StatisticsFunc func) {
        this.listeners = new ArrayList<>();
        this.addListener(func);
        this.simpleList = simpleList;
    }

    public long getFrames() {
        return frames;
    }

    public long getArp() {
        return arp;
    }

    public long getIpv4() {
        return ipv4;
    }

    public long getTcp() {
        return tcp;
    }

    public long getUdp() {
        return udp;
    }

    public long[] getProtocols() {
        return protocols;
    }

    public long getForwarded() {
        return forwarded;
    }

    public void plusForwarded(){
        this.forwarded++;
    }



    public SimpleList<String> getSimpleList() {
        return simpleList;
    }

    public void addListener(StatisticsFunc function){
        listeners.add(function);
    }

    public void analyze(PcapPacket packet){
        this.frames++;
        if (FrameAnalyzer.isArp(packet)){
            this.arp++;
        }
        if (FrameAnalyzer.isIpv4(packet)){
            this.ipv4++;
        }
        if (FrameAnalyzer.isTcp(packet)){
            this.tcp++;
        }
        if (FrameAnalyzer.isUdp(packet)){
            this.udp++;
        }
        simpleList.add(packet.toHexdump());
        for (StatisticsFunc func : listeners){
            func.showOnGui(this);
        }
    }



}
