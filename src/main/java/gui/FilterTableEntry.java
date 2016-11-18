package gui;

import model.FilterEntry;

import java.nio.ByteBuffer;

/**
 * Created by root on 12.11.2016.
 */
public class FilterTableEntry {
    private boolean incoming;
    private boolean allowRule;
    private String sourceMac;
    private String destMac;
    private String sourceIp;
    private String destIp;
    private int protocol;

    public FilterTableEntry() {
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public void setAllowRule(boolean allowRule) {
        this.allowRule = allowRule;
    }

    public void setSourceMac(String sourceMac) {
        this.sourceMac = sourceMac;
    }

    public void setDestMac(String destMac) {
        this.destMac = destMac;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public boolean isAllowRule() {
        return allowRule;
    }

    public String getSourceMac() {
        return sourceMac;
    }

    public String getDestMac() {
        return destMac;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getDestIp() {
        return destIp;
    }

    public int getProtocol() {
        return protocol;
    }

    public FilterEntry getFilterEntry(){
        //string.contains();
        //string.replaceAll(":","");
        FilterEntry out = new FilterEntry();
        //simple copy of data
        out.setIncoming(incoming);
        out.setAllow(allowRule);
        out.setSrcMac(sourceMac.toUpperCase());
        out.setDestMac(destMac.toUpperCase());
        out.setProtocol(protocol);
        //String of ipv4 to INT
        ByteBuffer srcIpBuffer = ByteBuffer.allocate(4);
        ByteBuffer dstIpBuffer = ByteBuffer.allocate(4);
        long tempSrcIp = 0;
        long tempDstIp = 0;
        String[] partSrcIp = sourceIp.split("[.]");
        String[] partDstIp = destIp.split("[.]");
        for (int i=0;i<partSrcIp.length;i++){
            //tempSrcIp += Integer.parseInt(partSrcIp[i]);
            srcIpBuffer.put(i,(byte)Integer.parseInt(partSrcIp[i]));
        }
        for (int i=0;i<partDstIp.length;i++){
            //tempDstIp += Integer.parseInt(partDstIp[i]);
            dstIpBuffer.put(i,(byte)Integer.parseInt(partDstIp[i]));
        }
        out.setSrcIp(srcIpBuffer.getInt());
        out.setDestIp(dstIpBuffer.getInt());
        return out;
    }
}
