package gui;

import model.*;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PeeringException;
import org.jnetpcap.protocol.JProtocol;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

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
    private JComboBox log1comboBox;
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
    private JTextField log1TextField7;
    private JTextField log2TextField1;
    private JTextField log2TextField2;
    private JTextField log2TextField3;
    private JTextField log2TextField4;
    private JTextField log2TextField5;
    private JTextField log2TextField7;
    private JCheckBox log2checkBox;
    private JComboBox log2comboBox;
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
                        //model.openIterfaceThread(comboBox2.getSelectedIndex(),1);
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
                model.getHandler(0).clearList();
                textPane1.setText("");
            }
        });
        log2ClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                model.getHandler(1).clearList();
                textPane2.setText("");
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
        guiMacTableModel = new GuiMacTableModel();
        SimpleList<String> logs1 = new SimpleList<String>(new Logs(textPane1,log1checkBox));
        SimpleList<String> logs2 = new SimpleList<String>(new Logs(textPane2,log2checkBox));
        StatisticsGroup[] statisticsArray = new StatisticsGroup[2];
        statisticsArray[0] = new StatisticsGroup(logs1);
        statisticsArray[1] = new StatisticsGroup(logs2);
        guiFilterTabArray = new GuiFilterTabModel[2];
        try {
            model = new Program(guiMacTableModel,guiFilterTabArray,statisticsArray);
        } catch (BridgeException e) {
            e.printStackTrace();
        }
        table1 = new JTable(guiMacTableModel);
        comboBox1 = new JComboBox(new DeviceComboBoxModel(model));
        comboBox2 = new JComboBox(new DeviceComboBoxModel(model));
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
