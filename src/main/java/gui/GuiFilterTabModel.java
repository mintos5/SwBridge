package gui;

import model.FrameFilter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by root on 10.11.2016.
 */
public class GuiFilterTabModel extends AbstractTableModel {
    private String[] columnNames = { "Index", "Direction", "Allow rule", "Source Mac", "Destination Mac", "Source Ip",
            "Destination Ip","Protocol"};
    private ArrayList<FilterTableEntry> data = new ArrayList<>();
    private FrameFilter model;

    public void setModel(FrameFilter model) {
        this.model = model;
    }

    public void removeData(int row){
        if (row>=0){
            model.removeData(row);
            data.remove(row);
        }
        this.fireTableDataChanged();
    }

    public void addData(FilterTableEntry input){
        model.addData(input.getFilterEntry());
        data.add(input);
        //TODO este pridat osetrenia
        this.fireTableDataChanged();
    }

    public void updateData(int row,FilterTableEntry input){
        if (row>=0){
            model.updateData(row,input.getFilterEntry());
            data.set(row, input);
            //TODO este pridat osetrenia
        }
        this.fireTableDataChanged();
    }

    public FilterTableEntry getData(int row){
        return data.get(row);
    }



    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        String direction = "";
        if (data.get(i).isIncoming()){
            direction = "incoming";
        }
        else{
            direction = "outgoing";
        }
        switch (i1){
            case 0:     return i;
            case 1:     return direction;
            case 2:     return data.get(i).isAllowRule();
            case 3:     return data.get(i).getSourceMac();
            case 4:     return data.get(i).getDestMac();
            case 5:     return data.get(i).getSourceIp();
            case 6:     return data.get(i).getDestIp();
            case 7:     return data.get(i).getProtocol();
            default:    return null;
        }
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
}