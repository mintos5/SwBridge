package gui;

import model.BridgeException;
import model.Program;
import model.SimpleList;
import model.SimpleListFunction;
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
    private static JFrame frame;
    private JPanel prefab;
    private JTabbedPane tabbedPane1;
    private JComboBox comboBox1;
    private JButton startButton;
    private JTextPane textPane1;
    private JButton sendFRAMEButton;
    private JComboBox comboBox2;
    private JTable table1;
    private JTextPane textPane2;
    private JButton resetMacButton;
    private JTextField textField1;
    private JButton setTTLButton;
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

        sendFRAMEButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                JPacket packet =
                        new JMemoryPacket(JProtocol.ETHERNET_ID,
                                " d4ca6d33 2906dc85 de9fc9e9 08004500 "
                                + " 003c7203 00008001 b30ac0a8 6961c0a8 "
                                + " 2b010800 447c0001 08df6162 63646566 "
                                + " 99999999 99999999 99999999 99999999 "
                                + " 99999999 99999999 9999");
                ByteBuffer bbuf = ByteBuffer.allocateDirect(packet.getTotalSize());
                packet.transferTo(bbuf);
                PcapPacket p2 = new PcapPacket(JMemory.Type.POINTER); // Uninitialized
                bbuf.flip(); // Have to flip the buffer to access the just written contents
                try {
                    p2.peer(bbuf); // No copies, peered directly with external buffer
                } catch (PeeringException e) {
                    e.printStackTrace();
                }
                //model.sendFrame(0,p2);
                ArrayList<NetworkInterface> interfaces = null;
                try {
                    interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface info : interfaces){
                        if(info.isUp()){
                            System.out.println(info.getName());
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        });
        resetMacButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                model.resetMacTable();
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
        guiMacTableModel = new GuiMacTableModel();
        SimpleList<String>[] arrayLogs = new SimpleList[2];
        arrayLogs[0] = new SimpleList<String>(new Logs(textPane1));
        arrayLogs[1] = new SimpleList<String>(new Logs(textPane2));
        try {
            model = new Program(guiMacTableModel,arrayLogs);
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

    public Logs(JTextPane textPane1) {
        this.textPane1 = textPane1;
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
                Document doc = textPane1.getDocument();
                try {
                    doc.insertString(doc.getLength(),fTest,null);
                    doc.insertString(doc.getLength(),"\n",null);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
