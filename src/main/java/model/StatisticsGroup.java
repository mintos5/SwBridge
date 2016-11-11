package model;

import java.util.ArrayList;

/**
 * Created by root on 10.11.2016.
 */
public class StatisticsGroup {
    double frames;
    double arp;
    double ipv4;
    double tcp;
    double udp;
    //protocols, 80,
    double[] protocols;
    private SimpleList<String> simpleList;
    private ArrayList<StatisticsFunc> listeners;

    public StatisticsGroup(SimpleList<String> simpleList) {
        this.simpleList = simpleList;
    }

    public SimpleList<String> getSimpleList() {
        return simpleList;
    }

    public void addListener(StatisticsFunc function){
        listeners.add(function);
    }

    public void analyze(byte[] payload){
        //FrameAnalyzer.destMac();
        for (StatisticsFunc func : listeners){
            func.showOnGui();
        }
    }



}
