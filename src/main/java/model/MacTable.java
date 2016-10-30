package model;


import gui.GuiMacTableModel;
import org.jnetpcap.packet.PcapPacket;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by root on 28.10.2016.
 */
public class MacTable extends TimerTask{
    private final ReentrantLock hashLock = new ReentrantLock(true);
    private LinkedHashMap<String,MacTableEntry> hashMap = new LinkedHashMap();
    private int defaulTTL = 2;
    private Timer updateTable;
    private GuiMacTableModel guiTableModel;


    public MacTable() {
        updateTable = new Timer();
        updateTable.schedule(this,0,1000);
    }

    public MacTable(GuiMacTableModel guiMacTableModel) {
        this.guiTableModel = guiMacTableModel;
        updateTable = new Timer();
        updateTable.schedule(this,0,1000);
    }

    public void disableTimer(){
        updateTable.cancel();
    }

    public void setDefaulTTL(int defaulTTL) {
        this.defaulTTL = defaulTTL;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void updateMap(String key,MacTableEntry value){
        hashMap.remove(key);
        hashMap.put(key,value);
    }

    private int getEntryPort(String key){
        int port = -1;
        MacTableEntry entry = hashMap.get(key);
        if (entry!=null) {
            port = hashMap.get(key).port;
        }
        return port;
    }

    public void run() {
        checkMap();
    }

    private void checkMap(){
        System.out.println("TIMER:");
        int port = -1;
        hashLock.lock();
        try {
            guiTableModel.clearData();
            //casove cekovanie
            Timestamp now = new Timestamp(System.currentTimeMillis());
            // Get an iterator
            Iterator<Map.Entry<String,MacTableEntry>> it = hashMap.entrySet().iterator();

            // Display elements
            while(it.hasNext()) {
                Map.Entry<String,MacTableEntry> entry = (Map.Entry)it.next();

                long diff = now.getTime() - entry.getValue().time.getTime();
                if (diff > entry.getValue().ttl*1000){
                    System.out.println("Too old for this");
                    it.remove();
                }
                else {
                    System.out.println("Whaaat");
                    //tu bude refresh pre grafiku
                    guiTableModel.addData(entry.getKey(),entry.getValue().port,diff);
                }
            }
            guiTableModel.refresh();
        }
        finally {
            hashLock.unlock();
        }
    }

    public int findPort(PcapPacket packet,int sourcePort){
        int port = -1;
        System.out.println("Thread port: "+sourcePort);
        //GET destination address
        byte[] destByte = packet.getByteArray(0,6);
        String dest = bytesToHex(destByte);

        //GET MacTableEntry key and values
        byte[] sourceByte = packet.getByteArray(6,6);
        String source = bytesToHex(sourceByte);
        MacTableEntry data = new MacTableEntry(new Timestamp(System.currentTimeMillis()),defaulTTL,sourcePort);
        hashLock.lock();
        try {
            updateMap(source,data);
            port = getEntryPort(dest);
        }
        finally {
            hashLock.unlock();
            return port;
        }
    }




    public static void main(String[] args) {
//        hashMap.put("janka",new MacTableEntry(null,1,1));
//        hashMap.put("jozko",new MacTableEntry(null,1,1));
//        hashMap.put("erika",new MacTableEntry(null,1,1));
//        hashMap.put("lubka",new MacTableEntry(null,1,1));
//
//        hashMap.get("erika");
//        updateField("janka",new MacTableEntry(null,1,1));
//        hashMap.get("jozko");
//
//        // Get a set of the entries
//        Set set = hashMap.entrySet();
//
//        // Get an iterator
//        Iterator i = set.iterator();
//
//        // Display elements
//        while(i.hasNext()) {
//            Map.Entry me = (Map.Entry)i.next();
//            System.out.print(me.getKey() + ": ");
//            System.out.println(me.getValue());
//        }
//
//        for (Map.Entry<String, MacTableEntry> entry : hashMap.entrySet()) {
//            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        }
    }
}

class MacTableEntry{
    Timestamp time;
    int ttl;
    int port;

    public MacTableEntry(Timestamp time, int ttl, int port) {
        this.time = time;
        this.ttl = ttl;
        this.port = port;
    }
}
