package model;

import gui.GuiFilterTabModel;
import org.jnetpcap.packet.PcapPacket;

import java.util.ArrayList;

/**
 * Created by root on 10.11.2016.
 */
public class FrameFilter {
    private GuiFilterTabModel guiFilterTabModel;
    private ArrayList<FilterEntry> arrayList = new ArrayList<>();

    public FrameFilter(GuiFilterTabModel guiFilterTabModel) {
        guiFilterTabModel.setModel(this);
        this.guiFilterTabModel = guiFilterTabModel;
    }

    public void addData(FilterEntry entry){
        this.arrayList.add(entry);
    }

    public void removeData(int pos){
        this.arrayList.remove(pos);
    }

    public void updateData(int pos,FilterEntry entry){
        this.arrayList.set(pos, entry);
    }

    public boolean filterPacket(PcapPacket packet,boolean incoming){
        return FrameAnalyzer.isFiltered(packet,incoming,arrayList.toArray(new FilterEntry[arrayList.size()]));
    }

}
