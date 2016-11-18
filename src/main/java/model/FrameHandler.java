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
    StatisticsGroup statisticsGroup;
    FrameFilter filter;

    public FrameHandler(int port, Program model, MacTable macTable, GuiFilterTabModel guiFilterTabModel, StatisticsGroup statistics) {
        this.port = port;
        this.model = model;
        this.counter = 0;
        this.macTable = macTable;
        this.statisticsGroup = statistics;
        this.filter = new FrameFilter(guiFilterTabModel);
    }

    public FrameFilter getFilter() {
        return filter;
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
        //System.out.println(pcapPacket.toHexdump());
        boolean outTest = false;
        System.out.println("Runing filters in "+port);
        boolean inTest = filter.filterPacket(pcapPacket,true);
        if (inTest){
            System.out.println("Filtering "+port);
            //not sending
            return;
        }
        if (destinationPort==port){
            System.out.println("Same PORT on port "+port);
        }
        else {
            if (destinationPort!=-1){
                outTest = model.sendFrame(destinationPort,pcapPacket);
            }
            else {
                System.out.println("Broadcasting");
                if(port == 0){
                    outTest = model.sendFrame(1,pcapPacket);
                }
                else {
                    outTest = model.sendFrame(0,pcapPacket);
                }
            }
        }
        statisticsGroup.analyze(pcapPacket,outTest);
    }

    public void clearStatistics(){
        statisticsGroup.getSimpleList().clear();
        statisticsGroup.clear();
    }

}
