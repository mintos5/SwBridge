package model;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;

/**
 * Created by michal on 3.10.2016.
 */
public class FrameHandler {

    public static void nextPacket(PcapPacket pcapPacket, SimpleList<String> s) {
        System.out.print("found one>");
        Ethernet eth0 = new Ethernet();
        if (pcapPacket.hasHeader(eth0)) {
            s.add(FormatUtils.mac(eth0.destination()));
        }
    }
}
