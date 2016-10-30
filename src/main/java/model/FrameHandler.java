package model;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;

/**
 * Created by michal on 3.10.2016.
 */
public class FrameHandler implements PcapPacketHandler<SimpleList<String>>{

    int port;
    Program model;
    long counter;
    MacTable macTable;

    public FrameHandler(Program model,MacTable macTable) {
        this.model = model;
        this.counter = 0;
        this.macTable = macTable;
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

    public void nextPacket(PcapPacket pcapPacket, SimpleList<String> s) {
        int destinationPort;
        counter++;
        if (port == 0){
            System.out.println(pcapPacket.toHexdump());
            destinationPort = macTable.findPort(pcapPacket,port);
            if (destinationPort!=-1){
                //model.sendFrame(destinationPort,pcapPacket);
            }
        }
        else {
            s.add(pcapPacket.toHexdump());
            macTable.findPort(pcapPacket,port);
            //model.sendFrame(0,pcapPacket);
        }
    }

}
