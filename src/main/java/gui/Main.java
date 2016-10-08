package gui;

import model.BridgeException;
import model.Program;
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
import java.nio.ByteBuffer;

/**
 * Created by michal on 2.10.2016.
 */
public class Main implements SimpleListFunction{
    private Program model;
    private JPanel prefab;
    private JTabbedPane tabbedPane1;
    private JComboBox comboBox1;
    private JButton startButton;
    private JTextPane textPane1;
    private JButton sendFRAMEButton;
    private JComboBox comboBox2;
    private Main self = this;
    private Boolean running = false;

    public Main() {
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (!running) {
                    try {
                        model.openIterfaceThread(comboBox1.getSelectedIndex(),0,self);
                        model.openIterfaceThread(comboBox2.getSelectedIndex(),1,self);
                    } catch (BridgeException e) {
                        e.printStackTrace();
                    }
                    startButton.setText("Stop");
                    running = true;
                }
                else {
                    model.closeInterface();
                    startButton.setText("Start");
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
                model.sendFrame(0,p2);
            }
        });
    }

    public static void main(String[] args) {
        Main mainko = new Main();
        JFrame frame = new JFrame("SwBridge");
        frame.setContentPane(mainko.prefab);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600,300));
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        try {
            model = new Program();
        } catch (BridgeException e) {
            e.printStackTrace();
        }
        comboBox1 = new JComboBox(new DeviceComboBoxModel(model));
        comboBox2 = new JComboBox(new DeviceComboBoxModel(model));
    }

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
