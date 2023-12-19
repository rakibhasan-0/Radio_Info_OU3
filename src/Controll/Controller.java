package Controll;
import Model.*;
import View.*;
import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Controller {
    private final MenuBarView menuBarView;
    private final Cache cache;
    private Channel selectedChannel;
    private Timer automaticUpdateTimer;
    private final UIManager uiManager;
    private final APIManager apiManager;

    public Controller(MenuBarView menuBarView, ProgramView programView) {
        this.menuBarView = menuBarView;
        this.uiManager = new UIManager(programView, menuBarView);
        this.cache = new Cache();
        apiManager = new APIManager(this);
        apiManager.fetchChannelDataFromAPI();
    }

    private void UpdateSchedule() {
        if (selectedChannel != null) {
            uiManager.setScheduleIsUpdatingLabel();
            cache.clearCacheForAChannel(selectedChannel);
            apiManager.fetchScheduleForChannel(selectedChannel);
        }else {
            JOptionPane.showMessageDialog(null, "Please select a channel first before updating the schedule.");
        }
    }

    private void updateChannels() {
        uiManager.setChannelUpdatingLabel();
        apiManager.fetchChannelDataFromAPI();
    }



    public void updatedChannels (HashSet<String> types, HashMap<String,ArrayList<Channel>> channelWithType) {
        SwingUtilities.invokeLater(() -> {
            cache.clearCache();
            uiManager.setupChannelButtons(types,channelWithType);
            uiManager.setChannelUpdatedLabel();
        });
    }


    public void getSchedule (Channel channel, ArrayList<Schedule> schedules) {
        cache.addSchedules(channel, schedules);
        SwingUtilities.invokeLater(() -> {
            selectedChannel  = channel;
            uiManager.updateProgramTable(channel, schedules);
            uiManager.setScheduleUpdatedLabel(channel.getChannelName());
        });
    }

    // it is just unecessary to make cache as normal; I mean you may not need to use it outside of the controller,

}