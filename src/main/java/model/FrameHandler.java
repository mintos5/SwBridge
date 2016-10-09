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

    public FrameHandler(Program model) {
        this.model = model;
        this.counter = 0;
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
        counter++;
        if (port == 0){
            System.out.println(pcapPacket.toHexdump());
            model.sendFrame(1,pcapPacket);
        }
        else {
            s.add(pcapPacket.toHexdump());
            model.sendFrame(0,pcapPacket);
        }
    }

}
