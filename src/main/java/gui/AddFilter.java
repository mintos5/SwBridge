package gui;

import javax.swing.*;
import java.awt.event.*;

public class AddFilter extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBox1;
    private JTextField srcMacField;
    private JTextField destMacField;
    private JTextField srcIpField;
    private JTextField destIpField;
    private JCheckBox allowRuleCheckBox;
    private JTextField indexField;
    private JTextField protocolField;
    private FilterTableEntry dataOut;

    public AddFilter() {
        this.dataOut = new FilterTableEntry();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public AddFilter(FilterTableEntry update){
        this();
        if (update.isIncoming()){
            comboBox1.setSelectedIndex(0);
        }
        else {
            comboBox1.setSelectedIndex(1);
        }
        allowRuleCheckBox.setSelected(update.isAllowRule());
        srcMacField.setText(update.getSourceMac());
        destMacField.setText(update.getDestMac());
        srcIpField.setText(update.getSourceIp());
        destIpField.setText(update.getDestIp());
        protocolField.setText(Integer.toString(update.getProtocol()));
    }

    private void onOK() {
        // add your code here
        try {
            this.dataOut.setAllowRule(allowRuleCheckBox.isSelected());
            this.dataOut.setDestMac(destMacField.getText());
            this.dataOut.setDestIp(destIpField.getText());
            this.dataOut.setSourceIp(srcIpField.getText());
            this.dataOut.setSourceMac(srcMacField.getText());
            this.dataOut.setProtocol(Integer.parseInt(protocolField.getText()));
            if (comboBox1.getSelectedIndex()==0){
                this.dataOut.setIncoming(true);
            }
            if (comboBox1.getSelectedIndex()==1){
                this.dataOut.setIncoming(false);
            }
        }
        catch (NumberFormatException ex){
            System.out.println("Chybne zadane udaje");
        }

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        this.dataOut = null;
        dispose();
    }

    public FilterTableEntry showDialog() {
        this.pack();
        this.setVisible(true);
        return this.dataOut;
    }
}
