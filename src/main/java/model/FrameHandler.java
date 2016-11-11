package model;

import gui.GuiFilterTabModel;
import org.jnetpcap.packet.PcapPacket;

/**
 * Created by michal on 3.10.2016.
 */
public class FrameHandler{

    int port;
    Program model;
    long counter;
    MacTable macTable;
    SimpleList<String> list;
    StatisticsGroup statisticsGroup;
    FrameFilter filter;

    public FrameHandler(int port, Program model, MacTable macTable, GuiFilterTabModel guiFilterTabModel, StatisticsGroup statistics) {
        this.port = port;
        this.model = model;
        this.counter = 0;
        this.macTable = macTable;
        this.statisticsGroup = statistics;
        this.list = statistics.getSimpleList();
        this.filter = new FrameFilter(guiFilterTabModel);
    }

    public long getCounter() {
        return counter;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void nextPacket(PcapPacket pcapPacket) {
        int destinationPort;
        counter++;
        destinationPort = macTable.findPort(pcapPacket,port);
//        if (destinationPort==port){
//            System.out.println("Same PORT");
//            list.add(pcapPacket.toHexdump());
//        }
//        if (destinationPort!=-1){
//            model.sendFrame(destinationPort,pcapPacket);
//            list.add(pcapPacket.toHexdump());
//        }
//        else {
//            System.out.println("Broadcasting");
//            if(port == 0){
//                model.sendFrame(1,pcapPacket);
//                list.add(pcapPacket.toHexdump());
//            }
//            else {
//                model.sendFrame(0,pcapPacket);
//                list.add(pcapPacket.toHexdump());
//            }
//        }
        list.add(pcapPacket.toHexdump());
        System.out.println(pcapPacket.toHexdump());
    }

    public void clearList(){
        this.list.clear();
    }

}
