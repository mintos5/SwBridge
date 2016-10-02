package gui;

import model.Program;

import javax.swing.*;
import java.awt.*;

/**
 * Created by michal on 2.10.2016.
 */
public class Main {

    private Program model;
    private JPanel prefab;

    public static void main(String[] args) {
        JFrame frame = new JFrame("SwBridge");
        frame.setContentPane(new Main().prefab);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600,300));
        frame.pack();
        frame.setVisible(true);
    }
}
