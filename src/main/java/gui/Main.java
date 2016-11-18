package gui;

import model.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by michal on 2.10.2016.
 */
public class Main{
    private Program model;
    private GuiMacTableModel guiMacTableModel;
    private GuiFilterTabModel[] guiFilterTabArray;
    private static JFrame frame;
    private JPanel prefab;
    private JTabbedPane tabbedPane1;
    private JComboBox comboBox1;
    private JButton startButton;
    private JTextPane textPane1;
    private JComboBox comboBox2;
    private JTable table1;
    private JTextPane textPane2;
    private JButton resetMacButton;
    private JTextField textFieldTTL;
    private JButton setTTLButton;
    private JTextField log1TextField1;
    private JTextField log1TextField2;
    private JCheckBox log1checkBox;
    private JButton addFilterButton1;
    private JButton updateButton1;
    private JButton removeButton1;
    private JTable filter1Table;
    private JButton addFilterButton2;
    private JButton updateButton2;
    private JButton removeButton2;
    private JTable filter2Table;
    private JTextField log1TextField3;
    private JTextField log1TextField4;
    private JTextField log1TextField5;
    private JTextField log2TextField1;
    private JTextField log2TextField2;
    private JTextField log2TextField3;
    private JTextField log2TextField4;
    private JTextField log2TextField5;
    private JCheckBox log2checkBox;
    private JTextField log1TextField6;
    private JTextField log2TextField6;
    private JButton log2ClearButton;
    private JButton log1ClearButton;
    private Main self = this;
    private Boolean running = false;

    public Main() {
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (!running) {
                    try {
                        model.openIterfaceThread(comboBox1.getSelectedIndex(),0);
                        model.openIterfaceThread(comboBox2.getSelectedIndex(),1);
                    } catch (BridgeException e) {
                        e.printStackTrace();
                    }
                    startButton.setText("Stop");
                    running = true;
                }
                else {
                    model.closeInterface();
                    startButton.setText("Start");
                    String out0 = model.getHandler(0).getCounter()+" readed frames from PORT0";
                    String out1 = model.getHandler(1).getCounter()+" readed frames from PORT1";
                    StringBuilder out = new StringBuilder(out0);
                    out.append(System.getProperty("line.separator"));
                    out.append(out1);
                    JOptionPane.showMessageDialog(Main.frame,out.toString());
                    running = false;
                }

            }
        });

        resetMacButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                model.resetMacTable();
            }
        });
        setTTLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                model.setMacTableTimer(Integer.parseInt(textFieldTTL.getText()));
            }
        });
        log1ClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                model.getHandler(0).clearStatistics();
                textPane1.setText("");
            }
        });
        log2ClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                model.getHandler(1).clearStatistics();
                textPane2.setText("");
            }
        });
        addFilterButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filterAdd(0);
            }
        });
        removeButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filterRemove(0);
            }
        });
        updateButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filterUpdate(0);
            }
        });
        addFilterButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filterAdd(1);
            }
        });
        removeButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filterRemove(1);
            }
        });
        updateButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filterUpdate(1);
            }
        });
    }

    public static void main(String[] args) {
        Main mainko = new Main();
        frame = new JFrame("SwBridge");
        frame.setContentPane(mainko.prefab);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600,300));
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        textPane1 = new JTextPane();
        textPane2 = new JTextPane();
        log1checkBox = new JCheckBox();
        log2checkBox = new JCheckBox();
        //MACtable
        guiMacTableModel = new GuiMacTableModel();
        //all for statisticsArray
        SimpleList<String> logs1 = new SimpleList<String>(new Logs(textPane1,log1checkBox));
        SimpleList<String> logs2 = new SimpleList<String>(new Logs(textPane2,log2checkBox));
        JTextField[] log1TextFields = new JTextField[8];
        JTextField[] log2TextFields = new JTextField[8];
        initTextFields(log1TextFields,log2TextFields);
        StatisticsGroup[] statisticsArray = new StatisticsGroup[2];
        statisticsArray[0] = new StatisticsGroup(logs1,new guiStatistics(log1TextFields));
        statisticsArray[1] = new StatisticsGroup(logs2,new guiStatistics(log2TextFields));
        //filters
        guiFilterTabArray = new GuiFilterTabModel[2];
        guiFilterTabArray[0] = new GuiFilterTabModel();
        guiFilterTabArray[1] = new GuiFilterTabModel();
        filter1Table = new JTable(guiFilterTabArray[0]);
        filter2Table = new JTable(guiFilterTabArray[1]);
        try {
            model = new Program(guiMacTableModel,guiFilterTabArray,statisticsArray);
        } catch (BridgeException e) {
            e.printStackTrace();
        }
        table1 = new JTable(guiMacTableModel);
        comboBox1 = new JComboBox(new DeviceComboBoxModel(model));
        comboBox2 = new JComboBox(new DeviceComboBoxModel(model));
    }
    private void initTextFields(JTextField[] log1TextFields,JTextField[] log2TextFields){
        log1TextFields[0] = log1TextField1 = new JTextField();
        log1TextFields[1] = log1TextField2 = new JTextField();
        log1TextFields[2] = log1TextField3 = new JTextField();
        log1TextFields[3] = log1TextField4 = new JTextField();
        log1TextFields[4] = log1TextField5 = new JTextField();
        log1TextFields[5] = log1TextField6 = new JTextField();

        log2TextFields[0] = log2TextField1 = new JTextField();
        log2TextFields[1] = log2TextField2 = new JTextField();
        log2TextFields[2] = log2TextField3 = new JTextField();
        log2TextFields[3] = log2TextField4 = new JTextField();
        log2TextFields[4] = log2TextField5 = new JTextField();
        log2TextFields[5] = log2TextField6 = new JTextField();
    }

    private void filterAdd(int port){
        FilterTableEntry entry = new AddFilter().showDialog();
        if (entry==null){
            return;
        }
        guiFilterTabArray[port].addData(entry);
    }
    private void filterRemove(int port){
        if (port==0){
            guiFilterTabArray[port].removeData(filter1Table.getSelectedRow());
        }
        if (port==1){
            guiFilterTabArray[port].removeData(filter2Table.getSelectedRow());
        }
    }
    private void filterUpdate(int port){
        if (port==0){
            int pos = filter1Table.getSelectedRow();
            if (pos<0){
                return;
            }
            FilterTableEntry entry = new AddFilter(guiFilterTabArray[port].getData(pos)).showDialog();
            if (entry==null){
                return;
            }
            guiFilterTabArray[port].updateData(pos,entry);
        }
        if (port==1){
            int pos = filter2Table.getSelectedRow();
            if (pos<0){
                return;
            }
            FilterTableEntry entry = new AddFilter(guiFilterTabArray[port].getData(pos)).showDialog();
            if (entry==null){
                return;
            }
            guiFilterTabArray[port].updateData(pos,entry);
        }
    }
}

