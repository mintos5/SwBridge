package gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by root on 30.10.2016.
 */
public class GuiMacTableModel extends AbstractTableModel {

    private String[] columnNames = { "Mac Address", "Port", "Time"};
    private ArrayList<TableEntry> data = new ArrayList<>();

    public void clearData(){
        data.clear();
    }
    public void addData(String address,int port,long diff){
        data.add(new TableEntry(address, port, diff));
    }
    public void refresh(){
        this.fireTableDataChanged();
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int i, int i1) {
        if (i1 == 0) {
            return data.get(i).address;
        }
        if (i1 == 1) {
            return data.get(i).port;
        }
        if (i1 == 2) {
            return data.get(i).diff;
        }
        return null;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
}

class TableEntry {
    String address;
    int port;
    long diff;

    public TableEntry(String address, int port, long diff) {
        this.address = address;
        this.port = port;
        this.diff = diff;
    }
}
