package model;

/**
 * Created by root on 10.11.2016.
 */
public class FilterEntry {
    boolean allow;
    boolean incoming;
    String srcMac;
    String destMac;
    int srcIp;// = new byte[4];
    int destIp;// = new byte[4];
    int protocol;
    int srcProtocol;
    int destProtocol;
    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public void setDestMac(String destMac) {
        this.destMac = destMac;
    }

    public void setSrcIp(int srcIp) {
        this.srcIp = srcIp;
    }

    public void setDestIp(int destIp) {
        this.destIp = destIp;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public void setSrcProtocol(int srcProtocol) {
        this.srcProtocol = srcProtocol;
    }

    public void setDestProtocol(int destProtocol) {
        this.destProtocol = destProtocol;
    }

    public boolean isAllow() {
        return allow;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public String getDestMac() {
        return destMac;
    }

    public int getSrcIp() {
        return srcIp;
    }

    public int getDestIp() {
        return destIp;
    }

    public int getProtocol() {
        return protocol;
    }

    public int getSrcProtocol() {
        return srcProtocol;
    }

    public int getDestProtocol() {
        return destProtocol;
    }
}
