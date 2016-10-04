package model;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;

/**
 * Created by michal on 3.10.2016.
 */
public class FrameHandler implements PcapPacketHandler<SimpleList<String>>{

    boolean winVersion = false;
    int port;
    Program model;

    public FrameHandler(Program model) {
        this.model = model;
    }

    public boolean isWinVersion() {
        return winVersion;
    }

    public void setWinVersion(boolean winVersion) {
        this.winVersion = winVersion;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void nextPacket(PcapPacket pcapPacket, SimpleList<String> s) {
        if (port == 0){
            System.out.println(pcapPacket.toHexdump());
        }
        else {
            s.add(pcapPacket.toHexdump());
        }
        //DANGEROUS
        //model.sendFrame(0,pcapPacket);
        //model.sendFrame(1,pcapPacket);
        //pcapPacket.send
//        Ethernet eth0 = new Ethernet();
//        if (pcapPacket.hasHeader(eth0)) {
//            s.add(FormatUtils.mac(eth0.destination()));
//        }
    }

}
