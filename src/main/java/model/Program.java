package model;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michal on 2.10.2016.
 */
public class Program {

    List<PcapIf> alldevs;

    public Program() {
        alldevs = new ArrayList<PcapIf>();
    }

    public List<PcapIf> getAlldevs() {
        return alldevs;
    }

    public void getDevices() {
        StringBuilder errbuf = new StringBuilder(); // For any error msgs
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.ERROR || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return;
        }
    }
}
