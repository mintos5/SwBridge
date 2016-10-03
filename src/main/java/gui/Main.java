package gui;

import model.BridgeException;
import model.Program;
import model.SimpleList;
import model.SimpleListFunction;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private Main self = this;

    public Main() {
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    model.openItergaceThread(comboBox1.getSelectedIndex(),self);
                } catch (BridgeException e) {
                    e.printStackTrace();
                }
            }
        });

        sendFRAMEButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                model.sendFrame(comboBox1.getSelectedIndex());
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
        model = new Program();
        comboBox1 = new JComboBox(new DeviceComboBoxModel(model));
        comboBox1.setSelectedIndex(0);
    }

    public <T> void print(T o) {
        //not running in swing...
        String test = "";
        if (o instanceof String) {
            test = (String) o;
        }
        System.out.println(test);

//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                Document doc = textPane1.getDocument();
//                doc.insertString(doc.getLength(),test,null);
//            }
//        });
    }
}

class DeviceComboBoxModel extends AbstractListModel implements ComboBoxModel{

    Program model;
    String selection;


    public DeviceComboBoxModel(Program model) {
        this.model = model;
        this.model.getDevices();
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