class DeviceComboBoxModel extends AbstractListModel implements ComboBoxModel{

    Program model;
    String selection;


    public DeviceComboBoxModel(Program model) {
        this.model = model;
    }

    public void setSelectedItem(Object o) {
        selection = (String) o;
    }

    public Object getSelectedItem() {
        return selection;
    }

    public int getSize() {
        return model.getAlldevs().size();
    }

    public Object getElementAt(int i) {
        return model.getAlldevs().get(i).getName();
    }
}

class Logs implements SimpleListFunction{

    private JTextPane textPane1;
    private JCheckBox checkBox;

    public Logs(JTextPane textPane1,JCheckBox checkBox) {
        this.textPane1 = textPane1;
        this.checkBox = checkBox;
    }

    @Override
    public <T> void print(T o) {
        //not running in swing...
        String test = "";

        if (o instanceof String) {
            test = (String) o;
        }
        final String fTest = test;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (checkBox.isSelected()){
                    Document doc = textPane1.getDocument();
                    try {
                        doc.insertString(doc.getLength(),fTest,null);
                        doc.insertString(doc.getLength(),"\n",null);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

class guiStatistics implements StatisticsFunc{
    private JTextField[] logTextFields;

    public guiStatistics(JTextField[] logTextFields) {
        this.logTextFields = logTextFields;
    }

    @Override
    public void showOnGui(StatisticsGroup model) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                logTextFields[0].setText(Long.toString(model.getFrames())+"/"+Long.toString(model.getOutFrames()));
                logTextFields[1].setText(Long.toString(model.getArp())+"/"+Long.toString(model.getOutArp()));
                logTextFields[2].setText(Long.toString(model.getIpv4())+"/"+Long.toString(model.getOutIpv4()));
                logTextFields[3].setText(Long.toString(model.getTcp())+"/"+Long.toString(model.getOutTcp()));
                logTextFields[4].setText(Long.toString(model.getUdp())+"/"+Long.toString(model.getOutUdp()));
                logTextFields[5].setText(Long.toString(model.getIcmp())+"/"+Long.toString(model.getOutIcmp()));
            }
        });
    }
}
