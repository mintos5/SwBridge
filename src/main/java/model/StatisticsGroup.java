package model;

import org.jnetpcap.packet.PcapPacket;

import java.util.ArrayList;

/**
 * Created by root on 10.11.2016.
 */
public class StatisticsGroup {
    //long forwarded = 0;
    long frames = 0;
    long arp = 0;
    long ipv4 = 0;
    long tcp = 0;
    long udp = 0;
    long icmp = 0;
    long outFrames = 0;
    long outArp = 0;
    long outIpv4 = 0;
    long outTcp = 0;
    long outUdp = 0;
    long outIcmp = 0;
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

    public long getIcmp() {
        return icmp;
    }

    public long getOutFrames() {
        return outFrames;
    }

    public long getOutArp() {
        return outArp;
    }

    public long getOutIpv4() {
        return outIpv4;
    }

    public long getOutTcp() {
        return outTcp;
    }

    public long getOutUdp() {
        return outUdp;
    }

    public long getOutIcmp() {
        return outIcmp;
    }

    public SimpleList<String> getSimpleList() {
        return simpleList;
    }

    public void addListener(StatisticsFunc function){
        listeners.add(function);
    }

    public void analyze(PcapPacket packet,boolean forwarded){
        if (forwarded){
            this.frames++;
            this.outFrames++;
            if (FrameAnalyzer.isArp(packet)){
                this.arp++;
                this.outArp++;
            }
            if (FrameAnalyzer.isIpv4(packet)){
                this.ipv4++;
                this.outIpv4++;
            }
            if (FrameAnalyzer.isTcp(packet)){
                this.tcp++;
                this.outTcp++;
            }
            if (FrameAnalyzer.isUdp(packet)){
                this.udp++;
                this.outUdp++;
            }
            if (FrameAnalyzer.isIcmp(packet)){
                this.icmp++;
                this.outIcmp++;
            }
            simpleList.add(packet.toHexdump());
            for (StatisticsFunc func : listeners){
                func.showOnGui(this);
            }
        }
        else {
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
            if (FrameAnalyzer.isIcmp(packet)){
                this.icmp++;
            }
            simpleList.add(packet.toHexdump());
            for (StatisticsFunc func : listeners){
                func.showOnGui(this);
            }
        }
    }

    public void clear(){
        this.frames = 0;
        this.arp = 0;
        this.ipv4 = 0;
        this.tcp = 0;
        this.udp = 0;
        this.icmp = 0;
        this.outFrames = 0;
        this.outArp = 0;
        this.outIpv4 = 0;
        this.outTcp = 0;
        this.outUdp = 0;
        this.outIcmp = 0;
    }


}
