package View;

import javax.swing.*;
import java.awt.*;

public class GUI {

    public GUI(){
        JFrame frame = new JFrame();
        JMenuBar menuBar = new JMenuBar();
        JMenu update = new JMenu("Update");
        JMenu typeOfChannel = new JMenu("Model.Channel Type");
        JMenuItem channelUpdate = new JMenuItem("ChannelUpdate");
        JMenuItem scheduleUpdate = new JMenuItem("ScheduleUpdate");
        update.add(channelUpdate);
        update.add(scheduleUpdate);
        menuBar.add(update);
        menuBar.add(typeOfChannel);
        frame.add(menuBar, BorderLayout.NORTH);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public static void main(String[] args){
        new GUI();
    }
}
