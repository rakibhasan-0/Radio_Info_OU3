
package Controll;
import Model.Channel;
import Model.Schedule;
import View.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UIManager {
    private final ProgramView programView;
    private final MenuBarView menuBarView;
    private Channel selectedChannel;
    private HashMap<String, ArrayList<Channel>> channelsWithTypes;
    private HashSet<String> types;


    public UIManager(ProgramView programView, MenuBarView menuBarView) {
        this.programView = programView;
        this.menuBarView = menuBarView;
    }


    public void addChannelType() {
        JMenu channelTypeMenu = menuBarView.getChannelsTypeMenu();
        for (String types : types){
            JMenuItem channelType = new JMenuItem(types);
            channelTypeMenu.add(channelType);
            channelType.addActionListener(e->displayChannels(types));
        }
    }


    public void displayChannels(String channelName){
        programView.clearChannelButtons();
        ArrayList<Channel> channels = channelsWithTypes.get(channelName);

        for (Channel channel : channels) {
            JButton button = new JButton(channel.getChannelName());
            button.setIcon(channel.getIcon());
            button.addActionListener(e -> {
                selectedChannel = channel;
            });
            programView.addChannelButton(button);
        }
    }


    public void setupChannelButtons(HashSet<String> types, HashMap<String,ArrayList<Channel>>channelsWithTypes) {
        this.types = types;
        this.channelsWithTypes = channelsWithTypes;
        addChannelType();
    }



    public void updateProgramTable(Channel channel, ArrayList<Schedule> schedules) {
        menuBarView.setSelectedChannelLabel(channel.getChannelName());
    }


    public void setChannelUpdatedLabel() {
        LocalDateTime lastUpdatedTime = LocalDateTime.now();
        String formattedTime = "Channels Updated: " + lastUpdatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        menuBarView.getChannelUpdatedLabel().setText(formattedTime);
    }


    public void setChannelUpdatingLabel(){
        menuBarView.getChannelUpdatedLabel().setText("Updating---");
    }

    public void setScheduleUpdatedLabel(String channelName) {
        LocalDateTime lastUpdatedTime = LocalDateTime.now();
        String formattedTime = "Schedule Updated: " + " << "+ channelName +" >> "+ lastUpdatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        menuBarView.getProgramUpdatedLabel().setText( formattedTime );
    }


    public void setScheduleIsUpdatingLabel(){
        menuBarView.getProgramUpdatedLabel().setText("Updating----");
    }

}
