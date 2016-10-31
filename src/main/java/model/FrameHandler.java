package model;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;

/**
 * Created by michal on 3.10.2016.
 */
public class FrameHandler{

    int port;
    Program model;
    long counter;
    MacTable macTable;
    SimpleList<String> list;

    public FrameHandler(int port,Program model,MacTable macTable,SimpleList<String> list) {
        this.port = port;
        this.model = model;
        this.counter = 0;
        this.macTable = macTable;
        this.list = list;
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
        if (destinationPort==port){
            System.out.println("Same PORT");
            list.add(pcapPacket.toHexdump());
        }
        if (destinationPort!=-1){
            model.sendFrame(destinationPort,pcapPacket);
            list.add(pcapPacket.toHexdump());
        }
        else {
            System.out.println("Broadcasting");
            if(port == 0){
                model.sendFrame(1,pcapPacket);
                list.add(pcapPacket.toHexdump());
            }
            else {
                model.sendFrame(0,pcapPacket);
                list.add(pcapPacket.toHexdump());
            }
        }
        System.out.println(pcapPacket.toHexdump());
    }

}
